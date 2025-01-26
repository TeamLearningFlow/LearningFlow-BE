package learningFlow.learningFlow_BE.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // equals와 hashCode 메소드 자동생성
public class MemoId implements Serializable {
    @Column(name = "collection_episode_id")
    private Long collectionEpisodeId;

    @Column(name = "user_id")
    private String userId;
}
