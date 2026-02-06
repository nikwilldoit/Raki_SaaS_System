package com.raki.pos.business;

import com.raki.pos.business.model.Business;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BusinessRepository {

    private final JdbcTemplate jdbcTemplate;

    public Optional<Business> findById(Integer businessId) {
        String sql = "SELECT * FROM businesses WHERE id = ?";

        try {
            Business business = jdbcTemplate.queryForObject(
                    sql,
                    new BeanPropertyRowMapper<>(Business.class),
                    businessId
            );
            return Optional.ofNullable(business);
        } catch (Exception e) {
            log.error("Error finding business by ID {}", businessId, e);
            return Optional.empty();
        }
    }

    public int update(Business b) {
        String sql = """
        UPDATE businesses
        SET name = ?, address = ?, phone = ?, is_active = ?
        WHERE id = ?
        """;
        return jdbcTemplate.update(
                sql,
                b.getName(),
                b.getAddress(),
                b.getPhone(),
                b.getIsActive(),
                b.getId()
        );
    }


}