package com.raki.pos.reservation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
public class ResRepository {
    private static final Logger logger = LogManager.getLogger(ResRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public ResRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ResDTO.ReservationListResponseDTO> fetchReservationListData(int businessId) {
        String sql = """
        SELECT 
            r.id, 
            r.customer_name, 
            r.customer_phone, 
            r.snapshot_service_name AS service_name,
            r.status,
            u.name AS employee_name,
            MIN(t.start_time) AS appointment_date
        FROM reservations r
        LEFT JOIN users u ON r.user_id = u.id
        LEFT JOIN reservation_timeslots rt ON r.id = rt.reservation_id
        LEFT JOIN timeslots t ON rt.timeslot_id = t.id
        WHERE r.business_id = ?
        GROUP BY 
            r.id, 
            r.customer_name, 
            r.customer_phone, 
            r.snapshot_service_name, 
            r.status, 
            u.name
        ORDER BY appointment_date DESC
    """;

        try { //query results are processed row by row using a lambda function.
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                // convert the Timestamp/Date to String for the DTO
                String dateStr = rs.getString("appointment_date");
                // If date is null (e.g. broken reservation), handle gracefully
                if (dateStr != null && dateStr.contains(".")) {
                    dateStr = dateStr.split("\\.")[0]; // Remove milliseconds if present
                }

                return new ResDTO.ReservationListResponseDTO(
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        rs.getString("customer_phone"),
                        rs.getString("service_name"),
                        rs.getString("employee_name"),
                        dateStr,
                        rs.getString("status")
                );
            }, businessId);

        } catch (DataAccessException e) {
            logger.error("Database error fetching reservations for businessId={}", businessId, e);
            throw e;
        }
    }

    public Map<String, Object> fetchReservationCreationData(int businessId) {
        String sqlServices = """
    SELECT 
        s.id AS serviceId,
        s.name,
        s.duration_minutes AS durationMinutes,
        s.price,
        CASE 
            WHEN d.id IS NOT NULL 
                 AND d.is_active = 1
                 AND (d.scope = 'SERVICE' OR d.scope = 'BOTH')
                 AND d.discount_type = 'PERCENT'
                THEN GREATEST(s.price - (s.price * d.discount_value / 100), 0)
            WHEN d.id IS NOT NULL 
                 AND d.is_active = 1
                 AND (d.scope = 'SERVICE' OR d.scope = 'BOTH')
                 AND d.discount_type = 'AMOUNT'
                THEN GREATEST(s.price - d.discount_value, 0)
            ELSE s.price
        END AS displayPrice
    FROM available_services s
    LEFT JOIN discount_policies d ON s.discount_id = d.id
    WHERE s.business_id = ? AND s.status = 'ACTIVE';
""";


        String sqlEmployees = """
        SELECT u.id, u.name
        FROM users u
        JOIN roles r ON u.role_id = r.id
        WHERE u.business_id = ?
          AND u.status = 'ACTIVE'
          AND r.name IN ('Owner', 'Employee');
    """;

        try {
            List<Map<String, Object>> services = jdbcTemplate.queryForList(sqlServices, businessId);
            List<Map<String, Object>> employees = jdbcTemplate.queryForList(sqlEmployees, businessId);

            return Map.of(
                    "services", services,
                    "employees", employees
            );
        } catch (DataAccessException e) {
            logger.error("Database error fetching reservation data for businessId={}", businessId, e);
            throw e;
        }
    }


    public List<String> fetchAvailability(int businessId, String date) {
        // SELECT DISTINCT: Ensures we don't get duplicate rows
        //    (e.g., if 3 barbers are free at 9:00, we just want '09:00' once).
        String sql = """
                    SELECT DISTINCT DATE_FORMAT(start_time, '%H:%i:%s') AS time
                    FROM timeslots
                    WHERE business_id = ? 
                      AND DATE(start_time) = ?
                      AND is_available = 1
                    ORDER BY time ASC
                """;

        try {
            List<String> slots = jdbcTemplate.queryForList(sql, String.class, businessId, date);

            logger.debug("Found {} unique time options for business {} on {}", slots.size(), businessId, date);

            return slots;

        }
        catch (DataAccessException e) {
            logger.error("Error fetching global availability for businessId={}", businessId, e);
            throw e;
        }
    }

    public List<String> fetchAvailability(int businessId, int employeeId, String date) {
        String sql = """
                    SELECT DATE_FORMAT(start_time, '%H:%i:%s') AS time
                    FROM timeslots
                    WHERE business_id = ? 
                      AND user_id = ? 
                      AND DATE(start_time) = ?
                      AND is_available = 1
                    ORDER BY start_time ASC
                """;

        try {
            List<String> availability = jdbcTemplate.queryForList(sql, String.class, businessId, employeeId, date);
            logger.debug("Availability found: {}", availability);
            return availability;
        }
        catch (EmptyResultDataAccessException e) {
            logger.info("No availability found for businessId={}, employeeId={}, date={}", businessId, employeeId, date);
            return List.of();
        }
        catch (DataAccessException e) {
            logger.error("Database error fetching availability for businessId={}, employeeId={}, date={}", businessId, employeeId, date, e);
            throw e;
        }
    }

    @Transactional
    public String createReservation(String customerPhone, String customerName, String date, int businessId, int serviceId, int employeeId) {
        // We need to insert a new reservation record into reservations table and reservation_timeslots table and
        // set timeslot as unavailable in timeslots table

        //TODO CONSIDER DISCOUNT AND TAXES LATER
        String findSlotSql;
        Object[] params;

        // If employeeId is 0 (Any Employee), we find the first available slot at that time
        // and grab the actual user_id of the staff member assigned to that slot.
        if (employeeId == 0) {
            findSlotSql = """
                    SELECT t.id, t.user_id, u.name as employee_name
                    FROM timeslots t
                    JOIN users u ON t.user_id = u.id
                    WHERE t.business_id = ? 
                      AND t.start_time = ? 
                      AND t.is_available = 1
                    LIMIT 1
                    """;
            params = new Object[]{businessId, date};
        } else {
            // If a specific employee is selected, we ensure the slot belongs to them.
            findSlotSql = """
                    SELECT t.id, t.user_id, u.name as employee_name
                    FROM timeslots t
                    JOIN users u ON t.user_id = u.id
                    WHERE t.business_id = ? 
                      AND t.user_id = ? 
                      AND t.start_time = ?
                      AND t.is_available = 1
                    """;
            params = new Object[]{businessId, employeeId, date};
        }

        Long timeslotId;
        Long assignedEmployeeId;
        String assignedEmployeeName;

        try {
            Map<String, Object> slotData = jdbcTemplate.queryForMap(findSlotSql, params);

            timeslotId = (Long) slotData.get("id");
            assignedEmployeeId = (Long) slotData.get("user_id");
            assignedEmployeeName = (String) slotData.get("employee_name");
        }
        catch (EmptyResultDataAccessException e) { //child of DataAccessException already handled in exception handler

            logger.warn("Booking failed: Slot unavailable. Business: {}, Emp: {}, Date: {}", businessId, employeeId, date);
            throw e;
        }

        String insertReservationSql = """
    INSERT INTO reservations (
        business_id, user_id, customer_name, customer_phone, service_id,
        snapshot_service_name, snapshot_price
    )
    VALUES (
        ?, ?, ?, ?, ?,
        (SELECT name FROM available_services WHERE id = ?),
        (
            SELECT 
                CASE 
                    WHEN d.id IS NOT NULL 
                         AND d.is_active = 1
                         AND (d.scope = 'SERVICE' OR d.scope = 'BOTH')
                         AND d.discount_type = 'PERCENT'
                        THEN GREATEST(s.price - (s.price * d.discount_value / 100), 0)
                    WHEN d.id IS NOT NULL 
                         AND d.is_active = 1
                         AND (d.scope = 'SERVICE' OR d.scope = 'BOTH')
                         AND d.discount_type = 'AMOUNT'
                        THEN GREATEST(s.price - d.discount_value, 0)
                    ELSE s.price
                END
        FROM available_services s
        LEFT JOIN discount_policies d ON s.discount_id = d.id
        WHERE s.id = ?
        )
    );
""";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        // Insert reservation

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertReservationSql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, businessId);
            ps.setLong(2, assignedEmployeeId);
            ps.setString(3, customerName);
            ps.setString(4, customerPhone);
            ps.setInt(5, serviceId);
            ps.setInt(6, serviceId);
            ps.setInt(7, serviceId);
            return ps;
        }, keyHolder);

        Number reservationId = keyHolder.getKey();
        if (reservationId == null) {
            throw new RuntimeException("Failed to insert reservation.");
        }

        // MARK TIMESLOT AS UNAVAILABLE
        String updateTimeslotSql = "UPDATE timeslots SET is_available = 0 WHERE id = ?";
        jdbcTemplate.update(updateTimeslotSql, timeslotId);

        // LINK RESERVATION TO TIMESLOT
        String linkSql = "INSERT INTO reservation_timeslots (reservation_id, timeslot_id) VALUES (?, ?)";
        jdbcTemplate.update(linkSql, reservationId.intValue(), timeslotId);

        logger.info("Reservation created: id)={}, customer={}, employee={}, timeslot={}",
                reservationId.intValue(), customerName, assignedEmployeeId, timeslotId);

        return "Reservation confirmed for " + customerName + " with " + assignedEmployeeName;
    }

    @Transactional
    public int deleteReservation(int reservationId, int businessId) {
        // Free timeslot associated with this reservation
        String findSlotSql = "SELECT timeslot_id FROM reservation_timeslots WHERE reservation_id = ?";
        try {
            Integer timeslotId = jdbcTemplate.queryForObject(findSlotSql, Integer.class, reservationId);
            if(timeslotId != null) {
                jdbcTemplate.update("UPDATE timeslots SET is_available = 1 WHERE id = ?", timeslotId);
            }
        } catch (Exception e) {
            // If no slot found , proceed to delete reservation anyway
            logger.warn("Could not free timeslot for reservation {}", reservationId);
        }
        // ON DELETE CASCADE in DB handles the junction table cleanup
        String sql = "DELETE FROM reservations WHERE id = ? AND business_id = ?";
        return jdbcTemplate.update(sql, reservationId, businessId);
    }

    public int updateReservation(int reservationId, int businessId, String status, String name, String phone) {
        String sql = """
            UPDATE reservations 
            SET status = ?, customer_name = ?, customer_phone = ?
            WHERE id = ? AND business_id = ?
        """;
        return jdbcTemplate.update(sql, status, name, phone, reservationId, businessId);
    }
}
