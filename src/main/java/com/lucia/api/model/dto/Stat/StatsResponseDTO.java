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
public class StatsResponseDTO {
    private Long totalCalls;
    private Long totalContacts;
    private Long totalAppointments;
    private Integer averageCallDuration; // en segundos
    private List<HistoricalDataDTO> historicalData;
    private List<TopConversationDTO> topConversations;
    private List<FrequentMotiveDTO> frequentMotives;
    private List<RecentContactDTO> recentContacts;
    private SummaryDTO summary;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistoricalDataDTO {
        private Integer llamadas;
        private String month;
        private Integer citas;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopConversationDTO {
        private Integer duration; // en segundos
        private String date;
        private String contact_name;
        private String motive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FrequentMotiveDTO {
        private Integer total_calls;
        private String motive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentContactDTO {
        private String name;
        private String phone_number;
        private String creation_date;
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SummaryDTO {
        private Long llamadas_ultimo_anio;
        private Long citas_ultimo_anio;
    }
}

