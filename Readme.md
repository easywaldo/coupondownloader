### 선착순쿠폰 다운로드 서비스

#### API 사용법
0. Active Profile 이 Local 로 세팅이 되어 있으므로 인텔리제이 또는 이클립스 등에서 Active.profile 을 local 로 설정하여 구동하여야 합니다.
1. Springboot 프로젝트를 빌드 후 인텔리제이 등을 이용하여 jar 를 실행합니다.
2. 8088 포트로 접속이 가능 합니다. (http://localhost:8088/swagger-ui.html)
3. Swagger 문서에 각 URI 에 대한 설명을 참고하여 쿠폰다운로드 테스트 진행이 가능 합니다.

#### 컨트롤러 구성
- CouponController
    - 쿠폰다운로드 응모 및 다운로드 완료한 회원리스트 조회가 가능합니다.

#### 테스트 시나리오
- /coupon/download/{couponId}/{userId} 로 쿠폰 다운로드 합니다.
- /coupon/userCouponList/{yyyyMMdd} 로 쿠폰 리스트 조회 합니다.

#### 인메모리 H2 DB 정보
http://localhost:8088/h2-console/
- 계정 : sa / password

### Redis 설정
- 기본 포트로 실행이 된 상태가 필요합니다.(6379 포트)
- 전역캐시서버인 레디스를 이용하여 유저아이디를 키로 설정하고 값에는 토큰값을 부여하여 쿠폰다운 과정에서 최초 토큰값과 레디스 최신값의 비교를 통해 중복된 쿠폰다운로드를 막도록 1차 역할을 수행합니다.
- 쿠폰다운르드 저장 과정에서 최소한의 대기시간을 설정하여 동일 유저에 대한 동시다운로드 시도가 성공하지 못하도록 2차 역할을 수행합니다.  