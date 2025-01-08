package learningFlow.learningFlow_BE.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCollectionId implements java.io.Serializable {
    private Long collection;
    private String user;
}
