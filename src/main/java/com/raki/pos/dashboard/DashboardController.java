package com.raki.pos.dashboard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            DashboardDTO.Response resp = dashboardService.getDashboard(token);
            // if there is no assigned business
            if (resp.getBusinessType() == null && !resp.isSuperAdmin()) {
                return ResponseEntity
                        .status(403) // ή HttpStatus.FORBIDDEN
                        .body(Map.of(
                                "error", "No business assigned to this user"
                        ));
            }

            return ResponseEntity.ok(resp);
        }
        catch (Exception ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
    }
}
