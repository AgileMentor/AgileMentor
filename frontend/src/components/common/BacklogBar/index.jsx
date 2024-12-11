import React, { useState } from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import { FaUser, FaPlus, FaListUl, FaTrash } from 'react-icons/fa';
import { useDrag } from 'react-dnd';
import axios from 'axios';
import { useProjects } from '../../../provider/projectContext';

const BacklogBar = ({
  id,
  title,
  priority: initialPriority,
  status: initialStatus,
  memberId,
  fetchBacklogs,
  description,
  projectId,
  onDelete,
  onClick,
}) => {
  const { backlogs, setBacklogs, members } = useProjects();
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  const assignee = members.find((member) => member.memberId === memberId);
  const assigneeProfileUrl = assignee?.profileImageUrl || null;

  const [{ isDragging }, drag] = useDrag(() => ({
    type: 'BACKLOG_ITEM',
    item: {
      id,
      title,
      priority: initialPriority,
      status: initialStatus,
      memberId,
      sprintId: null,
      description,
      projectId,
    },
    collect: (monitor) => ({
      isDragging: monitor.isDragging(),
    }),
  }));

  const translatedPriority =
    {
      HIGH: '상',
      MEDIUM: '중',
      LOW: '하',
    }[initialPriority?.toUpperCase()] || '중';

  const handlePriorityChange = () => {
    const newPriority =
      // eslint-disable-next-line no-nested-ternary
      initialPriority === 'HIGH'
        ? 'MEDIUM'
        : initialPriority === 'MEDIUM'
        ? 'LOW'
        : 'HIGH';

    const updatedBacklogs = backlogs.map((backlog) =>
      backlog.id === id ? { ...backlog, priority: newPriority } : backlog,
    );
    setBacklogs(updatedBacklogs);
  };

  const handleStatusChange = async (newStatus) => {
    const formattedStatus = {
      'To Do': 'TODO',
      'In Progress': 'IN_PROGRESS',
      Done: 'DONE',
    }[newStatus];

    const updatedBacklog = backlogs.find((backlog) => backlog.backlogId === id);
    try {
      const response = await axios.put(
        `https://api.agilementor.kr/api/projects/${projectId}/backlogs/${id}`,
        {
          ...updatedBacklog,
          status: formattedStatus,
        },
        {
          headers: {
            Cookie: document.cookie,
          },
          withCredentials: true,
        },
      );

      if (response.status === 200) {
        const updatedBacklogs = backlogs.map((backlog) =>
          backlog.id === id ? { ...backlog, status: formattedStatus } : backlog,
        );
        setBacklogs(updatedBacklogs);
        fetchBacklogs(projectId);
        setIsDropdownOpen(false);
      }
    } catch (error) {
      console.error('백로그 상태 변경 중 오류 발생:', error);
      alert('상태 변경에 실패했습니다.');
    }
  };

  const handleDelete = (e) => {
    e.stopPropagation();
    if (onDelete) onDelete(id);
  };

  const handleClick = async (e) => {
    e.stopPropagation();
    try {
      const response = await axios.get(
        `https://api.agilementor.kr/api/projects/${projectId}/backlogs/${id}`,
        {
          headers: {
            Cookie: document.cookie,
          },
          withCredentials: true,
        },
      );

      if (response.status === 200) {
        onClick(response.data);
      }
    } catch (error) {
      console.error('백로그 데이터 가져오기 실패:', error);
    }
  };

  return (
    <BarContainer ref={drag} isDragging={isDragging} onClick={handleClick}>
      <LeftSection>
        <SprintIcon>
          <FaListUl />
        </SprintIcon>
        <Text>{title}</Text>
      </LeftSection>
      <RightSection>
        <ActionButton color="#FFD771">
          <FaPlus style={{ marginRight: '4px' }} /> Story
        </ActionButton>
        <Dropdown>
          <DropdownButton
            onClick={(e) => {
              e.stopPropagation();
              setIsDropdownOpen(!isDropdownOpen);
            }}
          >
            <DropdownText>{initialStatus}</DropdownText>
            <DropdownArrow>▼</DropdownArrow>
          </DropdownButton>
          {isDropdownOpen && (
            <DropdownMenu>
              {['To Do', 'In Progress', 'Done'].map((option) => (
                <DropdownItem
                  key={option}
                  onClick={(e) => {
                    e.stopPropagation();
                    handleStatusChange(option);
                  }}
                >
                  {option}
                </DropdownItem>
              ))}
            </DropdownMenu>
          )}
        </Dropdown>
        <PriorityBadge
          priority={translatedPriority}
          onClick={(e) => {
            e.stopPropagation();
            handlePriorityChange();
          }}
        >
          {translatedPriority}
        </PriorityBadge>
        <DeleteIcon onClick={handleDelete}>
          <FaTrash />
        </DeleteIcon>
        {assigneeProfileUrl ? (
          <ProfileIcon
            src={assigneeProfileUrl}
            alt="Assignee Profile"
            onError={(e) => {
              e.target.src = 'https://via.placeholder.com/96';
            }}
          />
        ) : (
          <UserIcon>
            <FaUser />
          </UserIcon>
        )}
      </RightSection>
    </BarContainer>
  );
};

