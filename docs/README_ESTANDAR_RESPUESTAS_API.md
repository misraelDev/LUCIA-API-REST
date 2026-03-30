# 📋 Estándar de Respuestas de API - Guía para Desarrolladores

Este documento define el estándar **obligatorio** que todos los controladores deben seguir para:
1. **Respuestas de éxito**
2. **Respuestas de error**
3. **Documentación en Swagger**

> ⚠️ **IMPORTANTE**: Este estándar es **obligatorio** y debe cumplirse en todos los endpoints nuevos o modificados.

---

## 🎯 Formato Estándar: `ResponseDetail<T>`

Todos los endpoints deben usar la clase `ResponseDetail<T>` que implementa el estándar **RFC 7807**:

```java
ResponseDetail<T> {
    String title;      // Título descriptivo
    int status;        // Código HTTP
    String detail;     // Mensaje detallado
    T data;           // Payload (solo en éxito, null en errores)
}
```

---

## ✅ 1. RESPUESTAS DE ÉXITO (Obligatorio)

### 📌 Reglas que DEBES cumplir:

#### 1.1. Usar `ResponseDetail.success()`
```java
return ok(ResponseDetail.success("Título descriptivo", "Mensaje detallado.", datos));
```

#### 1.2. Método helper `ok()` en el controlador
```java
private static <T> ResponseEntity<ResponseDetail<T>> ok(ResponseDetail<T> body) {
    return ResponseEntity.status(body.getStatus())
            .contentType(Objects.requireNonNull(APPLICATION_JSON))
            .body(body);
}
```

#### 1.3. Estructura de la respuesta
- **`title`**: Título corto y descriptivo (ej: "Depósito creado", "URL de aliado generada")
- **`status`**: Siempre `200` (se establece automáticamente)
- **`detail`**: Mensaje descriptivo completo (ej: "Depósito creado exitosamente.")
- **`data`**: Objeto con los datos de respuesta (nunca `null` en éxito)

#### 1.4. Content-Type
- **Siempre**: `application/json` (MediaType.APPLICATION_JSON)

### 📝 Ejemplos Correctos:

#### Ejemplo 1: AlliesController
```java
@PostMapping("/generate-url")
public ResponseEntity<ResponseDetail<Object>> generateAllyUrl(
    @RequestHeader(value = "Authorization") String authorization,
    @RequestBody(required = false) GenerateAllyUrlRequest request
) {
    if (request == null) {
        request = new GenerateAllyUrlRequest();
    }
    ResponseEntity<?> serviceResponse = alliesService.generateAllyUrl(authorization, request);
    return ok(ResponseDetail.success("URL de aliado generada", 
        "URL de registro generada exitosamente.", 
        serviceResponse.getBody()));
}
```

#### Ejemplo 2: InvestmentController
```java
@PostMapping("/deposits")
public ResponseEntity<ResponseDetail<DepositResponse>> createDeposit(
        @RequestBody @jakarta.validation.Valid CreateDepositRequest request) {
    DepositResponse response = investmentService.createDeposit(
            request.getUserId(),
            request.getAmount());
    return ok(ResponseDetail.success("Depósito creado", 
        "Depósito creado exitosamente.", 
        response));
}
```

#### Ejemplo 3: Con lista de datos
```java
@GetMapping("/my-collaborators")
public ResponseEntity<ResponseDetail<Object>> getMyCollaborators(
    @RequestHeader(value = "Authorization", required = false) String authorization
) {
    ResponseEntity<?> serviceResponse = alliesService.getMyCollaborators(authorization);
    return ok(ResponseDetail.success("Mis colaboradores", 
        "Lista de colaboradores obtenida exitosamente.", 
        serviceResponse.getBody()));
}
```

### ❌ Errores Comunes a Evitar:

```java
// ❌ MAL: Devolver directamente el objeto sin ResponseDetail
return ResponseEntity.ok(datos);

// ❌ MAL: Usar Map.of() para respuestas de éxito
return ResponseEntity.ok(Map.of("data", datos));

// ❌ MAL: No usar el método helper ok()
return ResponseEntity.status(200).body(ResponseDetail.success(...));

// ❌ MAL: Título o detalle genérico
return ok(ResponseDetail.success("OK", "Éxito", datos)); // Muy genérico
```

---

## ❌ 2. RESPUESTAS DE ERROR (Obligatorio)

### 📌 Reglas que DEBES cumplir:

#### 2.1. NO manejar errores en el controlador
Los errores se manejan automáticamente por `GlobalExceptionHandler`. **NO** uses try-catch en los controladores.

#### 2.2. Lanzar excepciones apropiadas
Si necesitas lanzar un error, usa `ApiException` o excepciones estándar:

