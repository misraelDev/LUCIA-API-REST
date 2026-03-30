Perfecto. Aquí tienes la **documentación completa regenerada**, **genérica**, clara y con la **carpeta `model` (entity + dto)** incluida. Lista para pegar como README.

---

## Arquitectura Backend (Genérica)

Aplicación backend construida con **Spring Boot**, organizada con una **arquitectura por capas** y separación por **módulos funcionales**.

El objetivo es mantener el código **ordenado, desacoplado y fácil de extender**, independientemente del dominio o del proveedor externo con el que se integre.

---

## Estructura base de paquetes

```text id="arch"
com.example.api
├── controller
├── service
├── repository
├── model
│   ├── entity
│   └── dto
├── config
└── common
```

En **LUCIA-API-REST** el paquete raíz es `com.lucia.api`: las entidades JPA viven en `com.lucia.api.model.entity` y los DTOs (por dominio: `User`, `Call`, `Appointment`, …) en `com.lucia.api.model.dto`, con respuestas API compartidas en `com.lucia.api.model.dto.response` (`ResponseDetail`, `ApiResponseSchemas`).

---

## Regla de organización

* Cada **funcionalidad** es un **módulo**.
* Cada módulo tiene su propia carpeta en cada capa.
* No se mezclan clases de módulos distintos en el mismo paquete.
* La estructura se replica por capa.

---

## Ejemplo 1: Módulo `integration` (API externa)

### Estructura

```text id="ex-int"
com.example.api
├── controller
│   └── integration
│       └── IntegrationController.java
│
├── service
│   └── integration
│       └── IntegrationService.java
│
├── repository
│   └── integration
│       └── ExternalApiClient.java
│
└── model
    └── dto
        └── ExternalResourceDto.java
```

### Flujo

1. **Controller**

   * Recibe la petición HTTP.
   * Valida parámetros.
   * Llama al `service`.

2. **Service**

   * Orquesta la lógica.
   * Invoca al cliente HTTP.

3. **Repository**

   * Ejecuta llamadas HTTP (`WebClient`).
   * Sin lógica de negocio.

---

## Ejemplo 2: Módulo `deposit` (lógica + persistencia)

### Estructura

```text id="ex-dep"
com.example.api
├── controller
│   └── deposit
│       └── DepositController.java
│
├── service
│   └── deposit
│       └── DepositService.java
│
├── repository
│   └── deposit
│       └── DepositRepository.java
│
└── model
    ├── entity
    │   └── DepositEntity.java
    │
    └── dto
        ├── DepositRequestDto.java
        └── DepositResponseDto.java
```

### Flujo

1. **Controller**

   * Expone endpoints del módulo.
   * Usa DTOs de entrada y salida.

2. **Service**

   * Aplica reglas de negocio.
   * Mapea DTO ⇄ Entity.
   * Llama al `repository`.

3. **Repository**

   * Persistencia (JPA, JDBC, etc.).

---

## Responsabilidades por capa

| Capa         | Responsabilidad             |
| ------------ | --------------------------- |
| Controller   | HTTP, validación, DTOs      |
| Service      | Lógica y orquestación       |
| Repository   | Persistencia / integración  |
| Model.Entity | Modelo de datos             |
| Model.DTO    | Contratos de entrada/salida |
| Config       | Beans y configuración       |
| Common       | Tipos compartidos           |

---

## Reglas importantes

* ❌ No exponer `entity` en controllers
* ❌ No lógica de negocio en `controller` ni `repository`
* ❌ No mezclar módulos en un mismo paquete
* ✅ Controllers delegan
* ✅ Services coordinan
* ✅ Repositories acceden a datos o sistemas externos

---

## Flujo típico de request

```
HTTP Request
   ↓
Controller (DTO)
   ↓
Service (lógica / mapping)
   ↓
Repository (BD o API externa)
   ↓
Service
   ↓
Controller (Response DTO)
```

---

## Evolución

* Agregar nuevos módulos replicando la estructura.
* Introducir validaciones y logging.
* Soportar múltiples fuentes de datos.
* Escalar sin romper la organización existente.

---

Si quieres, puedo:

* Reducir esto a **1 sola página ultra corta**
* Adaptarlo a **estándar de equipo**
* Convertirlo en **plantilla base para nuevos proyectos**
