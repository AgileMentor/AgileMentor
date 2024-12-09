import React, { createContext, useContext, useState, useCallback, useMemo } from 'react';
import axios from 'axios';

const ProjectContext = createContext();

// eslint-disable-next-line react/prop-types
export const ProjectProvider = ({ children }) => {
  const [projects, setProjects] = useState([]);
  const [selectedProjectId, setSelectedProjectId] = useState(null);
  const [sprints, setSprints] = useState([]);
  const [members, setMembers] = useState([]);
  const [user, setUser] = useState(null);

  const fetchProjects = useCallback(async () => {
    try {
      const response = await axios.get('https://api.agilementor.kr/api/projects', {
        withCredentials: true,
      });
      setProjects(response.data);
    } catch (error) {
      console.error('프로젝트 데이터를 가져오는 중 오류 발생:', error);
    }
  }, []);

  const fetchSprints = useCallback(async (projectId) => {
    if (!projectId) {
      console.warn('프로젝트 ID가 없습니다.');
      return;
    }

    try {
      const response = await axios.get(
        `https://api.agilementor.kr/api/projects/${projectId}/sprints`,
        {
          withCredentials: true,
        },
      );
      setSprints(response.data);
    } catch (error) {
      console.error('스프린트 데이터를 가져오는 중 오류 발생:', error);
    }
  }, []);

  const fetchMembers = useCallback(async (projectId) => {
    if (!projectId) {
      console.warn('프로젝트 ID가 없습니다.');
      setMembers([]);
      return;
    }

    try {
      const response = await axios.get(
        `https://api.agilementor.kr/api/projects/${projectId}/members`,
        {
          withCredentials: true,
        },
      );
      setMembers(response.data);
    } catch (error) {
      console.error('멤버 데이터를 가져오는 중 오류 발생:', error);
    }
  }, []);

  const fetchUser = useCallback(async () => {
    try {
      const response = await axios.get('https://api.agilementor.kr/api/members', {
        withCredentials: true,
      });
      setUser(response.data);
    } catch (err) {
      console.error('사용자 데이터를 가져오는 중 오류 발생:', err);
    }
  }, []);

  const contextValue = useMemo(
    () => ({
      projects,
      setProjects,
      fetchProjects,
      selectedProjectId,
      setSelectedProjectId,
      sprints,
      setSprints,
      fetchSprints,
      members,
      fetchMembers,
      user,
      fetchUser,
    }),
    [projects, selectedProjectId, sprints, members, user, fetchProjects, fetchSprints, fetchMembers, fetchUser],
  );

  return (
    <ProjectContext.Provider value={contextValue}>
      {children}
    </ProjectContext.Provider>
  );
};

export const useProjects = () => {
  const context = useContext(ProjectContext);
  if (!context) {
    throw new Error('useProjects must be used within a ProjectProvider');
  }
  return context;
};
