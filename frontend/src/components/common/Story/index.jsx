import React, { useState } from 'react';
import styled from 'styled-components';
import { FaSquare } from 'react-icons/fa';

const Story = () => {
  const [selectedStoryIds, setSelectedStoryIds] = useState([]);

  const stories = [
    { id: 1, title: '스토리1', status: '진행 중' },
    { id: 2, title: '스토리2', status: '완료' },
    { id: 3, title: '스토리3', status: '진행 중' },
  ];

  const toggleSelectStory = (id) => {
    setSelectedStoryIds((prevIds) =>
      prevIds.includes(id)
        ? prevIds.filter((storyId) => storyId !== id)
        : [...prevIds, id],
    );
  };

  return (
    <StoryContainer>
      <Header>스토리</Header>
      <StoryList>
        {stories.map((story) => (
          <StoryItem
            key={story.id}
            isSelected={selectedStoryIds.includes(story.id)}
            onClick={() => toggleSelectStory(story.id)}
          >
            <StoryLeft>
              <StoryIcon />
              <StoryText>{story.title}</StoryText>
            </StoryLeft>
            <StoryStatus status={story.status}>{story.status}</StoryStatus>
          </StoryItem>
        ))}
      </StoryList>
      <AddStory>+ 스토리 만들기</AddStory>
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

const StoryList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.7rem;
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

const StoryIcon = styled(FaSquare)`
  color: #80a7f0;
  font-size: 2rem;
`;

const StoryText = styled.div`
  font-size: 1rem;
  font-weight: bold;

  @media (max-width: 768px) {
    font-size: 0.9rem;
  }
`;

const StoryStatus = styled.div`
  font-size: 0.9rem;
  font-weight: bold;
  color: ${(props) => (props.status === '진행 중' ? '#ffa500' : '#00b300')};
  background: ${(props) =>
    props.status === '진행 중'
      ? 'rgba(255, 165, 0, 0.2)'
      : 'rgba(0, 179, 0, 0.2)'};
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
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
