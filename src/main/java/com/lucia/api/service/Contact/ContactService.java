package com.lucia.api.service.Contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lucia.api.model.dto.Contact.ContactListResponseDTO;
import com.lucia.api.model.dto.Contact.ContactRequestDTO;
import com.lucia.api.model.dto.Contact.ContactResponseDTO;
import com.lucia.api.model.dto.common.PageMetaDTO;
import com.lucia.api.model.entity.Contact;
import com.lucia.api.repository.Contact.ContactRepository;
import com.lucia.api.exception.ResourceNotFoundException;
import com.lucia.api.service.WebSocket.WebSocketNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class ContactService {
    public static final int MAX_PER_PAGE = 100;

    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);
    
    @Autowired
    private ContactRepository contactRepository;
    
    @Autowired
    private WebSocketNotificationService notificationService;

    @Transactional
    public Contact create(ContactRequestDTO request) {
        Contact contact = new Contact();
        contact.setName(request.getName());
        contact.setEmail(request.getEmail());
        contact.setPhoneNumber(request.getPhoneNumber());
        contact.setCreatedAt(java.time.LocalDateTime.now());
        contact.setUpdatedAt(java.time.LocalDateTime.now());
        Contact saved = contactRepository.save(contact);
        
        // Notificación no debe fallar la operación principal
        try {
            notificationService.notifyNewContact(saved);
        } catch (Exception e) {
            logger.error("Error al enviar notificación de nuevo contacto", e);
            // No fallar la operación por un error de notificación
        }
        
        return saved;
    }

    public Contact getById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contacto no encontrado"));
    }

    public List<Contact> getAll() {
        return contactRepository.findAll();
    }

    /** Listado paginado (índice de página base 1). */
    @Transactional(readOnly = true)
    public ContactListResponseDTO listPaged(int page, int perPage) {
        int safePage = Math.max(1, page);
        int size = Math.min(MAX_PER_PAGE, Math.max(1, perPage));
        Pageable pageable =
                PageRequest.of(safePage - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Contact> pg = contactRepository.findAll(pageable);
        List<ContactResponseDTO> data =
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
        return ContactListResponseDTO.builder().data(data).meta(meta).build();
    }

    @Transactional
    public Contact update(Long id, ContactRequestDTO request) {
        Contact contact = getById(id);
        contact.setName(request.getName());
        contact.setEmail(request.getEmail());
        contact.setPhoneNumber(request.getPhoneNumber());
        contact.setUpdatedAt(java.time.LocalDateTime.now());
        Contact updated = contactRepository.save(contact);
        
        // Notificación no debe fallar la operación principal
        try {
            notificationService.notifyContactUpdated(updated);
        } catch (Exception e) {
            logger.error("Error al enviar notificación de actualización de contacto", e);
            // No fallar la operación por un error de notificación
        }
        
        return updated;
    }

    @Transactional
    public void delete(Long id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contacto no encontrado"));
        contactRepository.delete(contact);
        
        // Notificación no debe fallar la operación principal
        try {
            notificationService.notifyContactDeleted(contact);
        } catch (Exception e) {
            logger.error("Error al enviar notificación de eliminación de contacto", e);
            // No fallar la operación por un error de notificación
        }
    }

    public ContactResponseDTO toResponseDTO(Contact contact) {
        return ContactResponseDTO.builder()
                .id(contact.getId())
                .name(contact.getName())
                .email(contact.getEmail())
                .phoneNumber(contact.getPhoneNumber())
                .createdAt(contact.getCreatedAt())
                .updatedAt(contact.getUpdatedAt())
                .build();
    }
}
