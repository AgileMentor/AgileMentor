import React from 'react';
import styled from 'styled-components';
import PropTypes from 'prop-types';

const HelpSprintModal = ({ onCancel }) => (
  <Overlay>
    <ModalContainer>
      <ModalHeader>백로그 및 스프린트 페이지 도움말</ModalHeader>
      <ModalBody>
        <Section>
          <FeatureTitle>기능 1 - 스프린트 이용</FeatureTitle>
          <Image src="/image/SprintContainer.png" alt="Sprint" />
          <Description>
            스프린트 컨테이너에서 스프린트를 관리할 수 있습니다.
          </Description>
          <Image src="/image/startSprint.png" alt="Sprint" />
          <Description>
            스프린트 시작 버튼을 클릭하여 스프린트를 시작할 수 있습니다.
          </Description>
          <Image src="/image/completeSprint.png" alt="Sprint" />
          <Description>
            스프린트 완료 버튼을 클릭하여 스프린트를 완료할 수 있습니다.
          </Description>
        </Section>
        <Section>
          <FeatureTitle>기능 2 - 백로그 이용</FeatureTitle>
          <Image src="/image/backlogContainer.png" alt="backlog" />
          <Description>
            백로그 컨테이너에서 백로그를 관리할 수 있습니다.
          </Description>
          <Image src="/image/backlogTosprint.png" alt="backlog" />
          <Description>
            백로그를 드래그하여 원하는 스프린트에 적용할 수 있습니다.
          </Description>
          <Description>
            작업 만들기 버튼을 클릭하여 백로그를 생성할 수 있습니다.
          </Description>
        </Section>
        <Section>
          <FeatureTitle>기능 3 - 스토리 이용</FeatureTitle>
          <Image
            src="/image/story.png"
            alt="story"
            style={{ height: '300px', width: '160px' }}
          />
          <Description>
            스토리 컨테이너에서 스토리를 관리할 수 있습니다.
          </Description>
          <Image src="/image/newStory.png" alt="story" />
          <Description>
            스토리 생성 버튼을 클릭하여 새로운 스토리를 생성할 수 있습니다.
          </Description>
          <Image src="/image/clickStory.png" alt="story" />
          <Description>
            스토리를 클릭하여 해당 스토리가 할당된 작업들을 확인할 수 있습니다.
          </Description>
        </Section>
      </ModalBody>
      <ModalFooter>
        <CancelButton onClick={onCancel}>닫기</CancelButton>
      </ModalFooter>
    </ModalContainer>
  </Overlay>
);

HelpSprintModal.propTypes = {
  onCancel: PropTypes.func.isRequired,
};

export default HelpSprintModal;

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
