package com.lucia.api.exception;

/**
 * Error de API con código HTTP y título para mapeo RFC 7807 vía {@link GlobalExceptionHandler}.
 */
public class ApiException extends RuntimeException {

    private final int status;
    private final String title;

    public ApiException(int status, String title, String detail) {
        super(detail != null ? detail : title);
        this.status = status;
        this.title = title != null ? title : "Error";
    }

    public int getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return getMessage();
    }
}
