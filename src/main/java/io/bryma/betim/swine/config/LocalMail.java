package io.bryma.betim.swine.config;

import com.icegreen.greenmail.util.GreenMail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


@Component
public class LocalMail {

    private final Logger log = LoggerFactory.getLogger(LocalMail.class);


    private GreenMail greenMail;
    private String email;
    private String password;

    public LocalMail( @Value("${email}") String username, @Value("${password}") String password, GreenMail greenMail) {
        this.email = username;
        this.password = password;
        this.greenMail = greenMail;
    }


    void sendMail(String recipient, String subject, String message) throws MessagingException {

        Session session = greenMail.getSmtp().createSession();

        Message msg = new MimeMessage(session);

        InternetAddress addressTo = new InternetAddress(recipient);
        msg.setRecipient(Message.RecipientType.TO, addressTo);
        msg.setRecipient(Message.RecipientType.TO, addressTo);

        msg.setSubject(subject);
        msg.setContent(message, "text/plain");
        msg.setFrom(new InternetAddress(email));

        Transport t = session.getTransport("smtp");
        t.connect(email, password);
        t.sendMessage(msg, msg.getAllRecipients());
        t.close();
    }

}
