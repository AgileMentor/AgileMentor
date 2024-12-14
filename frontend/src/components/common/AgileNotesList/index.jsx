import React from 'react';
import { Typography, List, ListItem, ListItemText } from '@mui/material';
import CircleIcon from '@mui/icons-material/Circle';
import { useNavigate } from 'react-router-dom';

const agileNotes = [
  { id: 1, title: '효율적으로 회의하는 방법', contentKey: 'dailyscrum' },
  { id: 2, title: '지속적 통합이 무엇인가?', contentKey: 'ci' },
  {
    id: 3,
    title: '요구사항 우선순위를 설정해보세요.',
    contentKey: 'moscowpriority',
  },
  { id: 4, title: '백로그 관리에 대해..', contentKey: 'backlog' },
];

const AgileNotesList = () => {
  const navigate = useNavigate();

  const handleItemClick = (contentKey) => {
    navigate('/agilestudy', { state: { contentKey } }); // 키 전달
  };

  return (
    <List>
      {agileNotes.map((note) => (
        <ListItem
          key={note.id}
          sx={{
            display: 'flex',
            alignItems: 'center',
            cursor: 'pointer',
            '&:hover': {
              backgroundColor: '#f0f0f0',
            },
          }}
          onClick={() => handleItemClick(note.contentKey)}
        >
          <CircleIcon sx={{ color: '#356B60', fontSize: '0.5rem', mr: 1 }} />
          <ListItemText
            primary={
              <Typography sx={{ fontSize: '1rem', color: '#333' }}>
                {note.title}
              </Typography>
            }
          />
        </ListItem>
      ))}
    </List>
  );
};

export default AgileNotesList;
