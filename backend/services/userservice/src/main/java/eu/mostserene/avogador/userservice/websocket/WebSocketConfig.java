package eu.mostserene.avogador.userservice.websocket;

import eu.mostserene.avogador.userservice.security.AuthService;
import eu.mostserene.avogador.userservice.security.ForbiddenException;
import eu.mostserene.avogador.userservice.users.AuthUserDTO;
import eu.mostserene.avogador.userservice.utils.LoggerColors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.*;

import java.util.List;
import java.util.Objects;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private AuthService authService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
                //.withSockJS()
                //.setSuppressCors(true);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {

            @Override
            public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                assert accessor != null;

                try {
                    AuthUserDTO user = getSocketUser(accessor);
                    accessor.setUser(user.toPrincipal());
                } catch (Exception e) {
                    log.error(LoggerColors.error(e.getMessage()));
                    return null;
                }

                if (accessor.getDestination() != null) {
                    handleChannelPermission(accessor);
                }
                return message;
            }
        });
    }

    private AuthUserDTO getSocketUser(StompHeaderAccessor accessor) {
        List<String> nativeHeader = accessor.getNativeHeader("token");
        if (nativeHeader == null) {
            throw new RuntimeException("WebSocket token is missing");
        }
        return authService.decodeWebSocketToken(nativeHeader.get(0));
    }

    private void handleChannelPermission(StompHeaderAccessor accessor) {
        List<String> destination = List.of(Objects.requireNonNull(accessor.getDestination())
                .split("/"));

        if ("users".equals(destination.get(0))) {
            String userChannelId = destination.get(1);
            if (Objects.requireNonNull(accessor.getUser()).getName().equals(userChannelId)) {
                throw new ForbiddenException(accessor.getUser().getName() + " tried subscribing to " + destination);
            }
        }
    }

}
