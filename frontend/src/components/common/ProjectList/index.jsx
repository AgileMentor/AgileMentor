import React, { useState } from 'react';
import { Box, Typography, IconButton, ButtonBase } from '@mui/material';
import AddRoundedIcon from '@mui/icons-material/AddRounded';
// eslint-disable-next-line import/no-unresolved
import MinModal from '@components/common/MinModal';
// eslint-disable-next-line import/no-extraneous-dependencies
import axios from 'axios';
import { useProjects } from '../../../provider/projectContext';

const ICON_SIZE = 30;
const FONT_SIZE = '1rem';

const ProjectList = () => {
  const { projects, fetchProjects } = useProjects();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [projectTitle, setProjectTitle] = useState('');

  const openModal = () => setIsModalOpen(true);
  const closeModal = () => {
    setIsModalOpen(false);
    setProjectTitle('');
  };

  const handleConfirm = async () => {
    if (projectTitle.trim() === '') {
      alert('프로젝트 이름을 입력해 주세요.');
      return;
    }

    try {
      const response = await axios.post(
        'https://api.agilementor.kr/api/projects',
        { title: projectTitle },
        {
          withCredentials: true,
        },
      );

      if (response.status === 201) {
        alert('새 프로젝트가 성공적으로 생성되었습니다.');
        fetchProjects();
        closeModal();
      }
    } catch (error) {
      console.error('Error:', error);
      alert('프로젝트 생성 중 오류가 발생했습니다.');
    }
  };

  return (
    <Box>
      <Box display="flex" alignItems="center" mb={1}>
        <IconButton
          onClick={openModal}
          sx={{
            width: ICON_SIZE,
            height: ICON_SIZE,
            backgroundColor: '#fff',
            borderRadius: '8px',
            border: '1px solid #ffc107',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#ffc107',
            mr: 1,
            '&:hover': {
              backgroundColor: '#f0f0f0',
            },
          }}
        >
          <AddRoundedIcon fontSize="small" />
        </IconButton>
        <Typography variant="body1" sx={{ color: '#333', fontSize: FONT_SIZE }}>
          새 프로젝트 만들기
        </Typography>
      </Box>

      {projects.map((project) => (
        <Box
          key={project.projectId}
          display="flex"
          alignItems="center"
          justifyContent="space-between"
          mb={0.8}
        >
          <Box display="flex" alignItems="center">
            <Box
              sx={{
                width: ICON_SIZE,
                height: ICON_SIZE,
                backgroundColor: '#ffc107',
                borderRadius: '8px',
                mr: 1,
              }}
            />
            <ButtonBase
              sx={{
                textAlign: 'left',
                color: '#333',
              }}
            >
              <Typography variant="body1" sx={{ fontSize: FONT_SIZE }}>
                {project.title}
              </Typography>
            </ButtonBase>
          </Box>
        </Box>
      ))}

      {isModalOpen && (
        <MinModal
          title="프로젝트 생성"
          description="프로젝트 이름"
          value={projectTitle}
          onChange={(e) => setProjectTitle(e.target.value)}
          onCancel={closeModal}
          onConfirm={handleConfirm}
        />
      )}
    </Box>
  );
};

export default ProjectList;
