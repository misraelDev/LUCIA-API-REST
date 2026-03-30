package com.lucia.api.model.dto.Call;

import java.time.LocalDateTime;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallRequestDTO {
    @NotNull(message = "La fecha es obligatoria")
    private LocalDateTime date;

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 0, message = "La duración debe ser mayor o igual a 0")
    private Integer duration;

    @Size(max = 5000, message = "El motivo no puede exceder 5000 caracteres")
    private String motive;

    private Long contactId;

    @Size(max = 5000, message = "El resumen no puede exceder 5000 caracteres")
    private String summary;

    @Size(max = 200, message = "La intención no puede exceder 200 caracteres")
    private String intent;

    @Size(max = 10000, message = "Los mensajes no pueden exceder 10000 caracteres")
    private String messages;

    @Size(max = 500, message = "La URL del audio combinado no puede exceder 500 caracteres")
    private String audioCombined;

    @Size(max = 500, message = "La URL del audio del asistente no puede exceder 500 caracteres")
    private String audioAssistant;

    @Size(max = 500, message = "La URL del audio del cliente no puede exceder 500 caracteres")
    private String audioCustomer;

    // Explicit getters and setters as fallback
    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getMotive() {
        return motive;
    }

    public void setMotive(String motive) {
        this.motive = motive;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getAudioCombined() {
        return audioCombined;
    }

    public void setAudioCombined(String audioCombined) {
        this.audioCombined = audioCombined;
    }

    public String getAudioAssistant() {
        return audioAssistant;
    }

    public void setAudioAssistant(String audioAssistant) {
        this.audioAssistant = audioAssistant;
    }

    public String getAudioCustomer() {
        return audioCustomer;
    }

    public void setAudioCustomer(String audioCustomer) {
        this.audioCustomer = audioCustomer;
    }
}
