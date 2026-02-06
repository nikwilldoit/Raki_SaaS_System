package com.raki.pos.reservation;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "http://localhost:3000")
public class AvailableServiceController {

    private final JdbcTemplate jdbc;

    public AvailableServiceController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping
    public List<Map<String, Object>> listServices(@RequestParam Long businessId) {
        String sql = """
            SELECT id, name, price, status
            FROM available_services
            WHERE business_id = ? AND status = 'ACTIVE'
            """;
        return jdbc.queryForList(sql, businessId);
    }
}
