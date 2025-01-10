package learningFlow.learningFlow_BE.domain;

import jakarta.persistence.*;
import learningFlow.learningFlow_BE.domain.enums.ResourceType;
import lombok.*;

import java.util.List;

@Getter
@Setter
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

    @Column(name = "embedded_url",nullable = false)
    private String embeddedUrl; // 임베드 url


    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    private List<CollectionEpisode> episodes;
}
