package be.wiselife.challenge.mapper;

import be.wiselife.challenge.dto.ChallengeDto;
import be.wiselife.challengetalk.dto.ChallengeTalkDto;
import be.wiselife.challengetalk.entity.ChallengeTalk;
import be.wiselife.challengetalk.mapper.ChallengeTalkMapper;
import be.wiselife.member.service.MemberService;
import org.mapstruct.Mapper;

import be.wiselife.challenge.entity.Challenge;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChallengeMapper {
    ChallengeDto.SimpleResponse challengeToChallengeSimpleResponseDto(Challenge challenge);

    /*챌린지 생성 mapping*/
    default Challenge challengePostDtoToChallenge(ChallengeDto.Post challengePostDto) {
        if ( challengePostDto == null ) {
            return null;
        }

        Challenge.ChallengeBuilder challenge = Challenge.builder();

        challenge.challengeTitle( challengePostDto.getChallengeTitle() );
        challenge.challengeDescription( challengePostDto.getChallengeDescription() );
        challenge.challengeMaxParty( challengePostDto.getChallengeMaxParty() );
        challenge.challengeMinParty( challengePostDto.getChallengeMinParty() );
        if ( challengePostDto.getChallengeStartDate() != null ) {
            challenge.challengeStartDate( LocalDate.parse( challengePostDto.getChallengeStartDate() ) );
        }
        if ( challengePostDto.getChallengeEndDate() != null ) {
            challenge.challengeEndDate( LocalDate.parse( challengePostDto.getChallengeEndDate() ) );
        }
        challenge.challengeAuthDescription( challengePostDto.getChallengeAuthDescription() );
        challenge.challengeAuthCycle( challengePostDto.getChallengeAuthCycle() );
        challenge.challengeFeePerPerson( challengePostDto.getChallengeFeePerPerson() );

        /*챌린지 카테고리를 숫자로 받아 enum으로 변환하여 entity에 저장*/
        switch (challengePostDto.getChallengeCategoryId()){
            case 1:
                challenge.challengeCategory(Challenge.ChallengeCategory.BUCKET_LIST);
                break;
            case 2:
                challenge.challengeCategory(Challenge.ChallengeCategory.SHARED_CHALLENGE);
                break;
            case 3:
                challenge.challengeCategory(Challenge.ChallengeCategory.OFFLINE_CHALLENGE);
                break;
        }
        challenge.challengeExamImagePath(challengePostDto.getChallengeExamImagePath());
        challenge.challengeRepImagePath(challengePostDto.getChallengeRepImagePath());

        return challenge.build();
    }

    /* 챌린지patchDto => 챌린지 엔티티 */
    default Challenge challengePatchDtoToChallenge(ChallengeDto.Patch challengePatchDto) {
        if ( challengePatchDto == null ) {
            return null;
        }

        Challenge.ChallengeBuilder challenge = Challenge.builder();
        challenge.challengeId(challengePatchDto.getChallengeId());
        challenge.challengeTitle( challengePatchDto.getChallengeTitle() );
        challenge.challengeDescription( challengePatchDto.getChallengeDescription() );
        challenge.challengeMaxParty( challengePatchDto.getChallengeMaxParty() );
        challenge.challengeMinParty( challengePatchDto.getChallengeMinParty() );
        if ( challengePatchDto.getChallengeStartDate() != null ) {
            challenge.challengeStartDate( LocalDate.parse( challengePatchDto.getChallengeStartDate() ) );
        }
        if ( challengePatchDto.getChallengeEndDate() != null ) {
            challenge.challengeEndDate( LocalDate.parse( challengePatchDto.getChallengeEndDate() ) );
        }
        challenge.challengeAuthDescription( challengePatchDto.getChallengeAuthDescription() );
        challenge.challengeAuthCycle( challengePatchDto.getChallengeAuthCycle() );
        challenge.challengeFeePerPerson( challengePatchDto.getChallengeFeePerPerson() );

        /*챌린지 카테고리를 숫자로 받아 enum으로 변환하여 entity에 저장*/
        switch (challengePatchDto.getChallengeCategoryId()){
            case 1:
                challenge.challengeCategory(Challenge.ChallengeCategory.BUCKET_LIST);
                break;
            case 2:
                challenge.challengeCategory(Challenge.ChallengeCategory.SHARED_CHALLENGE);
                break;
            case 3:
                challenge.challengeCategory(Challenge.ChallengeCategory.OFFLINE_CHALLENGE);
                break;
        }
        challenge.challengeExamImagePath(challengePatchDto.getChallengeExamImagePath());
        challenge.challengeRepImagePath(challengePatchDto.getChallengeRepImagePath());
        return challenge.build();
    }

    /*챌린지 => 챌린지 상세 페이지 조회 detail ResponseDto*/
    default ChallengeDto.DetailResponse challengeToChallengeDetailResponseDto(Challenge challenge, ChallengeTalkMapper challengeTalkMapper, MemberService memberService) {
        if ( challenge == null && challengeTalkMapper == null ) {
            return null;
        }

        ChallengeDto.DetailResponse.DetailResponseBuilder detailResponse = ChallengeDto.DetailResponse.builder();

        if ( challenge != null ) {
            detailResponse.challengeId( challenge.getChallengeId() );
            detailResponse.challengeCategory( challenge.getChallengeCategory() );
            detailResponse.challengeTitle( challenge.getChallengeTitle() );
            detailResponse.challengeDescription( challenge.getChallengeDescription() );
            detailResponse.challengeCurrentParty( challenge.getChallengeCurrentParty() );
            detailResponse.challengeMaxParty( challenge.getChallengeMaxParty() );
            detailResponse.challengeMinParty( challenge.getChallengeMinParty() );
            detailResponse.challengeStartDate( challenge.getChallengeStartDate() );
            detailResponse.challengeEndDate( challenge.getChallengeEndDate() );
            detailResponse.challengeAuthDescription( challenge.getChallengeAuthDescription() );
            detailResponse.challengeAuthCycle( challenge.getChallengeAuthCycle() );
            detailResponse.challengeDirectLink( challenge.getChallengeDirectLink() );
            detailResponse.challengeFeePerPerson( challenge.getChallengeFeePerPerson() );
            detailResponse.challengeTotalReward( challenge.getChallengeTotalReward() );
            detailResponse.challengeViewCount( challenge.getChallengeViewCount() );
            detailResponse.isClosed( challenge.getIsClosed() );
            detailResponse.created_at( challenge.getCreated_at() );
            detailResponse.updated_at( challenge.getUpdated_at() );
            /* 챌린지 댓글을 챌린지 ResponseDto로 변환
            *
            * 챌린지 자체는 memberId를 저장하기에 이를 실제 화면상 보이는 memberName으로 보여줘야 하기에
            * ChallengeTalkMapper ,MemberService 까지 사용해야 한다...
            * */
            if(!challenge.getChallengeTalkList().isEmpty()){
                List<ChallengeTalkDto.response> challengeTalkResponseDtoList = new ArrayList<>();
                for(ChallengeTalk challengeTalk: challenge.getChallengeTalkList()){
                    challengeTalkResponseDtoList.add(challengeTalkMapper.challengeTalkToChallengeTalkResponseDto(challengeTalk, memberService.findMemberById(challengeTalk.getMemberId()).getMemberName()));
                }
                detailResponse.challengeTalks(challengeTalkResponseDtoList);
            }

        }

        return detailResponse.build();
    }
}