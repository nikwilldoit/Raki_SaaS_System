package com.raki.pos.admin;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
@Slf4j
public class SuperAdminContextController {

    private final JdbcTemplate jdbcTemplate;

    private String getCurrentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    private boolean isSuperAdmin(String email) {
        String sql = """
                SELECT r.name
                FROM users u
                JOIN roles r ON u.role_id = r.id
                WHERE u.email = ?
                """;
        try {
            String roleName = jdbcTemplate.queryForObject(sql, String.class, email);
            return "SuperAdmin".equalsIgnoreCase(roleName);
        }
        catch (Exception e) {
            return false;
        }
    }

    @PostMapping("/select-business")
    public ResponseEntity<Void> selectBusiness(@RequestBody SelectBusinessRequest req) {
        String email = getCurrentEmail();
        if (email == null || !isSuperAdmin(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {

            String findBizSql = """
                    SELECT id FROM businesses
                    WHERE TRIM(LOWER(name)) = TRIM(LOWER(?))
                    """;

            Integer businessId = jdbcTemplate.queryForObject(
                    findBizSql,
                    Integer.class,
                    req.getBusinessName()
            );

            String updateUserSql = "UPDATE users SET business_id = ? WHERE email = ?";
            jdbcTemplate.update(updateUserSql, businessId, email);

            log.info("SuperAdmin {} switched to business {} ({})",
                    email, businessId, req.getBusinessName());

            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception e) {
            log.error("Error selecting business for super admin", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Data
    public static class SelectBusinessRequest {
        private String businessName;
    }
}
