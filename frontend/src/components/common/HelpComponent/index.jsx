import React, { useState } from 'react';
import styled from 'styled-components';
import { FaQuestion } from 'react-icons/fa';
import PropTypes from 'prop-types';
// eslint-disable-next-line import/no-unresolved
import HelpDashboardModal from '@components/common/HelpDashboardModal';
// eslint-disable-next-line import/no-unresolved
import HelpKanbanboardModal from '@components/common/HelpKanbanboardModal';
// eslint-disable-next-line import/no-unresolved
import HelpAgileStudyModal from '@components/common/HelpAgileStudyModal';
// eslint-disable-next-line import/no-unresolved
import HelpBurndownModal from '@components/common/HelpBurndownModal';

const HelpButton = ({ page }) => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleButtonClick = () => {
    setIsModalOpen(true);
  };

  const handleModalClose = () => {
    setIsModalOpen(false);
  };

  const renderModal = () => {
    if (page === 'dashboard') {
      return <HelpDashboardModal onCancel={handleModalClose} />;
    }
    if (page === 'kanban') {
      return <HelpKanbanboardModal onCancel={handleModalClose} />;
    }
    if (page === 'agilestudy') {
      return <HelpAgileStudyModal onCancel={handleModalClose} />;
    }
    if (page === 'burndown') {
      return <HelpBurndownModal onCancel={handleModalClose} />;
    }
    return null;
  };

  return (
    <>
      <ButtonWrapper onClick={handleButtonClick} aria-label="Help">
        <FaQuestion />
      </ButtonWrapper>
      {isModalOpen && renderModal()}
    </>
  );
};

HelpButton.propTypes = {
  page: PropTypes.string.isRequired,
};

export default HelpButton;

const ButtonWrapper = styled.button`
  position: fixed;
  top: 10%;
  right: 3%;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  border: none;
  background-color: #ffc107;
  color: white;
  font-size: 1.6rem;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);

  &:hover {
    background-color: #e0a800;
  }

  svg {
    display: block;
  }
`;
