# LUCIA API REST

API REST de **Lucia** construida con **Spring Boot 4** y **Java 17**. Expone endpoints para usuarios, contactos, citas, llamadas, estadísticas, referidos, administración de tenants y notificaciones WebSocket, con persistencia en **PostgreSQL**.

## Requisitos

- **JDK 17**
- **Maven** (incluye wrapper: `mvnw` / `mvnw.cmd`)
- **PostgreSQL** accesible desde la máquina o contenedor donde corre la API

## Configuración

### Variables de entorno y archivos `.env`

La aplicación puede leer configuración desde **variables de entorno** del sistema y desde archivos **`.env`** / **`.env.local`** en la raíz del proyecto (no deben subirse a Git; están en `.gitignore`).

Variables típicas de base de datos (o equivalentes `SPRING_DATASOURCE_*`):

| Variable | Descripción |
|----------|-------------|
| `DB_HOST` | Host de PostgreSQL |
| `DB_PORT` | Puerto (ej. `5432`) |
| `DB_NAME` | Nombre de la base de datos |
| `DB_USERNAME` | Usuario |
| `DB_PASSWORD` | Contraseña |

Otras variables habituales (según tu despliegue): JWT, logging, `SPRING_SQL_INIT_MODE`, etc.

En el arranque, `ApiApplication` carga `.env` y puede hidratar `SPRING_DATASOURCE_*` a partir de `DB_*` cuando corresponda.

### `application.properties`

En `src/main/resources/application.properties` se definen JPA, multipart, JWT, rutas de medios y **SpringDoc** (OpenAPI). Ajusta credenciales y hosts **solo vía entorno**, no en commits.

## Ejecución local

Desde la raíz del repositorio:

```bash
./mvnw spring-boot:run
```

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Por defecto el servidor suele levantar en el puerto configurado en Spring (revisa logs al arrancar; en muchos entornos es **8080**).

## Documentación OpenAPI (Swagger)

Con la app en marcha (rutas según `application.properties`):

- **OpenAPI JSON**: `/v3/api-docs`
- **Swagger UI**: `/swagger-ui.html`

## Empaquetado y Docker

- **JAR**: `.\mvnw.cmd package` (artefacto bajo `target/`)
- **Docker**: existe un `Dockerfile` en la raíz del repo para construir la imagen de la API.

## Estructura del proyecto

Árbol **completo** del repositorio (todas las carpetas y archivos bajo la raíz), en formato tipo `tree` de Windows. **No** se listan (por volumen, artefactos o secretos):

- `target/` (salida de Maven; se genera al compilar)
- `.git/`
- `.env`, `.env.local`

