package com.lucia.api.service.Referral;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lucia.api.model.entity.Referral;
import com.lucia.api.repository.Referral.ReferralRepository;
import com.lucia.api.model.dto.Referral.ReferralRequestDTO;
import com.lucia.api.model.dto.Referral.ReferralResponseDTO;
import com.lucia.api.service.WebSocket.WebSocketNotificationService;
import com.lucia.api.exception.ResourceNotFoundException;
import com.lucia.api.exception.BadRequestException;
import com.lucia.api.exception.BusinessConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReferralService {
    private static final Logger logger = LoggerFactory.getLogger(ReferralService.class);
    
    private final ReferralRepository referralRepository;
    private final WebSocketNotificationService notificationService;

    @Autowired
    public ReferralService(ReferralRepository referralRepository, WebSocketNotificationService notificationService) {
        this.referralRepository = referralRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public ReferralResponseDTO createReferral(ReferralRequestDTO dto) {
        Referral referral = new Referral();
        referral.setSellerId(dto.getSellerId());
        referral.setReferralCode(dto.getReferralCode());
        referral.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        Referral saved = referralRepository.save(referral);
        
        // Notificación no debe fallar la operación principal
        try {
            notificationService.notifyNewReferral(saved);
        } catch (Exception e) {
            logger.error("Error al enviar notificación de nuevo referido", e);
            // No fallar la operación por un error de notificación
        }
        
        return toResponseDTO(saved);
    }

    public List<ReferralResponseDTO> getAllReferrals() {
        List<Referral> referrals = referralRepository.findAll();
        return referrals.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public ReferralResponseDTO getReferralById(Long id) {
        Referral referral = referralRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Código de referido no encontrado"));
        return toResponseDTO(referral);
    }

    @Transactional
    public ReferralResponseDTO updateReferral(Long id, ReferralRequestDTO dto) {
        Referral referral = referralRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Código de referido no encontrado"));

        // Validar que si se cambia el código, no esté duplicado
        if (dto.getReferralCode() != null && !dto.getReferralCode().equals(referral.getReferralCode())) {
            referralRepository.findByReferralCode(dto.getReferralCode())
                .filter(existingReferral -> !existingReferral.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessConflictException("El código de referido ya existe en el sistema");
                });
        }

        referral.setSellerId(dto.getSellerId());
        referral.setReferralCode(dto.getReferralCode() != null ? dto.getReferralCode() : referral.getReferralCode());
        referral.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : referral.getIsActive());
        Referral updated = referralRepository.save(referral);
        
        // Notificación no debe fallar la operación principal
        try {
            notificationService.notifyReferralUpdated(updated);
        } catch (Exception e) {
            logger.error("Error al enviar notificación de actualización de referido", e);
            // No fallar la operación por un error de notificación
        }
        
        return toResponseDTO(updated);
    }

    @Transactional
    public void deleteReferral(Long id) {
        Referral referral = referralRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Código de referido no encontrado"));
        referralRepository.delete(referral);
        
        // Notificación no debe fallar la operación principal
        try {
            notificationService.notifyReferralDeleted(referral);
        } catch (Exception e) {
            logger.error("Error al enviar notificación de eliminación de referido", e);
            // No fallar la operación por un error de notificación
        }
    }

    public ReferralResponseDTO toResponseDTO(Referral referral) {
        return ReferralResponseDTO.builder()
                .id(referral.getId())
                .sellerId(referral.getSellerId())
                .referralCode(referral.getReferralCode())
                .isActive(referral.getIsActive())
                .createdAt(referral.getCreatedAt())
                .updatedAt(referral.getUpdatedAt())
                .build();
    }
}