```java
// Para errores personalizados
throw new ApiException(404, "Usuario no encontrado", "El usuario con ID " + id + " no existe.");

// Para validaciones
throw new IllegalArgumentException("El monto debe ser mayor que cero.");

// Para recursos no encontrados
throw new ApiException(404, "Recurso no encontrado", "El depósito con ID " + id + " no existe.");
```

#### 2.3. Estructura automática de errores
El `GlobalExceptionHandler` convierte automáticamente las excepciones a `ResponseDetail<Void>`:

- **`title`**: Título del error (ej: "Datos inválidos", "No autorizado", "Recurso no encontrado")
- **`status`**: Código HTTP de error (400, 401, 404, 422, 500, etc.)
- **`detail`**: Mensaje descriptivo del error
- **`data`**: Siempre `null` o ausente

#### 2.4. Content-Type de errores
- **Siempre**: `application/problem+json` (RFC 7807)

### 📝 Ejemplos de Manejo Automático:

#### Ejemplo 1: Validación automática (@Valid)
```java
@PostMapping("/deposits")
public ResponseEntity<ResponseDetail<DepositResponse>> createDeposit(
        @RequestBody @jakarta.validation.Valid CreateDepositRequest request) {
    // Si la validación falla, GlobalExceptionHandler devuelve automáticamente:
    // {
    //   "title": "Entidad no procesable",
    //   "status": 422,
    //   "detail": "El monto debe ser mayor que cero."
    // }
    DepositResponse response = investmentService.createDeposit(...);
    return ok(ResponseDetail.success(...));
}
```

#### Ejemplo 2: Error en el servicio
```java
// En el servicio
public DepositResponse createDeposit(Long userId, BigDecimal amount) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ApiException(404, "Usuario no encontrado", 
            "El usuario con ID " + userId + " no existe."));
    // ...
}

// El GlobalExceptionHandler convierte automáticamente a:
// {
//   "title": "Usuario no encontrado",
//   "status": 404,
//   "detail": "El usuario con ID 123 no existe."
// }
```

### 📋 Códigos de Error Disponibles:

| Método Estático | Status | Título |
|----------------|--------|--------|
| `ResponseDetail.badRequest()` | 400 | Datos inválidos |
| `ResponseDetail.unauthorized()` | 401 | No autorizado |
| `ResponseDetail.forbidden()` | 403 | Prohibido |
| `ResponseDetail.notFound()` | 404 | Recurso no encontrado |
| `ResponseDetail.conflict()` | 409 | Conflicto |
| `ResponseDetail.unprocessableEntity()` | 422 | Entidad no procesable |
| `ResponseDetail.internalError()` | 500 | Error interno del servidor |

### ❌ Errores Comunes a Evitar:

```java
// ❌ MAL: Manejar errores en el controlador
try {
    // ...
} catch (Exception e) {
    return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
}

// ❌ MAL: Devolver ResponseDetail de error manualmente
return ResponseEntity.status(404).body(ResponseDetail.notFound("..."));

// ❌ MAL: Usar ResponseEntity.error() o similar
return ResponseEntity.badRequest().body(Map.of("error", "..."));

// ✅ BIEN: Dejar que GlobalExceptionHandler maneje los errores
public ResponseEntity<ResponseDetail<DepositResponse>> createDeposit(...) {
    // Si hay error, se lanza excepción y GlobalExceptionHandler la maneja
    DepositResponse response = investmentService.createDeposit(...);
    return ok(ResponseDetail.success(...));
}
```

---

## 📚 3. DOCUMENTACIÓN EN SWAGGER (Obligatorio)

### 📌 Reglas que DEBES cumplir:

#### 3.1. Anotar TODOS los endpoints con `@ApiResponses`
```java
@ApiResponses(@ApiResponse(
    responseCode = "200", 
    description = "Descripción breve", 
    content = @Content(schema = @Schema(implementation = ApiResponseSchemas.NombreDelEsquema.class))
))
```

#### 3.2. Crear esquema en `ApiResponseSchemas`
Para cada endpoint nuevo, crea una clase estática en `ApiResponseSchemas.java`:

```java
@Schema(description = "Respuesta: descripción del endpoint")
public static class NombreDelEsquema {
    public String title;
    public int status;
    public String detail;
    public TipoDelPayload data; // Tipo específico del payload
}
```

#### 3.3. Usar el esquema en el controlador
```java
@ApiResponses(@ApiResponse(
    responseCode = "200", 
    description = "URL generada", 
    content = @Content(schema = @Schema(implementation = ApiResponseSchemas.GenerateAllyUrl.class))
))
@PostMapping("/generate-url")
public ResponseEntity<ResponseDetail<Object>> generateAllyUrl(...) {
    // ...
}
```

### 📝 Ejemplos Correctos:

