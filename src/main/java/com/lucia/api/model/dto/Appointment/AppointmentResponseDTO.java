package com.lucia.api.model.dto.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import com.lucia.api.model.entity.Appointment.AppointmentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponseDTO {
    private Long id;
    private String summary;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;
    private AppointmentStatus status;
    private String description;
    private String location;
    private String contactPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Static builder method
    public static AppointmentResponseDTOBuilder builder() {
        return new AppointmentResponseDTOBuilder();
    }

    public static class AppointmentResponseDTOBuilder {
        private Long id;
        private String summary;
        private LocalTime startTime;
        private LocalTime endTime;
        private LocalDate date;
        private AppointmentStatus status;
        private String description;
        private String location;
        private String contactPhone;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public AppointmentResponseDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AppointmentResponseDTOBuilder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public AppointmentResponseDTOBuilder startTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public AppointmentResponseDTOBuilder endTime(LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public AppointmentResponseDTOBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public AppointmentResponseDTOBuilder status(AppointmentStatus status) {
            this.status = status;
            return this;
        }

        public AppointmentResponseDTOBuilder description(String description) {
            this.description = description;
            return this;
        }

        public AppointmentResponseDTOBuilder location(String location) {
            this.location = location;
            return this;
        }

        public AppointmentResponseDTOBuilder contactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
            return this;
        }

        public AppointmentResponseDTOBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AppointmentResponseDTOBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public AppointmentResponseDTO build() {
            AppointmentResponseDTO dto = new AppointmentResponseDTO();
            dto.id = this.id;
            dto.summary = this.summary;
            dto.startTime = this.startTime;
            dto.endTime = this.endTime;
            dto.date = this.date;
            dto.status = this.status;
            dto.description = this.description;
            dto.location = this.location;
            dto.contactPhone = this.contactPhone;
            dto.createdAt = this.createdAt;
            dto.updatedAt = this.updatedAt;
            return dto;
        }
    }
}
