package com.cimb.finalproject.controller;

import com.cimb.finalproject.dao.ProductRepo;
import com.cimb.finalproject.dao.UserRepo;
import com.cimb.finalproject.entity.Product;
import com.cimb.finalproject.entity.User;
import com.cimb.finalproject.util.EmailUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.management.RuntimeErrorException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    private String uploadPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\";

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

    private PasswordEncoder pwEncoder = new BCryptPasswordEncoder();

    @Autowired
    private EmailUtil emailUtil;

    @GetMapping("/all_users")
    public Iterable<User> getAllUser (){
        return userRepo.findAll();
    }

    @GetMapping("/{userId}")
    public User getAllUserById (@PathVariable  int userId){
        return userRepo.findById(userId).get();
    }

    @PostMapping("/login")
    public User userLogin (@RequestBody User userData){
        User findUser = userRepo.findByUsername(userData.getUsername()).get();
        System.out.println(userData.getPassword());
        System.out.println(findUser.getPassword());
        //check if not loged in yet
        if (pwEncoder.matches(userData.getPassword(), findUser.getPassword())) {
            System.out.println("masuk");
            return findUser;
        }
        //check authData from cache browser, karena di browser tersimpan hash passwordnya
        if (userData.getPassword().equals(findUser.getPassword())) {
            System.out.println("masuk");
            return findUser;
        }
        throw new RuntimeException("Wrong password!");
    }

    //find all with username
    @PostMapping("/getusername")
    public Iterable<User> getUserByUsername(@RequestBody User userData){
        return userRepo.findAllByUsername(userData.getUsername());
    }

    //find all with email
    @PostMapping("/getemail")
    public Iterable<User> getUserByEmail(@RequestBody User userData){
        return userRepo.findAllByEmail(userData.getEmail());
    }

    @PostMapping("/user_to_reset")
    public User getUserById(@RequestBody User userReset ){
        System.out.println(userReset.getVerificationCode());
        System.out.println(userReset.getId());

        User findUser = userRepo.getUserToReset(userReset.getId(),userReset.getVerificationCode()).get();
//        User findUser = userRepo.findById(userReset.getId()).get();
        return findUser;
    }

    @PostMapping("/register")
    @Transactional
    public User registerUser(@RequestBody User user) {
        String encodedPassword = pwEncoder.encode(user.getPassword());
        String verifyToken = pwEncoder.encode((user.getUsername()));
        user.setPassword(encodedPassword);
        user.setVerificationCode(verifyToken);
        user.setRole("user");
        userRepo.save(user);

        String linkToVerify = "http://localhost:8080/users/verify/" + user.getUsername() + "?token=" + verifyToken;
        String hrefhtml ="<a href=\""+linkToVerify+"\">link</a>";

        String message = "<h1>Welcome to New Style!</h1>\n Your account has been successfully registered!\n Click this "+hrefhtml+ " to verify your email \n";
        message+="if link doesnt work, click link bellow : \n";
        message+= "\n <a href=\""+linkToVerify+"\">"+linkToVerify+"</a>";

        emailUtil.sendEmail(user.getEmail(), "Email Confirmation", message);
        return user;
    }

    @GetMapping("/verify/{username}")
    public String userConfirmation(@PathVariable String username,@RequestParam String token){
        User findUserToConfirm = userRepo.findByUsername(username).get();
        System.out.println(token);
        if(findUserToConfirm.getVerificationCode().equals(token)){
            findUserToConfirm.setVerified(true);
            userRepo.save(findUserToConfirm);
            return "Thankyou your email has been verified";
        }
    throw new RuntimeException("Verification failed, system failure");

    }

    //send email to recovery lost password, link redirect to front end page used to create new password
    @PostMapping("/recoverypassword")
    @Transactional
    public User sendLinkResetPassword(@RequestBody User userData){
        User findUser = userRepo.findByEmail(userData.getEmail()).get();
//        findUser.setVerificationCode(findUser.getPassword());
        String message = "<h1>Link Recovery Password!</h1>\n ";
        message +="Silahkan klik <a href=\"http://localhost:3000/reset_password/"+findUser.getId()+"/"+findUser.getPassword()+"\">link</a> ini untuk reset ulang password anda";
        emailUtil.sendEmail(userData.getEmail(), "Email Confirmation", message);
        findUser.setVerificationCode(findUser.getPassword());
        return findUser;
    }

    @GetMapping("loginparams")
    public User getParams(@RequestParam("username") String username, @RequestParam("password") String password){
        User findUser = userRepo.findUserLogin(username,password);
        return findUser;
    }

    //change pass via user profile
    @PostMapping("/changepassword")
    public User userChangePassword(@RequestBody User userData, @RequestParam("oldPass") String oldPass,@RequestParam("newPass") String newPass){
        User findUser = userRepo.findById(userData.getId()).get();
        System.out.println(oldPass);
        System.out.println(newPass);

        if (pwEncoder.matches(oldPass, findUser.getPassword())) {
            System.out.println("masuk");
            String newPassEncoded = pwEncoder.encode(newPass);
            findUser.setPassword(newPassEncoded);
            userRepo.save(findUser);
            return findUser;
        }
        throw new RuntimeException("Wrong old password!");
    }

    //receive new password when forgot the old password
    @PutMapping("/resetpassword")
    @Transactional
    public User userResetPassword(@RequestBody User userData){
        User findUser = userRepo.findById(userData.getId()).get();


        String encodedPassword = pwEncoder.encode(userData.getPassword());
        userData.setPassword(encodedPassword);
        userData.setVerificationCode(null);
        userRepo.save(userData);

        return userData;
    }

    //update data from user profile
    @PostMapping("/update_profile/photo")
    public User updateProfilePicture(@RequestParam("file") MultipartFile file, @RequestParam("userData") String userData) throws JsonMappingException, JsonProcessingException{
        Date date = new Date();

        User userToUpdate = new ObjectMapper().readValue(userData, User.class);

        System.out.println(userToUpdate);

        User findUser = userRepo.findById(userToUpdate.getId()).get();


        if(!file.equals(null)){
            String fileExtension = file.getContentType().split("/")[1];
            String newFileName = "USER-" + date.getTime() + "." + fileExtension;

            // Get file's original name || can generate our own
            String fileName = StringUtils.cleanPath(newFileName);

            // Create path to upload destination + new file name
            Path path = Paths.get(StringUtils.cleanPath(uploadPath) + fileName);

            try {
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/profile_picture/")
                    .path(fileName).toUriString();

            userToUpdate.setProfilePicture(fileDownloadUri);
        }


        userRepo.save(userToUpdate);
        findUser.setPassword(null);
        return findUser;
    }

    @PutMapping("update_profile/data")
    public User updateProfileData (@RequestBody User userData){
        User findUser = userRepo.findById(userData.getId()).get();
        if(!findUser.equals(null)){
            return userRepo.save(userData);
        }
        throw new RuntimeException("System failure");
    }

    @GetMapping("/profile_picture/{fileName:.+}")
    public ResponseEntity<Object> downloadFile(@PathVariable String fileName){
        Path path = Paths.get(uploadPath + fileName);
        Resource resource = null;

        try {
            resource = new UrlResource(path.toUri());
        } catch(MalformedURLException e) {
            e.printStackTrace();
        }



        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/email")
    public String sendEmailKu(){
        emailUtil.sendEmail("wisnucaks@gmail.com", "Email Confirmation", "testing ini");
        return "ok";
    }


//    @GetMapping
//    public User userGetUsername(@RequestParam String username){
//        User findUser = userRepo.findByUsername(username).get();
//        return findUser;
//    }


}
