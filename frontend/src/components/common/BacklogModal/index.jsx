import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import axios from 'axios';
import { useProjects } from '../../../provider/projectContext';

const BacklogModal = ({ onCancel }) => {
  const { selectedProjectId, selectedBacklogId, fetchBacklogs, members, fetchStories } = useProjects();
  const [backlog, setBacklog] = useState(null);

  const fetchBacklogDetails = async () => {
    try {
      const response = await axios.get(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}/backlogs/${selectedBacklogId}`,
        {
          withCredentials: true,
        },
      );
      setBacklog(response.data);
    } catch (error) {
      console.error('백로그 데이터를 가져오는 중 오류 발생:', error);
      alert('백로그 데이터를 가져오는 데 실패했습니다.');
      onCancel();
    }
  };
  
  useEffect(() => {
    if (!selectedBacklogId || !selectedProjectId) return;
    fetchBacklogDetails();
  }, [selectedBacklogId, selectedProjectId, onCancel]);

  const handleConfirm = async () => {
    if (!backlog.title || !backlog.description || !backlog.status || !backlog.priority) {
      alert('모든 필수 값을 입력하세요!');
      return;
    }

    const dataToSend = {
      sprintId: backlog.sprintId || null,
      storyId: backlog.storyId || null,
      memberId: backlog.memberId || null,
      title: backlog.title,
      description: backlog.description,
      status: backlog.status,
      priority: backlog.priority,
    };

    try {
      const response = await axios.put(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}/backlogs/${selectedBacklogId}`,
        dataToSend,
        {
          withCredentials: true,
        },
      );

      if (response.status === 200) {
        alert('백로그가 성공적으로 업데이트되었습니다.');
        fetchBacklogs(selectedProjectId);
        fetchStories(selectedProjectId);
        onCancel();
      }
    } catch (error) {
      console.error('백로그 업데이트 중 오류 발생:', error);
      alert('백로그 업데이트에 실패했습니다.');
    }
  };

  if (!backlog) return null;

  return (
    <ModalOverlay>
      <ModalContainer>
        <TitleContainer>
          <EditableTitle
            type="text"
            value={backlog.title || ''}
            onChange={(e) => setBacklog({ ...backlog, title: e.target.value })}
          />
          <TitleHint>제목을 클릭하여 수정하세요.</TitleHint>
        </TitleContainer>

        <InputContainer>
          <Label>백로그 설명</Label>
          <StyledTextArea
            placeholder="백로그 설명을 입력하세요."
            value={backlog.description || ''}
            onChange={(e) => setBacklog({ ...backlog, description: e.target.value })}
          />
        </InputContainer>

        <InputContainer>
          <Row>
            <Column>
              <Label>담당자 선택</Label>
              <Select
                value={backlog.memberId === null ? 'none' : backlog.memberId}
                onChange={(e) =>
                  setBacklog({
                    ...backlog,
                    memberId: e.target.value === 'none' ? null : e.target.value,
                  })
                }
              >
                <option value="none">담당자가 없는 백로그</option>
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
                value={backlog.priority?.toUpperCase() || ''}
                onChange={(e) => setBacklog({ ...backlog, priority: e.target.value.toUpperCase() })}
              >
                <option value="MEDIUM">중간</option>
                <option value="HIGH">높음</option>
                <option value="LOW">낮음</option>
              </Select>
            </Column>

            <Column>
              <Label>진행 상태 설정</Label>
              <Select
                value={backlog.status?.toUpperCase() || ''}
                onChange={(e) => setBacklog({ ...backlog, status: e.target.value.toUpperCase() })}
              >
                {backlog.sprintId === null ? (
                  <>
                    <option value="TODO">To Do</option>
                    <option value="IN_PROGRESS">In Progress</option>
                  </>
                ) : (
                  <>
                    <option value="TODO">To Do</option>
                    <option value="IN_PROGRESS">In Progress</option>
                    <option value="DONE">Done</option>
                  </>
                )}
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
  onCancel: PropTypes.func.isRequired,
};

export default BacklogModal;

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
