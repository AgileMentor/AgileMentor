import React from 'react';
import styled from 'styled-components';
import PropTypes from 'prop-types';

const HelpDashboardModal = ({ onCancel }) => (
  <Overlay>
    <ModalContainer>
      <ModalHeader>대시보드 페이지 도움말</ModalHeader>
      <ModalBody>
        <Section>
          <FeatureTitle>기능 1 - 바로가기 메뉴 이용</FeatureTitle>
          <Image src="/image/siteMenu.png" alt="Site Menu" />
          <Description>
            개발에 자주 이용되는 사이트로 바로 이동할 수 있습니다.
          </Description>
        </Section>
        <Section>
          <FeatureTitle>기능 2 - 내 프로젝트 관리</FeatureTitle>
          <Image src="/image/myProject.png" alt="My Project" />
          <Description>
            새 프로젝트 만들기 버튼을 클릭하여 프로젝트를 생성할 수 있습니다.
          </Description>
          <Description>
            내가 참여하고 있는 프로젝트 목록을 확인할 수 있습니다.
          </Description>
        </Section>
        <Section>
          <FeatureTitle>기능 3 - 내 작업 목록 확인</FeatureTitle>
          <Image src="/image/onTasking.png" alt="onTasking" />
          <Description>
            담당자가 나인 현재 진행중인 백로그를 확인할 수 있습니다.
          </Description>
        </Section>
        <Section>
          <FeatureTitle>기능 4 - 애자일 학습하기기</FeatureTitle>
          <Image src="/image/studyAgile.png" alt="studyAgile" />
          <Description>
            애자일 학습하기 페이지 목록을 확인할 수 있습니다.
          </Description>
          <Description>
            원하는 제목 클릭 시 해당 페이지로 이동합니다.
          </Description>
        </Section>
      </ModalBody>
      <ModalFooter>
        <CancelButton onClick={onCancel}>닫기</CancelButton>
      </ModalFooter>
    </ModalContainer>
  </Overlay>
);

HelpDashboardModal.propTypes = {
  onCancel: PropTypes.func.isRequired,
};

export default HelpDashboardModal;

// Styled components
const Overlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
`;

const ModalContainer = styled.div`
  margin: 5vh 5vw;
  background-color: #fff;
  border-radius: 12px;
  padding: 2rem;
  width: 80%;
  max-width: 450px;
  max-height: 70vh;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  overflow-y: auto;
`;

const ModalHeader = styled.h2`
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1.5rem;
  text-align: center;
`;

const ModalBody = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

const Section = styled.div`
  margin-bottom: 1.5rem;
`;

const FeatureTitle = styled.h3`
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
`;

const Image = styled.img`
  width: 100%;
  height: auto;
  border-radius: 8px;
  margin-bottom: 0.5rem;
`;

const Description = styled.p`
  font-size: 0.9rem;
  color: #333;
  margin-bottom: 0.5rem;
`;

const ModalFooter = styled.div`
  display: flex;
  justify-content: center;
  margin-top: 1.5rem;
`;

const CancelButton = styled.button`
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 30px;
  padding: 0.5rem 1rem;
  font-size: 1rem;
  cursor: pointer;

  &:hover {
    background-color: #0056b3;
  }
`;
