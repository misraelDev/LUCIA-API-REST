package com.lucia.api.controller.WebSocket;

import com.lucia.api.model.dto.response.ApiResponseSchemas;
import com.lucia.api.model.dto.response.ResponseDetail;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String message) {
        logger.info("Mensaje recibido del cliente: {}", message);
        return "Hola desde el servidor: " + message;
    }

    @MessageMapping("/private-message")
    @SendToUser("/queue/reply")
    public String privateMessage(String message) {
        logger.info("Mensaje privado recibido: {}", message);
        return "Respuesta privada: " + message;
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Estado del servicio WebSocket",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.WebSocketStatus.class))))
    @GetMapping("/api/ws/status")
    @ResponseBody
    public ResponseEntity<ResponseDetail<Map<String, Object>>> getWebSocketStatus() {
        Map<String, Object> payload = Map.of(
                "status", "active",
                "endpoints", Map.of(
                        "ws", "/ws",
                        "topics", new String[] {
                                "/topic/requests/new",
                                "/topic/requests/updated",
                                "/topic/appointments/new",
                                "/topic/appointments/updated",
                                "/topic/appointments/cancelled",
                                "/topic/appointments/status-changed",
                                "/topic/appointments/by-date-consulted",
                                "/topic/contacts/new",
                                "/topic/contacts/updated",
                                "/topic/contacts/deleted",
                                "/topic/contacts/consulted",
                                "/topic/calls/new",
                                "/topic/calls/updated",
                                "/topic/calls/deleted",
                                "/topic/calls/consulted",
                                "/topic/calls/stats/consulted",
                                "/topic/stats/consulted",
                                "/topic/dashboard/stats/consulted",
                                "/topic/dashboard/metrics/updated",
                                "/topic/users/new",
                                "/topic/users/consulted",
                                "/topic/users/updated",
                                "/topic/users/deleted",
                                "/topic/user/consulted",
                                "/topic/heartbeat",
                                "/topic/greetings"
                        },
                        "userTopics", new String[] {
                                "/user/{userId}/requests/new",
                                "/user/{userId}/requests/updated"
                        },
                        "phoneTopics", new String[] {
                                "/topic/phone/{phoneNumber}/appointments/new",
                                "/topic/phone/{phoneNumber}/appointments/updated",
                                "/topic/phone/{phoneNumber}/appointments/cancelled",
                                "/topic/phone/{phoneNumber}/appointments/status-changed",
                                "/topic/phone/{phoneNumber}/contacts/new",
                                "/topic/phone/{phoneNumber}/contacts/updated",
                                "/topic/phone/{phoneNumber}/contacts/deleted"
                        },
                        "emailTopics", new String[] {
                                "/topic/email/{email}/contacts/new",
                                "/topic/email/{email}/contacts/updated",
                                "/topic/email/{email}/contacts/deleted"
                        },
                        "contactTopics", new String[] {
                                "/topic/contact/{contactId}/calls/new",
                                "/topic/contact/{contactId}/calls/updated",
                                "/topic/contact/{contactId}/calls/deleted"
                        }),
                "timestamp", System.currentTimeMillis());
        return ResponseDetail.ok(ResponseDetail.success(
                "Estado WebSocket",
                "Información de endpoints y topics disponibles.",
                payload));
    }
}
