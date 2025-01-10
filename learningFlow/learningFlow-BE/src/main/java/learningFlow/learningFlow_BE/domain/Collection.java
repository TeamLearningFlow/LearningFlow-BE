package learningFlow.learningFlow_BE.domain;

import jakarta.persistence.*;
import learningFlow.learningFlow_BE.domain.enums.Category;
import learningFlow.learningFlow_BE.domain.enums.MediaType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
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
    private Category category;

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

}
