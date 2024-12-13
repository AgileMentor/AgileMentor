import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
import axios from 'axios';
import { FaSquare, FaTrash } from 'react-icons/fa';
import { useProjects } from '../../../provider/projectContext';
import NewStoryModal from '../NewStoryModal';
import StoryDetailsModal from '../StoryDetailsModal';

const Story = () => {
  const {
    selectedProjectId,
    stories,
    fetchStories,
    selectedStoryIds,
    toggleSelectStory,
  } = useProjects();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentStoryId, setCurrentStoryId] = useState(null);

  useEffect(() => {
    if (selectedProjectId) {
      fetchStories(selectedProjectId);
    }
  }, [selectedProjectId, fetchStories]);

  const handleDeleteStory = async (storyId) => {
    if (!selectedProjectId) {
      alert('프로젝트 ID가 없습니다.');
      return;
    }

    try {
      await axios.delete(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}/stories/${storyId}`,
        { withCredentials: true }
      );
      if (selectedStoryIds.includes(storyId)) {
        toggleSelectStory(storyId);
      }
      alert('스토리가 성공적으로 삭제되었습니다.');
      fetchStories(selectedProjectId);
    } catch (error) {
      console.error('스토리 삭제 중 오류:', error);
      alert('스토리 삭제에 실패했습니다.');
    }
  };

  return (
    <StoryContainer>
      <Header>스토리</Header>
      <ScrollableStoryList>
        {stories.length > 0 ? (
          stories.map((story) => (
            <StoryItem
              key={story.storyId}
              isSelected={selectedStoryIds.includes(story.storyId)}
              onClick={() => {
                toggleSelectStory(story.storyId);
              }}
            >
              <StoryLeft>
                <StoryIcon
                  onClick={(e) => {
                    e.stopPropagation();
                    setCurrentStoryId(story.storyId);
                  }}
                />
                <StoryText>{story.title}</StoryText>
              </StoryLeft>
              <StoryActions>
                <StoryStatus status={story.status}>{story.status}</StoryStatus>
                <DeleteButton
                  onClick={(e) => {
                    e.stopPropagation();
                    handleDeleteStory(story.storyId);
                  }}
                >
                  <FaTrash />
                </DeleteButton>
              </StoryActions>
            </StoryItem>
          ))
        ) : (
          <NoStories>스토리가 없습니다.</NoStories>
        )}
      </ScrollableStoryList>
      <AddStory onClick={() => setIsModalOpen(true)}>+ 스토리 만들기</AddStory>

      {isModalOpen && (
        <NewStoryModal onCancel={() => setIsModalOpen(false)} />
      )}

      {currentStoryId && (
        <StoryDetailsModal
          storyId={currentStoryId}
          onClose={() => setCurrentStoryId(null)}
        />
      )}
    </StoryContainer>
  );
};

export default Story;

const StoryContainer = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 90%;
  max-width: 22rem;
  height: 67vh;
  background: #fff;
  border-radius: 0.5rem;
  padding: 1rem;
  box-shadow: 0 0.125rem 0.5rem rgba(0, 0, 0, 0.1);

  @media (max-width: 768px) {
    width: 100%;
    padding: 0.75rem;
  }
`;

const Header = styled.div`
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 1rem;
`;

const ScrollableStoryList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.7rem;
  overflow-y: auto;
  flex-grow: 1;
  max-height: calc(67vh - 4rem); /* Adjust to leave space for header and footer */
`;

const StoryItem = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #eff5ff;
  border-radius: 0.375rem;
  padding: 0.7rem 0.75rem;
  box-shadow: 0 0.0625rem 0.1875rem rgba(0, 0, 0, 0.1);
  cursor: pointer;
  border: ${(props) => (props.isSelected ? '2px solid #80a7f0' : 'none')};
  transition: border 0.2s ease;

  &:hover {
    border: ${(props) =>
      props.isSelected ? '2px solid #80a7f0' : '1px solid #ccc'};
  }

  @media (max-width: 768px) {
    padding: 0.5rem;
  }
`;

const StoryLeft = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
`;

const StoryActions = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
`;

const StoryIcon = styled(FaSquare)`
  color: #80a7f0;
  font-size: 2rem;
  cursor: pointer;

  &:hover {
    color: #0056b3;
  }
`;

const StoryText = styled.div`
  font-size: 1rem;
  font-weight: bold;

  @media (max-width: 768px) {
    font-size: 0.9rem;
  }
`;

const StoryStatus = styled.div`
  font-size: 0.5rem;
  font-weight: bold;
  color: ${(props) => (props.status === '진행 중' ? '#ffa500' : '#00b300')};
  background: ${(props) =>
    props.status === '진행 중'
      ? 'rgba(255, 165, 0, 0.2)'
      : 'rgba(0, 179, 0, 0.2)'};
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
`;

const DeleteButton = styled.button`
  background: transparent;
  border: none;
  cursor: pointer;
  color: #ff6b6b;
  font-size: 1.2rem;

  &:hover {
    color: #ff4c4c;
  }
`;

const AddStory = styled.div`
  font-size: 1rem;
  color: #868e96;
  margin-top: auto;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }

  @media (max-width: 768px) {
    font-size: 0.9rem;
  }
`;

const NoStories = styled.div`
  font-size: 1rem;
  color: #868e96;
  text-align: center;
`;
