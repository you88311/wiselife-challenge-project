package be.wiselife.challenge.service;

import be.wiselife.challenge.entity.Challenge;
import be.wiselife.challenge.repository.ChallengeRepository;
import be.wiselife.exception.BusinessLogicException;
import be.wiselife.exception.ExceptionCode;
import be.wiselife.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@Service
@Slf4j
public class ChallengeService {
    private final ChallengeRepository challengeRepository;
    private final ImageService imageService;


    public ChallengeService(ChallengeRepository challengeRepository, ImageService imageService) {

        this.challengeRepository = challengeRepository;
        //이미지 관련 추가를 위한 imageService DI
        this.imageService = imageService;
    }

    public Challenge createChallenge(Challenge challenge){
        imageService.patchChallengeRepImage(challenge);
        return saveChallenge(challenge);
    }

    public Challenge updateChallenge(Challenge changedChallenge){
        Challenge existingChallenge = findChallengeById(changedChallenge.getChallengeId());

        //권한 확인 로직 추가해야??

        /*실제 수정 로직
        * 수정할 값이 null인 경우 수정하지 않는다
        * 추후 수정가능 범위를 어떻게 제한할 것인지 할지 논의 필요함(시작 전 일정, 돈 수정 불가하게 !!! + 시작 후 아무것도 수정 불가)
        * */
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
        Optional.ofNullable(changedChallenge.getChallengeCurrentParty())
                .ifPresent(existingChallenge::setChallengeCurrentParty);
        Optional.ofNullable(changedChallenge.getChallengeViewCount())
                .ifPresent(existingChallenge::setChallengeViewCount);
        Optional.ofNullable(changedChallenge.getChallengeTotalReward())
                .ifPresent(existingChallenge::setChallengeTotalReward);
        Optional.ofNullable(changedChallenge.getIsClosed())
                .ifPresent(existingChallenge::setIsClosed);
        if (!Optional.ofNullable(changedChallenge.getChallengeRepImagePath()).isEmpty()) {
            log.info("image randomId ={}",existingChallenge.getRandomIdForImage());
            changedChallenge.setRandomIdForImage(existingChallenge.getRandomIdForImage());
            imageService.patchChallengeRepImage(changedChallenge);
            existingChallenge.setChallengeRepImagePath(changedChallenge.getChallengeRepImagePath());
        }

        return saveChallenge(existingChallenge);
    }

    public Challenge getChallenge(Long challengeId) {
        return findChallengeById(challengeId);
    }

    public void deleteChallenge(Long challengeId) {
        //권한 확인 필요??
        challengeRepository.delete(findChallengeById(challengeId));

    }

    /*조회수 증가 함수
    *
    * 추후 cookie를 이용한 중복 조회 기능 추가 예정
    * */
    public Challenge updateViewCount(Challenge challenge){
        challenge.setChallengeViewCount(challenge.getChallengeViewCount() + 1);
        return saveChallenge(challenge);
    }



    private Challenge findChallengeById(Long challengeId){
        return verifyChallengeById(challengeId);
    }

    private Challenge saveChallenge(Challenge challenge){
        return challengeRepository.save(challenge);
    }

    private Challenge verifyChallengeById(Long challengeId){
        Challenge savedChallenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CHALLENGE_NOT_FOUND));

        return savedChallenge;
    }



}