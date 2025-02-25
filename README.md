# Onboarding

## 👶🏼 Backend Members 👶🏼

|                                             |                                     |                                    |                                           |
| ------------------------------------------- | ----------------------------------- | ---------------------------------- | ----------------------------------------- |
| [김지민](https://github.com/jimin-fundamental) | [임경표](https://github.com/MODUGGAGI) | [이주연](https://github.com/LJYeon12) | [정재훈](https://github.com/JungJaehoon0430) |
| 백엔드 리드 👑                                   | 팀원 👨🏻‍💻                          | 팀원 👨🏻‍💻                         | 팀원 👨🏻‍💻                                |

---

## 🌟 프로젝트 소개

**Onboarding**은 학습 관리 및 피드백 제공 기능을 포함한 시스템입니다. 사용자가 효율적으로 학습을 진행할 수 있도록 돕는 다양한 기능을 제공합니다.

### **주요 기능**

1. **회원가입 및 로그인**

   - 이메일 기반 회원가입 (이메일 중복 확인 포함)
   - Access Token 기반 인증 (JWT 사용)
   - 로그인 및 로그아웃 기능 제공
   - 비밀번호 변경 및 계정 탈퇴 지원
   - 액세스 토큰 만료 시 Refresh Token을 이용한 자동 갱신

2. **학습 관리 시스템**

   - 사용자의 학습 목표 설정 기능
   - 진행 상황 및 학습 성과 시각화 제공
   - 추천 학습 자료 및 커리큘럼 제안

3. **보안 및 HTTPS 적용**

   - 모든 API 요청을 HTTPS를 통해 암호화하여 전송
   - AWS Route 53 및 SSL 인증서(ACM) 적용을 통한 HTTPS 지원
   - 로그인 및 비밀번호 변경 시 데이터 암호화 적용
   - 클라이언트-서버 간 데이터 암호화를 통해 안전한 정보 전송

4. **CI/CD 및 배포 자동화**

   - GitHub Actions를 활용한 CI/CD 파이프라인 구축
   - 테스트 자동화 및 빌드, 배포 자동화 적용
   - AWS EC2 및 S3를 활용한 무중단 배포
   - Docker & Nginx를 활용한 컨테이너 기반 배포 환경 구성
   - 배포 단계에서 보안 강화를 위한 환경 변수 관리 및 접근 제한 적용

---

## 🛠️ 기술 스택

- **Backend**: Spring Boot(Java)
- **Database**: RDS(MySQL), Redis
- **Cloud**: AWS (EC2, VPC)
- **API Documentation**: Swagger, Notion
- **Version Control**: GitHub

---

## 🖥️ 프로젝트 구조

### ERD 설계



### 인프라 구성도

---

## 🌐 API 명세서

