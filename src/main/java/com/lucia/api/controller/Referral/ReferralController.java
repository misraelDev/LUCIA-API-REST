package com.lucia.api.controller.Referral;

import com.lucia.api.model.dto.Referral.ReferralRequestDTO;
import com.lucia.api.model.dto.Referral.ReferralResponseDTO;
import com.lucia.api.model.dto.response.ApiResponseSchemas;
import com.lucia.api.model.dto.response.ResponseDetail;
import com.lucia.api.service.Referral.ReferralService;
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
@RequestMapping("/api/v1/referrals")
public class ReferralController {

    private final ReferralService referralService;

    @Autowired
    public ReferralController(ReferralService referralService) {
        this.referralService = referralService;
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Referido creado",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.ReferralCreate.class))))
    @PostMapping
    public ResponseEntity<ResponseDetail<ReferralResponseDTO>> createReferral(
            @Valid @RequestBody ReferralRequestDTO dto) {
        ReferralResponseDTO created = referralService.createReferral(dto);
        return ResponseDetail.ok(ResponseDetail.success(
                "Código de referido creado",
                "El código se registró correctamente.",
                created));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Lista de referidos",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.ReferralList.class))))
    @GetMapping
    public ResponseEntity<ResponseDetail<List<ReferralResponseDTO>>> getAllReferrals() {
        List<ReferralResponseDTO> referrals = referralService.getAllReferrals();
        return ResponseDetail.ok(ResponseDetail.success(
                "Referidos obtenidos",
                "Lista de códigos de referido recuperada exitosamente.",
                referrals));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Referido encontrado",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.ReferralById.class))))
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDetail<ReferralResponseDTO>> getReferralById(@PathVariable @Min(1) Long id) {
        ReferralResponseDTO referral = referralService.getReferralById(id);
        return ResponseDetail.ok(ResponseDetail.success(
                "Referido encontrado",
                "Detalle del código obtenido correctamente.",
                referral));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Referido actualizado",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.ReferralUpdate.class))))
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDetail<ReferralResponseDTO>> updateReferral(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody ReferralRequestDTO dto) {
        ReferralResponseDTO updated = referralService.updateReferral(id, dto);
        return ResponseDetail.ok(ResponseDetail.success(
                "Referido actualizado",
                "Los cambios se guardaron correctamente.",
                updated));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Referido eliminado",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.ReferralDelete.class))))
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDetail<Map<String, Boolean>>> deleteReferral(@PathVariable @Min(1) Long id) {
        referralService.deleteReferral(id);
        return ResponseDetail.ok(ResponseDetail.success(
                "Referido eliminado",
                "El código fue eliminado del sistema.",
                Map.of("deleted", true)));
    }
}
