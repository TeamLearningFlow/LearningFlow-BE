package learningFlow.learningFlow_BE.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import learningFlow.learningFlow_BE.domain.enums.ResourceType;
import lombok.*;

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

    @Setter
    private Integer currentProgress;
    @Column(nullable = false)
    private Integer totalProgress;
    @Column(nullable = false)
    private ResourceType resourceType;

}
