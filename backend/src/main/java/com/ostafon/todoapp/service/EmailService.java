package com.ostafon.todoapp.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmailService {
    private final String username = "sasaostafi8@gmail.com";
    private final String password = "wvca nmaw bekm xlax";

    public void sendTaskByEmail(String to, String subject, String body) throws MessagingException {
        System.out.println("➡️ Отправка письма: to=" + to + ", subject=" + subject + ", text=" + body);
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
        System.out.println("✅ Email успешно отправлен!");
    }

    public List<Map<String, String>> receiveEmailsIMAP() {
        List<Map<String, String>> messages = new ArrayList<>();
        try {
            Properties props = new Properties();
            props.put("mail.store.protocol", "imap");
            props.put("mail.imap.host", "imap.gmail.com");
            props.put("mail.imap.port", "993");
            props.put("mail.imap.ssl.enable", "true");

            Session session = Session.getInstance(props);
            Store store = session.getStore("imap");
            store.connect(username, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] emails = inbox.getMessages(Math.max(1, inbox.getMessageCount() - 10), inbox.getMessageCount());
            for (Message msg : emails) {
                Map<String, String> emailData = new HashMap<>();
                emailData.put("from", Arrays.toString(msg.getFrom()));
                emailData.put("subject", msg.getSubject());
                emailData.put("body", getTextFromMessage(msg));
                messages.add(emailData);
            }

            inbox.close();
            store.close();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("from", "Ошибка");
            error.put("subject", "IMAP");
            error.put("body", e.getMessage());
            messages.add(error);
        }
        return messages;
    }

    private String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i);
                if (part.isMimeType("text/plain")) {
                    return part.getContent().toString();
                } else if (part.isMimeType("text/html")) {
                    return org.jsoup.Jsoup.parse(part.getContent().toString()).text();
                }
            }
        }
        return "[Неподдерживаемый формат письма]";
    }



}
