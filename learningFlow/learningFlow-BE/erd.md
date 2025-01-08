erDiagram
User {
String login_id "우리 서비스에서 사용하는 고유 식별자 ID"
String email "이메일"
String provider_id "구글에서 저장하는 고유 ID"
String pw "비밀번호"
Enum socialType "구글 로그인 방식: GOOGLE, LOCAL"
Enum job "직업: 대학생, 직장인, 매칭 없음"
String interestField "관심 분야"
LocalDate birthDay "생년월일"
Enum gender "성별: 남, 여"
Enum role "권한: ADMIN, USER"
blob profilePhoto "프로필 사진"
Boolean inactive "비활성화 여부"
DateTime created_at "유저가 만들어진 시간"
DateTime updated_at "유저 정보 업데이트된 시간"
Enum preferType "사용자가 선호하는 미디어"
}

    Rules {
        String basicRule "기본 이용약관"
    }
    
    UserCollection {
        Long collectionID "컬렉션 ID"
        Long userID "유저 ID"
        int userCollectionStatus "컬렉션 내 리소스 진행 상태"
        LocalDate last_accessed_at "마지막 학습 시간"
    }
    
    Memo {
        int Id "기본키"
        Long collectionID "컬렉션 ID"
        Long userID "유저 ID"
        String contents "내용"
    }
    
    resource {
        Long id "기본키"
        Long collectionID "컬렉션 ID"
        String title "자료 제목"
        int courseProgress "진행 상태"
        String resourceCdDetails "자료 정보"
        Enum type "자료 타입"
        String url "자료 링크"
    }
    
    Collections {
        Long id "기본키"
        String title "제목"
        String creator "작성자"
        String keyword "키워드"
        String category "카테고리"
        String detailInformation "상세 정보"
        LocalDateTime created_at "생성일시"
        LocalDateTime updated_at "수정일시"
        Enum difficulty "난이도"
        int amount "자료 수"
        Enum format "형식: VIDEO, TEXT, BOTH"
    }

    User ||--o{ UserCollection : ""
    UserCollection ||--o| Collections : ""
    UserCollection ||--o{ resource : ""
    Collections ||--o{ Memo : ""
