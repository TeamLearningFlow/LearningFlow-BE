package learningFlow.learningFlow_BE.domain;

import jakarta.persistence.*;
import learningFlow.learningFlow_BE.domain.enums.InterestField;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "collection_keywords", joinColumns = @JoinColumn(name = "collection_id"))
    @Column(name = "keyword")
    private List<String> keywords = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterestField interestField;

    @Column(name = "detail_information", nullable = false)
    private String detailInformation;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "collection_difficulties", joinColumns = @JoinColumn(name = "collection_id"))
    @Column(name = "difficulty")
    private List<Integer> difficulty; // 1: 입문, 2: 초급, 3: 중급, 4: 실무

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private Integer resourceTypeRatio; //영상 기준 -> 100개 중 영상이 70개면 70으로 저장 -> 따라서 최댓값이 100이어야함.

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer bookmarkCount = 0;

    @Column(nullable = false)
    private String collectionImgUrl;

//    @ManyToOne
//    @JoinColumn(name = "image_id")
//    private Image image;
//    public void setImage(Image image) {
//        // 기존 이미지와의 관계 제거
//        if (this.image != null) {
//            this.image.getCollections().remove(this);
//        }
//        this.image = image;
//        // 새로운 이미지와 관계 설정
//        if (image != null) {
//            image.getCollections().add(this);
//        }
//    }

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL)
    private List<UserCollection> userCollections;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL)
    private List<CollectionEpisode> episodes;

    public void incrementBookmarkCount() {
        this.bookmarkCount++;
    }

    public void decrementBookmarkCount() {
        this.bookmarkCount--;
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
