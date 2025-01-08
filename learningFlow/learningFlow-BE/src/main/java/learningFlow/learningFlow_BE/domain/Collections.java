package learningFlow.learningFlow_BE.domain;

import jakarta.persistence.*;
import learningFlow.learningFlow_BE.domain.enums.Category;
import learningFlow.learningFlow_BE.domain.enums.MediaType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "collections")
public class Collections extends BaseEntity {

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
}
