package com.pos.appfrontend.configuration.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.appfrontend.dto.responses.TransactionStatusEvent;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class TransactionStatusHandler extends TextWebSocketHandler {

    private static final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, org.springframework.web.socket.@NonNull CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    public static void sendNotification(TransactionStatusEvent event) {
        String targetEmail = event.getEmail();

        for (WebSocketSession session : sessions) {
            String sessionEmail = (String) session.getAttributes().get("email");

            if (session.isOpen() && targetEmail.equals(sessionEmail)) {
                try {
                    String jsonMessage = objectMapper.writeValueAsString(event);
                    session.sendMessage(new TextMessage(jsonMessage));
                } catch (Exception e) {
                    log.error("Error sending WebSocket message: {}", e.getMessage());
                }
            }
        }
    }

}
