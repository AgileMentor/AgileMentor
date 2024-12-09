import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';

const DeleteConfirmModal = ({ onConfirm, onCancel }) => (
    <Overlay>
      <ModalContainer>
        <ModalHeader>프로젝트 삭제</ModalHeader>
        <ModalBody>
          <Message>정말로 이 프로젝트를 삭제하시겠습니까?</Message>
        </ModalBody>
        <ModalFooter>
          <CancelButton onClick={onCancel}>취소</CancelButton>
          <ConfirmButton onClick={onConfirm}>삭제</ConfirmButton>
        </ModalFooter>
      </ModalContainer>
    </Overlay>
  );

DeleteConfirmModal.propTypes = {
  onConfirm: PropTypes.func.isRequired,
  onCancel: PropTypes.func.isRequired,
};

export default DeleteConfirmModal;

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
  border-radius: 16px;
  padding: 20px;
  width: 400px;
  max-width: 90%;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
`;

const ModalHeader = styled.h2`
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 16px;
  text-align: center;
`;

const ModalBody = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 16px;
`;

const Message = styled.p`
  font-size: 16px;
  color: #333;
`;

const ModalFooter = styled.div`
  display: flex;
  justify-content: center;
  gap: 1rem;
`;

const CancelButton = styled.button`
  background-color: #e0e0e0;
  color: #333;
  border: none;
  border-radius: 30px;
  padding: 8px 20px;
  font-size: 14px;
  cursor: pointer;

  &:hover {
    background-color: #c7c7c7;
  }
`;

const ConfirmButton = styled.button`
  background-color: #e53935;
  color: white;
  border: none;
  border-radius: 30px;
  padding: 8px 20px;
  font-size: 14px;
  cursor: pointer;

  &:hover {
    background-color: #b71c1c;
  }
`;
