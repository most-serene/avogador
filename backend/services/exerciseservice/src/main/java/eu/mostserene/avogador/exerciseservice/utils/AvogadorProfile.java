package eu.mostserene.avogador.exerciseservice.utils;

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
