import React from 'react';
import styled from 'styled-components';
// eslint-disable-next-line import/no-unresolved
import { Common } from '@styles/globalStyle';

export const HEADER_HEIGHT = '9vh';

const IntroduceHeader = () => (
  <HeaderContainer>
    <LogoContainer>
      <LogoImage src="/image/logo.png" alt="Agile Mentor Logo" />
      <LogoText>Agile Mentor</LogoText>
    </LogoContainer>
    <EmptyContainer />
  </HeaderContainer>
);

export default IntroduceHeader;

const HeaderContainer = styled.header`
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: ${HEADER_HEIGHT};
  background-color: ${Common.colors.primary};
  box-shadow: 0px 0.4vh 0.6vh rgba(0, 0, 0, 0.1);
  z-index: 9999;
  position: relative;
  padding: 0 20px;
`;

const LogoContainer = styled.div`
  display: flex;
  align-items: center;
  cursor: pointer;
`;

const LogoImage = styled.img`
  width: 3vh;
  height: 3vh;
  margin-right: 1vh;
`;

const LogoText = styled.h1`
  font-size: 2vh;
  font-weight: bold;
  color: #000;
`;

const EmptyContainer = styled.div`
  width: 50%;
`;