BacklogBar.propTypes = {
  id: PropTypes.number.isRequired,
  title: PropTypes.string.isRequired,
  description: PropTypes.string.isRequired,
  priority: PropTypes.oneOf(['HIGH', 'MEDIUM', 'LOW']),
  status: PropTypes.oneOf(['TODO', 'IN_PROGRESS', 'DONE']),
  memberId: PropTypes.number,
  projectId: PropTypes.number.isRequired,
  onDelete: PropTypes.func,
  onClick: PropTypes.func,
  fetchBacklogs: PropTypes.func.isRequired,
};

BacklogBar.defaultProps = {
  priority: 'MEDIUM',
  status: 'TODO',
  memberId: null,
  onDelete: null,
  onClick: null,
};

export default BacklogBar;

const BarContainer = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #eff5ff;
  padding: 0.8rem 1rem;
  border-radius: 8px;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
  opacity: ${(props) => (props.isDragging ? 0.5 : 1)};
  cursor: pointer;
`;

const LeftSection = styled.div`
  display: flex;
  align-items: center;
  gap: 8px;
`;

const SprintIcon = styled.div`
  font-size: 1.2rem;
  color: #333;
`;

const Text = styled.span`
  font-size: 0.9rem;
  font-weight: bold;
  color: #333;
`;

const RightSection = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
`;

const ActionButton = styled.button`
  display: flex;
  align-items: center;
  background-color: ${(props) => props.color || '#ddd'};
  color: white;
  font-size: 0.9rem;
  font-weight: bold;
  border: none;
  border-radius: 4px;
  padding: 0.45rem 1.3rem;
  cursor: pointer;

  &:hover {
    opacity: 0.9;
  }
`;

const Dropdown = styled.div`
  position: relative;
`;

const DropdownButton = styled.div`
  display: flex;
  align-items: center;
  background-color: #bdc8ff;
  color: #ffffff;
  border-radius: 4px;
  padding: 0.25rem 1.3rem;
  cursor: pointer;
`;

const DropdownText = styled.span`
  font-size: 0.8rem;
  font-weight: bold;
`;

const DropdownArrow = styled.span`
  margin-left: 8px;
  font-size: 1.2rem;
`;

const DropdownMenu = styled.div`
  position: absolute;
  top: 100%;
  left: 0;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-top: 4px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  z-index: 10;
`;

const DropdownItem = styled.div`
  padding: 0.3rem 1rem;
  font-size: 0.7rem;
  cursor: pointer;
  color: #333;

  &:hover {
    background-color: #f0f0f0;
  }
`;

const PriorityBadge = styled.div`
  background-color: ${(props) =>
    // eslint-disable-next-line no-nested-ternary
    props.priority === '상'
      ? '#ff6b6b'
      : props.priority === '중'
      ? '#ffd700'
      : '#4caf50'};
  color: white;
  font-weight: bold;
  border-radius: 50%;
  width: 2rem;
  height: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
`;

const DeleteIcon = styled.div`
  font-size: 1.2rem;
  color: #ff6b6b;
  cursor: pointer;

  &:hover {
    opacity: 0.8;
  }
`;

const ProfileIcon = styled.img`
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  object-fit: cover;
`;

const UserIcon = styled.div`
  font-size: 1.2rem;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #d8e2fc;
  border-radius: 50%;
  width: 2rem;
  height: 2rem;
`;
