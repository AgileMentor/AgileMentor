import React, { useState } from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import axios from 'axios';
import { useProjects } from '../../../provider/projectContext';

const NewStoryModal = ({ onCancel }) => {
  const { selectedProjectId, fetchStories } = useProjects();
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');

  const handleSubmit = async () => {
    if (!title || !description) {
      alert('모든 필드를 입력해주세요.');
      return;
    }

    if (!selectedProjectId) {
      alert('선택된 프로젝트가 없습니다.');
      return;
    }

    try {
      const response = await axios.post(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}/stories`,
        {
          title,
          description,
        },
        {
          withCredentials: true,
        }
      );

      if (response.status === 200) {
        alert('스토리가 성공적으로 생성되었습니다.');
        fetchStories(selectedProjectId);
        onCancel();
      }
    } catch (error) {
      console.error('스토리 생성 중 오류:', error);
      alert('스토리 생성에 실패했습니다.');
    }
  };

  return (
    <ModalOverlay>
      <ModalContainer>
        <ModalTitle>새 스토리 만들기</ModalTitle>
        <InputContainer>
          <Label>스토리 이름</Label>
          <StyledInput
            type="text"
            placeholder="스토리 이름을 입력하세요."
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
        </InputContainer>
        <InputContainer>
          <Label>스토리 설명</Label>
          <StyledTextArea
            placeholder="스토리 설명을 입력하세요."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </InputContainer>
        <ButtonContainer>
          <CancelButton onClick={onCancel}>취소</CancelButton>
          <ConfirmButton onClick={handleSubmit}>생성</ConfirmButton>
        </ButtonContainer>
      </ModalContainer>
    </ModalOverlay>
  );
};

NewStoryModal.propTypes = {
  onCancel: PropTypes.func.isRequired,
};

export default NewStoryModal;

const ModalOverlay = styled.div`
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
  background-color: #fff;
  border-radius: 20px;
  padding: 25px;
  width: 400px;
  max-width: 90%;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
`;

const ModalTitle = styled.h2`
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 15px;
`;

const InputContainer = styled.div`
  margin-bottom: 15px;
  text-align: left;
`;

const Label = styled.label`
  display: block;
  margin-bottom: 5px;
  font-size: 14px;
  font-weight: bold;
`;

const StyledInput = styled.input`
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
`;

const StyledTextArea = styled.textarea`
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  height: 80px;
  resize: none;
`;

const ButtonContainer = styled.div`
  display: flex;
  justify-content: center;
  gap: 20px;
`;

const CancelButton = styled.button`
  background-color: #dcdcdc;
  color: #333;
  border: none;
  border-radius: 20px;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;

  &:hover {
    background-color: #bfbfbf;
  }
`;

const ConfirmButton = styled.button`
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;

  &:hover {
    background-color: #0056b3;
  }
`;
