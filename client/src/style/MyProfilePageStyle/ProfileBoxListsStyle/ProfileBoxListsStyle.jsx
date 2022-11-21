import styled from 'styled-components';

// ProfileBoxLists.jsx
export const ProfileBoxComponent = styled.div`
  display: flex;
  flex-direction: column;
  section {
    border: 0.1rem solid #d9d9d9;
  }
  header {
    display: flex;
  }
  .tabs {
    background-color: #eff1fe;
  }
  .active-tabs {
    background-color: #8673ff;
  }
`;

export const Tab = styled.button`
  width: 100px;
  height: 40px;
  border-radius: 15px 15px 0 0;
  text-align: center;
  position: relative;
  border: none;
  cursor: pointer;
`;

// ProfileBoxChallenge.jsx
export const ProfileBoxChallengeComponent = styled.div`
  margin: 2.5rem;
  .challenge-box {
    display: flex;
    margin-top: 2.5rem;
  }
  .challenge-box-image {
    width: 200px;
    height: 200px;
    border-radius: 60px;
    margin-right: 3rem;
  }
  .challenge-box-info {
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
  }
  .challenge-box-info-lists {
    display: flex;
    justify-content: space-between;
    padding-right: 20%;
  }
  .notice {
    text-align: right;
    font-size: 16px;
    color: #d9d9d9;
  }
  tag {
    font-size: 16px;
    background-color: #aec4fa;
    padding: 0.2rem;
    margin: 0.2rem;
    border-radius: 10%;
  }
`;

// ProfileBoxChallengeList.jsx
export const ProfileBoxChallengeListComponent = styled.div`
  margin: 2.5rem;
`;

// ProfileBoxOrderList
export const ProfileBoxOrderListComponent = styled.div`
  margin: 2.5rem;
`;