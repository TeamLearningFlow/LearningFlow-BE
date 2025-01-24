package learningFlow.learningFlow_BE.domain;

import jakarta.persistence.*;
import learningFlow.learningFlow_BE.domain.enums.UserCollectionStatus;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Builder
@Entity
@Table(name = "user_collection")
public class UserCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    private Collection collection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_collection_status", nullable = false)
    private Integer userCollectionStatus;

    @Column(name = "last_accessed_at", nullable = false)
    private LocalDate lastAccessedAt;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'IN_PROGRESS'", nullable = false)
    private UserCollectionStatus status;

    public void setUser(User user) {
        // 기존 유저와의 관계 제거
        if (this.user != null) {
            this.user.getUserCollections().remove(this);
        }
        this.user = user;
        // 새로운 유저와 관계 설정
        if (user != null && !user.getUserCollections().contains(this)) {
            user.getUserCollections().add(this);
        }
    }

    public void setCollection(Collection collection) {
        // 기존 컬렉션과의 관계 제거
        if (this.collection != null) {
            this.collection.getUserCollections().remove(this);
        }
        this.collection = collection;
        // 새로운 컬렉션과 관계 설정
        if (collection != null && !collection.getUserCollections().contains(this)) {
            collection.getUserCollections().add(this);
        }
    }
}

