CREATE TABLE balances (
      id BIGINT PRIMARY KEY AUTO_INCREMENT,
      user_id VARCHAR(255) NOT NULL,
      version BIGINT NOT NULL,
      balance BIGINT NOT NULL,
      created_at_utc TIMESTAMP NOT NULL,
      updated_at_utc TIMESTAMP NOT NULL
);

CREATE INDEX idx_balances_user_id ON balances(user_id);

CREATE TABLE balance_histories (
       id BIGINT PRIMARY KEY AUTO_INCREMENT,
       user_id VARCHAR(255) NOT NULL,
       type ENUM('CHARGED', 'USED') NOT NULL,
       amount BIGINT NOT NULL,
       created_at_utc TIMESTAMP NOT NULL,
       updated_at_utc TIMESTAMP NOT NULL
);

CREATE INDEX idx_balance_histories_user_id ON balance_histories(user_id);

CREATE TABLE pay_histories (
       id BIGINT PRIMARY KEY AUTO_INCREMENT,
       user_id VARCHAR(255) NOT NULL,
       amount BIGINT NOT NULL,
       reserve_id BIGINT NOT NULL UNIQUE,
       created_at_utc TIMESTAMP NOT NULL,
       updated_at_utc TIMESTAMP NOT NULL
);


CREATE INDEX idx_pay_histories_user_id ON pay_histories(user_id);
CREATE INDEX idx_pay_histories_reserve_id ON pay_histories(reserve_id);

CREATE TABLE queues (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        token CHAR(36) NOT NULL,
        user_id CHAR(36) NOT NULL,
        status ENUM('PENDING', 'ACTIVATED', 'EXPIRED') NOT NULL,
        expired_at_utc TIMESTAMP NOT NULL,
        created_at_utc TIMESTAMP NOT NULL,
        updated_at_utc TIMESTAMP NOT NULL
);

CREATE INDEX idx_queues_user_id ON queues(user_id);

CREATE TABLE reserves (
          id BIGINT PRIMARY KEY AUTO_INCREMENT,
          user_id VARCHAR(255) NOT NULL,
          seat_number INT NOT NULL,
          date_utc TIMESTAMP NOT NULL,
          created_at_utc TIMESTAMP NOT NULL,
          updated_at_utc TIMESTAMP NOT NULL
);

CREATE INDEX idx_reserves_user_id ON reserves(user_id);
