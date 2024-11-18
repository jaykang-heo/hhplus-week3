SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
SET autocommit = 0;
SET unique_checks = 0;
SET foreign_key_checks = 0;

DELIMITER //

CREATE PROCEDURE InsertPaymentTestData()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE batch_size INT DEFAULT 1000;
    DECLARE total_records INT DEFAULT 10000000;

    START TRANSACTION;

    WHILE i <= total_records DO
            -- Insert batch_size records at a time
            INSERT INTO payments (payment_id, reservation_id, amount, created_time_utc)
            WITH RECURSIVE numbers AS (
                SELECT
                    i as n,
                    CONCAT('PAY', LPAD(i, 12, '0')) as pay_id,
                    CONCAT('RES', LPAD(i, 12, '0')) as res_id,
                    1000 + FLOOR(RAND() * 99000) as amt,
                    DATE_ADD('2024-01-01 00:00:00',
                             INTERVAL FLOOR(RAND() * 31536000) SECOND) as created_time
                UNION ALL
                SELECT
                    n + 1,
                    CONCAT('PAY', LPAD(n + 1, 12, '0')),
                    CONCAT('RES', LPAD(n + 1, 12, '0')),
                    1000 + FLOOR(RAND() * 99000),
                    DATE_ADD('2024-01-01 00:00:00',
                             INTERVAL FLOOR(RAND() * 31536000) SECOND)
                FROM numbers
                WHERE n < i + batch_size - 1
            )
            SELECT pay_id, res_id, amt, created_time
            FROM numbers;

            SET i = i + batch_size;

            -- Commit and show progress every 100,000 records
            IF i % 100000 = 0 THEN
                COMMIT;
                SELECT CONCAT('Processed ', i, ' records of ', total_records) AS Progress;
                START TRANSACTION;
            END IF;
        END WHILE;

    -- Commit final batch
    COMMIT;

    -- Show final statistics
    SELECT
        COUNT(*) as total_records,
        COUNT(DISTINCT payment_id) as unique_payment_ids,
        MIN(created_time_utc) as earliest_date,
        MAX(created_time_utc) as latest_date,
        AVG(amount) as average_amount
    FROM payments;
END //

DELIMITER ;

-- Execute the procedure
CALL InsertPaymentTestData();

-- Clean up
DROP PROCEDURE IF EXISTS InsertPaymentTestData;

-- Reset settings
SET autocommit = 1;
SET unique_checks = 1;
SET foreign_key_checks = 1;

-- Analyze table for optimal query performance
ANALYZE TABLE payments;