package com.lucia.api.model.dto.response;

import com.lucia.api.model.dto.Appointment.AppointmentResponseDTO;
import com.lucia.api.model.dto.Call.CallResponseDTO;
import com.lucia.api.model.dto.Contact.ContactResponseDTO;
import com.lucia.api.model.dto.Referral.ReferralResponseDTO;
import com.lucia.api.model.dto.Request.RequestResponseDTO;
import com.lucia.api.model.dto.Stat.StatsAllResponseDTO;
import com.lucia.api.model.dto.Stat.StatsResponseDTO;
import com.lucia.api.model.dto.User.AuthResponse;
import com.lucia.api.model.dto.User.UserResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

/**
 * Esquemas OpenAPI para documentar envolturas {@code ResponseDetail<T>} por endpoint.
 */
public final class ApiResponseSchemas {

    private ApiResponseSchemas() {
    }

    @Schema(description = "Respuesta: usuario registrado")
    public static class UserSignUp {
        public String title;
        public int status;
        public String detail;
        public UserResponseDTO data;
    }

    @Schema(description = "Respuesta: inicio de sesión")
    public static class UserSignIn {
        public String title;
        public int status;
        public String detail;
        public AuthResponse data;
    }

    @Schema(description = "Respuesta: usuario actualizado")
    public static class UserUpdate {
        public String title;
        public int status;
        public String detail;
        public UserResponseDTO data;
    }

    @Schema(description = "Respuesta: cierre de sesión")
    public static class UserLogout {
        public String title;
        public int status;
        public String detail;
        public Map<String, String> data;
    }

    @Schema(description = "Respuesta: usuario eliminado")
    public static class UserDelete {
        public String title;
        public int status;
        public String detail;
        public Map<String, Boolean> data;
    }

    @Schema(description = "Respuesta: solicitud creada")
    public static class RequestCreate {
        public String title;
        public int status;
        public String detail;
        public RequestResponseDTO data;
    }

    @Schema(description = "Respuesta: lista de solicitudes")
    public static class RequestList {
        public String title;
        public int status;
        public String detail;
        public List<RequestResponseDTO> data;
    }

    @Schema(description = "Respuesta: solicitud por id")
    public static class RequestById {
        public String title;
        public int status;
        public String detail;
        public RequestResponseDTO data;
    }

    @Schema(description = "Respuesta: solicitud actualizada")
    public static class RequestUpdate {
        public String title;
        public int status;
        public String detail;
        public RequestResponseDTO data;
    }

    @Schema(description = "Respuesta: solicitud eliminada")
    public static class RequestDelete {
        public String title;
        public int status;
        public String detail;
        public Map<String, Boolean> data;
    }

    @Schema(description = "Respuesta: llamada creada")
    public static class CallCreate {
        public String title;
        public int status;
        public String detail;
        public CallResponseDTO data;
    }

    @Schema(description = "Respuesta: lista de llamadas")
    public static class CallList {
        public String title;
        public int status;
        public String detail;
        public List<CallResponseDTO> data;
    }

    @Schema(description = "Respuesta: llamada por id")
    public static class CallById {
        public String title;
        public int status;
        public String detail;
        public CallResponseDTO data;
    }

    @Schema(description = "Respuesta: llamada actualizada")
    public static class CallUpdate {
        public String title;
        public int status;
        public String detail;
        public CallResponseDTO data;
    }

    @Schema(description = "Respuesta: llamada eliminada")
    public static class CallDelete {
        public String title;
        public int status;
        public String detail;
        public Map<String, Boolean> data;
    }

    @Schema(description = "Respuesta: cita creada")
    public static class AppointmentCreate {
        public String title;
        public int status;
        public String detail;
        public AppointmentResponseDTO data;
    }

    @Schema(description = "Respuesta: lista de citas")
    public static class AppointmentList {
        public String title;
        public int status;
        public String detail;
        public List<AppointmentResponseDTO> data;
    }

    @Schema(description = "Respuesta: cita por id")
    public static class AppointmentById {
        public String title;
        public int status;
        public String detail;
        public AppointmentResponseDTO data;
    }

    @Schema(description = "Respuesta: cita actualizada")
    public static class AppointmentUpdate {
        public String title;
        public int status;
        public String detail;
        public AppointmentResponseDTO data;
    }

    @Schema(description = "Respuesta: cita eliminada")
    public static class AppointmentDelete {
        public String title;
        public int status;
        public String detail;
        public Map<String, Boolean> data;
    }

    @Schema(description = "Respuesta: contacto creado")
    public static class ContactCreate {
        public String title;
        public int status;
        public String detail;
        public ContactResponseDTO data;
    }

    @Schema(description = "Respuesta: lista de contactos")
    public static class ContactList {
        public String title;
        public int status;
        public String detail;
        public List<ContactResponseDTO> data;
    }

    @Schema(description = "Respuesta: contacto por id")
    public static class ContactById {
        public String title;
        public int status;
        public String detail;
        public ContactResponseDTO data;
    }

    @Schema(description = "Respuesta: contacto actualizado")
    public static class ContactUpdate {
        public String title;
        public int status;
        public String detail;
        public ContactResponseDTO data;
    }

    @Schema(description = "Respuesta: contacto eliminado")
    public static class ContactDelete {
        public String title;
        public int status;
        public String detail;
        public Map<String, Boolean> data;
    }

    @Schema(description = "Respuesta: referido creado")
    public static class ReferralCreate {
        public String title;
        public int status;
        public String detail;
        public ReferralResponseDTO data;
    }

    @Schema(description = "Respuesta: lista de referidos")
    public static class ReferralList {
        public String title;
        public int status;
        public String detail;
        public List<ReferralResponseDTO> data;
    }

    @Schema(description = "Respuesta: referido por id")
    public static class ReferralById {
        public String title;
        public int status;
        public String detail;
        public ReferralResponseDTO data;
    }

    @Schema(description = "Respuesta: referido actualizado")
    public static class ReferralUpdate {
        public String title;
        public int status;
        public String detail;
        public ReferralResponseDTO data;
    }

    @Schema(description = "Respuesta: referido eliminado")
    public static class ReferralDelete {
        public String title;
        public int status;
        public String detail;
        public Map<String, Boolean> data;
    }

    @Schema(description = "Respuesta: estadísticas agregadas")
    public static class StatsSummary {
        public String title;
        public int status;
        public String detail;
        public StatsResponseDTO data;
    }

    @Schema(description = "Respuesta: datos crudos de estadísticas")
    public static class StatsAllRaw {
        public String title;
        public int status;
        public String detail;
        public StatsAllResponseDTO data;
    }

    @Schema(description = "Respuesta: estado de WebSocket")
    public static class WebSocketStatus {
        public String title;
        public int status;
        public String detail;
        public Map<String, Object> data;
    }
}
