import React, { useState } from 'react';
import { Box, Typography } from '@mui/material';
// eslint-disable-next-line import/no-unresolved
import HelpComponent from '@components/common/HelpComponent';
// eslint-disable-next-line import/no-unresolved
import HelpKanbanboardModal from '@components/common/HelpKanbanboardModal';
import Kanban from '../../components/common/Kanban';
import { useProjects } from '../../provider/projectContext';

const KanbanboardPage = () => {
  const { projects, selectedProjectId } = useProjects();
  const [isHelpModalOpen, setIsHelpModalOpen] = useState(false);

  const selectedProjectTitle =
    projects.find((project) => project.projectId === selectedProjectId)
      ?.title || '프로젝트 선택하기';

  return (
    <Box sx={{ display: 'flex', height: '100vh-9vh' }}>
      <Box
        component="main"
        sx={{
          left: '18vw',
          width: 'calc(100vw - 18vw)',
          height: 'calc(100vh - 9vh)',
          backgroundColor: '#FAFAFA',
          padding: '0 20px',
          overflowY: 'auto',
          overflowX: 'auto',
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
            칸반 보드
          </Typography>
        </Box>

        <Kanban />
      </Box>

      <HelpComponent onClick={() => setIsHelpModalOpen(true)} page="kanban" />

      {isHelpModalOpen && (
        <HelpKanbanboardModal onCancel={() => setIsHelpModalOpen(false)} />
      )}
    </Box>
  );
};

export default KanbanboardPage;
