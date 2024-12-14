import React, { useState, useMemo } from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import { FaUser, FaListUl, FaTrash } from 'react-icons/fa';
import { useDrag } from 'react-dnd';
import axios from 'axios';
import { useProjects } from '../../../provider/projectContext';

const BacklogBar = ({ backlogId }) => {
  const { backlogs, members, fetchBacklogs, selectedProjectId, setselectedBacklogId, stories, fetchStories } = useProjects();
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isStoryDropdownOpen, setIsStoryDropdownOpen] = useState(false);

  const backlogData = backlogs.find((backlog) => backlog.backlogId === backlogId) || {};
  const currentStory = stories.find((story) => story.storyId === backlogData.storyId) || { title: '할당된 스토리 없음' };
  const assignee = members.find((member) => member.memberId === backlogData.memberId);
  const assigneeProfileUrl = assignee?.profileImageUrl || null;

  const itemFn = useMemo(() => backlogData, [backlogData]);

  const spec = useMemo(() => ({
    type: 'BACKLOG_ITEM',
    item: itemFn,
    collect: (monitor) => ({
      isDragging: monitor.isDragging(),
    }),
  }), [itemFn]);

  const [{ isDragging }, drag] = useDrag(spec);

  const translatedPriority =
    {
      HIGH: '상',
      MEDIUM: '중',
      LOW: '하',
    }[backlogData.priority?.toUpperCase()] || '중';

  const handlePriorityChange = async () => {
    if (!backlogData.priority) return;

    let newPriority;
    if (backlogData.priority === 'HIGH') {
      newPriority = 'MEDIUM';
    } else if (backlogData.priority === 'MEDIUM') {
      newPriority = 'LOW';
    } else {
      newPriority = 'HIGH';
    }

    try {
      const response = await axios.put(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}/backlogs/${backlogId}`,
        {
          ...backlogData,
          priority: newPriority,
        },
        {
          withCredentials: true,
        }
      );

      if (response.status === 200) {
        await fetchBacklogs(selectedProjectId);
        alert('우선순위가 성공적으로 업데이트되었습니다.');
      }
    } catch (error) {
      console.error('우선순위 변경 중 오류 발생:', error);
      alert('우선순위 변경에 실패했습니다.');
    }
  };

  const handleStatusChange = async (newStatus) => {
    const formattedStatus = {
      'To Do': 'TODO',
      'In Progress': 'IN_PROGRESS',
      'Done': 'DONE',
    }[newStatus];

    try {
      const response = await axios.put(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}/backlogs/${backlogId}`,
        {
          ...backlogData,
          status: formattedStatus,
        },
        {
          withCredentials: true,
        },
      );

      if (response.status === 200) {
        await fetchBacklogs(selectedProjectId);
        await fetchStories(selectedProjectId);
        setIsDropdownOpen(false);
      }
    } catch (error) {
      console.error('백로그 상태 변경 중 오류 발생:', error);
      alert('상태 변경에 실패했습니다.');
    }
  };

  const deleteBacklog = async () => {
    if (!selectedProjectId || !backlogId) {
      alert('프로젝트 또는 백로그 정보가 없습니다.');
      return;
    }

    try {
      const response = await axios.delete(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}/backlogs/${backlogId}`,
        {
          withCredentials: true,
        },
      );

      if (response.status === 204) {
        alert('백로그가 성공적으로 삭제되었습니다.');
        await fetchBacklogs(selectedProjectId);
      }
    } catch (error) {
      console.error('백로그 삭제 중 오류 발생:', error);
      alert('백로그 삭제에 실패했습니다.');
    }
  };

  const handleStoryChange = async (newStoryId) => {
    try {
      const response = await axios.put(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}/backlogs/${backlogId}`,
        { ...backlogData, storyId: newStoryId === 'none' ? null : newStoryId },
        { withCredentials: true }
      );
  
      if (response.status === 200) {
        await fetchBacklogs(selectedProjectId);
        await fetchStories(selectedProjectId);
        setIsStoryDropdownOpen(false);
        alert('스토리가 성공적으로 업데이트되었습니다.');
      }
    } catch (error) {
      console.error('스토리 변경 중 오류 발생:', error);
      alert('스토리 변경에 실패했습니다.');
    }
  };

  const handleClick = (e) => {
    e.stopPropagation();
    setselectedBacklogId(backlogId);
  };

  return (
    <BarContainer ref={drag} isDragging={isDragging} onClick={handleClick}>
      <LeftSection>
        <SprintIcon>
          <FaListUl />
        </SprintIcon>
        <Text>{backlogData.title}</Text>
      </LeftSection>
      <RightSection>
        <Dropdown>
          <ActionButton
            color="#FFD771"
            onClick={(e) => {
              e.stopPropagation();
              setIsStoryDropdownOpen(!isStoryDropdownOpen);
            }}
          >
            {currentStory.title}
          </ActionButton>
          {isStoryDropdownOpen && (
            <DropdownMenu>
              <DropdownItem
                onClick={(e) => {
                  e.stopPropagation();
                  handleStoryChange('none');
                }}
              >
                할당된 스토리 없음
              </DropdownItem>
              {stories.map((story) => (
                <DropdownItem
                  key={story.storyId}
                  onClick={(e) => {
                    e.stopPropagation();
                    handleStoryChange(story.storyId);
                  }}
                >
                  {story.title}
                </DropdownItem>
              ))}
            </DropdownMenu>
          )}
        </Dropdown>
        <Dropdown>
          <DropdownButton
            onClick={(e) => {
              e.stopPropagation();
              setIsDropdownOpen(!isDropdownOpen);
            }}
          >
            <DropdownText>{backlogData.status}</DropdownText>
            <DropdownArrow>▼</DropdownArrow>
          </DropdownButton>
          {isDropdownOpen && (
            <DropdownMenu>
              {['To Do', 'In Progress', backlogData.sprintId !== null && 'Done']
                .filter(Boolean)
                .map((option) => (
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
        <DeleteIcon
          onClick={(e) => {
            e.stopPropagation();
            deleteBacklog();
          }}
        >
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
  backlogId: PropTypes.number.isRequired,
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
  font-size: 0.8rem;
  font-weight: bold;
  border: none;
  border-radius: 4px;
  padding: 0.45rem 0.5rem;
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
  padding: 0.18rem 0.5rem;
  cursor: pointer;
`;

const DropdownText = styled.span`
  font-size: 0.6rem;
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
  min-width: 130px;
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
  background-color: ${(props) => {
    const colors = new Map([
      ['상', '#ff6b6b'],
      ['중', '#ffd700'],
      ['하', '#4caf50'],
    ]);
    return colors.get(props.priority) || '#4caf50';
  }};
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

