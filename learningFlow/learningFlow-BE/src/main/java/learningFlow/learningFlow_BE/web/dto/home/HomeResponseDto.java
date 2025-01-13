package learningFlow.learningFlow_BE.web.dto.home;

import learningFlow.learningFlow_BE.domain.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class HomeResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeInfoDto {
        List<RecommendedCollectionDto> recommendedCollections;
        List<RecentCollectionDto> recentCollections;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendedCollectionDto {
        Long collectionId;           // 컬렉션 ID
        String title;                // 컬렉션 제목
        String creator;              // 생성자
        Image image;                 // 컬렉션 이미지 URL
        Integer difficulty;          // 난이도
        String category;             // 카테고리
        String detailInformation;    // 상세 정보
        String collectionUrl;        // 컬렉션 상세 페이지 URL  // 추가됨
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentCollectionDto {
        Long collectionId;            // 컬렉션 ID
        String title;                 // 컬렉션 제목
        String creator;               // 생성자
        Image image;                  // 컬렉션 이미지 URL
        LocalDateTime lastAccessedAt; // 마지막 접속 시간, UserCollection 엔티티의 lastAccessedAt 필드, 최신 순으로 정렬 시 사용
        Integer progress;             // 진행률
        String collectionUrl;         // 컬렉션 상세 페이지 URL  // 추가됨
        Integer currentEpisode;       // 현재 학습 중인 에피소드 번호 // 추가됨
    }
}