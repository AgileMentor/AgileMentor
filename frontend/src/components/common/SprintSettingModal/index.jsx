import React, { useState } from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { DatePicker } from '@mui/x-date-pickers';
import axios from 'axios';

const SprintSettingModal = ({
  onCancel,
  initialData,
  onSave,
  projectId,
  sprintId,
}) => {
  const [sprintName, setSprintName] = useState(initialData.title || '');
  const [sprintGoal, setSprintGoal] = useState(initialData.goal || '');
  const [startDate] = useState(new Date(initialData.startDate) || null);
  const [endDate, setEndDate] = useState(new Date(initialData.endDate) || null);

  const handleSave = async () => {
    if (!sprintName || !sprintGoal || !endDate) {
      alert('스프린트 이름, 목표 및 종료 날짜를 모두 입력해주세요.');
      return;
    }

    const updatedData = {
      title: sprintName,
      goal: sprintGoal,
      startDate: startDate.toISOString().split('T')[0],
      endDate: endDate.toISOString().split('T')[0],
    };

    try {
      await axios.put(
        `https://api.agilementor.kr/api/projects/${projectId}/sprints/${sprintId}`,
        updatedData,
        {
          withCredentials: true,
        },
      );
      alert('스프린트가 성공적으로 수정되었습니다.');
      onSave(updatedData);
      onCancel();
    } catch (error) {
      console.error('스프린트 수정 중 오류 발생:', error);
      alert('스프린트를 수정하는 데 실패했습니다.');
    }
  };

  return (
    <ModalOverlay>
      <ModalContainer>
        <ModalTitle>스프린트 수정</ModalTitle>
        <Subtitle>스프린트 정보를 수정합니다.</Subtitle>

        <InputContainer>
          <Label>스프린트 이름</Label>
          <StyledInput
            type="text"
            placeholder="스프린트 이름을 입력하세요."
            value={sprintName}
            onChange={(e) => setSprintName(e.target.value)}
          />
        </InputContainer>

        <Row>
          <DatePickerContainer>
            <LocalizationProvider dateAdapter={AdapterDateFns}>
              <DatePicker
                label="시작 날짜"
                value={startDate}
                readOnly // 수정 불가능
                // eslint-disable-next-line react/jsx-props-no-spreading
                renderInput={(params) => <StyledInput {...params} disabled />}
              />
            </LocalizationProvider>
          </DatePickerContainer>

          <DatePickerContainer>
            <LocalizationProvider dateAdapter={AdapterDateFns}>
              <DatePicker
                label="종료 날짜"
                value={endDate}
                onChange={(newValue) => setEndDate(newValue)}
              />
            </LocalizationProvider>
          </DatePickerContainer>
        </Row>

        <InputContainer>
          <Label>스프린트 목표</Label>
          <StyledTextArea
            placeholder="스프린트 목표를 입력하세요."
            value={sprintGoal}
            onChange={(e) => setSprintGoal(e.target.value)}
          />
        </InputContainer>

        <ButtonContainer>
          <CancelButton onClick={onCancel}>취소</CancelButton>
          <SaveButton onClick={handleSave}>저장</SaveButton>
        </ButtonContainer>
      </ModalContainer>
    </ModalOverlay>
  );
};

SprintSettingModal.propTypes = {
  onCancel: PropTypes.func.isRequired,
  initialData: PropTypes.shape({
    title: PropTypes.string,
    goal: PropTypes.string,
    startDate: PropTypes.string,
    endDate: PropTypes.string,
  }).isRequired,
  onSave: PropTypes.func.isRequired,
  projectId: PropTypes.number.isRequired,
  sprintId: PropTypes.number.isRequired,
};

export default SprintSettingModal;

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

const Row = styled.div`
  display: flex;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 20px;
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

const SaveButton = styled.button`
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
