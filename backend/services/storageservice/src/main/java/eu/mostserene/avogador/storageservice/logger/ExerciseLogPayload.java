package eu.mostserene.avogador.storageservice.logger;

import lombok.Data;

@Data
public class ExerciseLogPayload implements LogPayload {
    String exercise;
    String event;
    String message;

    public ExerciseLogPayload() {
    }

    public ExerciseLogPayload(String exercise, String event, String message) {
        this.exercise = exercise;
        this.event = event;
        this.message = message;
    }

    @Override
    public String toString() {
        return "[" + exercise + "]-(" + event + ")-{" + message + "}";
    }
}
