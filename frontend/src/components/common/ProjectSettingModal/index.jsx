import React, { useState } from 'react';
import styled from 'styled-components';
import PropTypes from 'prop-types';
import axios from 'axios';
import { useProjects } from '../../../provider/projectContext';
import DeleteConfirmModal from '../DeleteConfirmModal';

const ProjectSettingModal = ({ onCancel }) => {
  const { selectedProjectId, projects, fetchProjects, fetchMembers, setSelectedProjectId } = useProjects();
  const selectedProject = projects.find((project) => project.projectId === selectedProjectId);
  const initialProjectName = selectedProject?.title || '프로젝트 이름 없음';

  const [projectName, setProjectName] = useState(initialProjectName);
  const [isSaving, setIsSaving] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);

  const handleNameChange = (e) => setProjectName(e.target.value);

  const handleConfirm = async () => {
    try {
      setIsSaving(true);
      await axios.put(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}`,
        { title: projectName },
        { withCredentials: true }
      );
      alert('프로젝트 이름이 성공적으로 변경되었습니다.');
      fetchProjects();
      onCancel();
    } catch (error) {
      console.error('프로젝트 이름 변경 중 오류 발생:', error);
      alert('프로젝트 이름 변경에 실패했습니다.');
    } finally {
      setIsSaving(false);
    }
  };

  const handleDelete = () => {
    setIsDeleteModalOpen(true);
  };

  const handleDeleteConfirm = async () => {
    try {
      await axios.delete(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}`,
        { withCredentials: true }
      );
      alert('프로젝트가 성공적으로 삭제되었습니다.');
      setSelectedProjectId(null); 
      fetchMembers(null);
      fetchProjects();
      setIsDeleteModalOpen(false);
      onCancel();
    } catch (error) {
      console.error('프로젝트 삭제 중 오류 발생:', error);
      alert('프로젝트 삭제에 실패했습니다.');
    }
  };

  return (
    <>
      <Overlay>
        <ModalContainer>
          <ModalHeader>프로젝트 설정</ModalHeader>
          <ModalBody>
            <Label>프로젝트 이름 변경하기</Label>
            <Input
              type="text"
              value={projectName}
              onChange={handleNameChange}
            />
            <DeleteSection>
              <DeleteLabel>프로젝트 삭제하기</DeleteLabel>
              <DeleteButton onClick={handleDelete}>
                삭제하기
              </DeleteButton>
            </DeleteSection>
          </ModalBody>
          <ModalFooter>
            <CancelButton onClick={onCancel} disabled={isSaving}>
              취소
            </CancelButton>
            <ConfirmButton onClick={handleConfirm} disabled={isSaving || !projectName.trim()}>
              {isSaving ? '저장 중...' : '완료'}
            </ConfirmButton>
          </ModalFooter>
        </ModalContainer>
      </Overlay>

      {isDeleteModalOpen && (
        <DeleteConfirmModal
          projectId={selectedProjectId}
          onCancel={() => setIsDeleteModalOpen(false)}
          onConfirm={handleDeleteConfirm}
        />
      )}
    </>
  );
};

ProjectSettingModal.propTypes = {
  onCancel: PropTypes.func.isRequired,
};

export default ProjectSettingModal;

const Overlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
`;

const ModalContainer = styled.div`
  background-color: #fff;
  border-radius: 12px;
  padding: 24px;
  width: 360px;
  max-width: 90%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
`;

const ModalHeader = styled.h2`
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1.5rem;
  text-align: center;
`;

const ModalBody = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

const Label = styled.div`
  font-size: 1rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
`;

const Input = styled.input`
  width: 95%;
  padding: 10px;
  font-size: 1rem;
  border: 1px solid #ddd;
  border-radius: 8px;

  &:focus {
    border-color: #007bff;
    outline: none;
  }
`;

const DeleteSection = styled.div`
  margin-top: 1.5rem;
`;

const DeleteLabel = styled.div`
  font-size: 1rem;
  font-weight: bold;
  margin-bottom: 1rem;
`;

const DeleteButton = styled.button`
  background-color: #e53935;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 7px 16px;
  font-size: 0.9rem;
  cursor: pointer;

  &:hover {
    background-color: #b71c1c;
  }
`;

const ModalFooter = styled.div`
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 1.5rem;
`;

const CancelButton = styled.button`
  background-color: #dcdcdc;
  color: #333;
  border: none;
  border-radius: 30px;
  padding: 8px 20px;
  font-size: 1rem;
  cursor: pointer;

  &:hover {
    background-color: #c7c7c7;
  }

  &:disabled {
    background-color: #f0f0f0;
    cursor: not-allowed;
  }
`;

const ConfirmButton = styled.button`
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 30px;
  padding: 8px 20px;
  font-size: 1rem;
  cursor: pointer;

  &:hover {
    background-color: #0056b3;
  }

  &:disabled {
    background-color: #7aa9ff;
    cursor: not-allowed;
  }
`;
