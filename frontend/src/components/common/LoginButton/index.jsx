import React from 'react';
import styled from 'styled-components';

const GoogleLoginButton = () => (
  <GoogleButton>
    <GoogleImage src="/image/google_login.png" alt="Google logo" />
  </GoogleButton>
);

export default GoogleLoginButton;

const GoogleButton = styled.button`
  border: none;
  border-radius: 20px;
  background-color: #ffffff;
  cursor: pointer;
  color: #000;
  transition: background-color 0.3s;

  &:hover {
    background-color: #f5f5f5;
  }
`;

const GoogleImage = styled.img`
  width: 140 px;
  height: 40px;
`;
