package com.raki.pos.dashboard;

import com.raki.pos.auth.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final DashboardRepository repo;
    private final JwtUtil jwtUtil;

    public DashboardService(DashboardRepository repo, JwtUtil jwtUtil) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
    }

    public DashboardDTO.Response getDashboard(String token) {
        String email = jwtUtil.extractEmail(token);
        return repo.loadDashboardInfo(email);
    }
}
