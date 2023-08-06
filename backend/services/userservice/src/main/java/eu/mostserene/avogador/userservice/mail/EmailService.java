package eu.mostserene.avogador.userservice.mail;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Component
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    /**
     * Sends an email as the Application
     * @param to the receiver
     * @param subject the subject of the email
     * @param text the body of the email
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Avogador <assistant@mostserene.eu>");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}
