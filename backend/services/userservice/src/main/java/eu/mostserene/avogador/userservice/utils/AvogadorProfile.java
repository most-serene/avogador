package eu.mostserene.avogador.userservice.utils;

public enum AvogadorProfile {
    DEVELOP("develop"),
    TESTING("testing"),
    STAGING("staging"),
    PRODUCTION("production");

    public final String profile;

    AvogadorProfile(String profile) {
        this.profile = profile;
    }
}
