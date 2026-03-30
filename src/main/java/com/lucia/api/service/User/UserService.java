package com.lucia.api.service.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lucia.api.model.dto.User.AuthResponse;
import com.lucia.api.model.dto.User.NavigationResponseDTO;
import com.lucia.api.model.dto.User.TenantBriefDTO;
import com.lucia.api.model.dto.User.UserListMetaDTO;
import com.lucia.api.model.dto.User.UserListResponseDTO;
import com.lucia.api.model.dto.User.UserRequestDTO;
import com.lucia.api.model.dto.User.UserResponseDTO;
import com.lucia.api.model.dto.User.UserSummaryDTO;
import com.lucia.api.model.entity.Tenant;
import com.lucia.api.model.entity.User;
import com.lucia.api.repository.Tenant.TenantRepository;
import com.lucia.api.repository.User.UserRepository;
import com.lucia.api.repository.User.UserSpecifications;
import com.lucia.api.security.JwtUtil;
import com.lucia.api.exception.BadRequestException;
import com.lucia.api.exception.InvalidCredentialsException;
import com.lucia.api.exception.ResourceNotFoundException;
import com.lucia.api.exception.BusinessConflictException;
import com.lucia.api.service.File.FileSystemStorageService;
import com.lucia.api.service.Cache.TokenCacheService;
import com.lucia.api.service.navigation.DashboardNavigationService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileSystemStorageService storageService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenCacheService tokenCacheService;

    @Autowired
    private DashboardNavigationService dashboardNavigationService;

    @Autowired
    private UserCursorCodec userCursorCodec;

    public static final int DEFAULT_PER_PAGE = 20;
    public static final int MAX_PER_PAGE = 100;

    /**
     * Misma política que el front y {@link UserRequestDTO}: 8–128 caracteres en claro y complejidad.
     */
    private void assertSignUpPasswordStrength(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
        if (password.length() < 8 || password.length() > 128) {
            throw new IllegalArgumentException("La contraseña debe tener entre 8 y 128 caracteres.");
        }
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,128}$")) {
            throw new IllegalArgumentException(
                    "La contraseña debe incluir mayúscula, minúscula, un número y un carácter especial.");
        }
    }

    @Transactional
    public UserResponseDTO signUp(UserRequestDTO request, MultipartFile profileImage) {
        assertSignUpPasswordStrength(request.getPassword());

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPaternalSurname(request.getPaternalSurname());
        user.setMaternalSurname(request.getMaternalSurname());
        user.setPhone(request.getPhone() != null && !request.getPhone().isBlank()
                ? request.getPhone().trim()
                : null);
        user.setRole(request.getRole() != null ? request.getRole() : User.Role.USER);
        user.setCreatedAt(java.time.OffsetDateTime.now());
        user.setUpdatedAt(java.time.OffsetDateTime.now());

        if (profileImage != null && !profileImage.isEmpty()) {
            String filename = storageService.generateUniqueFilename(profileImage.getOriginalFilename());
            storageService.save(profileImage, filename);
            user.setProfileImageUrl(filename);
        }

        User saved = userRepository.save(user);
        return UserResponseDTO.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .name(saved.getName())
                .paternalSurname(saved.getPaternalSurname())
                .maternalSurname(saved.getMaternalSurname())
                .phone(saved.getPhone())
                .profileImageUrl(saved.getProfileImageUrl())
                .role(saved.getRole())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    @Transactional
    public UserResponseDTO updateUser(Long userId, UserRequestDTO request, MultipartFile profileImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Validar que si se intenta cambiar el email, no esté duplicado
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new BusinessConflictException("El email ya está registrado en el sistema");
            }
            // El email no se actualiza en updateUser, solo se valida
            // Si necesitas actualizar el email, deberías agregarlo aquí
        }

        user.setName(request.getName());
        user.setPaternalSurname(request.getPaternalSurname());
        user.setMaternalSurname(request.getMaternalSurname());
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().isBlank() ? null : request.getPhone().trim());
        }
        user.setRole(request.getRole() != null ? request.getRole() : user.getRole());
        user.setUpdatedAt(java.time.OffsetDateTime.now());

        if (profileImage != null && !profileImage.isEmpty()) {
            // Eliminar imagen anterior si existe
            String oldImage = user.getProfileImageUrl();
            if (oldImage != null && !oldImage.isEmpty()) {
                storageService.delete(oldImage);
            }
            String filename = storageService.generateUniqueFilename(profileImage.getOriginalFilename());
            storageService.save(profileImage, filename);
            user.setProfileImageUrl(filename);
        }

        User updated = userRepository.save(user);
        return UserResponseDTO.builder()
                .id(updated.getId())
                .email(updated.getEmail())
                .name(updated.getName())
                .paternalSurname(updated.getPaternalSurname())
                .maternalSurname(updated.getMaternalSurname())
                .phone(updated.getPhone())
                .profileImageUrl(updated.getProfileImageUrl())
                .role(updated.getRole())
                .createdAt(updated.getCreatedAt())
                .updatedAt(updated.getUpdatedAt())
                .build();
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        // Eliminar imagen de perfil si existe
        String image = user.getProfileImageUrl();
        if (image != null && !image.isEmpty()) {
            storageService.delete(image);
        }
        userRepository.delete(user);
    }

    public AuthResponse signIn(String email, String password) {
        User user = userRepository
                .findByEmailWithTenant(email)
                .orElseThrow(() -> new InvalidCredentialsException("Usuario no encontrado"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Credenciales inválidas");
        }
        String role = user.getRole().apiAuthority();
        String token = jwtUtil.generateToken(user.getEmail(), role);
        Long expiresIn = jwtUtil.getExpiration();
        List<String> navigation = dashboardNavigationService.resolveNavigationKeys(user);
        TenantBriefDTO tenantBrief = dashboardNavigationService.toBrief(user.getTenant());
        return AuthResponse.builder()
                .accessToken(token)
                .expiresIn(expiresIn)
                .role(role)
                .navigation(navigation)
                .tenant(tenantBrief)
                .build();
    }

    public NavigationResponseDTO getMyNavigation(String email) {
        User user = userRepository
                .findByEmailWithTenant(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return NavigationResponseDTO.builder()
                .navigation(dashboardNavigationService.resolveNavigationKeys(user))
                .tenant(dashboardNavigationService.toBrief(user.getTenant()))
                .build();
    }

    /**
     * Listado paginado para admin. Orden fijo: {@code created_at DESC, id DESC}.
     * Offset: {@code page} + {@code per_page} (o {@code limit}). Cursor: {@code cursor} / {@code cursor_before}.
     */
    @Transactional(readOnly = true)
    public UserListResponseDTO listUsersForAdmin(
            int page,
            int perPage,
            String search,
            String roleStr,
            String cursor,
            String cursorBefore) {
        if (page < 1) {
            throw new BadRequestException("page debe ser >= 1.");
        }
        if (perPage < 1 || perPage > MAX_PER_PAGE) {
            throw new BadRequestException("per_page (o limit) debe estar entre 1 y " + MAX_PER_PAGE + ".");
        }
        boolean hasCursor = cursor != null && !cursor.isBlank();
        boolean hasBefore = cursorBefore != null && !cursorBefore.isBlank();
        if (hasCursor && hasBefore) {
            throw new BadRequestException("No usar cursor y cursor_before en la misma petición.");
        }

        User.Role roleEnum = parseRoleFilter(roleStr);
        Specification<User> base = UserSpecifications.withFilters(search, roleEnum);
        long total = userRepository.count(base);

        Sort sortDesc =
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));
        Sort sortAsc =
                Sort.by(Sort.Order.asc("createdAt"), Sort.Order.asc("id"));

        List<User> content;
        String nextC = null;
        String prevC = null;
        Integer currentPageOut;
        int lastPageVal = computeLastPage(total, perPage);

        if (hasBefore) {
            UserCursorCodec.CursorPayload anchor = userCursorCodec.decode(cursorBefore);
            OffsetDateTime ca = userCursorCodec.toCreatedAt(anchor);
            Specification<User> spec = base.and(UserSpecifications.keysetBefore(ca, anchor.getId()));
            Pageable pb = PageRequest.of(0, perPage + 1, sortAsc);
            List<User> batch = new ArrayList<>(userRepository.findAll(spec, pb).getContent());
            boolean hasMoreNewer = batch.size() > perPage;
            if (hasMoreNewer) {
                batch = new ArrayList<>(batch.subList(0, perPage));
            }
            Collections.reverse(batch);
            content = batch;
            if (!content.isEmpty()) {
                User first = content.get(0);
                User last = content.get(content.size() - 1);
                if (hasMoreNewer) {
                    prevC = userCursorCodec.encode(first.getCreatedAt(), first.getId());
                }
                if (countOlderThan(base, last.getCreatedAt(), last.getId()) > 0) {
                    nextC = userCursorCodec.encode(last.getCreatedAt(), last.getId());
                }
            } else {
                content = List.of();
            }
            currentPageOut = null;
        } else if (hasCursor) {
            UserCursorCodec.CursorPayload anchor = userCursorCodec.decode(cursor);
            OffsetDateTime ca = userCursorCodec.toCreatedAt(anchor);
            Specification<User> spec = base.and(UserSpecifications.keysetAfter(ca, anchor.getId()));
            Pageable pb = PageRequest.of(0, perPage + 1, sortDesc);
            List<User> batch = new ArrayList<>(userRepository.findAll(spec, pb).getContent());
            boolean hasMore = batch.size() > perPage;
            if (hasMore) {
                batch = new ArrayList<>(batch.subList(0, perPage));
            }
            content = batch;
            if (!content.isEmpty()) {
                User first = content.get(0);
                User last = content.get(content.size() - 1);
                prevC = userCursorCodec.encode(first.getCreatedAt(), first.getId());
                if (hasMore) {
                    nextC = userCursorCodec.encode(last.getCreatedAt(), last.getId());
                }
            } else {
                content = List.of();
            }
            currentPageOut = null;
        } else {
            Pageable pb = PageRequest.of(page - 1, perPage, sortDesc);
            Page<User> pg = userRepository.findAll(base, pb);
            content = pg.getContent();
            currentPageOut = page;
            if (!content.isEmpty()) {
                User first = content.get(0);
                User last = content.get(content.size() - 1);
                if (pg.hasNext()) {
                    nextC = userCursorCodec.encode(last.getCreatedAt(), last.getId());
                }
                if (page > 1) {
                    prevC = userCursorCodec.encode(first.getCreatedAt(), first.getId());
                }
            }
        }

        List<UserSummaryDTO> rows = content.stream().map(this::toUserSummary).toList();
        UserListMetaDTO meta =
                UserListMetaDTO.builder()
                        .currentPage(hasCursor || hasBefore ? null : currentPageOut)
                        .perPage(perPage)
                        .total(total)
                        .lastPage(lastPageVal)
                        .nextCursor(nextC)
                        .prevCursor(prevC)
                        .build();
        return UserListResponseDTO.builder().data(rows).meta(meta).build();
    }

    private long countOlderThan(Specification<User> base, OffsetDateTime createdAt, Long id) {
        Specification<User> spec = base.and(UserSpecifications.keysetAfter(createdAt, id));
        return userRepository.count(spec);
    }

    private static int computeLastPage(long total, int perPage) {
        if (total <= 0) {
            return 1;
        }
        return (int) ((total + perPage - 1) / perPage);
    }

    private static User.Role parseRoleFilter(String roleStr) {
        if (roleStr == null || roleStr.isBlank()) {
            return null;
        }
        try {
            return User.Role.fromPersistedOrApiName(roleStr);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("role debe ser admin, user o seller.");
        }
    }

    @Transactional(readOnly = true)
    public UserSummaryDTO getUserSummaryForAdmin(Long userId) {
        User user = userRepository
                .findByIdWithTenant(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return toUserSummary(user);
    }

    @Transactional
    public UserSummaryDTO updateUserTenant(Long userId, Long tenantIdOrNull) {
        User user = userRepository
                .findByIdWithTenant(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        if (tenantIdOrNull == null) {
            user.setTenant(null);
        } else {
            Tenant t = tenantRepository
                    .findById(tenantIdOrNull)
                    .orElseThrow(() -> new ResourceNotFoundException("Tenant no encontrado"));
            user.setTenant(t);
        }
        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);
        return getUserSummaryForAdmin(userId);
    }

    private UserSummaryDTO toUserSummary(User user) {
        Tenant t = user.getTenant();
        return UserSummaryDTO.builder()
                .id(String.valueOf(user.getId()))
                .email(user.getEmail())
                .role(user.getRole().apiAuthority())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .emailConfirmed(false)
                .tenantId(t != null ? t.getId() : null)
                .tenantName(t != null ? t.getName() : null)
                .build();
    }

    /**
     * Cierra la sesión del usuario invalidando su token JWT.
     * El token será agregado a una blacklist para que no pueda ser usado nuevamente.
     * 
     * @param token Token JWT a invalidar
     */
    public void logout(String token) {
        if (token != null && !token.isEmpty()) {
            tokenCacheService.invalidateToken(token);
        }
    }
}
