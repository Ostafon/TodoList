package com.ostafon.todoapp.controller;

import com.ostafon.todoapp.service.EmailService;
import com.ostafon.todoapp.service.TaskService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/email")
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend:3000"})
public class EmailController {
    private final EmailService emailService;
    private final TaskService taskService;

    public EmailController(EmailService emailService, TaskService taskService) {
        this.emailService = emailService;
        this.taskService = taskService;
    }


    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody Map<String, String> request) {
        String to = request.get("to");
        String subject = request.get("subject");
        String text = request.get("text");

        try {
            emailService.sendTaskByEmail(to, subject, text);
            return ResponseEntity.ok("Письмо отправлено");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Ошибка отправки: " + e.getMessage());
        }
    }


    @GetMapping("/inbox/imap")
    public ResponseEntity<List<Map<String, String>>> receiveEmails() {
        return ResponseEntity.ok(emailService.receiveEmailsIMAP());
    }
}
