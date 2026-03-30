package com.lucia.api.model.dto.Call;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallResponseDTO {
    private Long id;
    private LocalDateTime date;
    private Integer duration;
    private String motive;
    private Long contactId;
    /** Nombre del contacto (evita N peticiones al listar historial). */
    private String contactName;
    private String summary;
    private String intent;
    private String messages;
    private String audioCombined;
    private String audioAssistant;
    private String audioCustomer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CallResponseDTO(com.lucia.api.model.entity.Call call) {
        this.id = call.getId();
        this.date = call.getDate();
        this.duration = call.getDuration();
        this.motive = call.getMotive();
        this.contactId = call.getContact() != null ? call.getContact().getId() : null;
        this.contactName = call.getContact() != null ? call.getContact().getName() : null;
        this.summary = call.getSummary();
        this.intent = call.getIntent();
        this.messages = call.getMessages();
        this.audioCombined = call.getAudioCombined();
        this.audioAssistant = call.getAudioAssistant();
        this.audioCustomer = call.getAudioCustomer();
        this.createdAt = call.getCreatedAt();
        this.updatedAt = call.getUpdatedAt();
    }
}
