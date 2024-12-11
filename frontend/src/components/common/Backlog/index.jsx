import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import { useDrop } from 'react-dnd';
// eslint-disable-next-line import/no-unresolved
import BacklogBar from '@components/common/BacklogBar/index';
import axios from 'axios';
// eslint-disable-next-line import/no-unresolved
import NewBacklogModal from '@components/common/NewBacklogModal/index';
// eslint-disable-next-line import/no-unresolved
import BacklogModal from '@components/common/BacklogModal/index';
// eslint-disable-next-line import/no-unresolved
import Sprint from '@components/common/Sprint';
import { useProjects } from '../../../provider/projectContext';

const Backlog = ({
  backlogItems,
  setBacklogItems,
  setSprintItems,
  sprints = [],
}) => {
  const {
    selectedProjectId,
    fetchMembers,
    fetchSprints,
    fetchBacklogs,
    members,
  } = useProjects();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedBacklog, setSelectedBacklog] = useState(null);

  useEffect(() => {
    if (selectedProjectId) {
      fetchMembers(selectedProjectId);
    }
  }, [selectedProjectId, fetchMembers]);

  const createSprint = async () => {
    if (!selectedProjectId) {
      alert('선택된 프로젝트가 없습니다.');
      return;
    }

    try {
      const response = await axios.post(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}/sprints`,
        {},
        {
          headers: {
            Cookie: document.cookie,
          },
          withCredentials: true,
        },
      );

      if (response.status === 201) {
        fetchSprints(selectedProjectId);
      }
    } catch (error) {
      console.error('스프린트 생성 중 오류 발생:', error);
      alert('스프린트 생성에 실패했습니다.');
    }
  };

  const deleteBacklog = async (backlogId) => {
    if (!selectedProjectId || !backlogId) {
      alert('프로젝트 또는 백로그 정보가 없습니다.');
      return;
    }

    try {
      const response = await axios.delete(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}/backlogs/${backlogId}`,
        {
          headers: {
            Cookie: document.cookie,
          },
          withCredentials: true,
        },
      );

      if (response.status === 204) {
        alert('백로그가 성공적으로 삭제되었습니다.');
        fetchBacklogs(selectedProjectId);
      }
    } catch (error) {
      console.error('백로그 삭제 중 오류 발생:', error);
      alert('백로그 삭제에 실패했습니다.');
    }
  };

  const moveToBacklog = async (item) => {
    const updatedBacklog = { ...item, sprintId: null };

    try {
      const response = await axios.put(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}/backlogs/${item.id}`,
        updatedBacklog,
        { withCredentials: true },
      );

      if (response.status === 200) {
        fetchBacklogs(selectedProjectId);
        fetchSprints(selectedProjectId);
      }
    } catch (error) {
      console.error('백로그 이동 중 오류 발생:', error);
      alert('백로그를 스프린트로 이동하는 데 실패했습니다.');
    }
  };

  const [{ isOver }, drop] = useDrop(() => ({
    accept: 'BACKLOG_ITEM',
    drop: (item) => moveToBacklog(item),
    collect: (monitor) => ({
      isOver: monitor.isOver(),
    }),
  }));

  const noSprintBacklogs = backlogItems.filter(
    (item) => item.sprintId === null || item.sprintId === undefined,
  );

  return (
    <>
      <BacklogContainer ref={drop} isOver={isOver}>
        <Header>
          <HeaderLeft>
            <HeaderTitle>백로그</HeaderTitle>
          </HeaderLeft>
          <CreateButton onClick={createSprint}>스프린트 만들기</CreateButton>
        </Header>
        <BacklogContent>
          {noSprintBacklogs.map((item) => (
            <BacklogBar
              key={item.id}
              id={item.id}
              title={item.title}
              priority={item.priority}
              status={item.status}
              memberId={item.memberId}
              projectId={selectedProjectId}
              fetchBacklogs={fetchBacklogs}
              description={item.description}
              onDelete={deleteBacklog}
              onClick={(data) => {
                setSelectedBacklog(data);
              }}
            />
          ))}
          <AddTask onClick={() => setIsModalOpen(true)}>+ 작업 만들기</AddTask>
        </BacklogContent>
      </BacklogContainer>

      {sprints.map((sprint) => (
        <Sprint
          key={sprint.id}
          sprintId={sprint.id}
          sprintItems={backlogItems.filter(
            (item) => item.sprintId === sprint.id,
          )}
          setSprintItems={setSprintItems}
          setBacklogItems={setBacklogItems}
          fetchSprints={fetchSprints}
          title={sprint.title}
        />
      ))}

      {isModalOpen && (
        <NewBacklogModal
          onCancel={() => setIsModalOpen(false)}
          onConfirm={() => {
            setIsModalOpen(false);
            fetchBacklogs(selectedProjectId);
          }}
          assignees={members}
          projectId={selectedProjectId}
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
    </>
  );
};

Backlog.propTypes = {
  backlogItems: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number.isRequired,
      title: PropTypes.string.isRequired,
      priority: PropTypes.string,
      status: PropTypes.string,
      sprintId: PropTypes.number,
    }),
  ).isRequired,
  setBacklogItems: PropTypes.func.isRequired,
  setSprintItems: PropTypes.func.isRequired,
  sprints: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number.isRequired,
      title: PropTypes.string.isRequired,
    }),
  ).isRequired,
};

export default Backlog;

const BacklogContainer = styled.div`
  background-color: ${(props) => (props.isOver ? '#e6f7ff' : '#ffffff')};
  border: ${(props) => (props.isOver ? '2px dashed #80a7f0' : 'none')};
  border-radius: 8px;
  padding: 1rem;
  height: 30vh;
  box-shadow: 0 0.125rem 0.5rem rgba(0, 0, 0, 0.1);
  overflow-y: auto;
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

const CreateButton = styled.button`
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

const BacklogContent = styled.div`
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
