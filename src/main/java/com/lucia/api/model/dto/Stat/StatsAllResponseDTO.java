package com.lucia.api.model.dto.Stat;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsAllResponseDTO {
    private List<CallRawDTO> calls;
    private List<AppointmentRawDTO> appointments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CallRawDTO {
        private Long id;
        private String date;
        private Integer duration;
        private String motive;
        private String summary;
        private String intent;
        private String contact_name;
        private String contact_phone;
        private String contact_email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AppointmentRawDTO {
        private Long id;
        private String summary;
        private String date;
        private String start_time;
        private String end_time;
        private String status;
        private String description;
        private String location;
        private String contact_phone;
    }
}

