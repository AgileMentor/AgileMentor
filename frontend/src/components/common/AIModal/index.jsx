import React, { useState } from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import axios from 'axios';
import { useProjects } from '../../../provider/projectContext';


const AIModal = ({ onCancel }) => {
  const { selectedProjectId, addAIResponseData, fetchStories, fetchBacklogs, fetchSprints } = useProjects();
  const [projectDescription, setProjectDescription] = useState('');
  const [storyCount, setStoryCount] = useState('');
  const [sprintCount, setSprintCount] = useState('');
  const [loading, setLoading] = useState(false); 

  const handleConfirm = async () => {
    if (!projectDescription || !storyCount || !sprintCount) {
      alert('모든 필드를 입력해주세요.');
      return;
    }

    if (!selectedProjectId) {
      alert('선택된 프로젝트가 없습니다.');
      return;
    }

    setLoading(true);
    try {
      const response = await axios.post(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}/ai/generate-task`,
        {
          projectDescription,
          storyCount: parseInt(storyCount, 10),
          taskCount: parseInt(sprintCount, 10),
        },
        {
          headers: {
            Cookie: document.cookie,
          },
          withCredentials: true,
        }
      );

      if (response.status === 201 || response.status === 200) {
        console.log('AI 추천 생성 응답:', response.data);
        addAIResponseData(response.data);
        fetchSprints(selectedProjectId);
        fetchBacklogs(selectedProjectId);
        fetchStories(selectedProjectId);
        onCancel();
      }
    } catch (error) {
      console.error('AI 추천 생성 중 오류 발생:', error);
      alert('AI 추천 생성에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <ModalOverlay>
      <ModalContainer>
        <ModalTitle>AI 추천</ModalTitle>

        <InputContainer>
          <Label>프로젝트 설명</Label>
          <StyledTextArea
            placeholder="프로젝트 설명을 입력해주세요."
            value={projectDescription}
            onChange={(e) => setProjectDescription(e.target.value)}
          />
        </InputContainer>

        <InputContainer>
          <Description>스토리 및 스프린트 개수</Description>
          <Row>
            <Label>스토리</Label>
            <SmallInput
              type="number"
              value={storyCount}
              onChange={(e) => setStoryCount(e.target.value)}
            />
            <Label>개</Label>

            <Label>스프린트</Label>
            <SmallInput
              type="number"
              value={sprintCount}
              onChange={(e) => setSprintCount(e.target.value)}
            />
            <Label>개</Label>
          </Row>
        </InputContainer>

        <ButtonContainer>
          <CancelButton onClick={onCancel} disabled={loading}>
            취소
          </CancelButton>
          <ConfirmButton onClick={handleConfirm} disabled={loading}>
            {loading ? '처리 중...' : '완료'}
          </ConfirmButton>
        </ButtonContainer>
      </ModalContainer>
    </ModalOverlay>
  );
};

AIModal.propTypes = {
  onCancel: PropTypes.func.isRequired,
};

export default AIModal;

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
  width: 450px;
  max-width: 90%;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
`;

const ModalTitle = styled.h2`
  font-size: 22px;
  font-weight: bold;
  color: #3c763d;
  margin-bottom: 15px;
`;

const InputContainer = styled.div`
  margin-bottom: 20px;
  text-align: left;
`;

const Description = styled.p`
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
`;

const Label = styled.label`
  display: inline-block;
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-right: 5px;
`;

const StyledTextArea = styled.textarea`
  width: 100%;
  height: 80px;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
  resize: none;
  margin-top: 5px;
`;

const SmallInput = styled.input`
  width: 70px;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
  text-align: center;
`;

const Row = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  gap: 10px;
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
  &:disabled {
    cursor: not-allowed;
    background-color: #e0e0e0;
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
  &:disabled {
    cursor: not-allowed;
    background-color: #a5d8ff;
  }
`;
