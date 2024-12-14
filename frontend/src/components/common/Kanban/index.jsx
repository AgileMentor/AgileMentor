import React, { useEffect, useState } from 'react';
import { Box, Paper, Typography } from '@mui/material';
import axios from 'axios';
import KanbanCard from '../KanbanCard';
import { useProjects } from '../../../provider/projectContext';

const Kanban = () => {
  const { selectedProjectId, fetchBacklogs } = useProjects();
  const [tasks, setTasks] = useState([]);

  const loadBacklogs = async () => {
    try {
      const apiUrl = `https://api.agilementor.kr/api/projects/${selectedProjectId}/backlogs/active`;
      const response = await axios.get(apiUrl, {
        withCredentials: true,
      });
      setTasks(response.data);
    } catch (error) {
      console.error('Error fetching backlogs:', error);
    }
  };

  useEffect(() => {
    if (selectedProjectId) {
      loadBacklogs();
    }
  }, [selectedProjectId]);

  const updateTaskStatus = async (id, newStatus) => {
    try {
      const apiUrl = `https://api.agilementor.kr/api/projects/${selectedProjectId}/backlogs/${id}`;
      const taskToUpdate = tasks.find((task) => task.backlogId === id);

      const updatedTask = {
        ...taskToUpdate,
        status: newStatus,
      };

      const response = await axios.put(apiUrl, updatedTask, {
        withCredentials: true,
      });

      if (response.status === 200) {
        await loadBacklogs();
        fetchBacklogs(selectedProjectId);
      }
    } catch (error) {
      console.error('Error updating task status:', error);
      alert('작업 상태 업데이트 중 오류가 발생했습니다.');
    }
  };

  const todoTasks = tasks.filter((task) => task.status === 'TODO');
  const inProgressTasks = tasks.filter((task) => task.status === 'IN_PROGRESS');
  const doneTasks = tasks.filter((task) => task.status === 'DONE');

  return (
    <Box sx={{ display: 'flex', gap: 2, height: '65vh' }}>
      <Paper
        sx={{
          flex: 1,
          padding: 2,
          borderRadius: 2,
          backgroundColor: '#e0f7fa',
          overflowY: 'auto',
          height: '100%',
        }}
        elevation={3}
      >
        <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2, textAlign: 'center' }}>
          To Do
        </Typography>
        {todoTasks.map((task) => (
          <KanbanCard
            key={task.backlogId}
            title={task.title}
            status={task.status}
            titleFontSize="1.1rem"
            onChangeStatus={(newStatus) => updateTaskStatus(task.backlogId, newStatus)}
          />
        ))}
      </Paper>

      <Paper
        sx={{
          flex: 1,
          padding: 2,
          borderRadius: 2,
          backgroundColor: '#fff3e0',
          overflowY: 'auto',
          height: '100%',
        }}
        elevation={3}
      >
        <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2, textAlign: 'center' }}>
          In Progress
        </Typography>
        {inProgressTasks.map((task) => (
          <KanbanCard
            key={task.backlogId}
            title={task.title}
            status={task.status}
            titleFontSize="1.1rem"
            onChangeStatus={(newStatus) => updateTaskStatus(task.backlogId, newStatus)}
          />
        ))}
      </Paper>

      <Paper
        sx={{
          flex: 1,
          padding: 2,
          borderRadius: 2,
          backgroundColor: '#e8f5e9',
          overflowY: 'auto',
          height: '100%',
        }}
        elevation={3}
      >
        <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2, textAlign: 'center' }}>
          Done
        </Typography>
        {doneTasks.map((task) => (
          <KanbanCard
            key={task.backlogId}
            title={task.title}
            status={task.status}
            titleFontSize="1.1rem"
            onChangeStatus={(newStatus) => updateTaskStatus(task.backlogId, newStatus)}
          />
        ))}
      </Paper>
    </Box>
  );
};

export default Kanban;
