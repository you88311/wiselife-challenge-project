package be.wiselife.member.entity;

import be.wiselife.audit.TimeAudit;
import be.wiselife.exception.BusinessLogicException;
import be.wiselife.exception.ExceptionCode;
import be.wiselife.follow.entity.Follow;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name="Member_Table")
//Member는 생성일자만 있으면 되므로 TimeAudit만 상속 받는다.
public class Member extends TimeAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String memberDescription = "안녕하세요! 슬린이에요^^";

    //로그인 기능 추가뒤에 로그인 멤버의 토큰에서 이메일값 가져올 예정
    @Column(nullable = false,unique = true)
    private String memberEmail;

    /**
     * 로그인 기능이 추가되어도 로그인 멤버의 토큰에서 가져오지 않으며
     * 생성시에 uuid를 통한 기본값 배정이며,
     * 멤버가 수정시 원하는 값 입력 가능하나, 중복되는 memberName 없이 유효성 동작
     */
    @Column(nullable = false,unique = true)
    private String memberName;

    /**
     * 연관관계 매핑이 필요한 필드
     * 참여중인 챌린지가 없으므로 생성시 기본값 0건, 0원, 이미지는 image
     */

    @Column(nullable = false)
    private int memberExp=0;

    @Enumerated(EnumType.STRING)
    private MemberBadge memberBadge = MemberBadge.새내기;

    private int memberLevel = 1;

    @Column(nullable = false)
    private boolean hasRedCard = false;

    // 아래는 매핑 후에도 ResponseDTO에서 처리 가능한 필드

    @Column(nullable = false)
    private int memberChallengeTotalCount=0;

    @Column(nullable = false)
    private int memberChallengeSuccessCount=0;

    @Column(nullable = false)
    private double memberChallengePercentage=0;

    @Column(nullable = false)
    private double memberMoney=0;

    @Column(nullable = false)
    private String memberImage = "image";

    //이 필드는 팔로우 하트의 음영 처리를 위해 필요한 필드
    @Enumerated(EnumType.STRING)
    private FollowStatus followStatus=FollowStatus.SELF;

    @OneToMany(mappedBy = "following", cascade = CascadeType.PERSIST)
    private Set<Follow> follows = new HashSet<>();

    @Column(nullable = false)
    private int followerCount = 0;

    public void setFollows(Set<Follow> follows) {
        this.follows = follows;
    }


    /**
     * 연관관계 매핑 해야할것
     * voter, image, challenge, challengeReview
     */


    /**
     * 생성자는 필요시 작성예정
     */

    public enum FollowStatus {
        SELF,FOLLOW,UNFOLLOW;
    }
    public enum MemberBadge {
        // 레벨로 나타내면 몇이 최대인지 몰라서 우선 롤 계급제로 분류
        새내기(1),
        좀치는도전자(2),
        열정도전자(3),
        모범도전자(4),
        우수도전자(5),
        챌린지장인(6),
        시간의지배자(7),
        챌린지신(8);

        @Getter
        public int level;

        MemberBadge(int level) {
            this.level = level;
        }

        public static MemberBadge badgeOfLevel(int level) {
            switch (level) {
                case 1:return 새내기;
                case 2:return 좀치는도전자;
                case 3:return 열정도전자;
                case 4:return 모범도전자;
                case 5:return 우수도전자;
                case 6:return 챌린지장인;
                case 7:return 시간의지배자;
                case 8:return 챌린지신;
                default:throw new BusinessLogicException(ExceptionCode.NO_MORE_HIGH_GRADE);
            }
        }
    }

}
