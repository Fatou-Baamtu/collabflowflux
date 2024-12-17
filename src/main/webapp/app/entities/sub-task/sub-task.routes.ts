import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import SubTaskResolve from './route/sub-task-routing-resolve.service';

const subTaskRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/sub-task.component').then(m => m.SubTaskComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/sub-task-detail.component').then(m => m.SubTaskDetailComponent),
    resolve: {
      subTask: SubTaskResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/sub-task-update.component').then(m => m.SubTaskUpdateComponent),
    resolve: {
      subTask: SubTaskResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/sub-task-update.component').then(m => m.SubTaskUpdateComponent),
    resolve: {
      subTask: SubTaskResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default subTaskRoute;
