package eu.mostserene.avogador.exerciseservice.security.restapicontrol;

public enum RequestSource {
    REACT_APP("React-App"),
    REST_API("Rest-Api");

    public final String label;

    public static RequestSource valueOfLabel(String label) {
        for (RequestSource source : values()) {
            if (source.label.equals(label)) {
                return source;
            }
        }
        return null;
    }

    private RequestSource(String label) {
        this.label = label;
    }
}
