package com.cimb.finalproject.util;




import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.FileSystemResource;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;


import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender sender;
    private String uploadPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\";

    public void writePdf(OutputStream outputStream) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();
        Paragraph paragraph = new Paragraph();
        paragraph.add(new Chunk("hello!"));
        document.add(paragraph);
        document.close();
    }

    public void sendEmail(String toAddress, String subject, String body) {
        MimeMessage message = this.sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(body, true);

            this.sender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    public void sendMessageWithAttachment(String to, String subject, String body, String pathToAttachment) throws MessagingException {

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);
        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment(file.getFilename(), file);
            sender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }




}