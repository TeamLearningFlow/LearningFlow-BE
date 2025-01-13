package learningFlow.learningFlow_BE.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "memo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Memo extends BaseEntity {
    @EmbeddedId
    private MemoId id;

    @Column(nullable = true)
    private String contents;
}
