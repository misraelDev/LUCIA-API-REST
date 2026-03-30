package com.lucia.api.service.Appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lucia.api.model.dto.Appointment.AppointmentRequestDTO;
import com.lucia.api.model.entity.Appointment;
import com.lucia.api.repository.Appointment.AppointmentRepository;
import com.lucia.api.exception.ResourceNotFoundException;
import com.lucia.api.exception.BadRequestException;
import com.lucia.api.exception.BusinessConflictException;
import com.lucia.api.service.WebSocket.WebSocketNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private WebSocketNotificationService notificationService;

    public com.lucia.api.model.dto.Appointment.AppointmentResponseDTO toResponseDTO(Appointment appointment) {
        return com.lucia.api.model.dto.Appointment.AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .summary(appointment.getSummary())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .date(appointment.getDate())
                .status(appointment.getStatus())
                .description(appointment.getDescription())
                .location(appointment.getLocation())
                .contactPhone(appointment.getContactPhone())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }

    @Transactional
    public Appointment create(AppointmentRequestDTO request) {
        // Validaciones de negocio
        validateAppointmentBusinessRules(request);

        Appointment appointment = new Appointment();
        appointment.setSummary(request.getSummary());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setDate(request.getDate());
        appointment.setStatus(request.getStatus() != null ? request.getStatus() : Appointment.AppointmentStatus.UNASSIGNED);
        appointment.setDescription(request.getDescription());
        appointment.setLocation(request.getLocation());
        appointment.setContactPhone(request.getContactPhone());
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        Appointment saved = appointmentRepository.save(appointment);
        
        // Notificación no debe fallar la operación principal
        try {
            notificationService.notifyNewAppointment(saved);
        } catch (Exception e) {
            logger.error("Error al enviar notificación de nueva cita", e);
            // No fallar la operación por un error de notificación
        }
        
        return saved;
    }

    /**
     * Valida las reglas de negocio para una cita.
     */
    private void validateAppointmentBusinessRules(AppointmentRequestDTO request) {
        // Validar que la hora de fin sea posterior a la hora de inicio
        if (request.getStartTime() != null && request.getEndTime() != null) {
            if (!request.getEndTime().isAfter(request.getStartTime())) {
                throw new BadRequestException("La hora de fin debe ser posterior a la hora de inicio");
            }
        }

        // Validar que la fecha no sea en el pasado
        if (request.getDate() != null && request.getDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("No se pueden crear citas en fechas pasadas");
        }

        // Validar que si la fecha es hoy, la hora de inicio no sea en el pasado
        if (request.getDate() != null && request.getDate().equals(LocalDate.now()) 
                && request.getStartTime() != null) {
            LocalTime now = LocalTime.now();
            if (request.getStartTime().isBefore(now)) {
                throw new BadRequestException("No se pueden crear citas con hora de inicio en el pasado para el día de hoy");
            }
        }
    }

    public Appointment getById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));
    }

    public List<Appointment> getAll() {
        return appointmentRepository.findAll();
    }

    /**
     * Citas cuyo {@code contact_phone} coincide con el número indicado (solo dígitos, ignora formato).
     */
    @Transactional(readOnly = true)
    public List<Appointment> findByContactPhone(String phone) {
        if (phone == null) {
            return List.of();
        }
        String digits = phone.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return List.of();
        }
        return appointmentRepository.findByContactPhoneDigits(digits);
    }

    @Transactional
    public Appointment update(Long id, AppointmentRequestDTO request) {
        Appointment appointment = getById(id);

        // Crear un DTO temporal con los valores actuales para validar reglas de negocio
        // Si un campo no se proporciona, usar el valor actual
        AppointmentRequestDTO validationDTO = AppointmentRequestDTO.builder()
                .startTime(request.getStartTime() != null ? request.getStartTime() : appointment.getStartTime())
                .endTime(request.getEndTime() != null ? request.getEndTime() : appointment.getEndTime())
                .date(request.getDate() != null ? request.getDate() : appointment.getDate())
                .build();

        // Validar reglas de negocio con los valores finales (actuales o nuevos)
        validateAppointmentBusinessRules(validationDTO);

        // Validar transiciones de estado
        if (request.getStatus() != null && appointment.getStatus() != request.getStatus()) {
            validateStatusTransition(appointment.getStatus(), request.getStatus());
        }

        // Actualizar solo los campos proporcionados
        if (request.getSummary() != null) {
            appointment.setSummary(request.getSummary());
        }
        if (request.getStartTime() != null) {
            appointment.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            appointment.setEndTime(request.getEndTime());
        }
        if (request.getDate() != null) {
            appointment.setDate(request.getDate());
        }
        if (request.getStatus() != null) {
            appointment.setStatus(request.getStatus());
        }
        if (request.getDescription() != null) {
            appointment.setDescription(request.getDescription());
        }
        if (request.getLocation() != null) {
            appointment.setLocation(request.getLocation());
        }
        if (request.getContactPhone() != null) {
            appointment.setContactPhone(request.getContactPhone());
        }
        appointment.setUpdatedAt(LocalDateTime.now());
        Appointment updated = appointmentRepository.save(appointment);
        
        // Notificación no debe fallar la operación principal
        try {
            notificationService.notifyAppointmentUpdated(updated);
        } catch (Exception e) {
            logger.error("Error al enviar notificación de actualización de cita", e);
            // No fallar la operación por un error de notificación
        }
        
        return updated;
    }

    /**
     * Valida que la transición de estado sea válida según las reglas de negocio.
     */
    private void validateStatusTransition(Appointment.AppointmentStatus currentStatus, Appointment.AppointmentStatus newStatus) {
        // No se puede reactivar una cita cancelada
        if (currentStatus == Appointment.AppointmentStatus.CANCELLED && newStatus != Appointment.AppointmentStatus.CANCELLED) {
            throw new BusinessConflictException("No se puede cambiar el estado de una cita cancelada");
        }
    }

    @Transactional
    public void delete(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));
        appointmentRepository.delete(appointment);
        
        // Notificación no debe fallar la operación principal
        try {
            notificationService.notifyAppointmentCancelled(appointment);
        } catch (Exception e) {
            logger.error("Error al enviar notificación de eliminación de cita", e);
            // No fallar la operación por un error de notificación
        }
    }
    
}
