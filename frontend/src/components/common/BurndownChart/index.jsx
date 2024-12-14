import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Box, Paper, Typography } from '@mui/material';
// eslint-disable-next-line import/no-extraneous-dependencies
import { LineChart } from '@mui/x-charts';
import { useProjects } from '../../../provider/projectContext';

const BurndownChart = () => {
  const { selectedProjectId } = useProjects();
  const [sprintData, setSprintData] = useState([]);

  const fetchBurndownData = async () => {
    const apiUrl = `https://api.agilementor.kr/api/projects/${selectedProjectId}/sprints/burndown`;

    try {
      const response = await axios.get(apiUrl, {
        withCredentials: true,
      });
      setSprintData(response.data);
    } catch (error) {
      console.error('Error fetching burndown data:', error);
    }
  };

  useEffect(() => {
    if (selectedProjectId) {
      fetchBurndownData();
    }
  }, [selectedProjectId]);

  const completedData = sprintData.map((sprint) => sprint.completedInSprint);
  const remainingData = sprintData.map((sprint) => sprint.remainingBacklogs);

  const xLabels = sprintData.map(
    (sprint, index) => `${sprint.endDate} (#${index + 1})`
  );

  return (
    <Box
      sx={{
        height: '70vh',
        width: '75vw',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
      }}
    >
      <Paper
        sx={{
          height: '100%',
          width: '100%',
          borderRadius: 3,
          padding: 0,
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
        }}
        elevation={3}
      >
        <Typography variant="h6" sx={{ fontSize: '35px', marginTop: '10px' }}>
          번다운 차트
        </Typography>
        <Box
          sx={{
            height: '90%',
            width: '90%',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
          }}
        >
          <LineChart
            width={window.innerWidth * 0.9}
            height={window.innerHeight * 0.75}
            series={[
              { data: completedData, label: '완료된 작업' },
              { data: remainingData, label: '남은 백로그' },
            ]}
            xAxis={[{ scaleType: 'point', data: xLabels }]}
            yAxis={[{ min: 0 }]}
            slotProps={{
              legend: {
                itemGap: 20,
              },
            }}
          />
        </Box>
      </Paper>
    </Box>
  );
};

export default BurndownChart;
