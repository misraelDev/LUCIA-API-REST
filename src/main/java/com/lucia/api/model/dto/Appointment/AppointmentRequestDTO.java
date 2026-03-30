package com.lucia.api.model.dto.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import com.lucia.api.model.entity.Appointment.AppointmentStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequestDTO {
    @NotBlank(message = "El resumen es obligatorio")
    @Size(max = 200, message = "El resumen no puede exceder 200 caracteres")
    private String summary;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime startTime;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime endTime;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;

    private AppointmentStatus status;

    @Size(max = 5000, message = "La descripción no puede exceder 5000 caracteres")
    private String description;

    @Size(max = 255, message = "La ubicación no puede exceder 255 caracteres")
    private String location;

    @Size(max = 20, message = "El teléfono de contacto no puede exceder 20 caracteres")
    @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,9}$", 
             message = "El teléfono debe tener un formato válido")
    private String contactPhone;

    // Explicit getters and setters as fallback
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

    // Static builder method
    public static AppointmentRequestDTOBuilder builder() {
        return new AppointmentRequestDTOBuilder();
    }

    public static class AppointmentRequestDTOBuilder {
        private String summary;
        private LocalTime startTime;
        private LocalTime endTime;
        private LocalDate date;
        private AppointmentStatus status;
        private String description;
        private String location;
        private String contactPhone;

        public AppointmentRequestDTOBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public AppointmentRequestDTOBuilder startTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public AppointmentRequestDTOBuilder endTime(LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public AppointmentRequestDTOBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public AppointmentRequestDTOBuilder status(AppointmentStatus status) {
            this.status = status;
            return this;
        }

        public AppointmentRequestDTOBuilder description(String description) {
            this.description = description;
            return this;
        }

        public AppointmentRequestDTOBuilder location(String location) {
            this.location = location;
            return this;
        }

        public AppointmentRequestDTOBuilder contactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
            return this;
        }

        public AppointmentRequestDTO build() {
            AppointmentRequestDTO dto = new AppointmentRequestDTO();
            dto.summary = this.summary;
            dto.startTime = this.startTime;
            dto.endTime = this.endTime;
            dto.date = this.date;
            dto.status = this.status;
            dto.description = this.description;
            dto.location = this.location;
            dto.contactPhone = this.contactPhone;
            return dto;
        }
    }
}
