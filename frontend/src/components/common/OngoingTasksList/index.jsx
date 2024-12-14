import React, { useEffect, useState } from 'react';
import { Box, Typography, Divider } from '@mui/material';
import CircleIcon from '@mui/icons-material/Circle';
import { useProjects } from '../../../provider/projectContext';

const OngoingTasksList = () => {
  const { backlogs, fetchBacklogs, fetchUser, user } = useProjects();
  const [filteredBacklogs, setFilteredBacklogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        await fetchUser();
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    fetchData();
  }, [fetchUser]);

  useEffect(() => {
    if (user?.projectId) {
      fetchBacklogs(user.projectId);
    }
  }, [fetchBacklogs, user?.projectId]);

  useEffect(() => {
    if (backlogs && user) {
      const filteredData = backlogs.filter(
        (backlog) =>
          backlog.memberId === user.memberId &&
          backlog.status === 'IN_PROGRESS',
      );
      setFilteredBacklogs(filteredData);
      setLoading(false);
    } else {
      setLoading(true);
    }
  }, [backlogs, user]);

  const projects = [...new Set(filteredBacklogs.map((task) => task.projectId))];

  if (loading) {
    return <Typography>로딩 중...</Typography>;
  }

  return (
    <Box>
      {projects.map((projectId, index) => (
        <Box key={projectId} mb={1.5}>
          {index > 0 && <Divider sx={{ mb: 1 }} />}
          {filteredBacklogs
            .filter((task) => task.projectId === projectId)
            .map((task) => (
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
                  {projectId}
                </Typography>
              </Box>
            ))}
        </Box>
      ))}
    </Box>
  );
};

export default OngoingTasksList;
