package com.lucia.api.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDetail<T> {

    private String title;
    private int status;
    private String detail;
    private T data;

    public static <T> ResponseDetail<T> success(String title, String detail, T data) {
        ResponseDetail<T> rd = new ResponseDetail<>();
        rd.setTitle(title);
        rd.setStatus(200);
        rd.setDetail(detail);
        rd.setData(data);
        return rd;
    }

    public static ResponseDetail<Void> badRequest(String detail) {
        return error(400, "Datos inválidos", detail);
    }

    public static ResponseDetail<Void> unauthorized(String detail) {
        return error(401, "No autorizado", detail);
    }

    public static ResponseDetail<Void> forbidden(String detail) {
        return error(403, "Prohibido", detail);
    }

    public static ResponseDetail<Void> notFound(String detail) {
        return error(404, "Recurso no encontrado", detail);
    }

    public static ResponseDetail<Void> conflict(String detail) {
        return error(409, "Conflicto", detail);
    }

    public static ResponseDetail<Void> unprocessableEntity(String detail) {
        return error(422, "Entidad no procesable", detail);
    }

    public static ResponseDetail<Void> internalError(String detail) {
        return error(500, "Error interno del servidor", detail);
    }

    private static ResponseDetail<Void> error(int status, String title, String detail) {
        ResponseDetail<Void> rd = new ResponseDetail<>();
        rd.setTitle(title);
        rd.setStatus(status);
        rd.setDetail(detail);
        rd.setData(null);
        return rd;
    }

    public static <T> ResponseEntity<ResponseDetail<T>> ok(ResponseDetail<T> body) {
        return ResponseEntity.status(body.getStatus())
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .body(body);
    }
}
