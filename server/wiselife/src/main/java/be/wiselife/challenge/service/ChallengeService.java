package be.wiselife.challenge.service;

import be.wiselife.challenge.entity.Challenge;
import be.wiselife.challenge.repository.ChallengeRepository;
import be.wiselife.exception.BusinessLogicException;
import be.wiselife.exception.ExceptionCode;
import be.wiselife.image.service.ImageService;
import be.wiselife.member.entity.Member;
import be.wiselife.member.service.MemberService;
import be.wiselife.memberchallenge.service.MemberChallengeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * TODO: 수정, 삭제시 권한 확인하는 함수
 * */
@Transactional
@Service
@Slf4j
public class ChallengeService {
    private final ChallengeRepository challengeRepository;
    private final ImageService imageService;
    private final MemberService memberService;
    private final MemberChallengeService memberChallengeService;


    public ChallengeService(ChallengeRepository challengeRepository, ImageService imageService, MemberService memberService, MemberChallengeService memberChallengeService) {
        this.challengeRepository = challengeRepository;
        this.imageService = imageService;
        this.memberService = memberService;
        this.memberChallengeService = memberChallengeService;
    }

    public Challenge createChallenge(Challenge challenge, Member loginMember){
        challenge.setCreate_by_member(loginMember.getMemberName());
        challenge.setAuthorizedMemberId(loginMember.getMemberId());

        imageService.patchChallengeRepImage(challenge);
        imageService.postChallengeExamImage(challenge);
        challenge=participateChallenge(challenge, loginMember);

        return saveChallenge(challenge);
    }

    /**
     * 챌린지 수정 기능
     * TODO: 1) 시작 전 일정, 돈 수정 불가
     *       2) 시작 후 아무것도 수정 불가
     * */
    public Challenge updateChallenge(Challenge changedChallenge, Member loginMember, Long challengeId){
        Challenge existingChallenge = findChallengeById(challengeId);

        //유저 권한 확인
        checkMemberAuthorization(existingChallenge, loginMember);

        //챌린지 수정
        Optional.ofNullable(changedChallenge.getChallengeCategory())
                .ifPresent(existingChallenge::setChallengeCategory);
        Optional.ofNullable(changedChallenge.getChallengeTitle())
                .ifPresent(existingChallenge::setChallengeTitle);
        Optional.ofNullable(changedChallenge.getChallengeDescription())
                .ifPresent(existingChallenge::setChallengeDescription);
        Optional.ofNullable(changedChallenge.getChallengeCurrentParty())
                .ifPresent(existingChallenge::setChallengeCurrentParty);
        Optional.ofNullable(changedChallenge.getChallengeAuthCycle())
                .ifPresent(existingChallenge::setChallengeAuthCycle);
        Optional.ofNullable(changedChallenge.getChallengeStartDate())
                .ifPresent(existingChallenge::setChallengeStartDate);
        Optional.ofNullable(changedChallenge.getChallengeEndDate())
                .ifPresent(existingChallenge::setChallengeEndDate);
        Optional.ofNullable(changedChallenge.getChallengeMaxParty())
                .ifPresent(existingChallenge::setChallengeMaxParty);
        Optional.ofNullable(changedChallenge.getChallengeMinParty())
                .ifPresent(existingChallenge::setChallengeMinParty);
        Optional.ofNullable(changedChallenge.getChallengeViewCount())
                .ifPresent(existingChallenge::setChallengeViewCount);
        Optional.ofNullable(changedChallenge.getChallengeTotalReward())
                .ifPresent(existingChallenge::setChallengeTotalReward);
        Optional.ofNullable(changedChallenge.getIsClosed())
                .ifPresent(existingChallenge::setIsClosed);
        /**
         * 작성자 : 유현
         * 대표 이미지 수정시 사용하는 로직
         */

        if (!Optional.ofNullable(changedChallenge.getChallengeRepImagePath()).isEmpty()) {
            changedChallenge.setRandomIdForImage(existingChallenge.getRandomIdForImage());
            imageService.patchChallengeRepImage(changedChallenge);
            existingChallenge.setChallengeRepImagePath(changedChallenge.getChallengeRepImagePath());
        }

        /**
         * 작성자 : 유현
         * 예시 이미지 수정시 사용하는 로직
         */
        if (!Optional.ofNullable(changedChallenge.getChallengeExamImagePath()).isEmpty()) {
            changedChallenge.setRandomIdForImage(existingChallenge.getRandomIdForImage());
            String challengeExamImagePaths=imageService.patchChallengeExamImage(changedChallenge);
            existingChallenge.setChallengeExamImagePath(challengeExamImagePaths);
        }


        return saveChallenge(existingChallenge);
    }

    /**
     * 챌린지에는 참여인원에 대한 정보를 제공
     * 멤버에는 참여중, 참여했던 챌린지에 대한 정보를 제공
     * @param loginMember 현재 로그인한 유저
     * @param challenge 현재 참여하고자 하는 챌린지
     * @return challenge 참가했을때 잘 참여됐는지 즉시 확인가능
     */
    public Challenge participateChallenge(Challenge challenge,Member loginMember) {
        return memberChallengeService.patchMemberAndChallenge(challenge,loginMember);
    }

