package learningFlow.learningFlow_BE.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_collection")
@IdClass(UserCollectionId.class)
public class UserCollection {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    private Collections collection;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_collection_status", nullable = false)
    private Integer userCollectionStatus;

    @Column(name = "last_accessed_at", nullable = false)
    private LocalDate lastAccessedAt;
}
