-- Initial Table Creation (Without Indexes)
CREATE TABLE concert_seats (
   id BIGINT NOT NULL AUTO_INCREMENT,
   date_utc TIMESTAMP NOT NULL,
   seat_number BIGINT NOT NULL,
   amount BIGINT NOT NULL,
   PRIMARY KEY (id)
);

CREATE TABLE payments (
  id BIGINT NOT NULL AUTO_INCREMENT,
  payment_id VARCHAR(255) NOT NULL,
  reservation_id VARCHAR(255) NOT NULL,
  amount BIGINT NOT NULL,
  created_time_utc TIMESTAMP NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE wallets (
 id BIGINT NOT NULL AUTO_INCREMENT,
 queue_token VARCHAR(255) NOT NULL,
 balance BIGINT NOT NULL,
 created_time_utc TIMESTAMP NOT NULL,
 updated_time_utc TIMESTAMP NOT NULL,
 PRIMARY KEY (id)
);

-- Index Creation Statements
-- Indexes for concert_seats
CREATE INDEX idx_concert_seats_date ON concert_seats (date_utc);
CREATE INDEX idx_concert_seats_date_seat ON concert_seats (date_utc, seat_number);

-- Indexes for payments
CREATE UNIQUE INDEX uk_payments_reservation_id ON payments (reservation_id);
CREATE INDEX idx_payments_created_time ON payments (created_time_utc);

-- Indexes for wallets
CREATE UNIQUE INDEX idx_wallets_queue_token ON wallets (queue_token);
CREATE INDEX idx_wallets_updated_time ON wallets (updated_time_utc);

