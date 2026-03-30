package com.lucia.api.controller.Stats;

import com.lucia.api.model.dto.Stat.StatsAllResponseDTO;
import com.lucia.api.model.dto.Stat.StatsResponseDTO;
import com.lucia.api.model.dto.response.ApiResponseSchemas;
import com.lucia.api.model.dto.response.ResponseDetail;
import com.lucia.api.service.Stat.StatsService;
import com.lucia.api.service.WebSocket.WebSocketNotificationService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stats")
public class StatsController {

    private static final Logger log = LoggerFactory.getLogger(StatsController.class);

    @Autowired
    private StatsService statsService;

    @Autowired
    private WebSocketNotificationService webSocketNotificationService;

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Estadísticas agregadas",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.StatsSummary.class))))
    @GetMapping
    public ResponseEntity<ResponseDetail<StatsResponseDTO>> getAllStats() {
        StatsResponseDTO stats = statsService.getAllStats();
        try {
            webSocketNotificationService.notifyStatsRequested(
                    Map.of(
                            "stats", stats,
                            "timestamp", System.currentTimeMillis()));
        } catch (Exception e) {
            log.warn("Error al enviar notificación WebSocket: {}", e.getMessage());
        }
        return ResponseDetail.ok(ResponseDetail.success(
                "Estadísticas obtenidas",
                "Métricas del sistema recuperadas correctamente.",
                stats));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Datos crudos",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.StatsAllRaw.class))))
    @GetMapping("/all")
    public ResponseEntity<ResponseDetail<StatsAllResponseDTO>> getAllRawData() {
        StatsAllResponseDTO rawData = statsService.getAllRawData();
        return ResponseDetail.ok(ResponseDetail.success(
                "Datos crudos obtenidos",
                "Llamadas y citas en formato crudo recuperadas correctamente.",
                rawData));
    }
}
