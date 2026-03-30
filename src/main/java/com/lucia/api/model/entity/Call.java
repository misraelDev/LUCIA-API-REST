package com.lucia.api.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "calls")
public class Call {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha es obligatoria")
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 0, message = "La duración debe ser mayor o igual a 0")
    @Column(name = "duration", nullable = false)
    private Integer duration; // Duración en segundos

    @Size(max = 5000, message = "El motivo no puede exceder 5000 caracteres")
    @Column(name = "motive", columnDefinition = "TEXT")
    private String motive; // Motivo de la llamada

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact; // Contacto al que se llamó

    @Size(max = 5000, message = "El resumen no puede exceder 5000 caracteres")
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary; // Resumen de la llamada

    @Size(max = 200, message = "La intención no puede exceder 200 caracteres")
    @Column(name = "intent", length = 200)
    private String intent; // Intención detectada

    @Size(max = 10000, message = "Los mensajes no pueden exceder 10000 caracteres")
    @Column(name = "messages", columnDefinition = "TEXT")
    private String messages; // JSON con la transcripción

    @Size(max = 500, message = "La URL del audio combinado no puede exceder 500 caracteres")
    @Column(name = "audio_combined", columnDefinition = "TEXT")
    private String audioCombined; // URL del audio completo

    @Size(max = 500, message = "La URL del audio del asistente no puede exceder 500 caracteres")
    @Column(name = "audio_assistant", columnDefinition = "TEXT")
    private String audioAssistant; // URL del audio del asistente

    @Size(max = 500, message = "La URL del audio del cliente no puede exceder 500 caracteres")
    @Column(name = "audio_customer", columnDefinition = "TEXT")
    private String audioCustomer; // URL del audio del cliente

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (date == null) {
            date = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructores
    public Call() {}

    public Call(LocalDateTime date, Integer duration, String motive, Contact contact, 
                String summary, String intent, String messages, String audioCombined, 
                String audioAssistant, String audioCustomer) {
        this.date = date;
        this.duration = duration;
        this.motive = motive;
        this.contact = contact;
        this.summary = summary;
        this.intent = intent;
        this.messages = messages;
        this.audioCombined = audioCombined;
        this.audioAssistant = audioAssistant;
        this.audioCustomer = audioCustomer;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Call{" +
                "id=" + id +
                ", date=" + date +
                ", duration=" + duration +
                ", motive='" + motive + '\'' +
                ", contact=" + (contact != null ? contact.getId() : null) +
                ", summary='" + summary + '\'' +
                ", intent='" + intent + '\'' +
                ", audioCombined='" + audioCombined + '\'' +
                ", audioAssistant='" + audioAssistant + '\'' +
                ", audioCustomer='" + audioCustomer + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}