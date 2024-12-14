import React, {
  createContext,
  useContext,
  useState,
  useCallback,
  useMemo,
  useEffect,
} from 'react';
import axios from 'axios';

const ProjectContext = createContext();

// eslint-disable-next-line react/prop-types
export const ProjectProvider = ({ children }) => {
  const [projects, setProjects] = useState([]);
  const [selectedProjectId, setSelectedProjectId] = useState(null);
  const [selectedBacklogId, setselectedBacklogId] = useState(null);
  const [sprints, setSprints] = useState([]);
  const [backlogs, setBacklogs] = useState([]);
  const [members, setMembers] = useState([]);
  const [user, setUser] = useState(null);
  const [stories, setStories] = useState([]);
  const [selectedStoryIds, setSelectedStoryIds] = useState([]);
  const [isSprintActive, setIsSprintActive] = useState(false);

  const toggleSelectStory = useCallback((id) => {
    setSelectedStoryIds((prevIds) =>
      prevIds.includes(id)
        ? prevIds.filter((storyId) => storyId !== id)
        : [...prevIds, id],
    );
  }, []);

  useEffect(() => {
    setSelectedStoryIds([]);
  }, [selectedProjectId]);

  const fetchProjects = useCallback(async () => {
    try {
      const response = await axios.get(
        'https://api.agilementor.kr/api/projects',
        {
          withCredentials: true,
        },
      );
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
      const response = await axios.get(
        'https://api.agilementor.kr/api/members',
        {
          withCredentials: true,
        },
      );
      setUser(response.data);
    } catch (err) {
      console.error('사용자 데이터를 가져오는 중 오류 발생:', err);
    }
  }, []);

  const fetchBacklogs = useCallback(async (projectId) => {
    if (!projectId) {
      console.warn('프로젝트 ID가 없습니다.');
      setBacklogs([]);
      return;
    }

    try {
      const response = await axios.get(
        `https://api.agilementor.kr/api/projects/${projectId}/backlogs`,
        {
          withCredentials: true,
        },
      );
      setBacklogs(response.data);
    } catch (error) {
      console.error('백로그 데이터를 가져오는 중 오류 발생:', error);
    }
  }, []);

  const fetchStories = useCallback(async (projectId) => {
    if (!projectId) {
      console.warn('프로젝트 ID가 없습니다.');
      setStories([]);
      return;
    }

    try {
      const response = await axios.get(
        `https://api.agilementor.kr/api/projects/${projectId}/stories`,
        {
          withCredentials: true,
        },
      );
      setStories(response.data);
    } catch (error) {
      console.error('스토리 데이터를 가져오는 중 오류 발생:', error);
    }
  }, []);

  const addAIResponseData = useCallback((responseData) => {
    if (!responseData) {
      console.warn('AI 응답 데이터가 없습니다.');
      return;
    }
  
    const { sprints: newSprints, backlogs: newBacklogs, stories: newStories } = responseData;
  
    setSprints((prevSprints) => [...prevSprints, ...(newSprints || [])]);
    setBacklogs((prevBacklogs) => [...prevBacklogs, ...(newBacklogs || [])]);
    setStories((prevStories) => [...prevStories, ...(newStories || [])]);
  }, []);

  const contextValue = useMemo(
    () => ({
      projects,
      setProjects,
      fetchProjects,
      selectedProjectId,
      setSelectedProjectId,
      selectedBacklogId,
      setselectedBacklogId,
      sprints,
      setSprints,
      fetchSprints,
      backlogs,
      setBacklogs,
      fetchBacklogs,
      members,
      setMembers,
      fetchMembers,
      user,
      setUser,
      fetchUser,
      stories,
      setStories,
      fetchStories,
      selectedStoryIds,
      toggleSelectStory,
      addAIResponseData,
      isSprintActive,
      setIsSprintActive
    }),
    [
      projects,
      selectedProjectId,
      selectedBacklogId,
      sprints,
      backlogs,
      members,
      user,
      stories,
      selectedStoryIds,
      toggleSelectStory,
      isSprintActive
    ],
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
