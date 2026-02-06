package com.raki.pos.staff;

import com.raki.pos.model.StaffUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class StaffRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<StaffUser> findEmployeesByBusinessId(Integer businessId) {
        String sql = """
        SELECT
            u.id,
            u.business_id AS businessId,
            u.name,
            u.email,
            u.phone,
            u.status,
            r.name AS role
        FROM users u
        LEFT JOIN roles r ON u.role_id = r.id
        WHERE u.business_id = ?
          AND r.name = 'Employee'
        ORDER BY u.name
        """;
        return jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(StaffUser.class),
                businessId);
    }

    public List<StaffUser> findAllByBusinessId(Integer businessId) {
        String sql = """
        SELECT
            u.id,
            u.business_id AS businessId,
            u.name,
            u.email,
            u.phone,
            u.status,
            r.name AS role
        FROM users u
        LEFT JOIN roles r ON u.role_id = r.id
                        WHERE u.business_id = ?
                  AND r.name <> 'SuperAdmin'
        ORDER BY u.name
        """;
        return jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(StaffUser.class),
                businessId);
    }

    public List<StaffUser> findByBusinessId(Integer businessId) {
        String sql = """
        SELECT
            u.id,
            u.business_id AS businessId,
            u.name,
            u.email,
            u.phone,
            u.status,
            r.name AS role
        FROM users u
        LEFT JOIN roles r ON u.role_id = r.id
        WHERE u.business_id = ?
          AND r.name = 'Employee'
        ORDER BY u.name
        """;
        return jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(StaffUser.class),
                businessId);
    }

    public Optional<StaffUser> findByIdAndBusinessId(Integer id, Integer businessId) {
        String sql = """
        SELECT
            u.id,
            u.business_id AS businessId,
            u.name,
            u.email,
            u.phone,
            u.status,
            u.password_hash AS password,
            u.role_id AS roleId,
            r.name AS role
        FROM users u
        LEFT JOIN roles r ON u.role_id = r.id
        WHERE u.id = ? AND u.business_id = ?
        """;
        try {
            StaffUser u = jdbcTemplate.queryForObject(
                    sql,
                    new BeanPropertyRowMapper<>(StaffUser.class),
                    id, businessId
            );
            return Optional.ofNullable(u);
        } catch (Exception e) {
            log.error("Error loading staff user {} for business {}", id, businessId, e);
            return Optional.empty();
        }
    }

    public int update(StaffUser u) {
        String sql = """
        UPDATE users
        SET name = ?, email = ?, phone = ?, password_hash = ?, status = ?, role_id = ?
        WHERE id = ? AND business_id = ?
        """;
        return jdbcTemplate.update(
                sql,
                u.getName(),
                u.getEmail(),
                u.getPhone(),
                u.getPassword(),
                u.getStatus(),
                u.getRoleId(),
                u.getId(),
                u.getBusinessId()
        );
    }

    public Optional<Integer> findRoleIdByName(String roleName) {
        String sql = "SELECT id FROM roles WHERE name = ?";
        try {
            Integer id = jdbcTemplate.queryForObject(sql, Integer.class, roleName);
            return Optional.ofNullable(id);
        } catch (Exception e) {
            log.warn("Role not found for name={}", roleName);
            return Optional.empty();
        }
    }
}
