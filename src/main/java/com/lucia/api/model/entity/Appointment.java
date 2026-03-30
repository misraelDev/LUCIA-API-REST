package com.lucia.api.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El resumen es obligatorio")
    @Size(max = 200, message = "El resumen no puede exceder 200 caracteres")
    @Column(name = "summary", nullable = false)
    private String summary;

    @NotNull(message = "La hora de inicio es obligatoria")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull(message = "La hora de fin es obligatoria")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @NotNull(message = "La fecha es obligatoria")
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NotNull(message = "El estado es obligatorio")
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.UNASSIGNED;

    @Size(max = 5000, message = "La descripción no puede exceder 5000 caracteres")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Size(max = 255, message = "La ubicación no puede exceder 255 caracteres")
    @Column(name = "location")
    private String location;

    @Size(max = 20, message = "El teléfono de contacto no puede exceder 20 caracteres")
    @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,9}$", 
             message = "El teléfono debe tener un formato válido")
    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum AppointmentStatus {
        CONFIRMED,
        RESERVED,
        UNASSIGNED,
        CANCELLED,
        SCHEDULED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
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
}