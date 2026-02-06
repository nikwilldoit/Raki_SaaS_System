-- ====================================================================================
-- 1. INITIALIZE VARIABLES
-- Fetch IDs once and store them. If the name changes, you update only these lines.
-- ====================================================================================

-- Store the Business ID
SET @b_id = (SELECT id FROM businesses WHERE name = 'Snip & Style');

-- Store the User (Staff) ID
SET @u_id = (SELECT id FROM users WHERE name = 'John Smith');


-- ====================================================================================
-- 2. CLEANUP
-- Use the variable for a clean, fast delete.
-- ====================================================================================

DELETE FROM businesses_hours WHERE business_id = @b_id;


-- ====================================================================================
-- 3. INSERT BUSINESS HOURS
-- Notice how clean the VALUES (...) list becomes.
-- ====================================================================================

INSERT INTO businesses_hours (business_id, day_of_week, open_time, close_time) VALUES
(@b_id, 'MONDAY',    '09:00:00', '17:00:00'),
(@b_id, 'TUESDAY',   '09:00:00', '17:00:00'),
(@b_id, 'WEDNESDAY', '09:00:00', '17:00:00'),
(@b_id, 'THURSDAY',  '09:00:00', '17:00:00'),
(@b_id, 'FRIDAY',    '09:00:00', '17:00:00'),
(@b_id, 'SATURDAY',  '10:00:00', '14:00:00');


-- ====================================================================================
-- 4. INSERT TIMESLOTS
-- ====================================================================================
DELETE FROM timeslots WHERE business_id = @b_id AND user_id = @u_id;
 -- 18-12-2025 is a Thursday
INSERT INTO timeslots (business_id, user_id, start_time, is_available) VALUES 
(@b_id, @u_id, '2025-12-20 09:00:00', 1),
(@b_id, @u_id, '2025-12-20 09:30:00', 1),
(@b_id, @u_id, '2025-12-20 10:00:00', 1),
(@b_id, @u_id, '2025-12-20 10:30:00', 1),
(@b_id, @u_id, '2025-12-20 11:00:00', 1),
(@b_id, @u_id, '2025-12-20 11:30:00', 1),
-- Lunch Break (12:00-13:00) skipped manually
(@b_id, @u_id, '2025-12-20 13:00:00', 1),
(@b_id, @u_id, '2025-12-20 13:30:00', 1),
(@b_id, @u_id, '2025-12-20 14:00:00', 1),
(@b_id, @u_id, '2025-12-20 14:30:00', 1),
(@b_id, @u_id, '2025-12-20 15:00:00', 1),
(@b_id, @u_id, '2025-12-20 15:30:00', 1),
(@b_id, @u_id, '2025-12-20 16:00:00', 1),
(@b_id, @u_id, '2025-12-20 16:30:00', 1);

-- TUESDAY
INSERT INTO timeslots (business_id, user_id, start_time, is_available) VALUES 
(@b_id, @u_id, '2025-12-21 09:00:00', 1),
(@b_id, @u_id, '2025-12-21 09:30:00', 1),
(@b_id, @u_id, '2025-12-21 10:00:00', 1),
(@b_id, @u_id, '2025-12-21 10:30:00', 1),
(@b_id, @u_id, '2025-12-21 11:00:00', 1),
(@b_id, @u_id, '2025-12-21 11:30:00', 1);


-- Services
DELETE FROM available_services WHERE business_id = @b_id;
INSERT INTO available_services (business_id, name, description, price, duration_minutes)
VALUES (@b_id, 'haircut', 'A haircut', 50, 30),
       (@b_id, 'shave', 'A shave', 30, 15),
       (@b_id, 'haircut and shave', 'A haircut and a shave', 75, 30),
       (@b_id, 'beard trim', 'A beard trim', 25, 15),
       (@b_id, 'hair color', 'Hair coloring service', 100, 30);

