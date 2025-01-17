package learningFlow.learningFlow_BE.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemoId implements Serializable {
    @Column(name = "collection_episode_id")
    private Long collectionEpisodeId;

    @Column(name = "user_id")
    private String userId;
}
