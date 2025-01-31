package learningFlow.learningFlow_BE.domain;


//import jakarta.persistence.*;
//import lombok.*;
//
//import java.math.BigInteger;
//import java.util.List;
//
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Entity
//@Table(name = "image")
//public class Image {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String imageURL;
//
//    @Column(nullable = false)
//    private String fileType;
//
//    @Column(nullable = false)
//    private BigInteger fileSize;
//
//    @OneToMany(mappedBy = "image", cascade = CascadeType.ALL)
//    private List<User> users;
//
//    @OneToMany(mappedBy = "image", cascade = CascadeType.ALL)
//    private List<Collection> collections;
//
//}