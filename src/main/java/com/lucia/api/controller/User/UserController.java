package com.lucia.api.controller.User;

import com.lucia.api.model.dto.User.AuthResponse;
import com.lucia.api.model.dto.User.LoginRequestDTO;
import com.lucia.api.model.dto.User.NavigationResponseDTO;
import com.lucia.api.model.dto.User.UserListResponseDTO;
import com.lucia.api.model.dto.User.UserRequestDTO;
import com.lucia.api.model.dto.User.UserResponseDTO;
import com.lucia.api.model.dto.User.UserSummaryDTO;
import com.lucia.api.model.dto.User.UserTenantPatchDTO;
import com.lucia.api.model.dto.response.ApiResponseSchemas;
import com.lucia.api.model.dto.response.ResponseDetail;
import com.lucia.api.model.entity.User;
import com.lucia.api.service.User.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador REST para gestionar usuarios del sistema.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(security = {})
    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Usuario registrado",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.UserSignUp.class))))
    @PostMapping(value = "/signup", consumes = "multipart/form-data")
    public ResponseEntity<ResponseDetail<UserResponseDTO>> signUp(
            @RequestPart("email") String email,
            @RequestPart("password") String password,
            @RequestPart("name") String name,
            @RequestPart("paternalSurname") String paternalSurname,
            @RequestPart(value = "maternalSurname", required = false) String maternalSurname,
            @RequestPart("phone") String phone,
            @RequestPart(value = "role", required = false) String role,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        User.Role resolvedRole = (role == null || role.isBlank())
                ? User.Role.SELLER
                : User.Role.fromPersistedOrApiName(role);
        UserRequestDTO request = UserRequestDTO.builder()
                .email(email)
                .password(password)
                .name(name)
                .paternalSurname(paternalSurname)
                .maternalSurname(maternalSurname != null ? maternalSurname : "")
                .phone(phone)
                .role(resolvedRole)
                .build();
        UserResponseDTO response = userService.signUp(request, profileImage);
        return ResponseDetail.ok(ResponseDetail.success(
                "Usuario registrado",
                "La cuenta se creó correctamente.",
                response));
    }

    @Operation(security = {})
    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Sesión iniciada",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.UserSignIn.class))))
    @PostMapping("/signin")
    public ResponseEntity<ResponseDetail<AuthResponse>> signIn(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponse response = userService.signIn(request.getEmail(), request.getPassword());
        return ResponseDetail.ok(ResponseDetail.success(
                "Sesión iniciada",
                "Autenticación correcta.",
                response));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Navegación del dashboard (por tenant y rol)",
            content = @Content(schema = @Schema(implementation = NavigationResponseDTO.class))))
    @GetMapping("/me/navigation")
    public ResponseEntity<ResponseDetail<NavigationResponseDTO>> getMyNavigation(Authentication authentication) {
        String email = authentication.getName();
        NavigationResponseDTO dto = userService.getMyNavigation(email);
        return ResponseDetail.ok(ResponseDetail.success(
                "Navegación",
                "Secciones permitidas para tu cuenta.",
                dto));
    }

    @GetMapping
    public ResponseEntity<ResponseDetail<UserListResponseDTO>> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "per_page", required = false) Integer perPage,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String cursor,
            @RequestParam(name = "cursor_before", required = false) String cursorBefore) {
        int resolvedSize =
                perPage != null ? perPage : (limit != null ? limit : UserService.DEFAULT_PER_PAGE);
        UserListResponseDTO body = userService.listUsersForAdmin(
                page, resolvedSize, search, role, cursor, cursorBefore);
        return ResponseDetail.ok(ResponseDetail.success(
                "Usuarios",
                "Listado de usuarios del sistema.",
                body));
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ResponseDetail<UserSummaryDTO>> getUser(@PathVariable @Min(1) Long id) {
        UserSummaryDTO body = userService.getUserSummaryForAdmin(id);
        return ResponseDetail.ok(ResponseDetail.success(
                "Usuario",
                "Datos del usuario.",
                body));
    }

    @PatchMapping("/{id:\\d+}/tenant")
    public ResponseEntity<ResponseDetail<UserSummaryDTO>> patchUserTenant(
            @PathVariable @Min(1) Long id, @RequestBody UserTenantPatchDTO body) {
        UserSummaryDTO dto = userService.updateUserTenant(id, body.getTenantId(), body.getRole());
        return ResponseDetail.ok(ResponseDetail.success(
                "Tenant asignado",
                "Se actualizó la organización del usuario.",
                dto));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Usuario actualizado",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.UserUpdate.class))))
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ResponseDetail<UserResponseDTO>> updateUser(
            @PathVariable @Min(1) Long id,
            @RequestPart("email") String email,
            @RequestPart("name") String name,
            @RequestPart("paternalSurname") String paternalSurname,
            @RequestPart(value = "maternalSurname", required = false) String maternalSurname,
            @RequestPart(value = "phone", required = false) String phone,
            @RequestPart("role") String role,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        UserRequestDTO request = UserRequestDTO.builder()
                .email(email)
                .name(name)
                .paternalSurname(paternalSurname)
                .maternalSurname(maternalSurname != null ? maternalSurname : "")
                .phone(phone)
                .role(User.Role.fromPersistedOrApiName(role))
                .build();
        UserResponseDTO response = userService.updateUser(id, request, profileImage);
        return ResponseDetail.ok(ResponseDetail.success(
                "Usuario actualizado",
                "Los datos del usuario se guardaron correctamente.",
                response));
    }

    @Operation(security = {})
    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Sesión cerrada",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.UserLogout.class))))
    @PostMapping("/logout")
    public ResponseEntity<ResponseDetail<Map<String, String>>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            userService.logout(token);
        }
        return ResponseDetail.ok(ResponseDetail.success(
                "Sesión cerrada",
                "Sesión cerrada exitosamente.",
                Map.of("message", "Sesión cerrada exitosamente")));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Usuario eliminado",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.UserDelete.class))))
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDetail<Map<String, Boolean>>> deleteUser(@PathVariable @Min(1) Long id) {
        userService.deleteUser(id);
        return ResponseDetail.ok(ResponseDetail.success(
                "Usuario eliminado",
                "El usuario fue eliminado del sistema.",
                Map.of("deleted", true)));
    }
}
