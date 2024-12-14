import React, { useState, useMemo } from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import { FaTrash } from 'react-icons/fa';
import { useDrop } from 'react-dnd';
import axios from 'axios';
import BacklogBar from '../BacklogBar';
import SprintSettingModal from '../SprintSettingModal';
import BacklogModal from '../BacklogModal';
import SprintStartModal from '../SprintStartModal';
import { useProjects } from '../../../provider/projectContext';

const Sprint = ({ sprintId, showOnlyMyTasks }) => {
  const {
    fetchBacklogs,
    fetchSprints,
    selectedProjectId,
    user,
    backlogs,
    sprints,
    selectedBacklogId,
    setselectedBacklogId,
    selectedStoryIds,
    isSprintActive,
  } = useProjects();

  const currentSprint = sprints.find((s) => s.id === sprintId) || {};

  const [isStartModalOpen, setIsStartModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);

  const sprintBacklogItems = (backlogs || []).filter(
    (backlog) => backlog && backlog.sprintId === sprintId,
  );

  const filteredBacklogItems = useMemo(() => {
    let result = showOnlyMyTasks
      ? sprintBacklogItems.filter(
          (backlog) => backlog && backlog.memberId === user?.memberId,
        )
      : sprintBacklogItems;

    if (selectedStoryIds.length > 0) {
      result = result.filter((backlog) => selectedStoryIds.includes(backlog.storyId));
    }

    return result;
  }, [sprintBacklogItems, showOnlyMyTasks, user, selectedStoryIds]);

  const handleCancelStart = () => {
    setIsStartModalOpen(false);
  };

  const handleCancelEdit = () => {
    setIsEditModalOpen(false);
  };

  const deleteSprint = async () => {
    if (!sprintId || !selectedProjectId) {
      alert('스프린트 ID 또는 프로젝트 ID가 없습니다.');
      return;
    }

    try {
      await axios.delete(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}/sprints/${sprintId}`,
        { withCredentials: true },
      );
      alert('스프린트가 성공적으로 삭제되었습니다.');
      fetchSprints(selectedProjectId);
      fetchBacklogs(selectedProjectId);
    } catch (error) {
      console.error('스프린트 삭제 중 오류 발생:', error);
      alert('스프린트를 삭제하는 데 실패했습니다.');
    }
  };

  const completeSprint = async () => {
    try {
      const response = await axios.put(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}/sprints/${sprintId}/complete`,
        {},
        { withCredentials: true },
      );

      if (response.status === 200) {
        alert('스프린트가 성공적으로 완료되었습니다.');
        fetchSprints(selectedProjectId);
        fetchBacklogs(selectedProjectId);
      }
    } catch (error) {
      console.error('스프린트 완료 요청 실패:', error);
      alert('스프린트를 완료하는 데 실패했습니다.');
    }
  };

  const moveToSprint = async (item, targetSprintId) => {
    const { ...updatedBacklog } = { ...item, sprintId: targetSprintId };

    try {
      const response = await axios.put(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}/backlogs/${item.backlogId}`,
        updatedBacklog,
        { withCredentials: true },
      );

      if (response.status === 200) {
        fetchBacklogs(selectedProjectId);
      }
    } catch (error) {
      console.error('백로그 이동 중 오류 발생:', error);
      alert('백로그를 스프린트로 이동하는 데 실패했습니다.');
    }
  };

  const [{ isOver }, drop] = useDrop(() => ({
    accept: 'BACKLOG_ITEM',
    drop: (item) => moveToSprint(item, sprintId),
    collect: (monitor) => ({
      isOver: monitor.isOver(),
    }),
  }));

  if (currentSprint.isDone) {
    return null;
  }

  return (
    <SprintContainer
      ref={drop}
      isOver={isOver}
      onClick={(e) => {
        if (isEditModalOpen || isStartModalOpen || selectedBacklogId) return;
        e.stopPropagation();
        setIsEditModalOpen(true);
      }}
    >
      <Header>
        <HeaderLeft>
          <HeaderTitle>{currentSprint.title || '스프린트'}</HeaderTitle>
          <DeleteButton
            onClick={(e) => {
              e.stopPropagation();
              deleteSprint();
            }}
          >
            <FaTrash style={{ marginRight: '4px' }} />
            삭제
          </DeleteButton>
        </HeaderLeft>
        {currentSprint.isActivate ? (
          <CompleteButton
            onClick={(e) => {
              e.stopPropagation();
              completeSprint();
            }}
          >
            스프린트 완료
          </CompleteButton>
        ) : (
          <StartButton
            disabled={isSprintActive}
            onClick={(e) => {
              if (isSprintActive) return;
              e.stopPropagation();
              setIsStartModalOpen(true);
            }}
          >
            {isSprintActive ? '진행 중 스프린트 있음' : '스프린트 시작'}
          </StartButton>
        )}
      </Header>
      <SprintContent>
        {filteredBacklogItems.map((item) => (
          <BacklogBar key={item.backlogId} backlogId={item.backlogId} />
        ))}
        <AddTask>+ 작업 만들기</AddTask>
      </SprintContent>

      {isStartModalOpen && (
        <SprintStartModal onCancel={handleCancelStart} sprintId={sprintId} />
      )}

      {isEditModalOpen && currentSprint && (
        <SprintSettingModal onCancel={handleCancelEdit} sprintId={sprintId} />
      )}

      {selectedBacklogId && (
        <BacklogModal onCancel={() => setselectedBacklogId(null)} />
      )}
    </SprintContainer>
  );
};

Sprint.propTypes = {
  sprintId: PropTypes.number.isRequired,
  showOnlyMyTasks: PropTypes.bool.isRequired,
};

export default Sprint;

const SprintContainer = styled.div`
  background-color: ${(props) => (props.isOver ? '#e6f7ff' : '#ffffff')};
  border: ${(props) => (props.isOver ? '2px dashed #80a7f0' : 'none')};
  border-radius: 8px;
  padding: 1rem;
  box-shadow: 0 0.125rem 0.5rem rgba(0, 0, 0, 0.1);
  margin-bottom: 1.5rem;
  cursor: pointer;
`;

const Header = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
`;

const HeaderLeft = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
`;

const HeaderTitle = styled.h2`
  font-size: 1.2rem;
  font-weight: bold;
  color: #333;
`;

const DeleteButton = styled.button`
  display: flex;
  align-items: center;
  font-size: 0.9rem;
  color: #ff6b6b;
  background: none;
  border: none;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }

  svg {
    font-size: 0.8rem;
  }
`;

const StartButton = styled.button`
  background-color: #b0c9f8;
  color: #fff;
  font-size: 0.9rem;
  font-weight: bold;
  border: none;
  border-radius: 10px;
  padding: 0.6rem 1rem;
  cursor: pointer;

  &:hover {
    opacity: 0.9;
  }
`;

const CompleteButton = styled(StartButton)`
  background-color: #28a745;

  &:hover {
    opacity: 0.9;
  }
`;

const SprintContent = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

const AddTask = styled.div`
  font-size: 1rem;
  color: #666;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }
`;
