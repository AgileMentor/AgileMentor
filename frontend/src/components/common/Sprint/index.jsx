import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import { FaTrash } from 'react-icons/fa';
import { useDrop } from 'react-dnd';
import axios from 'axios';
// eslint-disable-next-line import/no-unresolved
import BacklogBar from '@components/common/BacklogBar';
// eslint-disable-next-line import/no-unresolved
import SprintSettingModal from '@components/common/SprintSettingModal';
// eslint-disable-next-line import/no-unresolved
import BacklogModal from '@components/common/BacklogModal/index';
// eslint-disable-next-line import/no-unresolved
import SprintStartModal from '@components/common/SprintStartModal';
import { useProjects } from '../../../provider/projectContext';

const Sprint = ({
  setSprintItems,
  title,
  sprintId,
  projectId,
  fetchSprints,
  fetchBacklogs,
  isDone,
  isActivate,
  showOnlyMyTasks,
  memberId,
}) => {
  const [isStartModalOpen, setIsStartModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [sprintDetails, setSprintDetails] = useState(null);
  const { backlogs, members } = useProjects();
  const [selectedBacklog, setSelectedBacklog] = useState(null);

  useEffect(() => {
    const fetchSprintItems = async () => {
      try {
        const response = await axios.get(
          `https://api.agilementor.kr/api/projects/${projectId}/sprints/${sprintId}`,
          { withCredentials: true },
        );
        setSprintItems(response.data.items);
      } catch (error) {
        console.error('스프린트 데이터 로드 실패:', error);
      }
    };
    fetchSprintItems();
  }, [projectId, sprintId, setSprintItems]);

  useEffect(() => {
    if (projectId && backlogs.length === 0) {
      fetchBacklogs(projectId);
    }
  }, [fetchBacklogs]);

  const sprintBacklogItems = backlogs.filter(
    (backlog) => backlog.sprintId === sprintId,
  );

  const filteredBacklogItems = showOnlyMyTasks
    ? sprintBacklogItems.filter((backlog) => backlog.memberId === memberId)
    : sprintBacklogItems;

  const handleCancelStart = () => {
    setIsStartModalOpen(false);
  };

  const handleCancelEdit = () => {
    setIsEditModalOpen(false);
  };

  const handleSave = () => {
    setIsEditModalOpen(false);
    fetchSprints();
  };

  const fetchSprintDetails = async () => {
    try {
      const response = await axios.get(
        `https://api.agilementor.kr/api/projects/${projectId}/sprints/${sprintId}`,
        { withCredentials: true },
      );
      setSprintDetails(response.data);
      setIsEditModalOpen(true);
    } catch (error) {
      console.error('스프린트 세부 정보 요청 실패:', error);
    }
  };

  const deleteSprint = async () => {
    if (!sprintId || !projectId) {
      alert('스프린트 ID 또는 프로젝트 ID가 없습니다.');
      return;
    }

    try {
      await axios.delete(
        `https://api.agilementor.kr/api/projects/${projectId}/sprints/${sprintId}`,
        { withCredentials: true },
      );
      alert('스프린트가 성공적으로 삭제되었습니다.');
      fetchSprints();
    } catch (error) {
      console.error('스프린트 삭제 중 오류 발생:', error);
      alert('스프린트를 삭제하는 데 실패했습니다.');
    }
  };

  const completeSprint = async () => {
    try {
      const response = await axios.put(
        `https://api.agilementor.kr/api/projects/${projectId}/sprints/${sprintId}/complete`,
        {},
        { withCredentials: true },
      );

      if (response.status === 200) {
        alert('스프린트가 성공적으로 완료되었습니다.');
        fetchSprints();
      }
    } catch (error) {
      console.error('스프린트 완료 요청 실패:', error);
      alert('스프린트를 완료하는 데 실패했습니다.');
    }
  };

  const moveToSprint = async (item, targetSprintId) => {
    const { ...updatedBacklog } = { ...item, sprintId: targetSprintId };
    console.log(updatedBacklog);

    try {
      const response = await axios.put(
        `https://api.agilementor.kr/api/projects/${projectId}/backlogs/${item.id}`,
        updatedBacklog,
        { withCredentials: true },
      );

      if (response.status === 200) {
        fetchBacklogs(projectId);
        fetchSprints(projectId);
      }
    } catch (error) {
      console.error('백로그 이동 중 오류 발생:', error);
      alert('백로그를 스프린트로 이동하는 데 실패했습니다.');
    }
  };

  const deleteBacklog = async (backlogId) => {
    if (!backlogId || !projectId) {
      alert('백로그 ID 또는 프로젝트 ID가 없습니다.');
      return;
    }

    try {
      await axios.delete(
        `https://api.agilementor.kr/api/projects/${projectId}/backlogs/${backlogId}`,
        { withCredentials: true },
      );
      alert('백로그가 성공적으로 삭제되었습니다.');
      fetchBacklogs(projectId);
    } catch (error) {
      console.error('백로그 삭제 중 오류 발생:', error);
      alert('백로그 삭제에 실패했습니다.');
    }
  };

  const [{ isOver }, drop] = useDrop(() => ({
    accept: 'BACKLOG_ITEM',
    drop: (item) => moveToSprint(item, sprintId),
    collect: (monitor) => ({
      isOver: monitor.isOver(),
    }),
  }));

  if (isDone) {
    return null;
  }

  return (
    <SprintContainer
      ref={drop}
      isOver={isOver}
      onClick={(e) => {
        if (isEditModalOpen || isStartModalOpen || selectedBacklog) return;
        e.stopPropagation();
        fetchSprintDetails();
      }}
    >
      <Header>
        <HeaderLeft>
          <HeaderTitle>{title || '스프린트'}</HeaderTitle>
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
        {isActivate ? (
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
            onClick={(e) => {
              e.stopPropagation();
              setIsStartModalOpen(true);
            }}
          >
            스프린트 시작
          </StartButton>
        )}
      </Header>
      <SprintContent>
        {filteredBacklogItems.map((item) => (
          <BacklogBar
            key={item.id}
            id={item.backlogId}
            title={item.title}
            priority={item.priority}
            status={item.status}
            memberId={item.memberId}
            description={item.description}
            fetchBacklogs={fetchBacklogs}
            projectId={projectId}
            onDelete={() => {
              deleteBacklog(item.backlogId);
            }}
            onClick={(data) => {
              setSelectedBacklog(data);
            }}
          />
        ))}
        <AddTask>+ 작업 만들기</AddTask>
      </SprintContent>

      {isStartModalOpen && (
        <SprintStartModal
          onCancel={handleCancelStart}
          projectId={projectId}
          sprintId={sprintId}
          fetchSprints={() => {
            fetchSprints();
          }}
        />
      )}

      {isEditModalOpen && sprintDetails && (
        <SprintSettingModal
          onCancel={handleCancelEdit}
          onSave={handleSave}
          projectId={projectId}
          sprintId={sprintId}
          initialData={{
            title: sprintDetails.title,
            goal: sprintDetails.goal,
            startDate: sprintDetails.startDate,
            endDate: sprintDetails.endDate,
          }}
        />
      )}

      {selectedBacklog && (
        <BacklogModal
          backlog={selectedBacklog}
          members={members}
          onCancel={() => setSelectedBacklog(null)}
          fetchBacklogs={fetchBacklogs}
        />
      )}
    </SprintContainer>
  );
};

Sprint.propTypes = {
  setSprintItems: PropTypes.func.isRequired,
  title: PropTypes.string.isRequired,
  sprintId: PropTypes.number.isRequired,
  projectId: PropTypes.number.isRequired,
  fetchSprints: PropTypes.func.isRequired,
  fetchBacklogs: PropTypes.func.isRequired,
  isDone: PropTypes.bool.isRequired,
  isActivate: PropTypes.bool.isRequired,
  showOnlyMyTasks: PropTypes.bool.isRequired,
  memberId: PropTypes.number.isRequired,
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
