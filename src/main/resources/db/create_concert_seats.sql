DROP PROCEDURE IF EXISTS InsertConcertSeatsTestData;
-- Temporarily disable keys and foreign key checks for faster insertion
SET FOREIGN_KEY_CHECKS = 0;
SET UNIQUE_CHECKS = 0;
SET AUTOCOMMIT = 0;

-- Create procedure to insert test data
DELIMITER //

CREATE PROCEDURE InsertConcertSeatsTestData()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE j INT;
    DECLARE batch_size INT DEFAULT 1000;
    DECLARE total_records INT DEFAULT 10000000;

    -- Start transaction
    START TRANSACTION;

    WHILE i <= total_records DO
            -- Insert records in smaller sub-batches
            SET j = 1;

            WHILE j <= batch_size AND i <= total_records DO
                    INSERT INTO concert_seats (date_utc, seat_number, amount)
                    VALUES (
                               -- Generate random dates between 2024 and 2025
                               TIMESTAMP(DATE_ADD('2024-01-01',
                                                  INTERVAL FLOOR(RAND() * 730) DAY) +
                                         INTERVAL FLOOR(RAND() * 86400) SECOND),
                               -- Generate seat numbers between 1 and 1000
                               FLOOR(1 + RAND() * 1000),
                               -- Generate amounts between 1000 and 10000 (in cents)
                               FLOOR(1000 + RAND() * 9000)
                           );

                    SET j = j + 1;
                    SET i = i + 1;
                END WHILE;

            -- Commit every 10 batches (10,000 records)
            IF i % 10000 = 0 THEN
                COMMIT;
                START TRANSACTION;
                SELECT CONCAT('Processed: ', i, ' records') AS progress;
            END IF;
        END WHILE;

    -- Commit any remaining records
    COMMIT;
END //

DELIMITER ;

-- Call the procedure
CALL InsertConcertSeatsTestData();

-- Clean up
DROP PROCEDURE IF EXISTS InsertConcertSeatsTestData;

-- Re-enable keys and foreign key checks
SET FOREIGN_KEY_CHECKS = 1;
SET UNIQUE_CHECKS = 1;
SET AUTOCOMMIT = 1;