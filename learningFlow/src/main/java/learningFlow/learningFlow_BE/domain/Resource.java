package learningFlow.learningFlow_BE.domain;

import jakarta.persistence.*;
import learningFlow.learningFlow_BE.domain.enums.ResourceType;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "resource")
public class Resource extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "resource_details", nullable = false)
    private String resourceDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ResourceType type;

    @Column(nullable = false)
    private String url; // 자료 링크

    @Column(name = "client_url", columnDefinition = "TEXT")
    private String clientUrl; // 클라이언트에게 보내줄 url
    // 블로그일 경우
    @Setter
    @Column(name = "source_code", columnDefinition = "TEXT")
    private String sourceCode;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer studyDuration; // 학습 시간 (초 단위 저장)

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    private List<CollectionEpisode> episodes;

    @Column(nullable = false)
    private Integer resourceQuantity; //초단위로 저장(유튜브 동영상이 초단위이기 때문)

    public void addEpisode(CollectionEpisode episode) {
        this.episodes.add(episode);
        if (episode.getResource() != this) {
            episode.setResource(this);
        }
    }

    public void removeEpisode(CollectionEpisode episode) {
        this.episodes.remove(episode);
        if (episode.getResource() == this) {
            episode.setResource(null);
        }
    }

    public void setClientUrl(String clientUrl) {
        if (this.clientUrl != null) return;
        this.clientUrl = clientUrl;
    }
}
