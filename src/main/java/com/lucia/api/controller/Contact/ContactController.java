package com.lucia.api.controller.Contact;

import com.lucia.api.model.dto.Contact.ContactListResponseDTO;
import com.lucia.api.model.dto.Contact.ContactRequestDTO;
import com.lucia.api.model.dto.Contact.ContactResponseDTO;
import com.lucia.api.model.dto.response.ApiResponseSchemas;
import com.lucia.api.model.dto.response.ResponseDetail;
import com.lucia.api.model.entity.Contact;
import com.lucia.api.service.Contact.ContactService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Contacto creado",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.ContactCreate.class))))
    @PostMapping
    public ResponseEntity<ResponseDetail<ContactResponseDTO>> create(@Valid @RequestBody ContactRequestDTO request) {
        Contact contact = contactService.create(request);
        return ResponseDetail.ok(ResponseDetail.success(
                "Contacto creado",
                "El contacto se registró correctamente.",
                contactService.toResponseDTO(contact)));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Contacto encontrado",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.ContactById.class))))
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDetail<ContactResponseDTO>> get(@PathVariable @Min(1) Long id) {
        Contact contact = contactService.getById(id);
        return ResponseDetail.ok(ResponseDetail.success(
                "Contacto encontrado",
                "Detalle del contacto obtenido correctamente.",
                contactService.toResponseDTO(contact)));
    }

    @ApiResponses(
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista paginada de contactos (query: page, per_page)"))
    @GetMapping
    public ResponseEntity<ResponseDetail<ContactListResponseDTO>> listContacts(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(name = "per_page", defaultValue = "10") @Min(1) int perPage) {
        ContactListResponseDTO body = contactService.listPaged(page, perPage);
        return ResponseDetail.ok(ResponseDetail.success(
                "Contactos obtenidos",
                "Lista paginada de contactos.",
                body));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Contacto actualizado",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.ContactUpdate.class))))
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDetail<ContactResponseDTO>> update(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody ContactRequestDTO request) {
        Contact contact = contactService.update(id, request);
        return ResponseDetail.ok(ResponseDetail.success(
                "Contacto actualizado",
                "Los cambios se guardaron correctamente.",
                contactService.toResponseDTO(contact)));
    }

    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Contacto eliminado",
            content = @Content(schema = @Schema(implementation = ApiResponseSchemas.ContactDelete.class))))
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDetail<Map<String, Boolean>>> delete(@PathVariable @Min(1) Long id) {
        contactService.delete(id);
        return ResponseDetail.ok(ResponseDetail.success(
                "Contacto eliminado",
                "El contacto fue eliminado del sistema.",
                Map.of("deleted", true)));
    }
}
