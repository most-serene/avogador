package eu.mostserene.avogador.executorservice.executor;

public class TLEDetector {
    private boolean detected;

    public void detect() {
        detected = true;
    }

    public boolean wasDetected() {
        return detected;
    }
}
