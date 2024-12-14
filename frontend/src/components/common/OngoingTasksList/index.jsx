import React, { useEffect, useState } from 'react';
import { Box, Typography, Divider } from '@mui/material';
import CircleIcon from '@mui/icons-material/Circle';
import axios from 'axios';
import { useProjects } from '../../../provider/projectContext';

const OngoingTasksList = () => {
  const { projects } = useProjects();
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);

  const convertPriority = (priority) => {
    switch (priority) {
      case 'HIGH':
        return '상';
      case 'MEDIUM':
        return '중';
      case 'LOW':
        return '하';
      default:
        return '알 수 없음';
    }
  };

  useEffect(() => {
    const fetchTasks = async () => {
      try {
        const response = await axios.get(
          'https://api.agilementor.kr/api/tasks',
          {
            withCredentials: true,
          }
        );

        if (response.status === 200) {
          setTasks(response.data);
        }

        setLoading(false);
      } catch (error) {
        console.error('작업 데이터를 가져오는 중 오류 발생:', error);
        alert('작업 데이터를 가져오는 데 실패했습니다.');
        setLoading(false);
      }
    };

    fetchTasks();
  }, []);

  const getProjectName = (projectId) => {
    const project = projects.find((proj) => proj.projectId === projectId);
    return project ? project.title : '알 수 없음';
  };

  if (loading) {
    return <Typography>로딩 중...</Typography>;
  }

  const tasksByProject = tasks.reduce((acc, task) => {
    if (!acc[task.projectId]) {
      acc[task.projectId] = [];
    }
    acc[task.projectId].push(task);
    return acc;
  }, {});

  return (
    <Box>
      {Object.entries(tasksByProject).map(([projectId, projectTasks], index) => (
        <Box key={projectId} mb={1.5}>
          {index > 0 && <Divider sx={{ mb: 1 }} />}
          <Typography variant="h6" sx={{ mb: 1 }}>
            {getProjectName(Number(projectId))}
          </Typography>
          {projectTasks.map((task) => (
            <Box
              key={task.backlogId}
              display="flex"
              alignItems="center"
              justifyContent="space-between"
              mb={1}
            >
              <Box display="flex" alignItems="center">
                <CircleIcon
                  sx={{ color: '#0eaaf9', fontSize: '0.5rem', mr: 1 }}
                />
                <Typography
                  variant="body1"
                  sx={{ fontSize: '1rem', color: '#333' }}
                >
                  {task.title}
                </Typography>
              </Box>
              <Typography
                variant="body2"
                sx={{ color: '#666', fontSize: '0.8rem' }}
              >
                {convertPriority(task.priority)}
              </Typography>
            </Box>
          ))}
        </Box>
      ))}
    </Box>
  );
};

export default OngoingTasksList;
