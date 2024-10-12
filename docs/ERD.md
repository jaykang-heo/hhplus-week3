```text
Table users {
  id VARCHAR(255) [pk]
  name VARCHAR(255) [not null]
  email VARCHAR(255) [not null, unique]
  created_at TIMESTAMP [not null]
  updated_at TIMESTAMP [not null]
}

Table balances {
  id BIGINT [pk, increment]
  user_id VARCHAR(255) [not null]
  version BIGINT [not null]
  balance BIGINT [not null]
  created_at_utc TIMESTAMP [not null]
  updated_at_utc TIMESTAMP [not null]

  Indexes {
    (user_id) [name: "idx_balances_user_id"]
  }
}

Table balance_histories {
  id BIGINT [pk, increment]
  user_id VARCHAR(255) [not null]
  type ENUM('CHARGED', 'USED') [not null]
  amount BIGINT [not null]
  created_at_utc TIMESTAMP [not null]

  Indexes {
    (user_id) [name: "idx_balance_histories_user_id"]
  }
}

Table pay_histories {
  id BIGINT [pk, increment]
  user_id VARCHAR(255) [not null]
  amount BIGINT [not null]
  reserve_id BIGINT [not null, unique]
  created_at_utc TIMESTAMP [not null]

  Indexes {
    (user_id) [name: "idx_pay_histories_user_id"]
    (reserve_id) [name: "idx_pay_histories_reserve_id", unique]
  }
}



Table queues {
  id BIGINT [pk, increment]
  token CHAR(36) [not null]
  user_id CHAR(36) [not null]
  status ENUM('PENDING', 'ACTIVATED', 'EXPIRED') [not null]
  expired_at_utc TIMESTAMP [not null]
  created_at_utc TIMESTAMP [not null]
  updated_at_utc TIMESTAMP [not null]

  Indexes {
    (user_id) [name: "idx_queues_user_id"]
  }
}

Table reserves {
  id BIGINT [pk, increment]
  user_id VARCHAR(255) [not null]
  status ENUM('PENDING', ACTIVATED)
  seat_number INT [not null]
  date_utc TIMESTAMP [not null]
  created_at_utc TIMESTAMP [not null]

  Indexes {
    (user_id) [name: "idx_reserves_user_id"]
  }
}

Ref: pay_histories.reserve_id > reserves.id
Ref: balances.user_id > users.id
Ref: balance_histories.user_id > users.id
Ref: pay_histories.user_id > users.id
Ref: queues.user_id > users.id
Ref: reserves.user_id > users.id
```
