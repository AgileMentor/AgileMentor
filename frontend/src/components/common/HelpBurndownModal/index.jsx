import React from 'react';
import styled from 'styled-components';
import PropTypes from 'prop-types';

const HelpBurndownModal = ({ onCancel }) => (
  <Overlay>
    <ModalContainer>
      <ModalHeader>번다운차트 페이지 도움말</ModalHeader>
      <ModalBody>
        <Section>
          <FeatureTitle>기능 1 - 번다운차트 확인하기</FeatureTitle>
          <Image src="/image/burndown.png" alt="BurndownChart" />
          <Description>
            완료된 작업과 남은 백로그 정보가 담겨있는 번다운차트를 확인할 수
            있습니다.
          </Description>
        </Section>
      </ModalBody>
      <ModalFooter>
        <CancelButton onClick={onCancel}>닫기</CancelButton>
      </ModalFooter>
    </ModalContainer>
  </Overlay>
);

HelpBurndownModal.propTypes = {
  onCancel: PropTypes.func.isRequired,
};

export default HelpBurndownModal;

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
