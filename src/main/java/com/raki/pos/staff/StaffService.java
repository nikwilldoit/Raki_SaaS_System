// src/main/java/com/raki/pos/staff/StaffService.java
package com.raki.pos.staff;

import com.raki.pos.model.StaffUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffService {

    private final StaffRepository staffRepository;
    private final JdbcTemplate jdbcTemplate;

    private String getCurrentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    private Optional<Integer> resolveCurrentBusinessId() {
        String email = getCurrentEmail();
        if (email == null) return Optional.empty();

        String sql = "SELECT business_id FROM users WHERE email = ? AND status = 'ACTIVE'";
        try {
            Integer businessId = jdbcTemplate.queryForObject(sql, Integer.class, email);
            return Optional.ofNullable(businessId);
        } catch (Exception e) {
            log.warn("No business_id found for {}", email, e);
            return Optional.empty();
        }
    }

    private String getCurrentRoleName() {
        String email = getCurrentEmail();
        if (email == null) return null;

        String sql = """
            SELECT r.name
            FROM users u
            JOIN roles r ON u.role_id = r.id
            WHERE u.email = ?
            """;
        try {
            return jdbcTemplate.queryForObject(sql, String.class, email);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isSuperAdmin() {
        String role = getCurrentRoleName();
        return role != null && role.equalsIgnoreCase("SuperAdmin");
    }

    private boolean isOwner() {
        String role = getCurrentRoleName();
        return role != null && role.equalsIgnoreCase("Owner");
    }

    // ---------- LIST ----------

    public List<StaffDTO> listStaffForCurrentBusiness() {
        Optional<Integer> maybeBusinessId = resolveCurrentBusinessId();
        if (maybeBusinessId.isEmpty()) {
            return List.of();
        }
        Integer businessId = maybeBusinessId.get();

        List<StaffUser> users;
        if (isSuperAdmin()) {
            // SuperAdmin: owner + employees
            users = staffRepository.findAllByBusinessId(businessId);
        } else if (isOwner()) {
            // Owner: μόνο employees
            users = staffRepository.findEmployeesByBusinessId(businessId);
        } else {
            throw new SecurityException("Not allowed");
        }

        return users.stream()
                .map(u -> new StaffDTO(
                        u.getId(),
                        u.getBusinessId(),
                        u.getName(),
                        u.getEmail(),
                        u.getPhone(),
                        u.getRole(),
                        u.getStatus()
                ))
                .toList();
    }

    // ---------- UPDATE ----------

    public Optional<StaffDTO> updateStaffForCurrentBusiness(
            Integer staffId,
            String name,
            String email,
            String phone,
            String rawPassword,
            String roleName,
            String status
    ) {
        Optional<Integer> maybeBusinessId = resolveCurrentBusinessId();
        if (maybeBusinessId.isEmpty()) {
            return Optional.empty();
        }
        Integer businessId = maybeBusinessId.get();

        Optional<StaffUser> opt = staffRepository.findByIdAndBusinessId(staffId, businessId);
        if (opt.isEmpty()) return Optional.empty();

        StaffUser target = opt.get();

        boolean currentIsSuperAdmin = isSuperAdmin();
        boolean currentIsOwner = isOwner();

        if (!currentIsSuperAdmin && !currentIsOwner) {
            throw new SecurityException("Not allowed");
        }

        // Owner ΔΕΝ μπορεί να πειράξει Owner
        if (currentIsOwner && "Owner".equalsIgnoreCase(target.getRole())) {
            throw new SecurityException("Owner cannot edit another Owner");
        }

        target.setName(name);
        target.setEmail(email);
        target.setPhone(phone);
        target.setStatus(status);

        if (rawPassword != null && !rawPassword.isBlank()) {
            target.setPassword(rawPassword); // plain text, όπως ζήτησες
        }

        if (roleName != null && !roleName.isBlank()) {
            staffRepository.findRoleIdByName(roleName)
                    .ifPresent(target::setRoleId);
        }

        int rows = staffRepository.update(target);
        if (rows == 0) return Optional.empty();

        return Optional.of(new StaffDTO(
                target.getId(),
                target.getBusinessId(),
                target.getName(),
                target.getEmail(),
                target.getPhone(),
                roleName != null ? roleName : target.getRole(),
                target.getStatus()
        ));
    }
}
