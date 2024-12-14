import React, { useState } from 'react';
import styled from 'styled-components';
import { FaQuestion } from 'react-icons/fa';
// eslint-disable-next-line import/no-unresolved
import CommonHelpModal from '@components/common/CommonHelpModal';

const CommonHelpButton = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleButtonClick = () => {
    setIsModalOpen(true);
  };

  const handleModalClose = () => {
    setIsModalOpen(false);
  };

  return (
    <>
      <ButtonWrapper onClick={handleButtonClick} aria-label="Help">
        <FaQuestion />
      </ButtonWrapper>
      {isModalOpen && <CommonHelpModal onCancel={handleModalClose} />}
    </>
  );
};

export default CommonHelpButton;

const ButtonWrapper = styled.button`
  position: fixed;
  top: 10%;
  right: 7%;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  border: none;
  background-color: #7a7a7a;
  color: white;
  font-size: 1.6rem;
  cursor: pointer;
  z-index: 1000;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);

  &:hover {
    background-color: #585858;
  }

  svg {
    display: block;
  }
`;
