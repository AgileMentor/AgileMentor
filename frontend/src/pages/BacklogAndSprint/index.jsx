import React, { useEffect, useState } from 'react';
import styled from 'styled-components';
// eslint-disable-next-line import/no-unresolved
import Story from '@components/common/Story/index';
// eslint-disable-next-line import/no-unresolved
import Sprint from '@components/common/Sprint/index';
// eslint-disable-next-line import/no-unresolved
import Backlog from '@components/common/Backlog/index';
// eslint-disable-next-line import/no-unresolved
import AIModal from '@components/common/AIModal';
// eslint-disable-next-line import/no-unresolved
import HelpComponent from '@components/common/HelpComponent';
import { useProjects } from '../../provider/projectContext';

const BacklogAndSprintPage = () => {
  const { projects, selectedProjectId, fetchSprints, fetchBacklogs, sprints, setIsSprintActive } =
    useProjects();

  const [showOnlyMyTasks, setShowOnlyMyTasks] = useState(false);
  const [isAIModalOpen, setIsAIModalOpen] = useState(false);

  const selectedProjectTitle =
    projects.find((project) => project.projectId === selectedProjectId)
      ?.title || '프로젝트 선택하기';

  useEffect(() => {
    if (selectedProjectId) {
      fetchSprints(selectedProjectId);
      fetchBacklogs(selectedProjectId);
    }
  }, [selectedProjectId, fetchSprints, fetchBacklogs]);

  useEffect(() => {
    const activeSprintExists = sprints.some((sprint) => sprint.isActivate);
    setIsSprintActive(activeSprintExists);
  }, [sprints]);

  if (!selectedProjectId) {
    return (
      <EmptyContainer>
        <Message>프로젝트를 선택해주세요.</Message>
      </EmptyContainer>
    );
  }

  return (
    <PageContainer>
      <MainContent>
        <HeaderContainer>
          <Title>{selectedProjectTitle}</Title>
          <Subtitle>백로그 및 스프린트</Subtitle>
          <HelpComponent />
        </HeaderContainer>
        <ContentContainer>
          <StoryContainer>
            <Story />
          </StoryContainer>
          <SprintSection>
            <ButtonContainer>
              <MyTasksButton
                onClick={() => setShowOnlyMyTasks(!showOnlyMyTasks)}
              >
                <Checkbox type="checkbox" checked={showOnlyMyTasks} readOnly />
                내 작업만 보기
              </MyTasksButton>
              <AIRecommendationButton
                onClick={() => setIsAIModalOpen(true)}
              >
                <StarIcon>⭐</StarIcon>
                AI 추천
              </AIRecommendationButton>
            </ButtonContainer>
            <SprintContainer>
              {sprints.map((sprint) => (
                <Sprint
                  key={sprint.id}
                  sprintId={sprint.id}
                  showOnlyMyTasks={showOnlyMyTasks}
                />
              ))}
            </SprintContainer>
            <BacklogContainer>
              <Backlog showOnlyMyTasks={showOnlyMyTasks} />
            </BacklogContainer>
          </SprintSection>
        </ContentContainer>
      </MainContent>
      {isAIModalOpen && <AIModal onCancel={() => setIsAIModalOpen(false)} />}
    </PageContainer>
  );
};

export default BacklogAndSprintPage;

const PageContainer = styled.div`
  display: flex;
  height: calc(100vh - 9vh);
`;

const MainContent = styled.main`
  position: relative;
  width: calc(100vw - 18vw);
  height: calc(100vh - 9vh);
  background-color: #fafafa;
  padding: 0 20px;
  overflow-y: auto;
  overflow-x: auto;
  color: #333;
`;

const HeaderContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-left: 3rem;
  margin-top: 2vh;
`;

const Title = styled.h1`
  font-weight: bold;
  font-size: 2rem;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
`;

const Subtitle = styled.h2`
  font-weight: normal;
  font-size: 1.2rem;
  color: #3a3a3a;
  margin-top: 0.3rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
`;

const ContentContainer = styled.div`
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
`;

const StoryContainer = styled.div`
  flex: 1;
  margin-left: 3rem;
`;

const SprintSection = styled.div`
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-right: 3rem;
  position: relative;
`;

const ButtonContainer = styled.div`
  display: flex;
  gap: 1rem;
  position: absolute;
  top: -3rem;
`;

const MyTasksButton = styled.button`
  display: flex;
  align-items: center;
  background-color: #80a7f0;
  color: #fff;
  font-size: 1rem;
  font-weight: bold;
  border: none;
  border-radius: 8px;
  padding: 0.6rem 1.2rem;
  cursor: pointer;
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.1);

  &:hover {
    opacity: 0.9;
  }
`;

const AIRecommendationButton = styled.button`
  display: flex;
  align-items: center;
  background-color: #ffe7b5;
  color: #9146ff;
  font-size: 1rem;
  font-weight: bold;
  border: none;
  border-radius: 8px;
  padding: 0.6rem 1.2rem;
  cursor: pointer;
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.1);

  &:hover {
    opacity: 0.9;
  }
`;

const Checkbox = styled.input`
  appearance: none;
  margin-right: 0.5rem;
  width: 1rem;
  height: 1rem;
  border: 2px solid #fff;
  border-radius: 4px;
  background-color: ${(props) => (props.checked ? '#FFF' : 'transparent')};
  cursor: pointer;

  &:checked {
    background-color: #fff;
  }

  &:focus {
    outline: none;
  }
`;

const StarIcon = styled.span`
  font-size: 1rem;
  color: #9146ff;
  margin-right: 0.5rem;
`;

const SprintContainer = styled.div`
  height: 35vh;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding-right: 0.5rem;
`;

const BacklogContainer = styled.div`
  height: 35vh;
  border-radius: 8px;
  flex-direction: column;
  gap: 1rem;
`;

const EmptyContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  height: calc(100vh - 9vh);
  background-color: #fafafa;
  color: #333;
`;

const Message = styled.div`
  font-size: 1.5rem;
  font-weight: bold;
  color: #555;
`;