    /**
     * 작성자 : 유현
     * 인증사진 등록 / 수정
     * @param certImageInfo 인증사진이 속한 Challenge 아이디와 인증사진 경로
     * @param loginMember 로그인한 사람의 이메일 정보를 가져오기위한 인자값
     * TODO
     * 챌린지 참여인원인지 판단하는 로직 추가
     */
    public Challenge updateCertImage(Challenge certImageInfo, Member loginMember) {



        Challenge challenge = findChallengeById(certImageInfo.getChallengeId());
        challenge.setChallengeCertImagePath(certImageInfo.getChallengeCertImagePath());

        String certImagePath= imageService.patchChallengeCertImage(challenge, loginMember);
        challenge.setChallengeCertImagePath(certImagePath);
        return challengeRepository.save(challenge);
    }

    /**
     * 작성자 : 유현
     * 챌린지 상세페이지 조회(팀원들하고 상의해야하는 부분)
     * 로그인 된 유저가 아닐시 인증사진은 안나오게
     * 로그인 된 유저면 자신이 인증한 사진만 볼 수 있게
     * TODO: 로그인 된 유저 중에 이 챌린지에 참여중인 멤버가 맞는지 판단 로직 필요
     */
    public Challenge getCertification(Challenge certImageInfo,Member loginMember) {

        String certImagePath = imageService.getChallengeCertImage(certImageInfo);

        certImageInfo.setChallengeCertImagePath(certImagePath);
        return certImageInfo;
    }

    public Challenge getChallenge(Long challengeId) {
        return findChallengeById(challengeId);
    }



    public void deleteChallenge(Long challengeId, Member loginMember) {

        Challenge savedChallenge = findChallengeById(challengeId);
        //유저 권한 확인
        checkMemberAuthorization(savedChallenge, loginMember);
        //삭제
        challengeRepository.delete(savedChallenge);
    }

    /**
    * 조회수 증가 함수
    * TODO: cookie를 이용한 중복 조회 기능
    * */
    public Challenge updateViewCount(Challenge challenge){
        challenge.setChallengeViewCount(challenge.getChallengeViewCount() + 1);
        return saveChallenge(challenge);
    }

    public Challenge findChallengeById(Long challengeId){
        return verifyChallengeById(challengeId);
    }

    private Challenge saveChallenge(Challenge challenge){
        return challengeRepository.save(challenge);
    }

    private Challenge verifyChallengeById(Long challengeId){
        return challengeRepository.findById(challengeId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHALLENGE_NOT_FOUND));
    }

    /**
     * 챌랜지 관련 유저의 권한 확인
     * 챌린지 수정, 삭제 시도시 사용한다.
     * @param Challenge 변경 시도하는 챌린지
     * @param loginMember 변경을 시도하는 맴버
     */
    private void checkMemberAuthorization(Challenge Challenge, Member loginMember){

        if(!memberService.isVerifiedMember(Challenge.getAuthorizedMemberId(), loginMember.getMemberId()))
            throw new BusinessLogicException(ExceptionCode.FORBIDDEN_MEMBER);

    }

    /**
     * 인기순 카테고리별 전체 첼린지 조회
     * @param categoryId 1~3 사이의 카테고리 id
     * @return 해당 카테고리의 챌린지 list
     */
    public List<Challenge> getAllChallengesInCategoryOrderByPopularity(Long categoryId) {
        Challenge.ChallengeCategory challengeCategory = categoryIdToChallengeCategory(categoryId);

        List<Challenge> challengeList = challengeRepository.findChallengesByChallengeCategoryOrderByChallengeViewCountDesc(challengeCategory)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHALLENGE_NOT_FOUND));

        return challengeList;
    }

    /**
     * 최신순 카테고리별 전체 챌린지 조회
     * @param categoryId 1~3 사이의 카테고리 id
     * @return 해당 카테고리의 챌린지 list
     */
    public List<Challenge> getAllChallengesInCategoryOrderByNewest(Long categoryId) {
        Challenge.ChallengeCategory challengeCategory = categoryIdToChallengeCategory(categoryId);

        List<Challenge> challengeList = challengeRepository.findChallengesByChallengeCategoryOrderByCreatedAtDesc(challengeCategory)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHALLENGE_NOT_FOUND));

        return challengeList;
    }

    /**
     * 카테고리 id => 카테고리 enum으로 변환
     * @param categoryId
     * @return
     */
    private Challenge.ChallengeCategory categoryIdToChallengeCategory(Long categoryId){
        Challenge.ChallengeCategory challengeCategory = null;

        if (categoryId == 1) {
            challengeCategory = Challenge.ChallengeCategory.BUCKET_LIST;
        } else if (categoryId == 2) {
            challengeCategory = Challenge.ChallengeCategory.SHARED_CHALLENGE;
        } else if (categoryId == 3){
            challengeCategory = Challenge.ChallengeCategory.OFFLINE_CHALLENGE;
        }

        return challengeCategory;
    }


}