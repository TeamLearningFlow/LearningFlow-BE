package learningFlow.learningFlow_BE.domain;

import jakarta.persistence.*;
import learningFlow.learningFlow_BE.domain.enums.InterestField;
import learningFlow.learningFlow_BE.domain.enums.MediaType;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "collection")
public class Collection extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String creator;

    @Column(nullable = false)
    private String keyword;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterestField interestField;

    @Column(name = "detail_information", nullable = false)
    private String detailInformation;

    @Column(nullable = false)
    private Integer difficulty;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private Image image;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL)
    private List<UserCollection> userCollections;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL)
    private List<CollectionEpisode> episodes;

    public void setImage(Image image) {
        // 기존 이미지와의 관계 제거
        if (this.image != null) {
            this.image.getCollections().remove(this);
        }
        this.image = image;
        // 새로운 이미지와 관계 설정
        if (image != null) {
            image.getCollections().add(this);
        }
    }

    public void addUserCollection(UserCollection userCollection) {
        this.userCollections.add(userCollection);
        if (userCollection.getCollection() != this) {
            userCollection.setCollection(this);
        }
    }

    public void removeUserCollection(UserCollection userCollection) {
        this.userCollections.remove(userCollection);
        if (userCollection.getCollection() == this) {
            userCollection.setCollection(null);
        }
    }

    public void addEpisode(CollectionEpisode episode) {
        this.episodes.add(episode);
        if (episode.getCollection() != this) {
            episode.setCollection(this);
        }
    }

    public void removeEpisode(CollectionEpisode episode) {
        this.episodes.remove(episode);
        if (episode.getCollection() == this) {
            episode.setCollection(null);
        }
    }
}
