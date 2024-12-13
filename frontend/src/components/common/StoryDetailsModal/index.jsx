import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import axios from 'axios';
import { useProjects } from '../../../provider/projectContext';

const StoryDetailsModal = ({ storyId, onClose }) => {
  const { selectedProjectId, fetchStories } = useProjects();
  const [storyDetails, setStoryDetails] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);

  useEffect(() => {
    const fetchStoryDetails = async () => {
      try {
        const response = await axios.get(
          `https://api.agilementor.kr/api/projects/${selectedProjectId}/stories/${storyId}`,
          { withCredentials: true }
        );
        setStoryDetails(response.data);
      } catch (error) {
        console.error('스토리 세부정보 가져오기 중 오류:', error);
        alert('스토리 세부정보를 가져오는 데 실패했습니다.');
      } finally {
        setLoading(false);
      }
    };

    if (storyId) {
      fetchStoryDetails();
    }
  }, [storyId, selectedProjectId]);

  const handleSave = async () => {
    try {
      await axios.put(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}/stories/${storyId}`,
        {
          title: storyDetails.title,
          description: storyDetails.description,
        },
        { withCredentials: true }
      );
      alert('스토리가 성공적으로 수정되었습니다.');
      setIsEditing(false);
      fetchStories(selectedProjectId);
    } catch (error) {
      console.error('스토리 수정 중 오류:', error);
      alert('스토리 수정에 실패했습니다.');
    }
  };

  if (loading) {
    return (
      <ModalOverlay>
        <ModalContainer>
          <ModalTitle>로딩 중...</ModalTitle>
        </ModalContainer>
      </ModalOverlay>
    );
  }

  if (!storyDetails) {
    return (
      <ModalOverlay>
        <ModalContainer>
          <ModalTitle>스토리 정보를 가져올 수 없습니다.</ModalTitle>
          <CloseButton onClick={onClose}>닫기</CloseButton>
        </ModalContainer>
      </ModalOverlay>
    );
  }

  return (
    <ModalOverlay>
      <ModalContainer>
        {isEditing ? (
          <>
            <ModalTitle>스토리 수정</ModalTitle>
            <Label>스토리 이름</Label>
            <Input
              type="text"
              value={storyDetails.title}
              onChange={(e) =>
                setStoryDetails({ ...storyDetails, title: e.target.value })
              }
              placeholder="스토리 이름을 입력하세요"
            />
            <Label>스토리 설명</Label>
            <TextArea
              value={storyDetails.description}
              onChange={(e) =>
                setStoryDetails({
                  ...storyDetails,
                  description: e.target.value,
                })
              }
              placeholder="스토리 설명을 입력하세요"
            />
            <ButtonGroup>
              <SaveButton onClick={handleSave}>저장</SaveButton>
              <CancelButton onClick={() => setIsEditing(false)}>
                취소
              </CancelButton>
            </ButtonGroup>
          </>
        ) : (
          <>
            <ModalTitle>스토리 상세 정보</ModalTitle>
            <InfoContainer>
              <InfoLabel>스토리 이름</InfoLabel>
              <InfoText>{storyDetails.title}</InfoText>
            </InfoContainer>
            <InfoContainer>
              <InfoLabel>스토리 설명</InfoLabel>
              <InfoText>{storyDetails.description}</InfoText>
            </InfoContainer>
            <ButtonGroup>
              <EditButton onClick={() => setIsEditing(true)}>수정</EditButton>
              <CloseButton onClick={onClose}>닫기</CloseButton>
            </ButtonGroup>
          </>
        )}
      </ModalContainer>
    </ModalOverlay>
  );
};

StoryDetailsModal.propTypes = {
  storyId: PropTypes.number.isRequired,
  onClose: PropTypes.func.isRequired,
};

export default StoryDetailsModal;

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
  padding: 20px;
  width: 400px;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
`;

const ModalTitle = styled.h2`
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
`;

const Label = styled.label`
  display: block;
  font-size: 14px;
  font-weight: bold;
  text-align: left;
  margin: 10px 0 5px 0;
`;

const Input = styled.input`
  width: 90%;
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
`;

const TextArea = styled.textarea`
  width: 90%;
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  resize: none;
`;

const InfoContainer = styled.div`
  margin-bottom: 15px;
`;

const InfoLabel = styled.p`
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
`;

const InfoText = styled.p`
  font-size: 14px;
  color: #555;
`;

const ButtonGroup = styled.div`
  display: flex;
  justify-content: center;
  gap: 20px;
`;

const SaveButton = styled.button`
  background-color: #28a745;
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;

  &:hover {
    background-color: #218838;
  }
`;

const CancelButton = styled.button`
  background-color: #6c757d;
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;

  &:hover {
    background-color: #5a6268;
  }
`;

const EditButton = styled.button`
  background-color: #ffc107;
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;

  &:hover {
    background-color: #e0a800;
  }
`;

const CloseButton = styled.button`
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
