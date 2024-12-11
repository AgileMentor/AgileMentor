import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import axios from 'axios';

const BacklogModal = ({ backlog, onCancel, members, fetchBacklogs }) => {
  const [modalTitle, setModalTitle] = useState('');
  const [story, setStory] = useState('');
  const [description, setDescription] = useState('');
  const [assignee, setAssignee] = useState('');
  const [priority, setPriority] = useState('');
  const [status, setStatus] = useState('');

  useEffect(() => {
    if (backlog) {
      setModalTitle(backlog.title || '백로그 이름');
      setStory(backlog.storyId || '');
      setDescription(backlog.description || '');
      setAssignee(backlog.memberId || '');
      setPriority(backlog.priority?.toLowerCase() || 'medium');
      setStatus(backlog.status?.toLowerCase() || 'todo');
    }
  }, [backlog]);

  const handleConfirm = async () => {
    if (!modalTitle || !priority || !status) {
      alert('모든 필수 값을 입력하세요!');
      return;
    }

    const formattedStatus = {
      todo: 'TODO',
      inprogress: 'IN_PROGRESS',
      done: 'DONE',
    }[status];

    const formattedPriority = priority.toUpperCase();

    const dataToSend = {
      title: modalTitle,
      description,
      priority: formattedPriority,
      status: formattedStatus,
      storyId: story || null,
      memberId: assignee || null,
      sprintId: backlog.sprintId || null,
    };

    try {
      const response = await axios.put(
        `https://api.agilementor.kr/api/projects/${backlog.projectId}/backlogs/${backlog.backlogId}`,
        dataToSend,
        {
          headers: {
            Cookie: document.cookie,
          },
          withCredentials: true,
        },
      );

      if (response.status === 200) {
        alert('백로그가 성공적으로 업데이트되었습니다.');
        fetchBacklogs(backlog.projectId);
        onCancel();
      }
    } catch (error) {
      console.error('백로그 업데이트 중 오류 발생:', error);
      alert('백로그 업데이트에 실패했습니다.');
    }
  };

  return (
    <ModalOverlay>
      <ModalContainer>
        <TitleContainer>
          <EditableTitle
            type="text"
            value={modalTitle}
            onChange={(e) => setModalTitle(e.target.value)}
          />
          <TitleHint>제목을 클릭하여 수정하세요.</TitleHint>
        </TitleContainer>

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
                <option value="">선택하기</option>
                {members.map((member) => (
                  <option key={member.memberId} value={member.memberId}>
                    {member.name}
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
                <option value="medium">중간</option>
                <option value="high">높음</option>
                <option value="low">낮음</option>
              </Select>
            </Column>

            <Column>
              <Label>진행 상태 설정</Label>
              <Select
                value={status}
                onChange={(e) => setStatus(e.target.value)}
              >
                <option value="todo">To Do</option>
                <option value="inprogress">In Progress</option>
                <option value="done">Done</option>
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

BacklogModal.propTypes = {
  backlog: PropTypes.shape({
    backlogId: PropTypes.number.isRequired,
    projectId: PropTypes.number.isRequired,
    title: PropTypes.string,
    storyId: PropTypes.string,
    description: PropTypes.string,
    memberId: PropTypes.string,
    priority: PropTypes.string,
    status: PropTypes.string,
    sprintId: PropTypes.number,
  }).isRequired,
  onCancel: PropTypes.func.isRequired,
  fetchBacklogs: PropTypes.func.isRequired,
  members: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.string.isRequired,
      name: PropTypes.string.isRequired,
    }),
  ).isRequired,
};

export default BacklogModal;

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

const TitleContainer = styled.div`
  margin-bottom: 20px;
`;

const EditableTitle = styled.input`
  font-size: 22px;
  font-weight: bold;
  color: #3c763d;
  border: none;
  text-align: center;
  background: #f9f9f9;
  width: 100%;
  outline: none;
  border-bottom: 2px dashed #ccc;
  padding: 5px;

  &:hover {
    background: #eef;
  }

  &:focus {
    border-bottom: 2px solid #007bff;
    background: #fff;
  }
`;

const TitleHint = styled.p`
  font-size: 12px;
  color: #666;
  margin-top: 5px;
  font-style: italic;
`;

const InputContainer = styled.div`
  margin-bottom: 20px;
  text-align: left;
`;

const Row = styled.div`
  display: flex;
  justify-content: space-between;
  gap: 20px;
`;

const Column = styled.div`
  flex: 1;
`;

const Select = styled.select`
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 5px;
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
`;

const ConfirmButton = styled.button`
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 10px 20px;
`;
const Label = styled.label`
  display: inline-block;
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 5px;
`;

const StyledTextArea = styled.textarea`
  width: 100%;
  height: 80px;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 5px;
`;
