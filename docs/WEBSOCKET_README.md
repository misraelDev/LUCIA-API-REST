# Conexión al WebSocket de la API LUCIA

Este proyecto expone endpoints WebSocket para notificaciones y comunicación en tiempo real.

## Endpoints WebSocket

- **URL WebSocket:** `/ws`
- **Protocolo:** STOMP sobre WebSocket
- **Ejemplo de endpoint para suscripción:** `/topic/greetings`, `/topic/appointments/new`, `/topic/calls/updated`, etc.
- **Mensajes privados:** `/user/queue/reply`

## Cómo conectarse

Puedes usar cualquier cliente STOMP compatible con WebSocket, como [stomp.js](https://stomp-js.github.io/) en JavaScript, o herramientas como Postman, Insomnia, o extensiones de navegador.

### Autenticación con JWT en WebSocket/STOMP

Para autenticarte, debes enviar el token JWT en el header `Authorization` durante la conexión STOMP (en el frame CONNECT). Esto es el método estándar soportado por Spring Security.

#### Ejemplo con stomp.js:

```js
import { Client } from '@stomp/stompjs';

const client = new Client({
  brokerURL: 'ws://localhost:8080/ws',
  connectHeaders: {
    Authorization: 'Bearer TU_TOKEN_JWT_AQUI'
  },
  reconnectDelay: 5000,
});

client.onConnect = function (frame) {
  // ...suscripciones...
};

client.activate();
```

> **Nota:** No es necesario enviar el token como parámetro de URL ni en cookies, solo en el header STOMP `Authorization`.

### Ejemplo con JavaScript (stomp.js)

```js
import { Client } from '@stomp/stompjs';

const client = new Client({
  brokerURL: 'ws://localhost:8080/ws', // Cambia el host y puerto según tu despliegue
  reconnectDelay: 5000,
});

client.onConnect = function (frame) {
  // Suscribirse a un tópico
  client.subscribe('/topic/greetings', message => {
    console.log('Mensaje recibido:', message.body);
  });

  // Suscribirse a notificaciones de llamadas
  client.subscribe('/topic/calls/updated', message => {
    console.log('Llamada actualizada:', message.body);
  });
};

client.activate();
```

### Ejemplo de suscripción a mensajes privados

```js
client.subscribe('/user/queue/reply', message => {
  console.log('Mensaje privado:', message.body);
});
```

## Tópicos disponibles

- `/topic/requests/new`, `/topic/appointments/updated`, `/topic/calls/new`, ...
- `/user/{userId}/requests/new` (mensajes privados)
- `/topic/phone/{phoneNumber}/appointments/new` (por teléfono)
- `/topic/email/{email}/contacts/new` (por email)
- `/topic/contact/{contactId}/calls/new` (por contacto)

Consulta el endpoint REST `/api/ws/status` para obtener la lista completa de tópicos y endpoints disponibles.

## Envío de mensajes

Para enviar mensajes al servidor, usa los endpoints configurados con `@MessageMapping`, por ejemplo:
- `/app/hello` para mensajes generales
- `/app/private-message` para mensajes privados

Ejemplo:
```js
client.publish({
  destination: '/app/hello',
  body: '¡Hola servidor!'
});
```

## Seguridad

- Algunos tópicos pueden requerir autenticación o autorización según la configuración de seguridad del backend.
- Usa el mismo mecanismo de autenticación que para los endpoints REST (por ejemplo, JWT en el header `Authorization`).

### ⚠️ Importante sobre SockJS y seguridad

Si usas SockJS (por ejemplo, con `webSocketFactory: () => new SockJS(...)` en stomp.js), **no es posible enviar el header Authorization en la petición HTTP inicial** (`/ws/info`). Por eso, si tu backend protege `/ws/**` con autenticación, la conexión fallará con 401 Unauthorized.

**Solución recomendada:**

- Permite acceso público a `/ws/**` en la configuración de seguridad HTTP de Spring (por ejemplo, en `SecurityConfig`).
- Valida el JWT únicamente en el frame STOMP CONNECT usando un interceptor de mensajes (`ChannelInterceptor` o `HandshakeInterceptor`).
- Así, la conexión handshake será pública, pero los mensajes STOMP sí estarán autenticados.

**Ejemplo de configuración en Spring Security:**

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
  http
    .authorizeHttpRequests()
      .requestMatchers("/ws/**").permitAll() // Permitir handshake público
      .anyRequest().authenticated()
    .and()
    .csrf().disable();
}
```

Luego, en tu interceptor STOMP, valida el JWT del header Authorization del frame CONNECT.

> Si usas WebSocket puro (no SockJS), el header Authorization sí viaja en el frame CONNECT y puedes proteger `/ws/**` normalmente.

---

## Recursos útiles
- [stomp.js documentación](https://stomp-js.github.io/guide/stompjs/)
- [Spring WebSocket Reference](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket)

---

Si tienes dudas sobre los tópicos o necesitas ejemplos para otros lenguajes, consulta el código fuente o abre un issue.
