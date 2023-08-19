package eu.mostserene.avogador.apigateway.status;

import java.util.List;

public interface StatusService {
    List<MicroServiceStatus> getMicroservicesStatus();
}
