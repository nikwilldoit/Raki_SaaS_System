package com.raki.pos.tax;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TaxPolicyRepository {

    private final JdbcTemplate jdbc;

    public TaxPolicyRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<TaxPolicyDTO> findAll() {
        String sql = """
        SELECT id, name, rate, tax_type
        FROM tax_policies
        ORDER BY id
        """;

        return jdbc.query(sql, (rs, rowNum) ->
                new TaxPolicyDTO(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getDouble("rate"),
                        rs.getString("tax_type")
                )
        );
    }

    public void insert(String name, double rate, String taxType) {
        String sql = "INSERT INTO tax_policies (name, tax_type, rate) VALUES (?, ?, ?)";
        jdbc.update(sql, name, taxType, rate);
    }
}
