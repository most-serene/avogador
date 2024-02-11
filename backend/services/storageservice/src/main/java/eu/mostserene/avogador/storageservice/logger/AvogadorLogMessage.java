package eu.mostserene.avogador.storageservice.logger;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class AvogadorLogMessage {
    Timestamp timestamp;
    String user;
    SeverityLevel severity;
    LogPayload payload;

    public AvogadorLogMessage() {
    }

    public AvogadorLogMessage(Timestamp timestamp, String user, SeverityLevel severity, LogPayload payload) {
        this.timestamp = timestamp;
        this.user = user;
        this.severity = severity;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "]-[" + severity + "]-("+ user + ")-{" + payload + "}";
    }
}
