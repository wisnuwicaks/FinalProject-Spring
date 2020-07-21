package com.cimb.finalproject.controller;


import com.cimb.finalproject.dao.*;
import com.cimb.finalproject.entity.*;
import com.cimb.finalproject.util.EmailUtil;
import jdk.nashorn.internal.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Objects;

@RestController
@RequestMapping("/transactions")
@CrossOrigin
public class TransactionController {
    private String uploadPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\trfslip\\";
    private String invoicePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\invoice\\";


    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private TransactionDetailRepo transactionDetailRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CartRepo cartRepo;

    @PostMapping("/add_transaction/{userId}")
    public Transaction addTransaction(@PathVariable int userId, @RequestBody Transaction trxData ){
        Date date = new Date();
        User findUser = userRepo.findById(userId).get();
        trxData.setBuyDate(date);
        trxData.setEndTrxDate(null);
        trxData.setUser(findUser);
        return transactionRepo.save(trxData);
    }

    @PostMapping("/add_detail/{trxId}/{productId}")
    public TransactionDetail addTransactionDetail(@PathVariable int trxId,@PathVariable int productId, @RequestBody TransactionDetail trxDtlData ){
        Product findProduct = productRepo.findById(productId).get();
        Transaction findTransaction = transactionRepo.findById(trxId).get();
        trxDtlData.setTransaction(findTransaction);
        trxDtlData.setProduct(findProduct);
        return transactionDetailRepo.save(trxDtlData);

    }

    @GetMapping("/all_trx")
    public Iterable<Transaction> getAllTrx(){
        return transactionRepo.findAll();
    }


    @GetMapping("/user/{userId}")
    public Iterable<Transaction> getTrxByUserId(@PathVariable int userId){
        return transactionRepo.findTrxByUserId(userId);
    }

    @GetMapping("/user/{userId}/{status}")
    public Iterable<Transaction> getUsrTrxByStatus(@PathVariable int userId,@PathVariable String status){
        return transactionRepo.findUserTrxByStatus(userId,status);
    }

    @GetMapping("/all_trx/filter/{status}")
    public Iterable<Transaction> getTrxByStatus(@PathVariable String status){
        return transactionRepo.findTrxByStatus(status);
    }

    @GetMapping("/trx_detail/{trxId}")
    public Iterable<TransactionDetail> getTrxDetailByUserId(@PathVariable int trxId){
        return transactionDetailRepo.findDetailTrxByTrxId(trxId);
    }

    @GetMapping("/trx_detail_join/{trxId}")
    public Iterable<Product> getProductJoin (@PathVariable int trxId){
        return productRepo.findProductByJoinTrx(trxId);
    }

    @PutMapping("/upload_slip/{trxId}")
    @Transactional
    public Transaction uploadSlip (@RequestParam("file") MultipartFile file, @PathVariable int trxId){
        Transaction findTransaction = transactionRepo.findById(trxId).get();
        Date date = new Date();
        if(!file.equals(null)){
            String fileExtension = file.getContentType().split("/")[1];
            String newFileName = "SLIP-" + date.getTime() + "." + fileExtension;

            // Get file's original name || can generate our own
            String fileName = StringUtils.cleanPath(newFileName);

            // Create path to upload destination + new file name
            Path path = Paths.get(StringUtils.cleanPath(uploadPath) + fileName);

            try {
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/transactions/slip_transfer/")
                    .path(fileName).toUriString();

            findTransaction.setTrfSlip(fileDownloadUri);
            findTransaction.setTrxMessage(null);
            transactionRepo.save(findTransaction);
        }
        return findTransaction;
    }

    @GetMapping("/slip_transfer/{fileName:.+}")
    @Transactional
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

    @PutMapping("/reject/{trxId}/{rejectMsg}")
    @Transactional
    public Transaction rejectTransaction(@PathVariable int trxId,@PathVariable String rejectMsg){
        Transaction findTransaction = transactionRepo.findById(trxId).get();
        findTransaction.setTrfSlip(null);
        findTransaction.setTrxMessage(rejectMsg);
        return transactionRepo.save(findTransaction);
    }

    @PutMapping("/approve/{trxId}/{productId}/{quantity}")
    @Transactional
    public Transaction approveTransaction(@PathVariable int trxId, @PathVariable int productId,@PathVariable int quantity){
        Date date = new Date();
        Transaction findTransaction = transactionRepo.findById(trxId).get();
        Product findProduct = productRepo.findById(productId).get();
        if(!findTransaction.getStatus().equals("Success")) {
            findTransaction.setStatus("Success");
            findTransaction.setEndTrxDate(date);
            transactionRepo.save(findTransaction);
        }
        findProduct.setSoldQty(findProduct.getSoldQty()+quantity);
        productRepo.save(findProduct);

        return  findTransaction;
    }


    @PostMapping("/send_invoice/{userId}/{fileName}/**")
    public String sendInvoice(@PathVariable int userId, @PathVariable String fileName) throws MessagingException {
        System.out.println("ini : "+fileName);

        User findUser = userRepo.findById(userId).get();

        String message = "<h1>Please finish payment!</h1>\n Bellow is the transaction detail, after you transfer, send the transfer slip through payment menu!\n";

        Path path = Paths.get(StringUtils.cleanPath(invoicePath) +fileName+".pdf");

        emailUtil.sendMessageWithAttachment(findUser.getEmail(),"New Style Invoice",message, path.toString());
        return path.toString();
    }


}
