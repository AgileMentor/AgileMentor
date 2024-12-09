import React from 'react';
// eslint-disable-next-line import/no-extraneous-dependencies
import { BrowserRouter } from 'react-router-dom';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import AppRoutes from './routes';
import { ProjectProvider } from './provider/projectContext';

const theme = createTheme({
  typography: {
    fontFamily: 'PaperlogyRegular, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif',
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <ProjectProvider>
        <DndProvider backend={HTML5Backend}>
          <BrowserRouter>
            <div className="App">
              <AppRoutes />
            </div>
          </BrowserRouter>
        </DndProvider>
      </ProjectProvider>
    </ThemeProvider>
  );
}

export default App;
