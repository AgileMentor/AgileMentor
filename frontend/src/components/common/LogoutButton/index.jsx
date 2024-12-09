import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import { FiLogOut } from 'react-icons/fi';
import axios from 'axios';
import { useProjects } from '../../../provider/projectContext';

const LogoutButton = ({ projectId }) => {
  const { fetchProjects, setSelectedProjectId, fetchMembers } = useProjects();

  const handleButtonClick = async () => {
    try {
      await axios.post(
        `https://api.agilementor.kr/api/projects/${projectId}/leave`,
        {},
        { withCredentials: true }
      );
      alert('프로젝트에서 나갔습니다.');

      setSelectedProjectId(null); 
      fetchMembers(null);
      fetchProjects();
    } catch (error) {
      console.error('Error handling logout:', error);
      alert('작업 중 오류가 발생했습니다. 다시 시도해 주세요.');
    }
  };

  return (
    <Button onClick={handleButtonClick}>
      <LogoutIcon />
      프로젝트 나가기
    </Button>
  );
};

LogoutButton.propTypes = {
  projectId: PropTypes.number.isRequired,
};

export default LogoutButton;

const Button = styled.button`
  display: flex;
  align-items: center;
  background: none;
  border: none;
  color: #4a4a4a;
  font-size: 18px;
  cursor: pointer;
  padding: 10px;
  font-family: 'PaperlogyBold', sans-serif;

  &:hover {
    color: #333;
  }
`;

const LogoutIcon = styled(FiLogOut)`
  margin-right: 8px;
  font-size: 24px;
`;
