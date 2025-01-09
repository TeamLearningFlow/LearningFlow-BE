# LearningFlow-BE: A backend repository of Team Learning Flow

**📝 백엔드 협업 규칙**

**커밋 컨벤션**

- "태그: 한글 커밋 메시지" 형식으로 작성
- 컨벤션 예시
    - feat: 새로운 기능 추가, 기존의 기능을 요구 사항에 맞추어 수정
    - fix: 버그 수정
    - docs: 문서 수정
    - style: 코드 포맷팅, 오타 수정, 주석 수정 및 삭제 등
    - refactor: 코드 리팩터링
    - chore: 빌드 및 패키지 수정 및 삭제
    - merge: 브랜치를 머지
    - ci: CI 관련 설정 수정
    - test: 테스트 코드 추가/수정
    - release: 버전 릴리즈

**PR 템플릿**

```
# 구현 기능
  - 구현한 기능을 요약하여 정리합니다.

# 구현 상태 (선택)
  - img, gif, video...
  - 혹은 내용 정리

# Resolve
  - 이슈 태그(ex: #7)
```

- PR 체크 리스트
    - PR 제목 형식 : `[Type] PR 제목`
        - ex. `[Feat] 회원가입 및 로그인 기능 개발`
        - 타입은 대문자로
    - label 설정
    - 작업자 자신을 Assign하고, Code Review 요청
    - 작성자 외 1명 확인 시 작성자가 merge

**issue 규칙**

- 각 기능에 맞는 이슈 템플릿 작성 (작업 및 변경사항 확인용)
- to-do에 구현해야할 기능을 작성하고, 구현이 끝나면 체크표시

**branch 규칙**

- 브랜치 네이밍 규칙: `feat/{도메인_혹은_큰_기능}` ex) `feat/user`
- `feat -> develop -> deploy -> main` 순으로 merge
- `feat` : 각 기능을 개발하는 브랜치
- `develop` : 각 기능의 개발을 완료하고 테스트 완료 후 병합하는 브랜치
- `deploy` : 배포 브랜치
