package com.lucia.api.service.Request;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lucia.api.model.entity.Request;
import com.lucia.api.repository.Request.RequestRepository;
import com.lucia.api.model.dto.Request.RequestRequestDTO;
import com.lucia.api.model.dto.Request.RequestResponseDTO;
import com.lucia.api.service.WebSocket.WebSocketNotificationService;
import com.lucia.api.exception.ResourceNotFoundException;
import com.lucia.api.exception.BadRequestException;
import com.lucia.api.exception.BusinessConflictException;
import com.lucia.api.repository.Referral.ReferralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestService {
    private static final Logger logger = LoggerFactory.getLogger(RequestService.class);
    
    private final RequestRepository requestRepository;
    private final WebSocketNotificationService notificationService;
    private final ReferralRepository referralRepository;

    @Autowired
    public RequestService(RequestRepository requestRepository, 
                         WebSocketNotificationService notificationService,
                         ReferralRepository referralRepository) {
        this.requestRepository = requestRepository;
        this.notificationService = notificationService;
        this.referralRepository = referralRepository;
    }

    @Transactional
    public RequestResponseDTO createRequest(RequestRequestDTO dto) {
        // Validar que el código de referido existe y está activo si se proporciona
        if (dto.getReferralCode() != null && !dto.getReferralCode().trim().isEmpty()) {
            referralRepository.findByReferralCode(dto.getReferralCode())
                .filter(referral -> referral.getIsActive() != null && referral.getIsActive())
                .orElseThrow(() -> new BadRequestException("El código de referido proporcionado no existe o no está activo"));
        }

        Request request = new Request();
        request.setName(dto.getName());
        request.setEmail(dto.getEmail());
        request.setPhone(dto.getPhone());
        request.setNeed(dto.getNeed());
        request.setMessage(dto.getMessage());
        request.setReferralCode(dto.getReferralCode());
        request.setSellerId(dto.getSellerId());
        request.setStatus(dto.getStatus() != null ? dto.getStatus() : Request.RequestStatus.PENDING);
        Request saved = requestRepository.save(request);
        
        // Notificación no debe fallar la operación principal
        try {
            notificationService.notifyNewRequest(saved);
        } catch (Exception e) {
            logger.error("Error al enviar notificación de nueva solicitud", e);
            // No fallar la operación por un error de notificación
        }
        
        return toResponseDTO(saved);
    }

    public List<RequestResponseDTO> getAllRequests() {
        List<Request> requests = requestRepository.findAll();
        return requests.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public RequestResponseDTO getRequestById(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        return toResponseDTO(request);
    }

    @Transactional
    public RequestResponseDTO updateRequest(Long id, RequestRequestDTO dto) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));

        // Validar que el código de referido existe y está activo si se proporciona
        if (dto.getReferralCode() != null && !dto.getReferralCode().trim().isEmpty()) {
            referralRepository.findByReferralCode(dto.getReferralCode())
                .filter(referral -> referral.getIsActive() != null && referral.getIsActive())
                .orElseThrow(() -> new BadRequestException("El código de referido proporcionado no existe o no está activo"));
        }

        // Validar transiciones de estado
        if (dto.getStatus() != null && request.getStatus() != dto.getStatus()) {
            validateStatusTransition(request.getStatus(), dto.getStatus());
        }

        request.setName(dto.getName());
        request.setEmail(dto.getEmail());
        request.setPhone(dto.getPhone());
        request.setNeed(dto.getNeed());
        request.setMessage(dto.getMessage());
        request.setReferralCode(dto.getReferralCode());
        request.setSellerId(dto.getSellerId());
        request.setStatus(dto.getStatus() != null ? dto.getStatus() : request.getStatus());
        Request updated = requestRepository.save(request);
        
        // Notificación no debe fallar la operación principal
        try {
            notificationService.notifyRequestUpdated(updated);
        } catch (Exception e) {
            logger.error("Error al enviar notificación de actualización de solicitud", e);
            // No fallar la operación por un error de notificación
        }
        
        return toResponseDTO(updated);
    }

    /**
     * Valida que la transición de estado sea válida según las reglas de negocio.
     */
    private void validateStatusTransition(Request.RequestStatus currentStatus, Request.RequestStatus newStatus) {
        // No se puede reactivar una solicitud cancelada
        if (currentStatus == Request.RequestStatus.CANCELLED && newStatus != Request.RequestStatus.CANCELLED) {
            throw new BusinessConflictException("No se puede cambiar el estado de una solicitud cancelada");
        }

        // No se puede cambiar de COMPLETED a otro estado
        if (currentStatus == Request.RequestStatus.COMPLETED && newStatus != Request.RequestStatus.COMPLETED) {
            throw new BusinessConflictException("No se puede cambiar el estado de una solicitud completada");
        }
    }

    @Transactional
    public void deleteRequest(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada"));
        requestRepository.delete(request);
        
        // Notificación no debe fallar la operación principal
        try {
            notificationService.notifyUserRequest(request.getSellerId(), request, "deleted");
        } catch (Exception e) {
            logger.error("Error al enviar notificación de eliminación de solicitud", e);
            // No fallar la operación por un error de notificación
        }
    }

    public RequestResponseDTO toResponseDTO(Request request) {
        return RequestResponseDTO.builder()
                .id(request.getId())
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .need(request.getNeed())
                .message(request.getMessage())
                .referralCode(request.getReferralCode())
                .sellerId(request.getSellerId())
                .status(request.getStatus())
                .submittedAt(request.getSubmittedAt())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}
