package eu.mostserene.avogador.apigateway.status;

import eu.mostserene.avogador.apigateway.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class StatusServiceImpl implements StatusService {

    private static final List<MicroService> endpoints = List.of(
            new MicroService("Users", "http://users/public/users/status"),
            new MicroService("Courses", "http://courses/public/courses/status")
    );

    @Override
    public List<MicroServiceStatus> getMicroservicesStatus() {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(4))
                //.setReadTimeout(Duration.ofSeconds(4))
                .build();
        return endpoints.stream()
                .map(microService -> {
                    try {
                        return new MicroServiceStatus(microService.name(), restTemplate.getForObject(microService.url(), String.class));
                    } catch (Exception e) {
                        log.error(LoggerColors.error(e.getMessage()));
                        return new MicroServiceStatus(microService.name(), "offline");
                    }
                })
                .toList();
    }

    private record MicroService(String name, String url) {
    }
}
