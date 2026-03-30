
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
public class StatResponseDto {
    private Long totalCalls;
    private Long totalContacts;
    private Long totalAppointments;
    private String averageCallDuration;
    private List<HistoricalDataDto> historicalData;
    private List<RecentConversationDto> recentConversations;
    private List<FrequentMotiveDto> frequentMotives;
    private List<RecentContactDto> recentContacts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistoricalDataDto {
        private String month;
        private Long callsCount;
        private Long appointmentsCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentConversationDto {
        private String contactName;
        private String date;
        private String motive;
        private Integer duration;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FrequentMotiveDto {
        private String motive;
        private Long count;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentContactDto {
        private String name;
        private String email;
        private String phoneNumber;
        private String createdAt;
    }
}
