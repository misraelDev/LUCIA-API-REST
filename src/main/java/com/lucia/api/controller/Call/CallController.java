package com.lucia.api.controller.Call;

import com.lucia.api.model.dto.Call.CallListResponseDTO;
import com.lucia.api.model.dto.Call.CallRequestDTO;
import com.lucia.api.model.dto.Call.CallResponseDTO;
import com.lucia.api.model.dto.response.ApiResponseSchemas;
import com.lucia.api.model.dto.response.ResponseDetail;
import com.lucia.api.model.entity.Call;
import com.lucia.api.service.Call.CallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/calls")
public class CallController {

    @Autowired
    private CallService callService;

    @Operation(security = {})
    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Llamada registrada",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.CallCreate.class))))
    @PostMapping
    public ResponseEntity<ResponseDetail<CallResponseDTO>> create(@Valid @RequestBody CallRequestDTO request) {
        Call call = callService.create(request);
        return ResponseDetail.ok(ResponseDetail.success(
                "Llamada creada",
                "El registro de llamada se guardó correctamente.",
                callService.toResponseDTO(call)));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Llamada encontrada",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.CallById.class))))
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('user','seller')")
    public ResponseEntity<ResponseDetail<CallResponseDTO>> get(@PathVariable @Min(1) Long id) {
        Call call = callService.getById(id);
        return ResponseDetail.ok(ResponseDetail.success(
                "Llamada encontrada",
                "Detalle de la llamada obtenido correctamente.",
                callService.toResponseDTO(call)));
    }

    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista paginada (query: page, per_page, from, to en ISO fechas)"))
    @GetMapping
    @PreAuthorize("hasAnyAuthority('user','seller')")
    public ResponseEntity<ResponseDetail<CallListResponseDTO>> listCalls(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(name = "per_page", defaultValue = "10") @Min(1) int perPage,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate to) {
        CallListResponseDTO body = callService.listPaged(page, perPage, from, to);
        return ResponseDetail.ok(ResponseDetail.success(
                "Llamadas obtenidas",
                "Lista paginada de llamadas.",
                body));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Llamada actualizada",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.CallUpdate.class))))
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('user','seller')")
    public ResponseEntity<ResponseDetail<CallResponseDTO>> update(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody CallRequestDTO request) {
        Call call = callService.update(id, request);
        return ResponseDetail.ok(ResponseDetail.success(
                "Llamada actualizada",
                "Los cambios se guardaron correctamente.",
                callService.toResponseDTO(call)));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Llamada eliminada",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.CallDelete.class))))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('user','seller')")
    public ResponseEntity<ResponseDetail<Map<String, Boolean>>> delete(@PathVariable @Min(1) Long id) {
        callService.delete(id);
        return ResponseDetail.ok(ResponseDetail.success(
                "Llamada eliminada",
                "El registro fue eliminado del sistema.",
                Map.of("deleted", true)));
    }
}
