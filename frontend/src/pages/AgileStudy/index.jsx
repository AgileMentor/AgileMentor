import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import styled from 'styled-components';
// eslint-disable-next-line import/no-unresolved
import HelpComponent from '@components/common/HelpComponent';
import AgileSideBar from '../../components/common/AgileSideBar';
import contentData from '../../contentData';

const AgileStudy = () => {
  const location = useLocation();
  const [currentContent, setContent] = useState('sprint'); // 기본값 설정

  // location.state에 contentKey가 있는 경우 설정
  useEffect(() => {
    if (location.state && location.state.contentKey) {
      setContent(location.state.contentKey);
    }
  }, [location.state]);

  const { title, subtitle, description, image, details } =
    contentData[currentContent] || {}; // 기본값 처리

  return (
    <PageContainer>
      <AgileSideBar setContent={setContent} />
      <MainContent>
        <HeaderContainer>
          <Title>{title}</Title>
          <Subtitle>{subtitle}</Subtitle>
          <HelpComponent page="agilestudy" />
        </HeaderContainer>
        <ContentContainer>
          <Section>
            <Paragraph dangerouslySetInnerHTML={{ __html: description }} />
          </Section>
          {image && (
            <ImageWrapper>
              <Image src={image} alt={title} />
            </ImageWrapper>
          )}
          <DetailsContainer dangerouslySetInnerHTML={{ __html: details }} />
        </ContentContainer>
      </MainContent>
    </PageContainer>
  );
};

export default AgileStudy;

// 스타일 정의
const PageContainer = styled.div`
  display: flex;
  height: calc(100vh - 9vh);
`;

const MainContent = styled.main`
  position: relative;
  width: calc(100vw - 18vw);
  height: calc(100vh - 9vh);
  background-color: #fafafa;
  padding: 0 20px;
  overflow-y: auto;
`;

const HeaderContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-left: 3rem;
  margin-top: 2vh;
`;

const Title = styled.h1`
  font-weight: bold;
  font-size: 2rem;
  color: #333;
`;

const Subtitle = styled.h2`
  font-weight: normal;
  font-size: 1.2rem;
  color: #3a3a3a;
`;

const ContentContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

const Section = styled.section`
  margin-bottom: 2rem;
`;

const Paragraph = styled.div`
  font-size: 1rem;
  margin-bottom: 1rem;
`;

const ImageWrapper = styled.div`
  text-align: center;
  margin: 2rem 0;
`;

const Image = styled.img`
  width: 100%;
  max-width: 600px;
  height: auto;
`;

const DetailsContainer = styled.div`
  font-size: 1rem;
  margin-top: 2rem;
  line-height: 1.5;
`;
