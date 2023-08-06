package eu.mostserene.avogador.userservice.mail;

import lombok.Data;

@Data
public class EmailMessageDTO {
    private String receiver;
    private String subject;
    private String body;

    public EmailMessageDTO() {
    }
}
