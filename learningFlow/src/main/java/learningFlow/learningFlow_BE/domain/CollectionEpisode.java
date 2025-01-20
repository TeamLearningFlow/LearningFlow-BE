package learningFlow.learningFlow_BE.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "collection_episode")
public class CollectionEpisode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    private Collection collection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @Column(name = "episode_name", nullable = false)
    private String episodeName;

    @Column(name = "episode_number", nullable = false)
    private Integer episodeNumber;

    public void setCollection(Collection collection) {
        // 기존 컬렉션과의 관계 제거
        if (this.collection != null) {
            this.collection.getEpisodes().remove(this);
        }
        this.collection = collection;
        // 새로운 컬렉션과 관계 설정
        if (collection != null && !collection.getEpisodes().contains(this)) {
            collection.getEpisodes().add(this);
        }
    }

    public void setResource(Resource resource) {
        // 기존 리소스와의 관계 제거
        if (this.resource != null) {
            this.resource.getEpisodes().remove(this);
        }
        this.resource = resource;
        // 새로운 리소스와 관계 설정
        if (resource != null && !resource.getEpisodes().contains(this)) {
            resource.getEpisodes().add(this);
        }
    }
}
