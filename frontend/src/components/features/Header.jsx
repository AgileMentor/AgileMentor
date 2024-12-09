import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import axios from 'axios';
// eslint-disable-next-line import/no-unresolved
import { Common } from '@styles/globalStyle';
import { Badge, IconButton, Menu, MenuItem, Typography, Divider, Button, Avatar, Box } from '@mui/material';
import NotificationsIcon from '@mui/icons-material/Notifications';
import { useProjects } from '../../provider/projectContext';

export const HEADER_HEIGHT = '9vh';

const Header = () => {
  const { user, fetchProjects } = useProjects();
  const [anchorEl, setAnchorEl] = useState(null);
  const [profileAnchorEl, setProfileAnchorEl] = useState(null);
  const [invitations, setInvitations] = useState([]);
  const open = Boolean(anchorEl);
  const profileOpen = Boolean(profileAnchorEl);

  const removeInvitation = (invitationId) => {
    setInvitations((prev) => prev.filter((inv) => inv.invitationId !== invitationId));
  };

  const handleInvitationAction = async (invitationId, action) => {
    try {
      await axios.post(
        `https://api.agilementor.kr/api/invitations/${invitationId}/${action}`,
        {},
        { withCredentials: true }
      );
      alert(`초대를 ${action === 'accept' ? '수락' : '거절'}했습니다.`);
      removeInvitation(invitationId);
      setAnchorEl(null);
      if (action === 'accept') {
        fetchProjects();
      }
    } catch (error) {
      console.error(`초대 ${action} 중 오류 발생:`, error);
      alert('작업 중 오류가 발생했습니다. 다시 시도해 주세요.');
    }
  };

  const fetchInvitations = async () => {
    try {
      const response = await axios.get('https://api.agilementor.kr/api/invitations', {
        withCredentials: true,
      });
      setInvitations(response.data);
    } catch (error) {
      console.error('초대 데이터를 가져오는 중 오류 발생:', error);
    }
  };

  useEffect(() => {
    if (user) {
      fetchInvitations();

      const intervalId = setInterval(() => {
        fetchInvitations();
      }, 30000);

      return () => clearInterval(intervalId);
    }

    return () => {};
  }, [user]);

  const handleOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleProfileOpen = (event) => {
    setProfileAnchorEl(event.currentTarget);
  };

  const handleProfileClose = () => {
    setProfileAnchorEl(null);
  };

  const handleLogout = () => {
    const logoutURL = 'https://api.agilementor.kr/api/auth/logout';
    window.location.href = logoutURL;
  };

  return (
    <HeaderContainer>
      <LogoContainer>
        <LogoImage src="/image/logo.png" alt="Agile Mentor Logo" />
        <LogoText>Agile Mentor</LogoText>
      </LogoContainer>
      <NotificationContainer>
        {user && (
          <>
            <IconButton color="inherit" onClick={handleOpen}>
              <Badge badgeContent={invitations.length} color="error">
                <NotificationsIcon fontSize="large" />
              </Badge>
            </IconButton>
            <Avatar
              src={user.picture}
              alt={user.name}
              onClick={handleProfileOpen}
              sx={{ cursor: 'pointer', marginLeft: 2 }}
            />
            <Menu
              anchorEl={profileAnchorEl}
              open={profileOpen}
              onClose={handleProfileClose}
              slotProps={{
                paper: {
                  style: {
                    width: '200px',
                    borderRadius: '10px',
                    boxShadow: '0 4px 10px rgba(0, 0, 0, 0.1)',
                    padding: '8px',
                  },
                },
              }}
            >
              <MenuItem
                sx={{
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  padding: '16px 8px',
                  gap: '8px',
                  borderBottom: '1px solid #e0e0e0',
                }}
              >
                <Typography
                  sx={{
                    fontWeight: 'bold',
                    fontSize: '16px',
                    color: '#333',
                  }}
                >
                  {user.name}
                </Typography>
                <Typography
                  sx={{
                    fontSize: '14px',
                    color: '#666',
                  }}
                >
                  {user.email}
                </Typography>
              </MenuItem>
              <MenuItem
                onClick={handleLogout}
                sx={{
                  display: 'flex',
                  justifyContent: 'center',
                  alignItems: 'center',
                  padding: '12px',
                  color: '#ff4d4f',
                  fontWeight: 'bold',
                  '&:hover': {
                    backgroundColor: '#ffe5e7',
                  },
                }}
              >
                로그아웃
              </MenuItem>
            </Menu>
          </>
        )}
        <Menu
          anchorEl={anchorEl}
          open={open}
          onClose={handleClose}
          slotProps={{
            paper: {
              style: { width: '300px' },
            },
          }}
        >
          <Typography variant="h6" sx={{ padding: '8px 16px' }}>
            알림
          </Typography>
          <Divider />
          {invitations.length > 0 ? (
            invitations.flatMap((invitation, index) => {
              const dividerKey = `divider-${invitation.invitationId}`;
              const menuItemKey = `menuitem-${invitation.invitationId}`;

              return [
                <MenuItem
                  key={menuItemKey}
                  sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'flex-start',
                    padding: '8px 16px',
                    gap: '8px',
                  }}
                >
                  <RowContainer>
                    <Box
                      component="span"
                      sx={{
                        width: '8px',
                        height: '8px',
                        backgroundColor: 'red',
                        borderRadius: '50%',
                        marginRight: '8px',
                        display: 'inline-block',
                      }}
                    />
                    <Typography>{invitation.projectTitle} 초대</Typography>
                  </RowContainer>
                  <ActionRow>
                    <Typography variant="body2" color="text.secondary">
                      By {invitation.invitorName}
                    </Typography>
                    <ButtonContainer>
                      <Button
                        variant="contained"
                        color="primary"
                        size="small"
                        onClick={() => handleInvitationAction(invitation.invitationId, 'accept')}
                      >
                        수락
                      </Button>
                      <Button
                        variant="outlined"
                        color="error"
                        size="small"
                        onClick={() => handleInvitationAction(invitation.invitationId, 'decline')}
                      >
                        거절
                      </Button>
                    </ButtonContainer>
                  </ActionRow>
                </MenuItem>,
                index < invitations.length - 1 && <Divider key={dividerKey} />,
              ];
            })
          ) : (
            <MenuItem>
              <Typography>알림이 존재하지 않습니다.</Typography>
            </MenuItem>
          )}
        </Menu>
      </NotificationContainer>
    </HeaderContainer>
  );
};

export default Header;

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

const NotificationContainer = styled.div`
  display: flex;
  align-items: center;
`;

const RowContainer = styled.div`
  display: flex;
  align-items: center;
`;

const ActionRow = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
`;

const ButtonContainer = styled.div`
  display: flex;
  gap: 8px;
`;
