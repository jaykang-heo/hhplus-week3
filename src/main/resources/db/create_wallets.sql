-- Performance optimization settings
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
SET autocommit = 0;
SET unique_checks = 0;
SET foreign_key_checks = 0;

DELIMITER //

CREATE PROCEDURE InsertWalletTestData()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE batch_size INT DEFAULT 1000;
    DECLARE total_records INT DEFAULT 10000000;

    START TRANSACTION;

    WHILE i <= total_records DO
            INSERT INTO wallets (queue_token, balance, created_time_utc, updated_time_utc)
            WITH RECURSIVE numbers AS (
                SELECT
                    i as n,
                    -- Generate unique queue token (combination of timestamp and sequence)
                    CONCAT('QT',
                           DATE_FORMAT(DATE_ADD('2024-01-01 00:00:00',
                                                INTERVAL (i DIV 1000) MINUTE), '%Y%m%d%H%i'),
                           LPAD(i, 8, '0')
                    ) as queue_token,
                    -- Generate random balance between 0 and 1,000,000 (in cents, so $0-$10,000)
                    FLOOR(RAND() * 1000000) as balance,
                    -- Created time spreads across 2024
                    DATE_ADD('2024-01-01 00:00:00',
                             INTERVAL FLOOR(RAND() * 31536000) SECOND) as created_time,
                    -- Updated time is after created time (0-7 days later)
                    DATE_ADD(
                            DATE_ADD('2024-01-01 00:00:00',
                                     INTERVAL FLOOR(RAND() * 31536000) SECOND),
                            INTERVAL FLOOR(RAND() * 7 * 24 * 3600) SECOND
                    ) as updated_time
                UNION ALL
                SELECT
                    n + 1,
                    CONCAT('QT',
                           DATE_FORMAT(DATE_ADD('2024-01-01 00:00:00',
                                                INTERVAL ((n + 1) DIV 1000) MINUTE), '%Y%m%d%H%i'),
                           LPAD(n + 1, 8, '0')
                    ),
                    FLOOR(RAND() * 1000000),
                    DATE_ADD('2024-01-01 00:00:00',
                             INTERVAL FLOOR(RAND() * 31536000) SECOND),
                    DATE_ADD(
                            DATE_ADD('2024-01-01 00:00:00',
                                     INTERVAL FLOOR(RAND() * 31536000) SECOND),
                            INTERVAL FLOOR(RAND() * 7 * 24 * 3600) SECOND
                    )
                FROM numbers
                WHERE n < i + batch_size - 1
            )
            SELECT queue_token, balance, created_time,
                   GREATEST(created_time, updated_time) as updated_time
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
        COUNT(DISTINCT queue_token) as unique_tokens,
        MIN(created_time_utc) as earliest_created,
        MAX(updated_time_utc) as latest_updated,
        AVG(balance) as average_balance,
        MIN(balance) as min_balance,
        MAX(balance) as max_balance
    FROM wallets;
END //

DELIMITER ;

-- Execute the procedure
CALL InsertWalletTestData();

-- Clean up
DROP PROCEDURE IF EXISTS InsertWalletTestData;

-- Reset settings
SET autocommit = 1;
SET unique_checks = 1;
SET foreign_key_checks = 1;

-- Analyze table for optimal query performance
ANALYZE TABLE wallets;

-- Add indexes after data insertion (if they don't exist)
CREATE UNIQUE INDEX IF NOT EXISTS idx_wallets_queue_token ON wallets(queue_token);
CREATE INDEX IF NOT EXISTS idx_wallets_updated_time ON wallets(updated_time_utc);