import React, { useState } from 'react';
import styled from 'styled-components';
import { FiLogOut } from 'react-icons/fi';
import axios from 'axios';
import { useProjects } from '../../../provider/projectContext';

const LogoutButton = () => {
  const { fetchProjects, setSelectedProjectId, selectedProjectId } = useProjects();
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleConfirmLogout = async () => {
    try {
      await axios.post(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}/leave`,
        {},
        { withCredentials: true }
      );
      alert('프로젝트에서 나갔습니다.');

      setSelectedProjectId(null); 
      fetchProjects();
      setIsModalOpen(false);
    } catch (error) {
      console.error('Error handling logout:', error);
      alert('작업 중 오류가 발생했습니다. 다시 시도해 주세요.');
      setIsModalOpen(false);
    }
  };

  const handleCancelLogout = () => {
    setIsModalOpen(false);
  };

  return (
    <>
      <Button onClick={() => setIsModalOpen(true)}>
        <LogoutIcon />
        프로젝트 나가기
      </Button>
      {isModalOpen && (
        <ModalOverlay>
          <ModalContainer>
            <ModalTitle>프로젝트에서 나가시겠습니까?</ModalTitle>
            <ModalButtonContainer>
              <CancelButton onClick={handleCancelLogout}>취소</CancelButton>
              <ConfirmButton onClick={handleConfirmLogout}>확인</ConfirmButton>
            </ModalButtonContainer>
          </ModalContainer>
        </ModalOverlay>
      )}
    </>
  );
};

export default LogoutButton;

const Button = styled.button`
  display: flex;
  align-items: center;
  background: none;
  border: none;
  color: #4a4a4a;
  font-size: 18px;
  cursor: pointer;
  padding: 10px;
  font-family: 'PaperlogyBold', sans-serif;

  &:hover {
    color: #333;
  }
`;

const LogoutIcon = styled(FiLogOut)`
  margin-right: 8px;
  font-size: 24px;
`;

const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
`;

const ModalContainer = styled.div`
  background-color: #fff;
  border-radius: 10px;
  padding: 20px;
  width: 400px;
  max-width: 90%;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
`;

const ModalTitle = styled.h2`
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
`;

const ModalButtonContainer = styled.div`
  display: flex;
  justify-content: center;
  gap: 10px;
`;

const CancelButton = styled.button`
  background-color: #ddd;
  color: #333;
  border: none;
  border-radius: 5px;
  padding: 10px 20px;
  cursor: pointer;

  &:hover {
    background-color: #ccc;
  }
`;

const ConfirmButton = styled.button`
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 5px;
  padding: 10px 20px;
  cursor: pointer;

  &:hover {
    background-color: #0056b3;
  }
`;
