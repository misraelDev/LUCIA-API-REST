package com.lucia.api.service.Stat;

import com.lucia.api.model.dto.Stat.StatResponseDto;
import com.lucia.api.model.dto.Stat.StatsResponseDTO;
import com.lucia.api.model.dto.Stat.StatsAllResponseDTO;
import com.lucia.api.model.entity.Appointment;
import com.lucia.api.model.entity.Call;
import com.lucia.api.model.entity.Contact;
import com.lucia.api.repository.Appointment.AppointmentRepository;
import com.lucia.api.repository.Call.CallRepository;
import com.lucia.api.repository.Contact.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class StatsService {

    @Autowired
    private CallRepository callRepository;
    
    @Autowired
    private ContactRepository contactRepository;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    public StatResponseDto getStatData() {
        // Obtener métricas principales
        Long totalCalls = callRepository.count();
        Long totalContacts = contactRepository.count();
        Long totalAppointments = appointmentRepository.count();
        String averageCallDuration = calculateAverageCallDuration();
        
        // Obtener datos históricos (último año)
        List<StatResponseDto.HistoricalDataDto> historicalData = getHistoricalData();
        
        // Obtener conversaciones recientes (últimas 10)
        List<StatResponseDto.RecentConversationDto> recentConversations = getRecentConversations();
        
        // Obtener motivos frecuentes (top 5)
        List<StatResponseDto.FrequentMotiveDto> frequentMotives = getFrequentMotives();
        
        // Obtener contactos recientes (últimos 10)
        List<StatResponseDto.RecentContactDto> recentContacts = getRecentContacts();
        
        return new StatResponseDto(
            totalCalls,
            totalContacts,
            totalAppointments,
            averageCallDuration,
            historicalData,
            recentConversations,
            frequentMotives,
            recentContacts
        );
    }
    
    private String calculateAverageCallDuration() {
        List<Call> calls = callRepository.findAll();
        if (calls.isEmpty()) {
            return "00:00";
        }
        
        double averageSeconds = calls.stream()
            .filter(call -> call.getDuration() != null)
            .mapToInt(Call::getDuration)
            .average()
            .orElse(0.0);
        
        int minutes = (int) (averageSeconds / 60);
        int seconds = (int) (averageSeconds % 60);
        
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    private List<StatResponseDto.HistoricalDataDto> getHistoricalData() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        
        // Obtener llamadas del último año
        List<Call> calls = callRepository.findAll().stream()
            .filter(call -> call.getDate() != null && call.getDate().isAfter(oneYearAgo))
            .collect(Collectors.toList());
        
        // Obtener citas del último año
        List<Appointment> appointments = appointmentRepository.findAll().stream()
            .filter(appointment -> appointment.getDate() != null && 
                    appointment.getDate().isAfter(oneYearAgo.toLocalDate()))
            .collect(Collectors.toList());
        
        // Agrupar por mes
        Map<String, Long> callsByMonth = calls.stream()
            .collect(Collectors.groupingBy(
                call -> call.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                Collectors.counting()
            ));
        
        Map<String, Long> appointmentsByMonth = appointments.stream()
            .collect(Collectors.groupingBy(
                appointment -> appointment.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                Collectors.counting()
            ));
        
        // Generar datos para los últimos 12 meses
        List<StatResponseDto.HistoricalDataDto> historicalData = List.of();
        LocalDateTime current = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        for (int i = 11; i >= 0; i--) {
            LocalDateTime monthDate = current.minusMonths(i);
            String monthKey = monthDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String monthName = monthDate.format(DateTimeFormatter.ofPattern("MMM"));
            
            Long callsCount = callsByMonth.getOrDefault(monthKey, 0L);
            Long appointmentsCount = appointmentsByMonth.getOrDefault(monthKey, 0L);
            
            historicalData.add(new StatResponseDto.HistoricalDataDto(monthName, callsCount, appointmentsCount));
        }
        
        return historicalData;
    }
    
    private List<StatResponseDto.RecentConversationDto> getRecentConversations() {
        return callRepository.findAll().stream()
            .filter(call -> call.getContact() != null)
            .sorted(Comparator.comparing(Call::getDate).reversed())
            .limit(10)
            .map(call -> new StatResponseDto.RecentConversationDto(
                call.getContact().getName(),
                call.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")),
                call.getMotive() != null ? call.getMotive() : "Sin motivo",
                call.getDuration() != null ? call.getDuration() : 0
            ))
            .collect(Collectors.toList());
    }
    
    private List<StatResponseDto.FrequentMotiveDto> getFrequentMotives() {
        Map<String, Long> motiveCounts = callRepository.findAll().stream()
            .filter(call -> call.getMotive() != null && !call.getMotive().trim().isEmpty())
            .collect(Collectors.groupingBy(
                Call::getMotive,
                Collectors.counting()
            ));
        
        return motiveCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .map(entry -> new StatResponseDto.FrequentMotiveDto(
                entry.getKey(),
                entry.getValue()
            ))
            .collect(Collectors.toList());
    }
    
    private List<StatResponseDto.RecentContactDto> getRecentContacts() {
        return contactRepository.findAll().stream()
            .sorted(Comparator.comparing(Contact::getCreatedAt).reversed())
            .limit(10)
            .map(contact -> new StatResponseDto.RecentContactDto(
                contact.getName(),
                contact.getEmail(),
                contact.getPhoneNumber(),
                contact.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
            ))
            .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las estadísticas del sistema en el formato solicitado
     */
    @Transactional(readOnly = true)
    public StatsResponseDTO getAllStats() {
        // Obtener métricas principales
        Long totalCalls = callRepository.count();
        Long totalContacts = contactRepository.count();
        Long totalAppointments = appointmentRepository.count();
        Integer averageCallDuration = calculateAverageCallDurationInSeconds();
        
        // Obtener datos históricos (último año)
        List<StatsResponseDTO.HistoricalDataDTO> historicalData = getHistoricalDataForStats();
        
        // Obtener top conversaciones (ordenadas por duración, más largas primero)
        List<StatsResponseDTO.TopConversationDTO> topConversations = getTopConversations();
        
        // Obtener motivos frecuentes
        List<StatsResponseDTO.FrequentMotiveDTO> frequentMotives = getFrequentMotivesForStats();
        
        // Obtener contactos recientes
        List<StatsResponseDTO.RecentContactDTO> recentContacts = getRecentContactsForStats();
        
        // Obtener resumen del último año
        StatsResponseDTO.SummaryDTO summary = getSummaryForStats();
        
        return StatsResponseDTO.builder()
                .totalCalls(totalCalls)
                .totalContacts(totalContacts)
                .totalAppointments(totalAppointments)
                .averageCallDuration(averageCallDuration)
                .historicalData(historicalData)
                .topConversations(topConversations)
                .frequentMotives(frequentMotives)
                .recentContacts(recentContacts)
                .summary(summary)
                .build();
    }

    /**
     * Obtiene llamadas y citas en formato crudo
     */
    @Transactional(readOnly = true)
    public StatsAllResponseDTO getAllRawData() {
        List<Call> calls = callRepository.findAll();
        List<Appointment> appointments = appointmentRepository.findAll();
        
        List<StatsAllResponseDTO.CallRawDTO> callDtos = calls.stream()
                .map(call -> {
                    Contact contact = call.getContact();
                    return StatsAllResponseDTO.CallRawDTO.builder()
                            .id(call.getId())
                            .date(call.getDate() != null ? 
                                call.getDate().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) : null)
                            .duration(call.getDuration())
                            .motive(call.getMotive())
                            .summary(call.getSummary())
                            .intent(call.getIntent())
                            .contact_name(contact != null ? contact.getName() : null)
                            .contact_phone(contact != null ? contact.getPhoneNumber() : null)
                            .contact_email(contact != null ? contact.getEmail() : null)
                            .build();
                })
                .collect(Collectors.toList());
        
        List<StatsAllResponseDTO.AppointmentRawDTO> appointmentDtos = appointments.stream()
                .map(appointment -> StatsAllResponseDTO.AppointmentRawDTO.builder()
                        .id(appointment.getId())
                        .summary(appointment.getSummary())
                        .date(appointment.getDate() != null ? appointment.getDate().toString() : null)
                        .start_time(appointment.getStartTime() != null ? appointment.getStartTime().toString() : null)
                        .end_time(appointment.getEndTime() != null ? appointment.getEndTime().toString() : null)
                        .status(appointment.getStatus() != null ? appointment.getStatus().name() : null)
                        .description(appointment.getDescription())
                        .location(appointment.getLocation())
                        .contact_phone(appointment.getContactPhone())
                        .build())
                .collect(Collectors.toList());
        
        return StatsAllResponseDTO.builder()
                .calls(callDtos)
                .appointments(appointmentDtos)
                .build();
    }

    private Integer calculateAverageCallDurationInSeconds() {
        List<Call> calls = callRepository.findAll();
        if (calls.isEmpty()) {
            return 0;
        }
        
        double averageSeconds = calls.stream()
            .filter(call -> call.getDuration() != null)
            .mapToInt(Call::getDuration)
            .average()
            .orElse(0.0);
        
        return (int) Math.round(averageSeconds);
    }

    private List<StatsResponseDTO.HistoricalDataDTO> getHistoricalDataForStats() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        
        // Obtener llamadas del último año
        List<Call> calls = callRepository.findAll().stream()
            .filter(call -> call.getDate() != null && call.getDate().isAfter(oneYearAgo))
            .collect(Collectors.toList());
        
        // Obtener citas del último año
        List<Appointment> appointments = appointmentRepository.findAll().stream()
            .filter(appointment -> appointment.getDate() != null && 
                    appointment.getDate().isAfter(oneYearAgo.toLocalDate()))
            .collect(Collectors.toList());
        
        // Agrupar por mes
        Map<String, Long> callsByMonth = calls.stream()
            .collect(Collectors.groupingBy(
                call -> call.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                Collectors.counting()
            ));
        
        Map<String, Long> appointmentsByMonth = appointments.stream()
            .collect(Collectors.groupingBy(
                appointment -> appointment.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                Collectors.counting()
            ));
        
        // Generar datos para los últimos 12 meses
        List<StatsResponseDTO.HistoricalDataDTO> historicalData = new ArrayList<>();
        LocalDateTime current = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        for (int i = 11; i >= 0; i--) {
            LocalDateTime monthDate = current.minusMonths(i);
            String monthKey = monthDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            
            Long callsCount = callsByMonth.getOrDefault(monthKey, 0L);
            Long appointmentsCount = appointmentsByMonth.getOrDefault(monthKey, 0L);
            
            historicalData.add(StatsResponseDTO.HistoricalDataDTO.builder()
                    .llamadas(callsCount.intValue())
                    .month(monthKey)
                    .citas(appointmentsCount.intValue())
                    .build());
        }
        
        return historicalData;
    }

    @Transactional(readOnly = true)
    private List<StatsResponseDTO.TopConversationDTO> getTopConversations() {
        return callRepository.findAll().stream()
            .filter(call -> call.getContact() != null && call.getDuration() != null)
            .sorted(Comparator.comparing(Call::getDuration).reversed())
            .limit(10)
            .map(call -> {
                Contact contact = call.getContact();
                return StatsResponseDTO.TopConversationDTO.builder()
                        .duration(call.getDuration())
                        .date(call.getDate() != null ? 
                            call.getDate().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) : null)
                        .contact_name(contact != null ? contact.getName() : null)
                        .motive(call.getMotive() != null ? call.getMotive() : "Sin motivo")
                        .build();
            })
            .collect(Collectors.toList());
    }

    private List<StatsResponseDTO.FrequentMotiveDTO> getFrequentMotivesForStats() {
        Map<String, Long> motiveCounts = callRepository.findAll().stream()
            .filter(call -> call.getMotive() != null && !call.getMotive().trim().isEmpty())
            .collect(Collectors.groupingBy(
                Call::getMotive,
                Collectors.counting()
            ));
        
        return motiveCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .map(entry -> StatsResponseDTO.FrequentMotiveDTO.builder()
                    .total_calls(entry.getValue().intValue())
                    .motive(entry.getKey())
                    .build())
            .collect(Collectors.toList());
    }

    private List<StatsResponseDTO.RecentContactDTO> getRecentContactsForStats() {
        return contactRepository.findAll().stream()
            .sorted(Comparator.comparing(Contact::getCreatedAt).reversed())
            .limit(10)
            .map(contact -> StatsResponseDTO.RecentContactDTO.builder()
                    .name(contact.getName())
                    .phone_number(contact.getPhoneNumber())
                    .creation_date(contact.getCreatedAt() != null ? 
                        contact.getCreatedAt().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) : null)
                    .email(contact.getEmail())
                    .build())
            .collect(Collectors.toList());
    }

    private StatsResponseDTO.SummaryDTO getSummaryForStats() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        
        Long callsLastYear = callRepository.findAll().stream()
            .filter(call -> call.getDate() != null && call.getDate().isAfter(oneYearAgo))
            .count();
        
        Long appointmentsLastYear = appointmentRepository.findAll().stream()
            .filter(appointment -> appointment.getDate() != null && 
                    appointment.getDate().isAfter(oneYearAgo.toLocalDate()))
            .count();
        
        return StatsResponseDTO.SummaryDTO.builder()
                .llamadas_ultimo_anio(callsLastYear)
                .citas_ultimo_anio(appointmentsLastYear)
                .build();
    }
}
