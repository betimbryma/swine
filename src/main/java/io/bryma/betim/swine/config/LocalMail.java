package io.bryma.betim.swine.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 * @author Betim Bryma
 */
@Component
public class LocalMail {

    private final Logger log = LoggerFactory.getLogger(LocalMail.class);


    private String email;
    private String password;
    private Session session;

    public LocalMail( @Value("${email}") String username, @Value("${password}") String password,
                     @Value("${hostOutgoing}") String hostOutgoing, @Value("${portOutgoing}") String portOutgoing,
                     @Value("${hostIncoming}") String hostIncoming, @Value("${portIncoming}") String portIncoming) {
        this.email = username;
        this.password = password;

        Properties props = new Properties();

        props.setProperty("mail.smtp.host", hostOutgoing);
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.port", portOutgoing);
        props.setProperty("mail.smtp.socketFactory.class", "com.icegreen.greenmail.util.DummySSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false" );
        props.setProperty("mail.from", email);

        this.session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email,password);
            }
        });
    }


    public void sendMail(String recipient, String subject, String message) throws MessagingException {
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
