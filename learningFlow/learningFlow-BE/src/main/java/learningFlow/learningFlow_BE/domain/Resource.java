package learningFlow.learningFlow_BE.domain;

import jakarta.persistence.*;
import learningFlow.learningFlow_BE.domain.enums.ResourceType;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collections_id", nullable = false)
    private Collections collections;

    @Column(nullable = false)
    private String title;

    @Column(name = "course_progress", nullable = false)
    private Integer courseProgress;

    @Column(name = "resource_details", nullable = false)
    private String resourceDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ResourceType type;

    @Column(nullable = false)
    private String url;
}
