import React from 'react';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { FaRunning, FaThList, FaBook, FaChartLine } from 'react-icons/fa';

const NavigateMenu = () => {
  const navigate = useNavigate();

  return (
    <Container>
      <Title>이용하기</Title>
      <MenuItem onClick={() => navigate('/backlogandsprint')}>
        <Icon as={FaRunning} />
        <MenuText>백로그 및 스프린트</MenuText>
      </MenuItem>
      <MenuItem onClick={() => navigate('/kanbanboard')}>
        <Icon as={FaThList} />
        <MenuText>칸반보드</MenuText>
      </MenuItem>
      <MenuItem onClick={() => navigate('/agilestudy')}>
        <Icon as={FaBook} />
        <MenuText>애자일 학습하기</MenuText>
      </MenuItem>
      <MenuItem onClick={() => navigate('/burndown')}>
        <Icon as={FaChartLine} />
        <MenuText>번다운 차트</MenuText>
      </MenuItem>
    </Container>
  );
};

export default NavigateMenu;

const Container = styled.div`
  width: 100%;
  padding: 0 1vw;
`;

const Title = styled.h3`
  display: flex;
  align-items: center;
  align-self: flex-start;
  font-size: 2.4vh;
  font-weight: bold;
  margin-bottom: 3vh;
  color: #151313;

  @media (max-width: 768px) {
    font-size: 2.2vh;
    margin-bottom: 2.4vh;
  }

  @media (max-width: 480px) {
    font-size: 2vh;
    margin-bottom: 2vh;
  }
`;

const MenuItem = styled.div`
  display: flex;
  align-items: center;
  margin-bottom: 2vh;
  color: #666;
  font-size: 1.8vh;
  cursor: pointer;
  transition: color 0.3s;

  &:hover {
    color: #333;
  }

  @media (max-width: 768px) {
    font-size: 1.6vh;
    margin-bottom: 1.6vh;
  }

  @media (max-width: 480px) {
    font-size: 1.4vh;
    margin-bottom: 1.2vh;
  }
`;

const Icon = styled.div`
  font-size: 1.8vh;
  margin-right: 0.8vw;

  @media (max-width: 768px) {
    font-size: 1.4vh;
  }

  @media (max-width: 480px) {
    font-size: 1.2vh;
  }
`;

const MenuText = styled.span`
  margin-left: 0.8vw;
`;
