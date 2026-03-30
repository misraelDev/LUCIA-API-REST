package com.lucia.api.controller.Request;

import com.lucia.api.model.dto.Request.RequestRequestDTO;
import com.lucia.api.model.dto.Request.RequestResponseDTO;
import com.lucia.api.model.dto.response.ApiResponseSchemas;
import com.lucia.api.model.dto.response.ResponseDetail;
import com.lucia.api.service.Request.RequestService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/requests")
public class RequestController {

    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Solicitud creada",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.RequestCreate.class))))
    @PostMapping
    public ResponseEntity<ResponseDetail<RequestResponseDTO>> createRequest(
            @Valid @RequestBody RequestRequestDTO dto) {
        RequestResponseDTO created = requestService.createRequest(dto);
        return ResponseDetail.ok(ResponseDetail.success(
                "Solicitud creada",
                "La solicitud se registró correctamente.",
                created));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Lista de solicitudes",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.RequestList.class))))
    @GetMapping
    public ResponseEntity<ResponseDetail<List<RequestResponseDTO>>> getAllRequests() {
        List<RequestResponseDTO> requests = requestService.getAllRequests();
        return ResponseDetail.ok(ResponseDetail.success(
                "Solicitudes obtenidas",
                "Lista de solicitudes recuperada exitosamente.",
                requests));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Solicitud encontrada",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.RequestById.class))))
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDetail<RequestResponseDTO>> getRequestById(@PathVariable @Min(1) Long id) {
        RequestResponseDTO request = requestService.getRequestById(id);
        return ResponseDetail.ok(ResponseDetail.success(
                "Solicitud encontrada",
                "Detalle de la solicitud obtenido correctamente.",
                request));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Solicitud actualizada",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.RequestUpdate.class))))
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDetail<RequestResponseDTO>> updateRequest(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody RequestRequestDTO dto) {
        RequestResponseDTO updated = requestService.updateRequest(id, dto);
        return ResponseDetail.ok(ResponseDetail.success(
                "Solicitud actualizada",
                "Los cambios se guardaron correctamente.",
                updated));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Solicitud eliminada",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.RequestDelete.class))))
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDetail<Map<String, Boolean>>> deleteRequest(@PathVariable @Min(1) Long id) {
        requestService.deleteRequest(id);
        return ResponseDetail.ok(ResponseDetail.success(
                "Solicitud eliminada",
                "La solicitud fue eliminada del sistema.",
                Map.of("deleted", true)));
    }
}
