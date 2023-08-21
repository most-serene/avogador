package eu.mostserene.avogador.apigateway.status;

import lombok.Data;

@Data
public class MicroServiceStatus {
    private String name;

    private String status;

    public MicroServiceStatus() {
    }

    public MicroServiceStatus(String name, String status) {
        this.name = name;
        this.status = status;
    }
}
