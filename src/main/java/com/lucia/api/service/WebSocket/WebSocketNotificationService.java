package com.lucia.api.service.WebSocket;

import com.lucia.api.model.entity.Request;
import com.lucia.api.model.dto.Stat.StatResponseDto;
import com.lucia.api.model.dto.Call.CallResponseDTO;
import com.lucia.api.model.entity.Appointment;
import com.lucia.api.model.entity.Contact;
import com.lucia.api.model.entity.Call;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class WebSocketNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketNotificationService.class);
    
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
        // --- Referral Notifications ---
        public void notifyNewReferral(com.lucia.api.model.entity.Referral referral) {
            try {
                messagingTemplate.convertAndSend("/topic/referrals/new", referral);
            } catch (Exception e) {
                logger.error("Error al enviar notificación WebSocket para nueva referral", e);
            }
        }

        public void notifyReferralUpdated(com.lucia.api.model.entity.Referral referral) {
            try {
                messagingTemplate.convertAndSend("/topic/referrals/updated", referral);
            } catch (Exception e) {
                logger.error("Error al enviar notificación WebSocket para referral actualizada", e);
            }
        }

        public void notifyReferralDeleted(com.lucia.api.model.entity.Referral referral) {
            try {
                messagingTemplate.convertAndSend("/topic/referrals/deleted", referral);
            } catch (Exception e) {
                logger.error("Error al enviar notificación WebSocket para referral eliminada", e);
            }
        }

    /**
     * Notifica a todos los clientes sobre una nueva solicitud
     */
    public void notifyNewRequest(Request request) {
        try {
            logger.info("Enviando notificación WebSocket para nueva solicitud ID: {}", request.getId());
            
            // Notificar a todos los clientes suscritos al topic general
            messagingTemplate.convertAndSend("/topic/requests/new", request);
            
            // Notificar específicamente al seller si tiene sellerId
            if (request.getSellerId() != null) {
                String sellerTopic = "/topic/seller/" + request.getSellerId() + "/requests/new";
                messagingTemplate.convertAndSend(sellerTopic, request);
                logger.info("Notificación enviada al seller {} en topic: {}", request.getSellerId(), sellerTopic);
            }
            
            logger.info("Notificación WebSocket enviada exitosamente para solicitud ID: {}", request.getId());
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket para solicitud ID: {}", request.getId(), e);
        }
    }

    /**
     * Notifica a todos los clientes sobre una solicitud actualizada
     */
    public void notifyRequestUpdated(Request request) {
        try {
            logger.info("Enviando notificación WebSocket para solicitud actualizada ID: {}", request.getId());
            
            // Notificar a todos los clientes suscritos al topic general
            messagingTemplate.convertAndSend("/topic/requests/updated", request);
            
            // Notificar específicamente al seller si tiene sellerId
            if (request.getSellerId() != null) {
                String sellerTopic = "/topic/seller/" + request.getSellerId() + "/requests/updated";
                messagingTemplate.convertAndSend(sellerTopic, request);
                logger.info("Notificación enviada al seller {} en topic: {}", request.getSellerId(), sellerTopic);
            }
            
            logger.info("Notificación WebSocket enviada exitosamente para solicitud actualizada ID: {}", request.getId());
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket para solicitud actualizada ID: {}", request.getId(), e);
        }
    }

    /**
     * Notifica a un usuario específico sobre sus solicitudes
     */
    public void notifyUserRequest(String userId, Request request, String action) {
        try {
            String userTopic = "/user/" + userId + "/requests/" + action;
            logger.info("Enviando notificación personalizada a usuario {} en topic: {}", userId, userTopic);
            
            messagingTemplate.convertAndSendToUser(userId, "/requests/" + action, request);
            
            logger.info("Notificación personalizada enviada exitosamente a usuario: {}", userId);
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación personalizada a usuario: {}", userId, e);
        }
    }

    /**
     * Envía un heartbeat para mantener las conexiones activas
     */
    public void sendHeartbeat() {
        try {
            messagingTemplate.convertAndSend("/topic/heartbeat", "ping");
            logger.debug("Heartbeat enviado a todos los clientes conectados");
        } catch (Exception e) {
            logger.debug("Error al enviar heartbeat: {}", e.getMessage());
        }
    }

    /**
     * Notifica a todos los clientes que se consultaron las estadísticas
     */
    public void notifyStatsRequested(Map<String, Object> stats) {
        try {
            logger.info("Enviando notificación WebSocket de consulta de estadísticas");
            
            // Notificar en el topic de stats consultadas
            messagingTemplate.convertAndSend("/topic/stats/consulted", (Object) stats);
            
            // También notificar en el topic del dashboard si existe
            messagingTemplate.convertAndSend("/topic/dashboard/stats/consulted", (Object) stats);
            
            logger.info("Notificación WebSocket de estadísticas enviada exitosamente");
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket de estadísticas", e);
        }
    }

    // ========== APPOINTMENT NOTIFICATIONS ==========

    /**
     * Notifica a todos los clientes sobre una nueva cita
     */
    public void notifyNewAppointment(Appointment appointment) {
        try {
            logger.info("Enviando notificación WebSocket para nueva cita ID: {}", appointment.getId());
            
            // Notificar a todos los clientes suscritos al topic general
            messagingTemplate.convertAndSend("/topic/appointments/new", appointment);
            
            // Notificar específicamente al teléfono de contacto si existe
            if (appointment.getContactPhone() != null) {
                String phoneTopic = "/topic/phone/" + appointment.getContactPhone() + "/appointments/new";
                messagingTemplate.convertAndSend(phoneTopic, appointment);
                logger.info("Notificación enviada al teléfono {} en topic: {}", appointment.getContactPhone(), phoneTopic);
            }
            
            logger.info("Notificación WebSocket enviada exitosamente para cita ID: {}", appointment.getId());
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket para cita ID: {}", appointment.getId(), e);
        }
    }

    /**
     * Notifica a todos los clientes sobre una cita actualizada
     */
    public void notifyAppointmentUpdated(Appointment appointment) {
        try {
            logger.info("Enviando notificación WebSocket para cita actualizada ID: {}", appointment.getId());
            
            // Notificar a todos los clientes suscritos al topic general
            messagingTemplate.convertAndSend("/topic/appointments/updated", appointment);
            
            // Notificar específicamente al teléfono de contacto si existe
            if (appointment.getContactPhone() != null) {
                String phoneTopic = "/topic/phone/" + appointment.getContactPhone() + "/appointments/updated";
                messagingTemplate.convertAndSend(phoneTopic, appointment);
                logger.info("Notificación enviada al teléfono {} en topic: {}", appointment.getContactPhone(), phoneTopic);
            }
            
            logger.info("Notificación WebSocket enviada exitosamente para cita actualizada ID: {}", appointment.getId());
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket para cita actualizada ID: {}", appointment.getId(), e);
        }
    }

    /**
     * Notifica a todos los clientes sobre una cita cancelada
     */
    public void notifyAppointmentCancelled(Appointment appointment) {
        try {
            logger.info("Enviando notificación WebSocket para cita cancelada ID: {}", appointment.getId());
            
            // Notificar a todos los clientes suscritos al topic general
            messagingTemplate.convertAndSend("/topic/appointments/cancelled", appointment);
            
            // Notificar específicamente al teléfono de contacto si existe
            if (appointment.getContactPhone() != null) {
                String phoneTopic = "/topic/phone/" + appointment.getContactPhone() + "/appointments/cancelled";
                messagingTemplate.convertAndSend(phoneTopic, appointment);
                logger.info("Notificación enviada al teléfono {} en topic: {}", appointment.getContactPhone(), phoneTopic);
            }
            
            logger.info("Notificación WebSocket enviada exitosamente para cita cancelada ID: {}", appointment.getId());
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket para cita cancelada ID: {}", appointment.getId(), e);
        }
    }

    /**
     * Notifica a todos los clientes sobre un cambio de estado de cita
     */
    public void notifyAppointmentStatusChanged(Appointment appointment, String oldStatus, String newStatus) {
        try {
            logger.info("Enviando notificación WebSocket para cambio de estado de cita ID: {}: {} -> {}", 
                       appointment.getId(), oldStatus, newStatus);
            
            // Crear payload con información del cambio
            Map<String, Object> statusChange = Map.of(
                "appointment", appointment,
                "oldStatus", oldStatus,
                "newStatus", newStatus,
                "timestamp", System.currentTimeMillis()
            );
            
            // Cast a (Object) para evitar error de ambigüedad
            messagingTemplate.convertAndSend("/topic/appointments/status-changed", (Object) statusChange);
            
            // Notificar específicamente al teléfono de contacto si existe
            if (appointment.getContactPhone() != null) {
                String phoneTopic = "/topic/phone/" + appointment.getContactPhone() + "/appointments/status-changed";
                messagingTemplate.convertAndSend(phoneTopic, (Object) statusChange);
                logger.info("Notificación enviada al teléfono {} en topic: {}", appointment.getContactPhone(), phoneTopic);
            }
            
            logger.info("Notificación WebSocket enviada exitosamente para cambio de estado de cita ID: {}", appointment.getId());
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket para cambio de estado de cita ID: {}", appointment.getId(), e);
        }
    }

    /**
     * Notifica a todos los clientes sobre citas consultadas por fecha
     */
    public void notifyAppointmentsByDateConsulted(LocalDate date, List<Appointment> appointments) {
        try {
            logger.info("Enviando notificación WebSocket de consulta de citas por fecha: {}", date);
            
            // Crear payload con información de la consulta
            Map<String, Object> consultation = Map.of(
                "date", date.toString(),
                "appointments", appointments,
                "count", appointments.size(),
                "timestamp", System.currentTimeMillis()
            );
            
            // Cast a (Object) para evitar error de ambigüedad
            messagingTemplate.convertAndSend("/topic/appointments/by-date-consulted", (Object) consultation);
            
            logger.info("Notificación WebSocket de consulta por fecha enviada exitosamente");
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket de consulta por fecha", e);
        }
    }

    // ========== CONTACT NOTIFICATIONS ==========

    /**
     * Notifica a todos los clientes sobre un nuevo contacto
     */
    public void notifyNewContact(Contact contact) {
        try {
            logger.info("Enviando notificación WebSocket para nuevo contacto ID: {}", contact.getId());
            
            // Notificar a todos los clientes suscritos al topic general
            messagingTemplate.convertAndSend("/topic/contacts/new", contact);
            
            // Notificar específicamente al email si existe
            if (contact.getEmail() != null) {
                String emailTopic = "/topic/email/" + contact.getEmail() + "/contacts/new";
                messagingTemplate.convertAndSend(emailTopic, contact);
                logger.info("Notificación enviada al email {} en topic: {}", contact.getEmail(), emailTopic);
            }
            
            // Notificar específicamente al teléfono si existe
            if (contact.getPhoneNumber() != null) {
                String phoneTopic = "/topic/phone/" + contact.getPhoneNumber() + "/contacts/new";
                messagingTemplate.convertAndSend(phoneTopic, contact);
                logger.info("Notificación enviada al teléfono {} en topic: {}", contact.getPhoneNumber(), phoneTopic);
            }
            
            logger.info("Notificación WebSocket enviada exitosamente para contacto ID: {}", contact.getId());
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket para contacto ID: {}", contact.getId(), e);
        }
    }

    /**
     * Notifica a todos los clientes sobre un contacto actualizado
     */
    public void notifyContactUpdated(Contact contact) {
        try {
            logger.info("Enviando notificación WebSocket para contacto actualizado ID: {}", contact.getId());
            
            // Notificar a todos los clientes suscritos al topic general
            messagingTemplate.convertAndSend("/topic/contacts/updated", contact);
            
            // Notificar específicamente al email si existe
            if (contact.getEmail() != null) {
                String emailTopic = "/topic/email/" + contact.getEmail() + "/contacts/updated";
                messagingTemplate.convertAndSend(emailTopic, contact);
                logger.info("Notificación enviada al email {} en topic: {}", contact.getEmail(), emailTopic);
            }
            
            // Notificar específicamente al teléfono si existe
            if (contact.getPhoneNumber() != null) {
                String phoneTopic = "/topic/phone/" + contact.getPhoneNumber() + "/contacts/updated";
                messagingTemplate.convertAndSend(phoneTopic, contact);
                logger.info("Notificación enviada al teléfono {} en topic: {}", contact.getPhoneNumber(), phoneTopic);
            }
            
            logger.info("Notificación WebSocket enviada exitosamente para contacto actualizado ID: {}", contact.getId());
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket para contacto actualizado ID: {}", contact.getId(), e);
        }
    }

    /**
     * Notifica a todos los clientes sobre un contacto eliminado
     */
    public void notifyContactDeleted(Contact contact) {
        try {
            logger.info("Enviando notificación WebSocket para contacto eliminado ID: {}", contact.getId());
            
            // Notificar a todos los clientes suscritos al topic general
            messagingTemplate.convertAndSend("/topic/contacts/deleted", contact);
            
            // Notificar específicamente al email si existe
            if (contact.getEmail() != null) {
                String emailTopic = "/topic/email/" + contact.getEmail() + "/contacts/deleted";
                messagingTemplate.convertAndSend(emailTopic, contact);
                logger.info("Notificación enviada al email {} en topic: {}", contact.getEmail(), emailTopic);
            }
            
            // Notificar específicamente al teléfono si existe
            if (contact.getPhoneNumber() != null) {
                String phoneTopic = "/topic/phone/" + contact.getPhoneNumber() + "/contacts/deleted";
                messagingTemplate.convertAndSend(phoneTopic, contact);
                logger.info("Notificación enviada al teléfono {} en topic: {}", contact.getPhoneNumber(), phoneTopic);
            }
            
            logger.info("Notificación WebSocket enviada exitosamente para contacto eliminado ID: {}", contact.getId());
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket para contacto eliminado ID: {}", contact.getId(), e);
        }
    }

    /**
     * Notifica a todos los clientes sobre contactos consultados
     */
    public void notifyContactsConsulted(List<Contact> contacts) {
        try {
            logger.info("Enviando notificación WebSocket de consulta de contactos");
            
            // Crear payload con información de la consulta
            Map<String, Object> consultation = Map.of(
                "contacts", contacts,
                "count", contacts.size(),
                "timestamp", System.currentTimeMillis()
            );
            
            // Cast a (Object) para evitar error de ambigüedad
            messagingTemplate.convertAndSend("/topic/contacts/consulted", (Object) consultation);
            
            logger.info("Notificación WebSocket de consulta de contactos enviada exitosamente");
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket de consulta de contactos", e);
        }
    }

    // ========== CALL NOTIFICATIONS ==========

    /**
     * Notifica a todos los clientes sobre una nueva llamada
     */
    public void notifyNewCall(Call call) {
        try {
            logger.info("Enviando notificación WebSocket para nueva llamada ID: {}", call.getId());
            
            // Convertir a DTO para evitar problemas de serialización
            CallResponseDTO callDto = new CallResponseDTO(call);
            
            // Notificar a todos los clientes suscritos al topic general
            messagingTemplate.convertAndSend("/topic/calls/new", callDto);
            
            // Notificar específicamente al contacto si existe
            if (call.getContact() != null && call.getContact().getId() != null) {
                String contactTopic = "/topic/contact/" + call.getContact().getId() + "/calls/new";
                messagingTemplate.convertAndSend(contactTopic, callDto);
                logger.info("Notificación enviada al contacto {} en topic: {}", call.getContact().getId(), contactTopic);
            }
            
            logger.info("Notificación WebSocket enviada exitosamente para llamada ID: {}", call.getId());
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket para llamada ID: {}", call.getId(), e);
        }
    }

    /**
     * Notifica a todos los clientes sobre una llamada actualizada
     */
    public void notifyCallUpdated(Call call) {
        try {
            logger.info("Enviando notificación WebSocket para llamada actualizada ID: {}", call.getId());
            
            // Convertir a DTO para evitar problemas de serialización
            CallResponseDTO callDto = new CallResponseDTO(call);
            
            // Notificar a todos los clientes suscritos al topic general
            messagingTemplate.convertAndSend("/topic/calls/updated", callDto);
            
            // Notificar específicamente al contacto si existe
            if (call.getContact() != null && call.getContact().getId() != null) {
                String contactTopic = "/topic/contact/" + call.getContact().getId() + "/calls/updated";
                messagingTemplate.convertAndSend(contactTopic, callDto);
                logger.info("Notificación enviada al contacto {} en topic: {}", call.getContact().getId(), contactTopic);
            }
            
            logger.info("Notificación WebSocket enviada exitosamente para llamada actualizada ID: {}", call.getId());
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket para llamada actualizada ID: {}", call.getId(), e);
        }
    }

    /**
     * Notifica a todos los clientes sobre una llamada eliminada
     */
    public void notifyCallDeleted(Call call) {
        try {
            logger.info("Enviando notificación WebSocket para llamada eliminada ID: {}", call.getId());
            
            // Convertir a DTO para evitar problemas de serialización
            CallResponseDTO callDto = new CallResponseDTO(call);
            
            // Notificar a todos los clientes suscritos al topic general
            messagingTemplate.convertAndSend("/topic/calls/deleted", callDto);
            
            // Notificar específicamente al contacto si existe
            if (call.getContact() != null && call.getContact().getId() != null) {
                String contactTopic = "/topic/contact/" + call.getContact().getId() + "/calls/deleted";
                messagingTemplate.convertAndSend(contactTopic, callDto);
                logger.info("Notificación enviada al contacto {} en topic: {}", call.getContact().getId(), contactTopic);
            }
            
            logger.info("Notificación WebSocket enviada exitosamente para llamada eliminada ID: {}", call.getId());
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket para llamada eliminada ID: {}", call.getId(), e);
        }
    }

    /**
     * Notifica a todos los clientes sobre llamadas consultadas
     */
    public void notifyCallsConsulted(List<Call> calls) {
        try {
            logger.info("Enviando notificación WebSocket de consulta de llamadas");
            
            // Convertir las entidades Call a DTOs para evitar problemas de serialización
            List<CallResponseDTO> callDtos = calls.stream()
                    .map(CallResponseDTO::new)
                    .toList();
            
            // Crear payload con información de la consulta
            Map<String, Object> consultation = Map.of(
                "calls", callDtos,
                "count", calls.size(),
                "timestamp", System.currentTimeMillis()
            );
            
            // Cast a (Object) para evitar error de ambigüedad
            messagingTemplate.convertAndSend("/topic/calls/consulted", (Object) consultation);
            
            logger.info("Notificación WebSocket de consulta de llamadas enviada exitosamente");
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket de consulta de llamadas", e);
        }
    }

    /**
     * Notifica a todos los clientes sobre estadísticas de llamadas consultadas
     */
    public void notifyCallStatsConsulted(Map<String, Object> stats) {
        try {
            logger.info("Enviando notificación WebSocket de consulta de estadísticas de llamadas");
            
            // Cast a (Object) para evitar error de ambigüedad
            messagingTemplate.convertAndSend("/topic/calls/stats/consulted", (Object) stats);
            
            logger.info("Notificación WebSocket de estadísticas de llamadas enviada exitosamente");
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket de estadísticas de llamadas", e);
        }
    }

    // ========== DASHBOARD NOTIFICATIONS ==========

    /**
     * Notifica a todos los clientes sobre datos del dashboard consultados
     */
    public void notifyDashboardStatsConsulted(StatResponseDto dashboardStats) {
        try {
            logger.info("Enviando notificación WebSocket de consulta del dashboard");
            
            // Crear payload con información del dashboard
            Map<String, Object> dashboardData = Map.of(
                "dashboardStats", dashboardStats,
                "timestamp", System.currentTimeMillis()
            );
            
            // Cast a (Object) para evitar error de ambigüedad
            messagingTemplate.convertAndSend("/topic/dashboard/stats/consulted", (Object) dashboardData);
            
            logger.info("Notificación WebSocket del dashboard enviada exitosamente");
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket del dashboard", e);
        }
    }

    /**
     * Notifica a todos los clientes sobre actualización de métricas del dashboard
     */
    public void notifyDashboardMetricsUpdated(Map<String, Object> metrics) {
        try {
            logger.info("Enviando notificación WebSocket de actualización de métricas del dashboard");
            
            // Crear payload con las métricas actualizadas
            Map<String, Object> metricsData = Map.of(
                "metrics", metrics,
                "timestamp", System.currentTimeMillis()
            );
            
            // Cast a (Object) para evitar error de ambigüedad
            messagingTemplate.convertAndSend("/topic/dashboard/metrics/updated", (Object) metricsData);
            
            logger.info("Notificación WebSocket de métricas del dashboard enviada exitosamente");
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket de métricas del dashboard", e);
        }
    }

    // ========== USER NOTIFICATIONS ==========

    /**
     * Notifica a todos los clientes sobre un nuevo usuario creado
     */
    public void notifyNewUser(Map<String, Object> userData) {
        try {
            logger.info("Enviando notificación WebSocket para nuevo usuario creado");
            
            // Cast a (Object) para evitar error de ambigüedad
            messagingTemplate.convertAndSend("/topic/users/new", (Object) userData);
            
            logger.info("Notificación WebSocket de nuevo usuario enviada exitosamente");
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket de nuevo usuario", e);
        }
    }

    /**
     * Notifica a todos los clientes sobre usuarios consultados
     */
    public void notifyUsersConsulted(Map<String, Object> consultationData) {
        try {
            logger.info("Enviando notificación WebSocket de consulta de usuarios");
            
            // Cast a (Object) para evitar error de ambigüedad
            messagingTemplate.convertAndSend("/topic/users/consulted", (Object) consultationData);
            
            logger.info("Notificación WebSocket de consulta de usuarios enviada exitosamente");
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket de consulta de usuarios", e);
        }
    }

    /**
     * Notifica a todos los clientes sobre un usuario específico consultado
     */
    public void notifyUserConsulted(Map<String, Object> userConsultationData) {
        try {
            logger.info("Enviando notificación WebSocket de consulta de usuario específico");
            
            // Cast a (Object) para evitar error de ambigüedad
            messagingTemplate.convertAndSend("/topic/user/consulted", (Object) userConsultationData);
            
            logger.info("Notificación WebSocket de consulta de usuario específico enviada exitosamente");
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket de consulta de usuario específico", e);
        }
    }

    /**
     * Notifica a todos los clientes sobre un usuario actualizado
     */
    public void notifyUserUpdated(Map<String, Object> userUpdateData) {
        try {
            logger.info("Enviando notificación WebSocket de usuario actualizado");
            
            // Cast a (Object) para evitar error de ambigüedad
            messagingTemplate.convertAndSend("/topic/users/updated", (Object) userUpdateData);
            
            logger.info("Notificación WebSocket de usuario actualizado enviada exitosamente");
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket de usuario actualizado", e);
        }
    }

    /**
     * Notifica a todos los clientes sobre un usuario eliminado
     */
    public void notifyUserDeleted(Map<String, Object> userDeleteData) {
        try {
            logger.info("Enviando notificación WebSocket de usuario eliminado");
            
            // Cast a (Object) para evitar error de ambigüedad
            messagingTemplate.convertAndSend("/topic/users/deleted", (Object) userDeleteData);
            
            logger.info("Notificación WebSocket de usuario eliminado enviada exitosamente");
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket de usuario eliminado", e);
        }
    }
}