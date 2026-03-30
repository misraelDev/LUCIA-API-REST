package com.lucia.api.service.Call;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lucia.api.model.dto.Call.CallListResponseDTO;
import com.lucia.api.model.dto.Call.CallRequestDTO;
import com.lucia.api.model.dto.Call.CallResponseDTO;
import com.lucia.api.model.dto.common.PageMetaDTO;
import com.lucia.api.model.entity.Call;
import com.lucia.api.model.entity.Contact;
import com.lucia.api.repository.Call.CallRepository;
import com.lucia.api.repository.Contact.ContactRepository;
import com.lucia.api.exception.ResourceNotFoundException;
import com.lucia.api.exception.BadRequestException;
import com.lucia.api.service.WebSocket.WebSocketNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class CallService {
    public static final int MAX_PER_PAGE = 100;

    private static final Logger logger = LoggerFactory.getLogger(CallService.class);
    
    @Autowired
    private CallRepository callRepository;

    @Autowired
    private ContactRepository contactRepository;
    
    @Autowired
    private WebSocketNotificationService notificationService;

    @Transactional
    public Call create(CallRequestDTO request) {
        // Validaciones de negocio
        if (request.getDuration() != null && request.getDuration() < 0) {
            throw new BadRequestException("La duración de la llamada no puede ser negativa");
        }

        if (request.getDate() != null && request.getDate().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("La fecha de la llamada no puede ser en el futuro");
        }

        Call call = new Call();
        call.setDate(request.getDate() != null ? request.getDate() : LocalDateTime.now());
        call.setDuration(request.getDuration());
        call.setMotive(request.getMotive());
        call.setSummary(request.getSummary());
        call.setIntent(request.getIntent());
        call.setMessages(request.getMessages());
        call.setAudioCombined(request.getAudioCombined());
        call.setAudioAssistant(request.getAudioAssistant());
        call.setAudioCustomer(request.getAudioCustomer());
        call.setCreatedAt(LocalDateTime.now());
        call.setUpdatedAt(LocalDateTime.now());
        if (request.getContactId() != null) {
            Contact contact = contactRepository.findById(request.getContactId())
                .orElseThrow(() -> new ResourceNotFoundException("Contacto no encontrado"));
            call.setContact(contact);
        }
        Call saved = callRepository.save(call);
        
        // Notificación no debe fallar la operación principal
        try {
            notificationService.notifyNewCall(saved);
        } catch (Exception e) {
            logger.error("Error al enviar notificación de nueva llamada", e);
            // No fallar la operación por un error de notificación
        }
        
        return saved;
    }

    public Call getById(Long id) {
        return callRepository
                .findByIdWithContact(id)
                .orElseThrow(() -> new ResourceNotFoundException("Llamada no encontrada"));
    }

    public List<Call> getAll() {
        return callRepository.findAll();
    }

    /**
     * Listado paginado (página base 1). Filtra por fecha de llamada si se envían {@code from} y/o {@code to} (inclusive).
     */
    @Transactional(readOnly = true)
    public CallListResponseDTO listPaged(int page, int perPage, LocalDate from, LocalDate to) {
        int safePage = Math.max(1, page);
        int size = Math.min(MAX_PER_PAGE, Math.max(1, perPage));
        Pageable pageable =
                PageRequest.of(safePage - 1, size, Sort.by(Sort.Direction.DESC, "date"));
        Page<Call> pg;
        if (from != null && to != null) {
            LocalDateTime start = from.atStartOfDay();
            LocalDateTime end = to.atTime(LocalTime.MAX);
            pg = callRepository.findByDateBetween(start, end, pageable);
        } else if (from != null) {
            pg = callRepository.findByDateGreaterThanEqual(from.atStartOfDay(), pageable);
        } else if (to != null) {
            pg = callRepository.findByDateLessThanEqual(to.atTime(LocalTime.MAX), pageable);
        } else {
            pg = callRepository.findAll(pageable);
        }
        List<CallResponseDTO> data =
                pg.getContent().stream().map(this::toResponseDTO).toList();
        int lastPage = pg.getTotalPages();
        if (lastPage < 1) {
            lastPage = 1;
        }
        PageMetaDTO meta =
                PageMetaDTO.builder()
                        .currentPage(safePage)
                        .perPage(size)
                        .total(pg.getTotalElements())
                        .lastPage(lastPage)
                        .build();
        return CallListResponseDTO.builder().data(data).meta(meta).build();
    }

    @Transactional
    public Call update(Long id, CallRequestDTO request) {
        Call call = getById(id);

        // Validaciones de negocio
        if (request.getDuration() != null && request.getDuration() < 0) {
            throw new BadRequestException("La duración de la llamada no puede ser negativa");
        }

        if (request.getDate() != null && request.getDate().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("La fecha de la llamada no puede ser en el futuro");
        }

        call.setDate(request.getDate() != null ? request.getDate() : call.getDate());
        call.setDuration(request.getDuration() != null ? request.getDuration() : call.getDuration());
        if (request.getMotive() != null) {
            call.setMotive(request.getMotive());
        }
        if (request.getSummary() != null) {
            call.setSummary(request.getSummary());
        }
        if (request.getIntent() != null) {
            call.setIntent(request.getIntent());
        }
        if (request.getMessages() != null) {
            call.setMessages(request.getMessages());
        }
        if (request.getAudioCombined() != null) {
            call.setAudioCombined(request.getAudioCombined());
        }
        if (request.getAudioAssistant() != null) {
            call.setAudioAssistant(request.getAudioAssistant());
        }
        if (request.getAudioCustomer() != null) {
            call.setAudioCustomer(request.getAudioCustomer());
        }
        call.setUpdatedAt(LocalDateTime.now());
        
        // Actualizar contacto solo si se proporciona un nuevo contactId
        if (request.getContactId() != null) {
            Contact contact = contactRepository.findById(request.getContactId())
                .orElseThrow(() -> new ResourceNotFoundException("Contacto no encontrado"));
            call.setContact(contact);
        }
        // Si contactId es null, mantener el contacto existente (no se actualiza)
        
        Call updated = callRepository.save(call);
        
        // Notificación no debe fallar la operación principal
        try {
            notificationService.notifyCallUpdated(updated);
        } catch (Exception e) {
            logger.error("Error al enviar notificación de actualización de llamada", e);
            // No fallar la operación por un error de notificación
        }
        
        return updated;
    }

    @Transactional
    public void delete(Long id) {
        Call call = callRepository
                .findByIdWithContact(id)
                .orElseThrow(() -> new ResourceNotFoundException("Llamada no encontrada"));
        callRepository.delete(call);
        
        // Notificación no debe fallar la operación principal
        try {
            notificationService.notifyCallDeleted(call);
        } catch (Exception e) {
            logger.error("Error al enviar notificación de eliminación de llamada", e);
            // No fallar la operación por un error de notificación
        }
    }

    public CallResponseDTO toResponseDTO(Call call) {
        return new CallResponseDTO(call);
    }
}
