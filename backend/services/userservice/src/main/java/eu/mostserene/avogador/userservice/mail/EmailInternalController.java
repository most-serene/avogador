package eu.mostserene.avogador.userservice.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@Slf4j
public class EmailInternalController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    private void sendEmail(@RequestBody EmailMessageDTO message) {
        emailService.sendSimpleEmail(message.getReceiver(), message.getSubject(), message.getBody());
    }
}
