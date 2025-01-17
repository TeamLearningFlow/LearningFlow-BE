package learningFlow.learningFlow_BE.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "memo")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Memo extends BaseEntity {
    @EmbeddedId
    private MemoId id;

    @Column
    private String contents;
}
