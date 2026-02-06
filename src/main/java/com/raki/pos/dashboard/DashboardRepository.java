package com.raki.pos.dashboard;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardRepository {

    private final JdbcTemplate jdbc;

    public DashboardRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public DashboardDTO.Response loadDashboardInfo(String email) {

        String sql = """
            SELECT 
                u.id AS userId,
                u.business_id AS businessId,

                r.name AS employeeType,
                b.type AS businessType,
                (CASE WHEN r.name = 'SuperAdmin' THEN TRUE ELSE FALSE END) AS superAdmin
            FROM users u
            JOIN roles r ON r.id = u.role_id
            LEFT JOIN businesses b ON b.id = u.business_id
            WHERE u.email = ?
        """;

        return jdbc.queryForObject(sql, (rs, rowNum) ->
                        new DashboardDTO.Response(
                                rs.getLong("userId"),
                                rs.getLong("businessId"),

                                rs.getString("employeeType"),
                                rs.getString("businessType"),
                                rs.getBoolean("superAdmin")
                        ),
                email
        );
    }
}
