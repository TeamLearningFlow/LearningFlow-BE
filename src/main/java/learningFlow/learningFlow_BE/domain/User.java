package learningFlow.learningFlow_BE.domain;

import jakarta.persistence.*;
import learningFlow.learningFlow_BE.domain.enums.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@Builder
@Entity
@Table(name = "user")
public class User extends BaseEntity {

    @Id
    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(name = "provider_id")
    private String providerId;

    @Column(nullable = false)
    private String pw;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Job job;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_interests")
    @Enumerated(EnumType.STRING)
    @Column(name = "interest_field", nullable = false)
    private List<InterestField> interestFields;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean inactive;

    @Enumerated(EnumType.STRING)
    @Column(name = "prefer_type", nullable = false)
    private MediaType preferType;

    @Column(nullable = true)
    private String profileImgUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_bookmarks", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "collection_id")
    private List<Long> bookmarkedCollectionIds = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserCollection> userCollections;

    // added
    public void addBookmark(Long collectionId) {
        if (!bookmarkedCollectionIds.contains(collectionId)) {
            bookmarkedCollectionIds.add(collectionId);
        }
    }

    // added
    public void removeBookmark(Long collectionId) {
        bookmarkedCollectionIds.remove(collectionId);
    }

    // added
    public boolean hasBookmarked(Long collectionId) {
        return bookmarkedCollectionIds.contains(collectionId);
    }

//    public void setImage(Image image) {
//        //기존 이미지와의 연관관계 제거
//        if (this.image != null) {
//            this.image.getUsers().remove(this);
//        }
//        this.image = image;
//        //새로운 이미지와 연관관계 설정
//        if (image != null) {
//            image.getUsers().add(this);
//        }
//    }

    public void addUserCollection(UserCollection userCollection) {
        this.userCollections.add(userCollection);
        if (userCollection.getUser() != this) {
            userCollection.setUser(this);
        }
    }

    public void removeUserCollection(UserCollection userCollection) {
        this.userCollections.remove(userCollection);
        if (userCollection.getUser() == this) {
            userCollection.setUser(null);
        }
    }

    public void changePassword(String newEncodedPassword) {
        this.pw = newEncodedPassword;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateJob(Job job) {
        this.job = job;
    }

    public void updateInterestFields(List<InterestField> interestFields) {
        this.interestFields = interestFields;
    }

    public void updateImage(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

//    public void updateBirthDay(LocalDate birthDay) {
//        this.birthDay = birthDay;
//    }

    public void updatePreferType(MediaType preferType) {
        this.preferType = preferType;
    }
}
