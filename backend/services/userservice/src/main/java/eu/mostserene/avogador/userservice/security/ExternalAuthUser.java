package eu.mostserene.avogador.userservice.security;

import lombok.Data;

@Data
public abstract class ExternalAuthUser {
    private String email;
    private String domain;
    private String givenName;
    private String familyName;
    private String picture;
    private String provider;

    public ExternalAuthUser() {
    }

    public ExternalAuthUser(String email, String domain, String givenName, String familyName, String picture, String provider) {
        this.email = email;
        this.domain = domain;
        this.givenName = givenName;
        this.familyName = familyName;
        this.picture = picture;
        this.provider = provider;
    }
}
