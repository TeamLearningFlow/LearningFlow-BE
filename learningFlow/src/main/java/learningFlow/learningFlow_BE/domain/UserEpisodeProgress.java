package learningFlow.learningFlow_BE.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import learningFlow.learningFlow_BE.domain.enums.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_episode_progress")
public class UserEpisodeProgress extends BaseEntity{
    @EmbeddedId
    private UserEpisodeProgressId userEpisodeProgressId;
    @Column(nullable = false)
    private int episodeNumber;

    private Integer currentProgress;
    @Column(nullable = false)
    private Integer totalProgress;
    @Column(nullable = false)
    private ResourceType resourceType;

}
