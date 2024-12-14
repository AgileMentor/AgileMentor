import React from 'react';
// eslint-disable-next-line import/no-extraneous-dependencies
import { Routes, Route } from 'react-router-dom';
// eslint-disable-next-line import/no-unresolved
import Introduce from '@pages/Introduce/index';
// eslint-disable-next-line import/no-unresolved
import Layout from '@components/Layout/index';
// eslint-disable-next-line import/no-unresolved
import Dashboard from '@pages/Dashboard/index';
// eslint-disable-next-line import/no-unresolved
import Burndown from '@pages/Burndown/index';
// eslint-disable-next-line import/no-unresolved
import Kanbanboard from '@pages/Kanbanboard/index';
// eslint-disable-next-line import/no-unresolved
import BacklogAndSprint from '@pages/BacklogAndSprint';
// eslint-disable-next-line import/no-unresolved
import AgileStudy from '@pages/AgileStudy';

import PATHS from './path';

const AppRoutes = () => (
  <Routes>
    <Route element={<Layout />}>
      <Route path={PATHS.ROOT} element={<Introduce />} />
      <Route path={PATHS.DASHBOARD} element={<Dashboard />} />
      <Route path={PATHS.BURNDOWN} element={<Burndown />} />
      <Route path={PATHS.KANBAN} element={<Kanbanboard />} />
      <Route path={PATHS.BACKLOGANDSPRINT} element={<BacklogAndSprint />} />
      <Route path={PATHS.AGILESTUDY} element={<AgileStudy />} />
    </Route>
  </Routes>
);

export default AppRoutes;
