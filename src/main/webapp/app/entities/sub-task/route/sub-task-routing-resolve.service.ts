import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISubTask } from '../sub-task.model';
import { SubTaskService } from '../service/sub-task.service';

const subTaskResolve = (route: ActivatedRouteSnapshot): Observable<null | ISubTask> => {
  const id = route.params.id;
  if (id) {
    return inject(SubTaskService)
      .find(id)
      .pipe(
        mergeMap((subTask: HttpResponse<ISubTask>) => {
          if (subTask.body) {
            return of(subTask.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default subTaskResolve;
