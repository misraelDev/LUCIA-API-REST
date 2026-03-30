package com.lucia.api.controller.admin;

import com.lucia.api.model.dto.admin.TenantAdminCreateRequest;
import com.lucia.api.model.dto.admin.TenantAdminDTO;
import com.lucia.api.model.dto.admin.TenantAdminUpdateRequest;
import com.lucia.api.model.dto.response.ResponseDetail;
import com.lucia.api.service.Tenant.TenantAdminService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/tenants")
@PreAuthorize("hasAuthority('admin')")
public class TenantAdminController {

    @Autowired
    private TenantAdminService tenantAdminService;

    @GetMapping
    public ResponseEntity<ResponseDetail<List<TenantAdminDTO>>> list() {
        List<TenantAdminDTO> list = tenantAdminService.listAll();
        return ResponseDetail.ok(
                ResponseDetail.success("Tenants", "Listado de organizaciones.", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDetail<TenantAdminDTO>> get(@PathVariable Long id) {
        TenantAdminDTO dto = tenantAdminService.getById(id);
        return ResponseDetail.ok(ResponseDetail.success("Tenant", "Organización encontrada.", dto));
    }

    @PostMapping
    public ResponseEntity<ResponseDetail<TenantAdminDTO>> create(
            @Valid @RequestBody TenantAdminCreateRequest request) {
        TenantAdminDTO dto = tenantAdminService.create(request);
        return ResponseDetail.ok(ResponseDetail.success("Tenant creado", "La organización se registró.", dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDetail<TenantAdminDTO>> update(
            @PathVariable Long id, @Valid @RequestBody TenantAdminUpdateRequest request) {
        TenantAdminDTO dto = tenantAdminService.update(id, request);
        return ResponseDetail.ok(ResponseDetail.success("Tenant actualizado", "Cambios guardados.", dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDetail<Void>> delete(@PathVariable Long id) {
        tenantAdminService.deleteById(id);
        return ResponseDetail.ok(
                ResponseDetail.success(
                        "Tenant eliminado",
                        "La organización se eliminó. Los usuarios vinculados quedaron sin organización.",
                        null));
    }
}
