import React, { useState } from 'react';
import styled from 'styled-components';
import { FaFolderPlus } from 'react-icons/fa';
// eslint-disable-next-line import/no-extraneous-dependencies
import axios from 'axios';
// eslint-disable-next-line import/no-unresolved
import MinModal from '@components/common/MinModal';
import { useProjects } from '../../../provider/projectContext';

const CreateProjectButton = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [projectTitle, setProjectTitle] = useState('');
  const { fetchProjects } = useProjects();

  const openModal = () => {
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setProjectTitle('');
  };

  const handleConfirm = async () => {
    if (projectTitle.trim() === '') {
      alert('프로젝트 이름을 입력해 주세요.');
      return;
    }

    try {
      const response = await axios.post(
        'https://api.agilementor.kr/api/projects',
        { title: projectTitle },
        {
          withCredentials: true,
        },
      );

      if (response.status === 201) {
        alert('프로젝트가 성공적으로 생성되었습니다.');
        fetchProjects();
        closeModal();
      }
    } catch (error) {
      console.error('Error:', error);
      alert('프로젝트 생성 중 오류가 발생했습니다.');
    }
  };

  return (
    <>
      <Button onClick={openModal}>
        <Icon />
        프로젝트 만들기
      </Button>

      {isModalOpen && (
        <MinModal
          title="프로젝트 생성"
          description="프로젝트 이름"
          value={projectTitle}
          onChange={(e) => setProjectTitle(e.target.value)}
          onCancel={closeModal}
          onConfirm={handleConfirm}
        />
      )}
    </>
  );
};

export default CreateProjectButton;

const Button = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #b0c9f8;
  color: #fff;
  border: none;
  border-radius: 1vh;
  padding: 1.2vh 2vw;
  font-size: 1.8vh;
  font-family: 'PaperlogyBold';
  cursor: pointer;
  width: 15vw;

  @media (max-width: 768px) {
    font-size: 1.2vh;
    padding: 1vh 1.4vw;
  }

  @media (max-width: 480px) {
    font-size: 1vh;
    padding: 0.8vh 1.2vw;
  }
`;

const Icon = styled(FaFolderPlus)`
  margin-right: 0.8vw;
  font-size: 2vh;

  @media (max-width: 768px) {
    font-size: 1.3vh;
  }

  @media (max-width: 480px) {
    font-size: 1.2vh;
  }
`;
