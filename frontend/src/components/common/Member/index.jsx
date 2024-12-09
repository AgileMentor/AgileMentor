import React, { useState } from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import { FaCrown } from 'react-icons/fa';
// eslint-disable-next-line import/no-unresolved
import MinModal from '@components/common/MinModal';
import axios from 'axios';
import { useProjects } from '../../../provider/projectContext';

const Member = ({ members, isAdmin }) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [email, setEmail] = useState('');
  const { selectedProjectId, fetchMembers } = useProjects();

  const openModal = () => {
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setEmail('');
  };

  const handleInvite = async () => {
    if (!selectedProjectId) {
      alert('프로젝트를 선택해주세요.');
      return;
    }

    const response = await axios.post(
      `https://api.agilementor.kr/api/projects/${selectedProjectId}/invitations`,
      { email },
      {
        withCredentials: true,
      },
    );

    if (response.status === 200) {
      alert('초대가 성공적으로 완료되었습니다.');
      closeModal();
    }
  };

  const handleKick = async (memberId) => {
    if (!selectedProjectId || !memberId) {
      alert('프로젝트 또는 멤버 정보가 없습니다.');
      return;
    }

    const response = await axios.delete(
      `https://api.agilementor.kr/api/projects/${selectedProjectId}/members/${memberId}`,
      {
        withCredentials: true,
      },
    );

    if (response.status === 204) {
      alert('멤버가 성공적으로 추방되었습니다.');
      fetchMembers(selectedProjectId);
    }
  };

  return (
    <Container>
      <Header>
        <Title>멤버 관리하기</Title>
        <InviteButton onClick={openModal}>초대하기</InviteButton>
      </Header>
      <MemberList>
        {members.map((member) => (
          <MemberItem key={member.memberId}>
            <MemberName>
              <ProfileImage
                src={member.profileImageUrl}
                alt={`${member.name}'s profile`}
                onError={(e) => {
                  e.target.src = 'https://via.placeholder.com/96';
                }}
              />
              {member.name}
              {member.isAdmin && <CrownIcon />}
            </MemberName>
            {isAdmin && !member.isAdmin && (
              <KickButton onClick={() => handleKick(member.memberId)}>
                추방
              </KickButton>
            )}
          </MemberItem>
        ))}
      </MemberList>

      {isModalOpen && (
        <MinModal
          title="초대하기"
          description="초대하실 분의 이메일을 입력해 주세요."
          type="email"
          onChange={(e) => setEmail(e.target.value)}
          onCancel={closeModal}
          onConfirm={handleInvite}
        />
      )}
    </Container>
  );
};

Member.propTypes = {
  members: PropTypes.arrayOf(
    PropTypes.shape({
      memberId: PropTypes.number.isRequired,
      name: PropTypes.string.isRequired,
      profileImageUrl: PropTypes.string,
      isAdmin: PropTypes.bool.isRequired,
    }),
  ).isRequired,
  isAdmin: PropTypes.bool.isRequired,
};

export default Member;

const Container = styled.div`
  padding: 0 1vw 1vh 1vw;
`;

const Header = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.5vh;
`;

const Title = styled.h3`
  font-size: 2vh;
  font-weight: bold;
  color: #333;
`;

const InviteButton = styled.button`
  background-color: #ffd771;
  color: #fff;
  border: none;
  border-radius: 0.5vw;
  padding: 0.5vh 1vw;
  font-size: 1.4vh;
  cursor: pointer;

  &:hover {
    background-color: #ffd966;
  }
`;

const MemberList = styled.ul`
  list-style-type: none;
  padding: 0;
  margin: 0;
`;

const MemberItem = styled.li`
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1vh;
`;

const MemberName = styled.span`
  font-size: 1.6vh;
  color: #333;
  display: flex;
  align-items: center;
  gap: 0.5vw;
`;

const ProfileImage = styled.img`
  width: 2.4vh;
  height: 2.4vh;
  border-radius: 50%;
  object-fit: cover;
`;

const CrownIcon = styled(FaCrown)`
  color: #ffd700;
  margin-left: 0.5vw;
  font-size: 1.6vh;
`;

const KickButton = styled.button`
  background-color: #ffb3b3;
  color: white;
  border: none;
  border-radius: 1vw;
  padding: 0.5vh 1vw;
  font-size: 1.4vh;
  cursor: pointer;

  &:hover {
    background-color: #ff8080;
  }
`;
