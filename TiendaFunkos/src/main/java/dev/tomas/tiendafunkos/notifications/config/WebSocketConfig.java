package dev.tomas.tiendafunkos.notifications.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Configuración de los WebSockets
 * https://www.baeldung.com/websockets-spring
 * Se define un WebSocketHandler para cada entidad o tipo de notificación o evento
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketFunkosHandler(), "/ws" + "/funkos");
    }



    @Bean
    public WebSocketHandler webSocketFunkosHandler() {
        return new WebSocketHandler("Funkos");
    }

}