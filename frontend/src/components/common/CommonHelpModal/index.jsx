import React from 'react';
import styled from 'styled-components';
import PropTypes from 'prop-types';

const CommonHelpModal = ({ onCancel }) => (
  <Overlay>
    <ModalContainer>
      <ModalHeader>알림 및 사이드바 도움말</ModalHeader>
      <ModalBody>
        <Section>
          <FeatureTitle>기능 1 - 알림 기능 이용</FeatureTitle>
          <Image
            src="/image/bell.png"
            alt="Invite"
            style={{ height: '200px', width: '200px' }}
          />
          <Description>
            알림 버튼을 클릭하여 초대받은 프로젝트를 확인할 수 있습니다.
          </Description>
        </Section>
        <Section>
          <FeatureTitle>기능 2 - 프로젝트 생성하기</FeatureTitle>
          <Image
            src="/image/newProject.png"
            alt="newProjectButton"
            style={{ height: '40px', width: '200px' }}
          />
          <Description>프로젝트 만들기 버튼을 클릭하여</Description>
          <Image
            src="/image/addProject.png"
            alt="addProject"
            style={{ height: '150px', width: '250px' }}
          />
          <Description>새로운 프로젝트를 생성할 수 있습니다.</Description>
        </Section>
        <Section>
          <FeatureTitle>기능 3 - 프로젝트 선택하기</FeatureTitle>
          <Image
            src="/image/selectProject.png"
            alt="selectProject"
            style={{ height: '80px', width: '200px' }}
          />
          <Description>
            프로젝트 선택하기 버튼을 클릭하여 프로젝트를 선택할 수 있습니다.
          </Description>
        </Section>
        <Section>
          <FeatureTitle>기능 4 - 페이지 이동하기</FeatureTitle>
          <Image
            src="/image/navigateMenu.png"
            alt="Invite"
            style={{ height: '200px', width: '200px' }}
          />
          <Description>
            클릭하여 대시보드, 백로그 및 스프린트, 칸반보드, 애자일 학습하기,
            번다운 차트 페이지로 이동할 수 있습니다.
          </Description>
        </Section>
        <Section>
          <FeatureTitle>기능 5 - 멤버 관리하기</FeatureTitle>
          <Image
            src="/image/member.png"
            alt="Member"
            style={{ height: '90px', width: '250px' }}
          />
          <Description>
            현재 프로젝트에 참가 중인 멤버를 확인할 수 있습니다.
          </Description>
          <Image
            src="/image/inviteMember.png"
            alt="Member"
            style={{ height: '150px', width: '250px' }}
          />
          <Description>
            프로젝트에 멤버를 초대하거나 추방할 수 있습니다.
          </Description>
        </Section>
        <Section>
          <FeatureTitle>기능 6 - 프로젝트 관리하기</FeatureTitle>
          <Image
            src="/image/setting.png"
            alt="setting"
            style={{ height: '200px', width: '250px' }}
          />
          <Description>
            선택된 프로젝트를 수정하거나 삭제할 수 있습니다.
          </Description>
          <Image
            src="/image/settingDelete.png"
            alt="Member"
            style={{ height: '100px', width: '250px' }}
          />
          <Description>현재 프로젝트를 나갈 수 있습니다.</Description>
        </Section>
      </ModalBody>
      <ModalFooter>
        <CancelButton onClick={onCancel}>닫기</CancelButton>
      </ModalFooter>
    </ModalContainer>
  </Overlay>
);

CommonHelpModal.propTypes = {
  onCancel: PropTypes.func.isRequired,
};

export default CommonHelpModal;

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
