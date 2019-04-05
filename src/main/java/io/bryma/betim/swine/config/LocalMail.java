package io.bryma.betim.swine.config;

import at.ac.tuwien.dsg.smartcom.GreenMailOutputAdapter;
import at.ac.tuwien.dsg.smartcom.utils.PropertiesLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.util.GreenMail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
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

    private static final Logger log = LoggerFactory.getLogger(GreenMailOutputAdapter.class);
    private static final Session session = getMailSession();
    private static String username;
    private static String password;

    private static Session getMailSession(){
        final Properties props = new Properties();

        username = PropertiesLoader.getProperty("EmailAdapter.properties", "username");
        password = PropertiesLoader.getProperty("EmailAdapter.properties", "password");

        props.setProperty("mail.smtp.host", PropertiesLoader.getProperty("EmailAdapter.properties", "hostOutgoing"));
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.port", PropertiesLoader.getProperty("EmailAdapter.properties", "portOutgoing"));
        props.setProperty("mail.smtp.socketFactory.class", "com.icegreen.greenmail.util.DummySSLSocketFactory" );
        props.setProperty("mail.smtp.socketFactory.fallback", "false" );
        props.setProperty("mail.from", username);

        return Session.getInstance(props, new javax.mail.Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username,password);
            }
        });

    }

    public static void sendMail(String recipient, String subject, String message) throws MessagingException {
        javax.mail.Message msg = new MimeMessage(session);

        InternetAddress addressTo = new InternetAddress(recipient);
        msg.setRecipient(javax.mail.Message.RecipientType.TO, addressTo);
        msg.setRecipient(javax.mail.Message.RecipientType.TO, addressTo);

        msg.setSubject(subject);
        msg.setContent(message, "text/plain");
        msg.setFrom(new InternetAddress(username));

        Transport t = session.getTransport("smtp");
        t.connect(username, password);
        t.sendMessage(msg, msg.getAllRecipients());
        t.close();
    }

}
