import axios from 'axios';
import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

import {
  Container,
  ChallengeProgress,
  ChallengeImage,
  ChallengeWrapper,
  ChallengeTitle,
  ChallengeDescription,
  Certification,
  CertificationWrapper,
  Review,
} from '../../style/ChallengeDetailProgress/ChallengeDetailProgressStyle';

import Loading from '../Loading/Loading';
import ChartBar from '../ProfileList/ChartBar';
import DdayFormatter from './DdayFormatter';
import Modal from './Modal';
import ImageModal from './ImageModal';
import smile from '../../image/smile.jpg';

export default function ChallengeDetailProgress() {
  const parmas = useParams();
  const [loading, setLoading] = useState(true);
  const [challenge, setChallenge] = useState([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [imageModalOpen, setImageModalOpen] = useState(false);
  const [imageTransform, setImageTransfrom] = useState('');

  //url 파라미터값 받아오기
  const challengeId = Number(parmas.id);

  // 챌린지조회
  const getChallenge = async () => {
    setLoading(true);
    try {
      const response = await axios.get(`/challenges/${challengeId}`, {
        headers: {
          'ngrok-skip-browser-warning': 'none',
        },
      });
      const challengeList = response.data.data;
      console.log('challengeList>>>', challengeList);
      setChallenge(challengeList);
      setLoading(false);
    } catch (error) {
      //useeffect 안에서 window. 쓸필요x
      alert(error);
      console.log('error', error);
    }
  };

  //챌린지조회 axios 실행
  useEffect(() => {
    getChallenge();
  }, []);

  //인증하기 모달창 띄우기
  const showCertificationModal = () => {
    setModalOpen(true);
  };

  //인증사진 더보기 모달창 띄우기
  const showImageModal = () => {
    setImageModalOpen(true);
  };

  //챌린지 진행률 계산
  const today = new Date();
  const startDate = new Date(challenge.challengeStartDate);
  const endDate = new Date(challenge.challengeEndDate);

  //챌린지 총 일수
  const distance = endDate.getTime() - startDate.getTime();
  const totalDay = Math.floor(distance / (1000 * 60 * 60 * 24));

  //챌린지 해온 시간
  const gap = today.getTime() - startDate.getTime();
  const pastDay = Math.floor(gap / (1000 * 60 * 60 * 24));
  console.log('지나온 시간>>', pastDay);
  const progress = Math.ceil((pastDay / totalDay) * 100);
  console.log('진행률>>>', progress);

  // const certification = challenge.challengeCertImages.filter((id) => id === );

  //early return pattern
  if (loading) return <Loading />;

  const imageTest = [
    '인증 예시1',
    '인증 예시2',
    '인증 예시3',
    '인증 예시4',
    '인증 예시5',
    '인증 예시6',
    '인증 예시7',
    '인증 예시8',
    '인증 예시9',
    '인증 예시10',
  ];

  return (
    <Container>
      <div>{`조회수 ${challenge.challengeViewCount}`}</div>
      <ChallengeProgress>
        {/* 이미지 */}
        <div className="image">
          <ChallengeImage src={smile} alt="도전 할 항목의 이미지" />
        </div>

        <ChallengeWrapper>
          {/* 챌린지 이름, 디데이 */}
          <ChallengeTitle>
            <div className="title">{challenge.challengeTitle}</div>
            <div className="d_day">
              <DdayFormatter endDate={challenge.challengeEndDate} />
            </div>
          </ChallengeTitle>

          <ChallengeDescription>
            <div className="margin_left3">챌린지 진행률:</div>
            <div>
              <ChartBar percentage={progress} />
            </div>
          </ChallengeDescription>

          <ChallengeDescription>
            <div className="margin_left2">참여 인원:</div>
            <div>{`${challenge.challengeCurrentParty}명`}</div>
          </ChallengeDescription>

          <ChallengeDescription>
            <div className="margin_left">챌린지 기간:</div>
            <div>{`${challenge.challengeStartDate} ~ ${challenge.challengeEndDate}`}</div>
          </ChallengeDescription>

          <ChallengeDescription>
            <div className="margin_left">챌린지 금액:</div>
            <div>{challenge.challengeFeePerPerson}원</div>
          </ChallengeDescription>

          <ChallengeDescription>
            <div className="margin_left">결제한 금액:</div>
            <div>{challenge.challengeFeePerPerson}원</div>
          </ChallengeDescription>

          <ChallengeDescription>
            <div className="margin_left">도전중인 유저:</div>
            {challenge.participatingMember.map((member) => {
              return (
                <div key={challenge.participatingMember.memberId}>
                  {member.participatingMemberName}
                </div>
              );
            })}
          </ChallengeDescription>
        </ChallengeWrapper>
      </ChallengeProgress>

      <Certification>
        <CertificationWrapper>
          <div className="title">챌린지 설명</div>
          <div className="pd-5">{challenge.challengeDescription}</div>
        </CertificationWrapper>

        <CertificationWrapper>
          <div className="title">인증 방법</div>
          <div className="pd-5">{challenge.challengeAuthDescription}</div>
        </CertificationWrapper>

        <CertificationWrapper>
          <div className="title">인증 예시</div>
          <img src="./img/smile.jpg" alt="*" />
        </CertificationWrapper>
      </Certification>
      {/* challengeId, challengeCertImagePath */}

      <Review>
        <div className="flex">
          <div className="marginRight"> 인증 사진</div>
          <div>
            <div>{`오늘 인증 횟수  / ${challenge.challengeAuthCycle}`}</div>
            <div className="cursur" onClick={showCertificationModal}>
              인증 사진 올리기
            </div>
          </div>
          {modalOpen && (
            <Modal
              setModalOpen={setModalOpen}
              imageTransform={imageTransform}
              setImageTransfrom={setImageTransfrom}
            />
          )}
        </div>
        <div
          style={{
            border: '2px solid #eff1fe',
            marginTop: '3%',
            display: 'grid',
            gridTemplateColumns: 'repeat(4, 1fr)',
            width: '100%',
          }}
        >
          {/* {challenge.challengeExamImagePath.map((image) => { */}
          {imageTest.splice(0, 8).map((image, index) => {
            return (
              <div
                key={index}
                style={{
                  border: '2px solid red',
                  width: '90%',
                  height: '180px',
                  display: 'flex',
                  marginBottom: '3%',
                }}
              >
                {/* <img src="*" alt="" /> */}

                {index === 7 ? (
                  <div
                    style={{
                      backgroundColor: 'grey',
                      display: 'flex',
                      justifyContent: 'center',
                      alignItems: 'center',
                      width: '100%',
                    }}
                  >
                    <div onClick={showImageModal}>더보기gfddhgfhjf</div>
                    {imageModalOpen && (
                      <ImageModal
                        setImageModalOpen={setImageModalOpen}
                        imageTest={imageTest}
                      />
                    )}
                  </div>
                ) : (
                  <div
                    style={{
                      width: '100%',
                    }}
                  >
                    <div>{image}</div>
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </Review>

      <Review>
        <div>후기 사진</div>
        <div style={{ border: '2px solid red', marginTop: '3%' }}>
          <img src="*" alt="" />
        </div>
      </Review>
    </Container>
  );
}