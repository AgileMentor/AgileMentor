import React from 'react';
import styled from 'styled-components';
import { IoHome } from 'react-icons/io5';
import PropTypes from 'prop-types';
import { useNavigate } from 'react-router-dom';

const AgileSideBar = ({ setContent }) => {
  const navigate = useNavigate();

  return (
    <SidebarContainer>
      <SearchBox>
        <input type="text" placeholder="검색어를 입력하세요." />
      </SearchBox>

      <Content>
        <h2>애자일 학습하기</h2>
        <Section>
          <h3>스크럼</h3>
          <ul>
            <SidebarButton onClick={() => setContent('sprint')}>
              스프린트
            </SidebarButton>
            <SidebarButton onClick={() => setContent('dailyscrum')}>
              데일리 스크럼 미팅
            </SidebarButton>
            <SidebarButton onClick={() => setContent('review')}>
              스프린트 리뷰
            </SidebarButton>
            <SidebarButton onClick={() => setContent('retrospective')}>
              스프린트 회고
            </SidebarButton>
            <SidebarButton onClick={() => setContent('backlog')}>
              백로그 관리
            </SidebarButton>
            <SidebarButton onClick={() => setContent('planning')}>
              스프린트 계획
            </SidebarButton>
          </ul>
        </Section>

        <Section>
          <h3>XP</h3>
          <ul>
            <SidebarButton onClick={() => setContent('tdd')}>
              테스트 주도 개발
            </SidebarButton>
            <SidebarButton onClick={() => setContent('pairprogramming')}>
              페어 프로그래밍
            </SidebarButton>
            <SidebarButton onClick={() => setContent('ci')}>
              지속적 통합
            </SidebarButton>
            <SidebarButton onClick={() => setContent('smallrelease')}>
              작은 릴리스
            </SidebarButton>
            <SidebarButton onClick={() => setContent('refactoring')}>
              리팩토링
            </SidebarButton>
          </ul>
        </Section>

        <Section>
          <h3>칸반(Kanban)</h3>
          <ul>
            <SidebarButton onClick={() => setContent('visualizeWork')}>
              작업 시각화
            </SidebarButton>
            <SidebarButton onClick={() => setContent('wiplimit')}>
              WIP 제한
            </SidebarButton>
            <SidebarButton onClick={() => setContent('flowmanagement')}>
              흐름 관리
            </SidebarButton>
            <SidebarButton onClick={() => setContent('kanbanboard')}>
              칸반 보드
            </SidebarButton>
          </ul>
        </Section>

        <Section>
          <h3>린(Lean) 소프트웨어 개발</h3>
          <ul>
            <SidebarButton onClick={() => setContent('removewaste')}>
              낭비 제거
            </SidebarButton>
            <SidebarButton onClick={() => setContent('learningamplification')}>
              학습 증진
            </SidebarButton>
            <SidebarButton onClick={() => setContent('rapiddelivery')}>
              신속한 전달
            </SidebarButton>
            <SidebarButton onClick={() => setContent('continuousimprovement')}>
              지속적 개선
            </SidebarButton>
          </ul>
        </Section>

        <Section>
          <h3>FDD (Feature-Driven Development)</h3>
          <ul>
            <SidebarButton onClick={() => setContent('featurefocus')}>
              기능 중심 개발
            </SidebarButton>
            <SidebarButton onClick={() => setContent('regularrelease')}>
              정기 릴리스
            </SidebarButton>
            <SidebarButton onClick={() => setContent('roledivision')}>
              역할 구분
            </SidebarButton>
          </ul>
        </Section>

        <Section>
          <h3>DSDM (Dynamic Systems Development Method)</h3>
          <ul>
            <SidebarButton onClick={() => setContent('timeboxing')}>
              시간 제한
            </SidebarButton>
            <SidebarButton onClick={() => setContent('moscowpriority')}>
              요구사항 우선순위 설정
            </SidebarButton>
            <SidebarButton onClick={() => setContent('iterativedevelopment')}>
              반복적 개발
            </SidebarButton>
            <SidebarButton onClick={() => setContent('userparticipation')}>
              사용자 참여
            </SidebarButton>
          </ul>
        </Section>

        <Section>
          <h3>크리스탈(Crystal)</h3>
          <ul>
            <SidebarButton onClick={() => setContent('tailoredapproach')}>
              팀 맞춤 개발 접근
            </SidebarButton>
            <SidebarButton onClick={() => setContent('enhancecommunication')}>
              의사소통 강화
            </SidebarButton>
            <SidebarButton onClick={() => setContent('feedbackimprovement')}>
              피드백 기반 개선
            </SidebarButton>
          </ul>
        </Section>
      </Content>

      <Footer>
        <DashboardLink onClick={() => navigate('/dashboard')}>
          <IoHomeIcon />
          대시보드로 이동하기
        </DashboardLink>
      </Footer>
    </SidebarContainer>
  );
};

AgileSideBar.propTypes = {
  setContent: PropTypes.func.isRequired,
};

export default AgileSideBar;

// 스타일 정의
const SidebarContainer = styled.div`
  background-color: #fff;
  width: 18vw;
  height: calc(100vh - 9vh);
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
`;

const SearchBox = styled.div`
  padding: 10px;
  border-bottom: 1px solid #eaeaea;

  input {
    width: 90%;
    padding: 8px;
    font-size: 1.6vh;
    border: 1px solid #ddd;
    border-radius: 5px;
    outline: none;

    &::placeholder {
      color: #aaa;
    }
  }
`;

const Content = styled.div`
  flex: 1;
  overflow-y: auto;
  width: 100%;
  padding: 30px;
  box-sizing: border-box;
`;

const Section = styled.div`
  margin-bottom: 1.5rem;

  h3 {
    margin-bottom: 10px;
    font-size: 1.8vh;
    color: #0073e6;
    font-weight: bold;
  }

  ul {
    list-style: none;
    padding: 0;
  }
`;

const SidebarButton = styled.button`
  font-size: 1.6vh;
  color: #333;
  background: none;
  border: none;
  text-align: left;
  cursor: pointer;
  padding: 5px 0;
  width: 100%;

  &:hover {
    text-decoration: underline;
    color: #0073e6;
  }
`;

const Footer = styled.div`
  width: 100%;
  padding: 2vh 0;
  border-top: 1px solid #eaeaea;
  display: flex;
  justify-content: flex-start;
  padding-left: 2vw;
`;

const DashboardLink = styled.div`
  display: flex;
  align-items: center;
  color: #5f8f86;
  font-size: 1.8vh;
  cursor: pointer;

  &:hover {
    color: #2a7a7a;
  }
`;

const IoHomeIcon = styled(IoHome)`
  margin-right: 0.8vw;
  font-size: 2vh;
  color: #5f8f86;
`;
