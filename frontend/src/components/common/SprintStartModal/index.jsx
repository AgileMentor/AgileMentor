import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import axios from 'axios';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { DatePicker } from '@mui/x-date-pickers';
import { useProjects } from '../../../provider/projectContext';

const SprintStartModal = ({ onCancel, sprintId }) => {
  const { selectedProjectId, fetchSprints } = useProjects();
  const [sprint, setSprint] = useState(null);

  useEffect(() => {
    const fetchSprintDetails = async () => {
      if (!selectedProjectId || !sprintId) return;
      try {
        const response = await axios.get(
          `https://api.agilementor.kr/api/projects/${selectedProjectId}/sprints/${sprintId}`,
          { withCredentials: true }
        );
        setSprint(response.data);
      } catch (error) {
        console.error('스프린트 정보 불러오기 오류:', error);
        alert('스프린트 정보를 불러오는 데 실패했습니다.');
        onCancel();
      }
    };

    fetchSprintDetails();
  }, [selectedProjectId, sprintId, onCancel]);

  const handleConfirm = async () => {
    if (!sprint?.title || !sprint?.goal || !sprint?.endDate) {
      alert('스프린트 이름, 목표 및 종료 날짜를 모두 입력해주세요.');
      return;
    }

    const formattedEndDate = new Date(sprint.endDate).toISOString().split('T')[0];

    try {
      const response = await axios.put(
        `https://api.agilementor.kr/api/projects/${selectedProjectId}/sprints/${sprintId}/start`,
        {
          title: sprint.title,
          goal: sprint.goal,
          endDate: formattedEndDate,
        },
        {
          withCredentials: true,
        }
      );

      if (response.status === 200) {
        fetchSprints(selectedProjectId);
        alert('스프린트가 성공적으로 시작되었습니다.');
        onCancel();
      }
    } catch (error) {
      console.error('스프린트 시작 중 오류:', error);
      alert('스프린트를 시작하는 데 실패했습니다.');
    }
  };

  if (!sprint) {
    return (
      <ModalOverlay>
        <ModalContainer>
          <ModalTitle>로딩 중...</ModalTitle>
        </ModalContainer>
      </ModalOverlay>
    );
  }

  return (
    <ModalOverlay>
      <ModalContainer>
        <ModalTitle>스프린트 시작</ModalTitle>
        <Subtitle>스프린트 정보를 수정하여 시작합니다.</Subtitle>

        <InputContainer>
          <Label>스프린트 이름</Label>
          <StyledInput
            type="text"
            placeholder="스프린트 이름을 입력하세요."
            value={sprint.title || ''}
            onChange={(e) => setSprint((prev) => ({ ...prev, title: e.target.value }))}
          />
        </InputContainer>

        <DatePickerContainer>
          <LocalizationProvider dateAdapter={AdapterDateFns}>
            <DatePicker
              label="종료 날짜"
              value={sprint.endDate ? new Date(sprint.endDate) : null}
              onChange={(newValue) =>
                setSprint((prev) => ({ ...prev, endDate: newValue }))
              }
            />
          </LocalizationProvider>
        </DatePickerContainer>

        <InputContainer>
          <Label>스프린트 목표</Label>
          <StyledTextArea
            placeholder="스프린트 목표를 입력하세요."
            value={sprint.goal || ''}
            onChange={(e) => setSprint((prev) => ({ ...prev, goal: e.target.value }))}
          />
        </InputContainer>

        <ButtonContainer>
          <CancelButton onClick={onCancel}>취소</CancelButton>
          <ConfirmButton onClick={handleConfirm}>완료</ConfirmButton>
        </ButtonContainer>
      </ModalContainer>
    </ModalOverlay>
  );
};

SprintStartModal.propTypes = {
  onCancel: PropTypes.func.isRequired,
  sprintId: PropTypes.number.isRequired,
};

export default SprintStartModal;

const ModalOverlay = styled.div`
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
  border-radius: 20px;
  padding: 25px;
  width: 450px;
  max-width: 90%;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
`;

const ModalTitle = styled.h2`
  font-size: 22px;
  font-weight: bold;
  color: #3c763d;
  margin-bottom: 10px;
`;

const Subtitle = styled.p`
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
`;

const InputContainer = styled.div`
  margin-bottom: 20px;
  text-align: left;
`;

const Label = styled.label`
  display: inline-block;
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
`;

const StyledInput = styled.input`
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
`;

const StyledTextArea = styled.textarea`
  width: 100%;
  height: 80px;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
  resize: none;
`;

const DatePickerContainer = styled.div`
  flex: 1;
`;

const ButtonContainer = styled.div`
  display: flex;
  justify-content: center;
  gap: 20px;
`;

const CancelButton = styled.button`
  background-color: #dcdcdc;
  color: #333;
  border: none;
  border-radius: 20px;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;

  &:hover {
    background-color: #bfbfbf;
  }
`;

const ConfirmButton = styled.button`
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 20px;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;

  &:hover {
    background-color: #0056b3;
  }
`;
