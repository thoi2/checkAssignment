# CheckAssignment

## 기술 스택
Backend: Spring Boot 3.x + JPA + MySQL 8.0

Build: Gradle 9.2.1

Container: Docker Compose

Deploy: AWS EC2 t3.small (서울 리전)

Frontend: Vanilla JS + HTML/CSS


## 요구사항
- 체크박스 목록 (default unCheck)
- 체크/언체크 DB 즉시 반영
- 고정 7개 + 커스텀 최대 200개
- 파일명 검사 성능 테스트

**서비스**: [http://3.34.4.189:8080](http://3.34.4.189:8080)

## 구현 특징

### DB 설계

name 고유인덱스

@Version 낙관적 락: 동시성 제어

fixed 컬럼: 고정/커스텀 권한 분리



### 확장자
normalize(): trim+소문자 통일

FileNameUtil: 경로/이중확장자 제거



### API

GET /extensions: fixed/custom 분리 응답

PATCH toggle: fixed 전용

커스텀 200개 제한 검증


### 배포 최적화
서울 리전: 빌드 2-3배 향상

t3.small: 메모리 안정성

MySQL 내부망만 허용

text

## ERD
```mermaid
erDiagram
    EXTENSION {
        Long id PK
        String name UK
        boolean fixed
        boolean blocked
        Long version
        LocalDateTime createdAt
    }
 ```
