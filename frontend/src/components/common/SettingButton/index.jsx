import React, { useState } from 'react';
import styled from 'styled-components';
import ProjectSettingModal from '../ProjectSettingModal';

const SettingButton = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleOpenModal = () => setIsModalOpen(true);
  const handleCloseModal = () => setIsModalOpen(false);

  return (
    <>
      <Button onClick={handleOpenModal}>설정하기</Button>

      {isModalOpen && (
        <ProjectSettingModal
          onCancel={handleCloseModal}
        />
      )}
    </>
  );
};

export default SettingButton;

const Button = styled.button`
  background-color: #808080;
  color: white;
  border: none;
  border-radius: 0.5rem;
  padding: 0.5rem 0.8rem;
  font-size: 1rem;
  cursor: pointer;
  box-shadow: 0 0.2rem 0.6rem rgba(0, 0, 0, 0.3);
  transition: background-color 0.3s, transform 0.2s;
  width: 25vw;
  height: 4vh;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    background-color: #6e6e6e;
    transform: scale(1.05);
  }

  &:active {
    background-color: #5a5a5a;
    transform: scale(0.95);
  }

  @media (max-width: 768px) {
    font-size: 1.2rem;
    width: 40vw;
    height: 8vh;
  }

  @media (max-width: 480px) {
    font-size: 1rem;
    width: 50vw;
    height: 10vh;
  }
`;

