import React, { useState } from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import axios from 'axios';
import { useProjects } from '../../../provider/projectContext';

const NewBacklogModal = ({
  onCancel,
  onConfirm,
}) => {
  const { fetchBacklogs, selectedProjectId, members, stories, fetchStories } = useProjects();
  const [backlogName, setBacklogName] = useState('');
  const [story, setStory] = useState('');
  const [description, setDescription] = useState('');
  const [assignee, setAssignee] = useState('');
  const [priority, setPriority] = useState('');

  const handleConfirm = async () => {
    if (!backlogName || !description || !priority) {
      alert('백로그 이름, 설명, 우선순위를 입력해주세요.');
      return;
    }

    try {
      const body = {
        title: backlogName,
        description,
        priority: priority.toUpperCase(),
        ...(story && story !== 'none' && { storyId: story }),
        ...(assignee && assignee !== 'none' && { memberId: assignee }),
      };

      await axios.post(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}/backlogs`,
        body,
        { withCredentials: true },
      );

      alert('백로그가 성공적으로 생성되었습니다.');
      onConfirm();
      fetchBacklogs(selectedProjectId);
      fetchStories(selectedProjectId);
      
    } catch (error) {
      console.error('백로그 생성 중 오류:', error);
      alert('백로그 생성에 실패했습니다.');
    }
  };

  return (
    <ModalOverlay>
      <ModalContainer>
        <ModalTitle>백로그 생성하기</ModalTitle>

        <InputContainer>
          <Label>백로그 이름</Label>
          <StyledInput
            type="text"
            placeholder="백로그 이름을 입력하세요."
            value={backlogName}
            onChange={(e) => setBacklogName(e.target.value)}
          />
        </InputContainer>

        <InputContainer>
          <Label>상위 스토리</Label>
          <Select
            value={story}
            onChange={(e) => setStory(e.target.value)}
          >
            <option value="" disabled>선택하기</option>
            <option value="none">상위 스토리가 없는 백로그</option>
            {stories.map((s) => (
              <option key={s.storyId} value={s.storyId}>
                {s.title}
              </option>
            ))}
          </Select>
        </InputContainer>

        <InputContainer>
          <Label>백로그 설명</Label>
          <StyledTextArea
            placeholder="백로그 설명을 입력하세요."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </InputContainer>

        <InputContainer>
          <Row>
            <Column>
              <Label>담당자 선택</Label>
              <Select
                value={assignee}
                onChange={(e) => {
                  setAssignee(e.target.value);
                }}
              >
                <option value="" disabled>선택하기</option>
                <option value="none">담당자가 없는 백로그</option>
                {members.map((user) => (
                  <option key={user.memberId} value={user.memberId}>
                    {user.name}
                  </option>
                ))}
              </Select>
            </Column>
            <Column>
              <Label>우선순위 선택</Label>
              <Select
                value={priority}
                onChange={(e) => setPriority(e.target.value)}
              >
                <option value="" disabled>선택하기</option>
                <option value="HIGH">높음</option>
                <option value="MEDIUM">중간</option>
                <option value="LOW">낮음</option>
              </Select>
            </Column>
          </Row>
        </InputContainer>

        <ButtonContainer>
          <CancelButton onClick={onCancel}>취소</CancelButton>
          <ConfirmButton onClick={handleConfirm}>확인</ConfirmButton>
        </ButtonContainer>
      </ModalContainer>
    </ModalOverlay>
  );
};

NewBacklogModal.propTypes = {
  onCancel: PropTypes.func.isRequired,
  onConfirm: PropTypes.func.isRequired,
};

export default NewBacklogModal;

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
  width: 500px;
  max-width: 90%;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
`;

const ModalTitle = styled.h2`
  font-size: 22px;
  font-weight: bold;
  color: #3c763d;
  margin-bottom: 20px;
`;

const InputContainer = styled.div`
  margin-bottom: 20px;
  text-align: left;
`;

const Label = styled.label`
  display: inline-block;
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
`;

const StyledInput = styled.input`
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
`;

const StyledTextArea = styled.textarea`
  width: 100%;
  height: 80px;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
  resize: none;
`;

const Select = styled.select`
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
  color: #333;

  &:focus {
    border-color: #007bff;
    outline: none;
  }
`;

const Row = styled.div`
  display: flex;
  justify-content: space-between;
  gap: 20px;
`;

const Column = styled.div`
  flex: 1;
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