#### Ejemplo 1: AlliesController
```java
// En ApiResponseSchemas.java
@Schema(description = "Respuesta: URL de aliado generada")
public static class GenerateAllyUrl {
    public String title;
    public int status;
    public String detail;
    public Object data; // GenerateAllyUrlResponse con url, referrerId, etc.
}

// En AlliesController.java
@ApiResponses(@ApiResponse(
    responseCode = "200", 
    description = "URL generada", 
    content = @Content(schema = @Schema(implementation = ApiResponseSchemas.GenerateAllyUrl.class))
))
@PostMapping("/generate-url")
public ResponseEntity<ResponseDetail<Object>> generateAllyUrl(...) {
    // ...
}
```

#### Ejemplo 2: InvestmentController
```java
// En ApiResponseSchemas.java
@Schema(description = "Respuesta: depósito creado")
public static class CreateDeposit {
    public String title;
    public int status;
    public String detail;
    public DepositResponse data;
}

// En InvestmentController.java
@ApiResponses(@ApiResponse(
    responseCode = "200", 
    description = "OK", 
    content = @Content(schema = @Schema(implementation = ApiResponseSchemas.CreateDeposit.class))
))
@PostMapping("/deposits")
public ResponseEntity<ResponseDetail<DepositResponse>> createDeposit(...) {
    // ...
}
```

#### Ejemplo 3: Con lista de objetos
```java
// En ApiResponseSchemas.java
@Schema(description = "Respuesta: lista de inversiones")
public static class GetAllInvestments {
    public String title;
    public int status;
    public String detail;
    public List<InvestmentSummaryResponse> data;
}

// En InvestmentController.java
@ApiResponses(@ApiResponse(
    responseCode = "200", 
    description = "OK", 
    content = @Content(schema = @Schema(implementation = ApiResponseSchemas.GetAllInvestments.class))
))
@GetMapping
public ResponseEntity<ResponseDetail<List<InvestmentSummaryResponse>>> getAllInvestmentsSummary() {
    // ...
}
```

### 📋 Convenciones de Nomenclatura:

- **Esquema**: PascalCase descriptivo (ej: `CreateDeposit`, `GetAllInvestments`, `GenerateAllyUrl`)
- **Description en @Schema**: "Respuesta: [descripción]" (ej: "Respuesta: depósito creado")
- **Description en @ApiResponse**: Breve y clara (ej: "OK", "URL generada", "Depósito creado")

### ❌ Errores Comunes a Evitar:

```java
// ❌ MAL: Sin anotación @ApiResponses
@PostMapping("/deposits")
public ResponseEntity<ResponseDetail<DepositResponse>> createDeposit(...) {
    // ...
}

// ❌ MAL: Esquema genérico o incorrecto
@ApiResponses(@ApiResponse(responseCode = "200", description = "OK"))
// Falta el content con el schema

// ❌ MAL: Esquema con tipo incorrecto
public static class CreateDeposit {
    public Object data; // Muy genérico, debería ser DepositResponse
}

// ❌ MAL: Nombre del esquema no descriptivo
public static class Response1 { // No descriptivo
    // ...
}
```

---

## 🔍 Checklist de Implementación

Antes de hacer commit, verifica:

### ✅ Respuestas de Éxito
- [ ] Usas `ResponseDetail.success()` con título y detalle descriptivos
- [ ] Usas el método helper `ok()` del controlador
- [ ] El `data` contiene el objeto de respuesta (no `null`)
- [ ] Content-Type es `application/json`

### ✅ Respuestas de Error
- [ ] NO manejas errores en el controlador (dejas que `GlobalExceptionHandler` lo haga)
- [ ] Lanzas excepciones apropiadas (`ApiException`, `IllegalArgumentException`, etc.)
- [ ] Los mensajes de error son claros y descriptivos

### ✅ Documentación Swagger
- [ ] Tienes `@ApiResponses` en cada endpoint
- [ ] Creaste el esquema correspondiente en `ApiResponseSchemas`
- [ ] El esquema tiene el tipo correcto para `data`
- [ ] La descripción es clara y descriptiva

---

## 📖 Referencias

- **Clase base**: `com.lucia.api.model.dto.response.ResponseDetail`
- **Esquemas**: `com.lucia.api.model.dto.response.ApiResponseSchemas`
- **Manejo de errores**: `com.lucia.api.exception.GlobalExceptionHandler`
- **Excepción API**: `com.lucia.api.exception.ApiException`
- **Ejemplos de referencia**: 
  - `AlliesController.java`
  - `InvestmentController.java`
  - `CourseController.java`

---

## 💡 Notas Importantes

1. **Los errores NO se documentan en Swagger** - Se infieren automáticamente desde `GlobalExceptionHandler`
2. **Solo documenta respuestas 200** - Los errores se manejan globalmente
3. **Mantén consistencia** - Usa los mismos patrones que los controladores de referencia
4. **Títulos y detalles descriptivos** - Evita mensajes genéricos como "OK" o "Éxito"

---

**Última actualización**: Febrero 2026
