# ZOOM API 연동 (Server-To-Server) 샘플 코드
RestTemplates를 이용하여 Open Api 연동 구현을 위한 샘플 프로젝트.
Server-to-Server 인증 방식을 이용하여 외부 api 연동을 구현하는 것이 주 목표로 구현 범위는 최소한으로 진행함.

## 개발 환경
- **Language** : Java 8
- **Framework** : Spring Boot 2.6.5
- **IDE** : STS4
- **DB** : H2
- **ORM** : JPA
  
## 구현 목표 
1. RestTemplates를 이용하여 외부 Api 요청.
2. 미팅 CRUD만 구현하여 범위 최소화.
3. 요청 파라미터가 너무 많은 경우 일부 생략.
4. 외부에 요청하여 받은 응답 파라미터를 DB에 저장하는 과정 구현. -> DTO를 생성하여 데이터 전달.
5. api 구현이 주 목표이므로 DB는 H2를 이용하여 임시로 구현.
