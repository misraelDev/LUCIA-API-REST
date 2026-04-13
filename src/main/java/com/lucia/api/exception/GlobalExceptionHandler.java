package com.lucia.api.exception;

import com.lucia.api.http.ProblemJson;
import com.lucia.api.model.dto.response.ResponseDetail;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ResponseDetail<Void>> handleApiException(ApiException ex) {
        logger.debug("ApiException: {} - {}", ex.getTitle(), ex.getDetail());
        HttpStatusCode statusCode = HttpStatusCode.valueOf(ex.getStatus());
        return problem(statusCode, ex.getTitle(), ex.getDetail());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDetail<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.info("Validation error occurred: {}", ex.getMessage());

        List<String> details = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = error instanceof FieldError fieldError
                            ? fieldError.getField()
                            : error.getObjectName();
                    return "%s: %s".formatted(fieldName, error.getDefaultMessage());
                })
                .collect(Collectors.toList());

        String detail = String.join("; ", details);
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "Entidad no procesable", detail);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDetail<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.info("Resource not found: {}", ex.getMessage());
        return problem(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ResponseDetail<Void>> handleInvalidCredentials(InvalidCredentialsException ex) {
        logger.warn("Invalid credentials: {}", ex.getMessage());
        return problem(HttpStatus.UNAUTHORIZED, "No autorizado", ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ResponseDetail<Void>> handleUnauthorized(UnauthorizedException ex) {
        logger.warn("Unauthorized access: {}", ex.getMessage());
        return problem(HttpStatus.UNAUTHORIZED, "No autorizado", ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseDetail<Void>> handleForbidden(ForbiddenException ex) {
        logger.warn("Forbidden access: {}", ex.getMessage());
        return problem(HttpStatus.FORBIDDEN, "Prohibido", ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseDetail<Void>> handleBadRequest(BadRequestException ex) {
        logger.info("Bad request: {}", ex.getMessage());

        String message = ex.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String title = "Datos inválidos";

        if (message != null) {
            String lowerMessage = message.toLowerCase();
            if (lowerMessage.contains("no está registrado")
                    || lowerMessage.contains("incorrecta")
                    || lowerMessage.contains("credenciales")
                    || lowerMessage.contains("no autorizado")) {
                status = HttpStatus.UNAUTHORIZED;
                title = "No autorizado";
            } else if (lowerMessage.contains("permiso")
                    || lowerMessage.contains("prohibido")
                    || lowerMessage.contains("forbidden")) {
                status = HttpStatus.FORBIDDEN;
                title = "Prohibido";
            }
        }

        return problem(status, title, message);
    }

    @ExceptionHandler(BusinessConflictException.class)
    public ResponseEntity<ResponseDetail<Void>> handleBusinessConflict(BusinessConflictException ex) {
        logger.warn("Business conflict: {}", ex.getMessage());
        return problem(HttpStatus.CONFLICT, "Conflicto", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseDetail<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.error("Data integrity violation: {}", ex.getMessage());

        IntegrityViolationInfo violationInfo = extractIntegrityViolationInfo(ex.getMessage());
        return problem(HttpStatus.CONFLICT, "Conflicto", violationInfo.getMessage());
    }

    private IntegrityViolationInfo extractIntegrityViolationInfo(String errorMessage) {
        if (errorMessage == null) {
            return new IntegrityViolationInfo(
                    "Error de integridad de datos en la base de datos",
                    null);
        }

        String lowerMessage = errorMessage.toLowerCase();
        String fieldName = extractFieldNameFromErrorMessage(errorMessage);

        if (lowerMessage.contains("unique") || lowerMessage.contains("duplicate")) {
            String displayField = toDisplayFieldName(fieldName);
            String message = fieldName != null
                    ? String.format("El valor del campo '%s' ya existe en el sistema y debe ser único", displayField)
                    : "El valor proporcionado ya existe en el sistema y debe ser único";
            return new IntegrityViolationInfo(message, fieldName);
        }

        if (lowerMessage.contains("foreign key") || lowerMessage.contains("referencia")) {
            return new IntegrityViolationInfo(
                    "No se puede realizar la operación debido a una referencia inválida",
                    null);
        }

        if (lowerMessage.contains("not null") || lowerMessage.contains("null")) {
            String message = fieldName != null
                    ? String.format("El campo '%s' es obligatorio y no puede ser nulo", fieldName)
                    : "Uno o más campos obligatorios no pueden ser nulos";
            return new IntegrityViolationInfo(message, fieldName);
        }

        return new IntegrityViolationInfo(
                "Error de integridad de datos en la base de datos",
                null);
    }

    private String extractFieldNameFromErrorMessage(String errorMessage) {
        if (errorMessage == null) {
            return null;
        }

        Pattern keyPattern = Pattern.compile(
                "Key \\(([^)]+)\\)=",
                Pattern.CASE_INSENSITIVE);
        Matcher keyMatcher = keyPattern.matcher(errorMessage);
        if (keyMatcher.find()) {
            return keyMatcher.group(1);
        }

        Pattern postgresConstraintPattern = Pattern.compile(
                "unique constraint \"([^\"]+)\"",
                Pattern.CASE_INSENSITIVE);
        Matcher postgresMatcher = postgresConstraintPattern.matcher(errorMessage);
        if (postgresMatcher.find()) {
            return extractFieldFromConstraintName(postgresMatcher.group(1));
        }

        Pattern mysqlKeyPattern = Pattern.compile(
                "for key ['\"]([^'\"]+)['\"]",
                Pattern.CASE_INSENSITIVE);
        Matcher mysqlMatcher = mysqlKeyPattern.matcher(errorMessage);
        if (mysqlMatcher.find()) {
            return mysqlMatcher.group(1);
        }

        Pattern columnPattern = Pattern.compile(
                "(?:column|field|campo) ['\"]([^'\"]+)['\"]",
                Pattern.CASE_INSENSITIVE);
        Matcher columnMatcher = columnPattern.matcher(errorMessage);
        if (columnMatcher.find()) {
            return columnMatcher.group(1);
        }

        return null;
    }

    private String extractFieldFromConstraintName(String constraintName) {
        if (constraintName == null || constraintName.isEmpty()) {
            return null;
        }

        if ("uk6dotkott2kjsp8vw4d0m25fb7".equalsIgnoreCase(constraintName)) {
            return "email";
        }

        String cleaned = constraintName.replaceFirst("^[^_]+_", "");
        cleaned = cleaned.replaceFirst("_(key|unique|idx|pkey|uk|pk)$", "");
        return cleaned.isEmpty() ? null : cleaned;
    }

    private String toDisplayFieldName(String fieldName) {
        if (fieldName == null || fieldName.isBlank()) {
            return "desconocido";
        }
        if ("email".equalsIgnoreCase(fieldName)) {
            return "correo electrónico";
        }
        return fieldName;
    }

    private static class IntegrityViolationInfo {
        private final String message;
        private final String fieldName;

        IntegrityViolationInfo(String message, String fieldName) {
            this.message = message;
            this.fieldName = fieldName;
        }

        public String getMessage() {
            return message;
        }

        @SuppressWarnings("unused")
        public String getFieldName() {
            return fieldName;
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDetail<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        logger.error("Illegal argument: {}", ex.getMessage());
        return problem(HttpStatus.BAD_REQUEST, "Datos inválidos", ex.getMessage());
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ResponseDetail<Void>> handleConstraintViolation(
            jakarta.validation.ConstraintViolationException ex) {
        logger.info("Constraint violation: {}", ex.getMessage());

        String detail = ex.getConstraintViolations().stream()
                .map(v -> "%s: %s".formatted(v.getPropertyPath(), v.getMessage()))
                .collect(Collectors.joining("; "));

        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "Entidad no procesable", detail);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseDetail<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        logger.warn("HTTP method not supported: {}", ex.getMessage());
        return problem(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Método no permitido",
                "El método " + ex.getMethod() + " no está permitido para este endpoint.");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ResponseDetail<Void>> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex) {
        logger.warn("Media type not supported: {}", ex.getMessage());
        return problem(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Tipo de contenido no soportado",
                "El tipo de contenido proporcionado no es compatible con este endpoint.");
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ResponseDetail<Void>> handleMissingMultipartPart(
            MissingServletRequestPartException ex) {
        logger.info("Missing multipart part: {}", ex.getRequestPartName());
        return problem(
                HttpStatus.BAD_REQUEST,
                "Datos inválidos",
                "No llegaron bien los datos del formulario. Recarga la página, completa los campos e inténtalo otra vez.");
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ResponseDetail<Void>> handleTimeout(TimeoutException ex) {
        logger.error("Operation timeout: {}", ex.getMessage());
        return problem(
                HttpStatus.REQUEST_TIMEOUT,
                "Tiempo de espera agotado",
                ex.getMessage() != null ? ex.getMessage() : "Timeout en la operación.");
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ResponseDetail<Void>> handleExternalServiceError(ExternalServiceException ex) {
        logger.error("External service error: {}", ex.getMessage(), ex);
        return problem(
                HttpStatus.BAD_GATEWAY,
                "Error de servicio externo",
                ex.getMessage() != null ? ex.getMessage() : "Error en servicio externo.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDetail<Void>> handleRuntimeException(RuntimeException ex) {
        logger.error("Runtime exception occurred: ", ex);
        String message = ex.getMessage() != null ? ex.getMessage() : "Error de ejecución";
        return problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                message + " (" + ex.getClass().getSimpleName() + ")");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDetail<Void>> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred: ", ex);
        String message = ex.getMessage() != null ? ex.getMessage() : "Ha ocurrido un error interno del servidor";
        return problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                message + " (" + ex.getClass().getSimpleName() + ")");
    }

    private ResponseEntity<ResponseDetail<Void>> problem(HttpStatusCode statusCode, String title, String detail) {
        ResponseDetail<Void> body = new ResponseDetail<>();
        body.setTitle(title);
        body.setStatus(statusCode.value());
        body.setDetail(detail != null ? detail : title);
        body.setData(null);
        return ResponseEntity.status(statusCode)
                .contentType(ProblemJson.MEDIA_TYPE)
                .body(body);
    }
}