```text
LUCIA-API-REST
+--- .mvn
|   \--- wrapper
|       \--- maven-wrapper.properties
+--- docs
|   +--- README.COMPILE.md
|   +--- README_ARQUITECTURA_CAPAS.md
|   +--- README_COMMITS_Y_RAMAS.md
|   +--- README_ESTANDAR_RESPUESTAS_API.md
|   \--- WEBSOCKET_README.md
+--- mediafiles
+--- sql
|   +--- example_admin_navigation_with_tenants.sql
|   +--- example_navigation_user_home_only.sql
|   +--- seed_demo_marzo_2026.sql
|   \--- tenants_two_inserts_one_update.sql
+--- src
|   +--- main
|   |   +--- java
|   |   |   \--- com
|   |   |       \--- lucia
|   |   |           \--- api
|   |   |               +--- config
|   |   |               |   +--- JacksonConfig.java
|   |   |               |   +--- OpenApiConfig.java
|   |   |               |   +--- SecurityConfig.java
|   |   |               |   +--- WebSocketConfig.java
|   |   |               |   \--- WebSocketInterceptor.java
|   |   |               +--- controller
|   |   |               |   +--- admin
|   |   |               |   |   \--- TenantAdminController.java
|   |   |               |   +--- Appointment
|   |   |               |   |   \--- AppointmentController.java
|   |   |               |   +--- Call
|   |   |               |   |   \--- CallController.java
|   |   |               |   +--- Contact
|   |   |               |   |   \--- ContactController.java
|   |   |               |   +--- Referral
|   |   |               |   |   \--- ReferralController.java
|   |   |               |   +--- Request
|   |   |               |   |   \--- RequestController.java
|   |   |               |   +--- Stats
|   |   |               |   |   \--- StatsController.java
|   |   |               |   +--- User
|   |   |               |   |   \--- UserController.java
|   |   |               |   \--- WebSocket
|   |   |               |       \--- WebSocketController.java
|   |   |               +--- exception
|   |   |               |   +--- ApiError.java
|   |   |               |   +--- ApiException.java
|   |   |               |   +--- BadRequestException.java
|   |   |               |   +--- BusinessConflictException.java
|   |   |               |   +--- ErrorResponse.java
|   |   |               |   +--- ExternalServiceException.java
|   |   |               |   +--- ForbiddenException.java
|   |   |               |   +--- GlobalExceptionHandler.java
|   |   |               |   +--- InvalidCredentialsException.java
|   |   |               |   +--- ResourceNotFoundException.java
|   |   |               |   \--- UnauthorizedException.java
|   |   |               +--- http
|   |   |               |   \--- ProblemJson.java
|   |   |               +--- model
|   |   |               |   +--- dto
|   |   |               |   |   +--- admin
|   |   |               |   |   |   +--- TenantAdminCreateRequest.java
|   |   |               |   |   |   +--- TenantAdminDTO.java
|   |   |               |   |   |   \--- TenantAdminUpdateRequest.java
|   |   |               |   |   +--- Appointment
|   |   |               |   |   |   +--- AppointmentDto.java
|   |   |               |   |   |   +--- AppointmentRequestDTO.java
|   |   |               |   |   |   \--- AppointmentResponseDTO.java
|   |   |               |   |   +--- Call
|   |   |               |   |   |   +--- CallDto.java
|   |   |               |   |   |   +--- CallListResponseDTO.java
|   |   |               |   |   |   +--- CallRequestDTO.java
|   |   |               |   |   |   \--- CallResponseDTO.java
|   |   |               |   |   +--- common
|   |   |               |   |   |   \--- PageMetaDTO.java
|   |   |               |   |   +--- Contact
|   |   |               |   |   |   +--- ContactDto.java
|   |   |               |   |   |   +--- ContactListResponseDTO.java
|   |   |               |   |   |   +--- ContactRequestDTO.java
|   |   |               |   |   |   \--- ContactResponseDTO.java
|   |   |               |   |   +--- Referral
|   |   |               |   |   |   +--- ReferralDto.java
|   |   |               |   |   |   +--- ReferralRequestDTO.java
|   |   |               |   |   |   \--- ReferralResponseDTO.java
|   |   |               |   |   +--- Request
|   |   |               |   |   |   +--- RequestDto.java
|   |   |               |   |   |   +--- RequestRequestDTO.java
|   |   |               |   |   |   \--- RequestResponseDTO.java
|   |   |               |   |   +--- response
|   |   |               |   |   |   +--- ApiResponseSchemas.java
|   |   |               |   |   |   \--- ResponseDetail.java
|   |   |               |   |   +--- Stat
|   |   |               |   |   |   +--- StatResponseDto.java
|   |   |               |   |   |   +--- StatsAllResponseDTO.java
|   |   |               |   |   |   \--- StatsResponseDTO.java
|   |   |               |   |   \--- User
|   |   |               |   |       +--- AuthResponse.java
|   |   |               |   |       +--- LoginRequestDTO.java
|   |   |               |   |       +--- NavigationResponseDTO.java
|   |   |               |   |       +--- TenantBriefDTO.java
|   |   |               |   |       +--- UserDto.java
|   |   |               |   |       +--- UserListMetaDTO.java
|   |   |               |   |       +--- UserListResponseDTO.java
|   |   |               |   |       +--- UserRequestDTO.java
|   |   |               |   |       +--- UserResponseDTO.java
|   |   |               |   |       +--- UserSummaryDTO.java
|   |   |               |   |       \--- UserTenantPatchDTO.java
|   |   |               |   \--- entity
|   |   |               |       +--- Appointment.java
|   |   |               |       +--- Call.java
|   |   |               |       +--- Contact.java
|   |   |               |       +--- Referral.java
|   |   |               |       +--- Request.java
|   |   |               |       +--- Tenant.java
|   |   |               |       +--- User.java
|   |   |               |       \--- UserRoleConverter.java
|   |   |               +--- repository
|   |   |               |   +--- Appointment
|   |   |               |   |   \--- AppointmentRepository.java
|   |   |               |   +--- Call
|   |   |               |   |   \--- CallRepository.java
|   |   |               |   +--- Contact
|   |   |               |   |   \--- ContactRepository.java
|   |   |               |   +--- Referral
|   |   |               |   |   \--- ReferralRepository.java
|   |   |               |   +--- Request
|   |   |               |   |   \--- RequestRepository.java
|   |   |               |   +--- Tenant
|   |   |               |   |   \--- TenantRepository.java
|   |   |               |   \--- User
|   |   |               |       +--- UserRepository.java
|   |   |               |       \--- UserSpecifications.java
|   |   |               +--- security
|   |   |               |   +--- JwtAuthenticationFilter.java
|   |   |               |   +--- JwtUtil.java
|   |   |               |   \--- UserDetailsServiceImpl.java
|   |   |               +--- service
|   |   |               |   +--- Appointment
|   |   |               |   |   \--- AppointmentService.java
|   |   |               |   +--- Cache
|   |   |               |   |   \--- TokenCacheService.java
|   |   |               |   +--- Call
|   |   |               |   |   \--- CallService.java
|   |   |               |   +--- Contact
|   |   |               |   |   \--- ContactService.java
|   |   |               |   +--- File
|   |   |               |   |   +--- FileSystemStorageService.java
|   |   |               |   |   \--- StorageService.java
|   |   |               |   +--- navigation
|   |   |               |   |   \--- DashboardNavigationService.java
|   |   |               |   +--- Referral
|   |   |               |   |   \--- ReferralService.java
|   |   |               |   +--- Request
|   |   |               |   |   \--- RequestService.java
|   |   |               |   +--- Stat
|   |   |               |   |   \--- StatsService.java
|   |   |               |   +--- Tenant
|   |   |               |   |   \--- TenantAdminService.java
|   |   |               |   +--- User
|   |   |               |   |   +--- UserCursorCodec.java
|   |   |               |   |   \--- UserService.java
|   |   |               |   \--- WebSocket
|   |   |               |       \--- WebSocketNotificationService.java
|   |   |               \--- ApiApplication.java
|   |   \--- resources
|   |       +--- application.properties
|   |       \--- application-prod.properties
|   \--- test
|       \--- java
|           \--- com
|               \--- lucia
|                   \--- api
|                       \--- ApiApplicationTests.java
+--- .gitattributes
+--- .gitignore
+--- Dockerfile
+--- mvnw
+--- mvnw.cmd
+--- pom.xml
\--- README.md
```

## Documentación adicional

En `docs/` encontrarás guías como arquitectura por capas, estándar de respuestas y convenciones de commits (si aplica).
