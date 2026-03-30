package com.lucia.api.controller.Appointment;

import com.lucia.api.model.dto.Appointment.AppointmentRequestDTO;
import com.lucia.api.model.dto.Appointment.AppointmentResponseDTO;
import com.lucia.api.model.dto.response.ApiResponseSchemas;
import com.lucia.api.model.dto.response.ResponseDetail;
import com.lucia.api.model.entity.Appointment;
import com.lucia.api.service.Appointment.AppointmentService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Cita creada",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.AppointmentCreate.class))))
    @PreAuthorize("hasAnyAuthority('user','seller')")
    @PostMapping
    public ResponseEntity<ResponseDetail<AppointmentResponseDTO>> create(
            @Valid @RequestBody AppointmentRequestDTO request) {
        Appointment appointment = appointmentService.create(request);
        return ResponseDetail.ok(ResponseDetail.success(
                "Cita creada",
                "La cita se registró correctamente.",
                appointmentService.toResponseDTO(appointment)));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Citas asociadas al teléfono",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.AppointmentList.class))))
    @PreAuthorize("hasAnyAuthority('user','seller')")
    @GetMapping("/phone/{phone}")
    public ResponseEntity<ResponseDetail<List<AppointmentResponseDTO>>> listByContactPhone(
            @PathVariable String phone) {
        List<Appointment> appointments = appointmentService.findByContactPhone(phone);
        List<AppointmentResponseDTO> dtos =
                appointments.stream().map(appointmentService::toResponseDTO).collect(Collectors.toList());
        return ResponseDetail.ok(ResponseDetail.success(
                "Citas por teléfono",
                "Citas asociadas al número de contacto indicado.",
                dtos));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Cita encontrada",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.AppointmentById.class))))
    @PreAuthorize("hasAnyAuthority('user','seller')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDetail<AppointmentResponseDTO>> get(@PathVariable @Min(1) Long id) {
        Appointment appointment = appointmentService.getById(id);
        return ResponseDetail.ok(ResponseDetail.success(
                "Cita encontrada",
                "Detalle de la cita obtenido correctamente.",
                appointmentService.toResponseDTO(appointment)));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Lista de citas",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.AppointmentList.class))))
    @PreAuthorize("hasAnyAuthority('user','seller')")
    @GetMapping
    public ResponseEntity<ResponseDetail<List<AppointmentResponseDTO>>> getAll() {
        List<Appointment> appointments = appointmentService.getAll();
        List<AppointmentResponseDTO> dtos = appointments.stream()
                .map(appointmentService::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseDetail.ok(ResponseDetail.success(
                "Citas obtenidas",
                "Lista de citas recuperada exitosamente.",
                dtos));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Cita actualizada",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.AppointmentUpdate.class))))
    @PreAuthorize("hasAnyAuthority('user','seller')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDetail<AppointmentResponseDTO>> update(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody AppointmentRequestDTO request) {
        Appointment appointment = appointmentService.update(id, request);
        return ResponseDetail.ok(ResponseDetail.success(
                "Cita actualizada",
                "Los cambios se guardaron correctamente.",
                appointmentService.toResponseDTO(appointment)));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Cita eliminada",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.AppointmentDelete.class))))
    @PreAuthorize("hasAnyAuthority('user','seller')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDetail<Map<String, Boolean>>> delete(@PathVariable @Min(1) Long id) {
        appointmentService.delete(id);
        return ResponseDetail.ok(ResponseDetail.success(
                "Cita eliminada",
                "La cita fue eliminada del sistema.",
                Map.of("deleted", true)));
    }
}
