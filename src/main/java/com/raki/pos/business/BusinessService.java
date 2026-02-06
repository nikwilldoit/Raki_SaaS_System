package com.raki.pos.business;

import com.raki.pos.business.model.Business;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final JdbcTemplate jdbcTemplate;


    public Optional<Business> findBusinessById(Integer id) {
        return businessRepository.findById(id);
    }

    private String getCurrentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }


    public Optional<Business> findBusinessForCurrentUser() {
        String email = getCurrentEmail();
        if (email == null) {
            log.warn("No authenticated user found in SecurityContext");
            return Optional.empty();
        }

        String sql = "SELECT business_id FROM users WHERE email = ? AND status = 'ACTIVE'";
        Integer businessId;
        try {
            businessId = jdbcTemplate.queryForObject(sql, Integer.class, email);
        } catch (Exception e) {
            log.error("Failed to resolve business_id for email {}", email, e);
            return Optional.empty();
        }

        if (businessId == null) {
            return Optional.empty();
        }
        return businessRepository.findById(businessId);
    }


    public Optional<Business> updateBusinessForCurrentUser(Business incoming) {
        Optional<Business> opt = findBusinessForCurrentUser();
        if (opt.isEmpty()) {
            return Optional.empty();
        }

        Business b = opt.get();
        b.setName(incoming.getName());
        b.setAddress(incoming.getAddress());
        b.setPhone(incoming.getPhone());
        b.setIsActive(incoming.getIsActive());

        int rows = businessRepository.update(b);
        log.info("Rows updated = {}", rows);

        return rows > 0 ? Optional.of(b) : Optional.empty();
    }


}
