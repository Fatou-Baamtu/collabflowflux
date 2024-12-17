import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'collabflowApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'project',
    data: { pageTitle: 'collabflowApp.project.home.title' },
    loadChildren: () => import('./project/project.routes'),
  },
  {
    path: 'task',
    data: { pageTitle: 'collabflowApp.task.home.title' },
    loadChildren: () => import('./task/task.routes'),
  },
  {
    path: 'sub-task',
    data: { pageTitle: 'collabflowApp.subTask.home.title' },
    loadChildren: () => import('./sub-task/sub-task.routes'),
  },
  {
    path: 'comment',
    data: { pageTitle: 'collabflowApp.comment.home.title' },
    loadChildren: () => import('./comment/comment.routes'),
  },
  {
    path: 'audit-log',
    data: { pageTitle: 'collabflowApp.auditLog.home.title' },
    loadChildren: () => import('./audit-log/audit-log.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
