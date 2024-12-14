import React from 'react';
import { Box, Typography } from '@mui/material';
import styled from 'styled-components';
// eslint-disable-next-line import/no-unresolved
import HelpComponent from '@components/common/HelpComponent';
import BurndownChart from '../../components/common/BurndownChart';
import { useProjects } from '../../provider/projectContext';

const BurndownPage = () => {
  const { projects, selectedProjectId } = useProjects();

  const selectedProjectTitle =
    projects.find((project) => project.projectId === selectedProjectId)
      ?.title || '프로젝트 선택하기';

  if (!selectedProjectId) {
    return (
      <EmptyContainer>
        <Message>프로젝트를 선택해주세요.</Message>
      </EmptyContainer>
    );
  }

  return (
    <Box sx={{ display: 'flex', height: 'calc(100vh - 9vh)' }}>
      <Box
        component="main"
        sx={{
          left: '18vw',
          width: 'calc(100vw - 18vw)',
          height: 'calc(100vh - 9vh)',
          backgroundColor: '#FAFAFA',
          padding: '0 3vw',
          overflowY: 'auto',
          color: '#333',
        }}
      >
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'flex-start',
            mb: 3,
            marginTop: '5vh',
          }}
        >
          <Typography
            variant="h6"
            sx={{
              fontWeight: 'bold',
              fontSize: '3.3vh',
              whiteSpace: 'nowrap',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
            }}
          >
            {selectedProjectTitle}
          </Typography>
          <Typography
            variant="body2"
            sx={{
              fontSize: '2vh',
              whiteSpace: 'nowrap',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              marginTop: '0.5vh',
              color: '#3A3A3A',
            }}
          >
            번다운 차트
          </Typography>
        </Box>
        <BurndownChart />
      </Box>
      <HelpComponent page="burndown" />
    </Box>
  );
};

export default BurndownPage;

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
