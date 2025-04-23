package com.esc.wmg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.esc.wmg.service.sendMailService;

@Controller
public class EmailController {

    @Autowired
    private sendMailService sendEmailService;

    @GetMapping("/sendEmail")
    public String sendEmail(@RequestParam String recipient,
                            @RequestParam String body,
                            @RequestParam String subject) {
        sendEmailService.sendMail(recipient, body, subject);
        return "SENDED";
    }
}
