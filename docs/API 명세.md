# API 명세서

## 1. **Payment API**

### 1.1 결제 처리 (`/payment/pay`)

- **URL:** `/payment/pay`
- **Method:** `POST`
- **설명:** 결제 요청을 처리하고 거래 세부 정보를 반환합니다.
- **Headers:**
  - `Authorization` (필수): 인증 헤더
- **Request Body:**
  ```json
  {
    "amount": Long
  }
  ```
  - `amount` (Long): 결제할 금액
- **Response:**
  ```json
  {
    "userId": "String",
    "orderNumber": "String"
  }
  ```
  - `userId` (String): 사용자 ID
  - `orderNumber` (String): 주문 번호

### 1.2 잔액 충전 (`/payment/charge`)

- **URL:** `/payment/charge`
- **Method:** `POST`
- **설명:** 사용자의 잔액을 충전하고 거래 세부 정보를 반환합니다.
- **Headers:**
  - `Authorization` (필수): 인증 헤더
- **Request Body:**
  ```json
  {
    "amount": Long
  }
  ```
  - `amount` (Long): 충전할 금액
- **Response:**
  ```json
  {
    "userId": "String",
    "balance": Long
  }
  ```
  - `userId` (String): 사용자 ID
  - `balance` (Long): 충전 후 잔액

### 1.3 잔액 조회 (`/payment/balance`)

- **URL:** `/payment/balance`
- **Method:** `GET`
- **설명:** 사용자의 현재 잔액을 조회합니다.
- **Headers:**
  - `Authorization` (필수): 인증 헤더
- **Response:**
  ```json
  {
    "userId": "String",
    "balance": Long
  }
  ```
  - `userId` (String): 사용자 ID
  - `balance` (Long): 현재 잔액

## 2. **Queue API**

### 2.1 토큰 발급 (`/queue/issue/token`)

- **URL:** `/queue/issue/token`
- **Method:** `POST`
- **설명:** 인증을 위한 새 토큰을 발급합니다.
- **Response:**
  ```json
  {
    "token": "String"
  }
  ```
  - `token` (String): 발급된 토큰

### 2.2 대기열 정보 조회 (`/queue/info`)

- **URL:** `/queue/info`
- **Method:** `GET`
- **설명:** 현재 대기열 상태에 대한 정보를 조회합니다.
- **Headers:**
  - `Authorization` (필수): 인증 헤더
- **Response:**
  ```json
  {
    "userId": "String",
    "currentCount": Long,
    "totalCount": Long,
    "createdAt": "Instant"
  }
  ```
  - `userId` (String): 사용자 ID
  - `currentCount` (Long): 현재 대기열 위치
  - `totalCount` (Long): 총 대기열 수
  - `createdAt` (Instant): 대기열 생성 시간

## 3. **Reservation API**

### 3.1 사용 가능한 좌석 찾기 (`/reservation/available/seats`)

- **URL:** `/reservation/available/seats`
- **Method:** `GET`
- **설명:** 주어진 날짜에 대해 사용 가능한 좌석을 검색합니다.
- **Query Parameters:**
  - `dateUtc` (Instant, 필수): 조회할 날짜의 UTC 시간
- **Response:**
  ```json
  {
    "availableSeats": [
      {
        "dateTime": "Instant",
        "availableRows": [Int],
        "availableSeats": [Int]
      },
      ...
    ]
  }
  ```
  - `availableSeats` (List): 날짜별 이용 가능한 좌석 정보 목록
    - `dateTime` (Instant): 좌석 이용 가능 날짜 및 시간
    - `availableRows` (List<Int>): 이용 가능한 열 번호 목록
    - `availableSeats` (List<Int>): 이용 가능한 좌석 번호 목록

### 3.2 사용 가능한 날짜 찾기 (`/reservation/available/dates`)

- **URL:** `/reservation/available/dates`
- **Method:** `GET`
- **설명:** 예약 가능한 날짜를 검색합니다.
- **Response:**
  ```json
  {
    "availableDates": [ "Instant" ],
    "blockedDates": [ "Instant" ]
  }
  ```
  - `availableDates` (List<Instant>): 예약 가능한 날짜 목록
  - `blockedDates` (List<Instant>): 예약 불가능한 날짜 목록

### 3.3 좌석 예약 (`/reservation/reserve`)

- **URL:** `/reservation/reserve`
- **Method:** `POST`
- **설명:** 주어진 날짜와 시간에 대해 좌석을 예약합니다.
- **Headers:**
  - `Authorization` (필수): 인증 헤더
- **Request Body:**
  ```json
  {
    "dateTime": "Instant",
    "row": Int,
    "seat": Int
  }
  ```
  - `dateTime` (Instant): 예약할 날짜 및 시간
  - `row` (Int): 예약할 열 번호
  - `seat` (Int): 예약할 좌석 번호
- **Response:**
  ```json
  {
    "reservationId": Long,
    "userId": "String",
    "status": "String",
    "seatNumber": Int,
    "createdAt": "Instant",
    "expiresAt": "Instant"
  }
  ```
  - `reservationId` (Long): 예약 ID
  - `userId` (String): 사용자 ID
  - `status` (String): 예약 상태 (예: "PENDING")
  - `seatNumber` (Int): 예약된 좌석 번호
  - `createdAt` (Instant): 예약 생성 시각
  - `expiresAt` (Instant): 예약 만료 시각