# 프로젝트 마일스톤 
https://github.com/users/jaykang-heo/projects/2

# API 명세
[API 명세.md](docs/API%20%EB%AA%85%EC%84%B8.md)

# Sequence Diagram
- [결제.md](docs/%EA%B2%B0%EC%A0%9C.md)
- [대기열.md](docs/%EB%8C%80%EA%B8%B0%EC%97%B4.md)
- [예약_가능한_날짜_조회.md](docs/%EC%98%88%EC%95%BD_%EA%B0%80%EB%8A%A5%ED%95%9C_%EB%82%A0%EC%A7%9C_%EC%A1%B0%ED%9A%8C.md)
- [예약_가능한_좌석_조회.md](docs/%EC%98%88%EC%95%BD_%EA%B0%80%EB%8A%A5%ED%95%9C_%EC%A2%8C%EC%84%9D_%EC%A1%B0%ED%9A%8C.md)
- [잔액_조회.md](docs/%EC%9E%94%EC%95%A1_%EC%A1%B0%ED%9A%8C.md)
- [좌석예약.md](docs/%EC%A2%8C%EC%84%9D%EC%98%88%EC%95%BD.md)
- [충전.md](docs/%EC%B6%A9%EC%A0%84.md)

# ERD
![img.png](docs/img.png)
[ERD 원문](docs/ERD.md)

# 프로젝트 설명
- 설명: 콘서트 예약 서비스에 필요한 기능을 제공합니다
- 기술 스택:
    - 서비스
        - Spring Boot 3.3.4
        - Java 17
        - Spring JPA
        - Spring Web
    - 테스트 & 유틸
        - H2
        - mockito
        - mockk
        - Spring Boot Webflux
        - ktlint 11.3.1
    - 아키텍처: Clean + Layered Architecture
        - api
            - request
            - response
        - application
        - domain
            - model
            - command
            - query
            - validator
            - service
        - repository
            - jpa
            - model
