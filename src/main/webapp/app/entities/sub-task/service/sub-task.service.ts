import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISubTask, NewSubTask } from '../sub-task.model';

export type PartialUpdateSubTask = Partial<ISubTask> & Pick<ISubTask, 'id'>;

type RestOf<T extends ISubTask | NewSubTask> = Omit<T, 'dueDate'> & {
  dueDate?: string | null;
};

export type RestSubTask = RestOf<ISubTask>;

export type NewRestSubTask = RestOf<NewSubTask>;

export type PartialUpdateRestSubTask = RestOf<PartialUpdateSubTask>;

export type EntityResponseType = HttpResponse<ISubTask>;
export type EntityArrayResponseType = HttpResponse<ISubTask[]>;

@Injectable({ providedIn: 'root' })
export class SubTaskService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sub-tasks');

  create(subTask: NewSubTask): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(subTask);
    return this.http
      .post<RestSubTask>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(subTask: ISubTask): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(subTask);
    return this.http
      .put<RestSubTask>(`${this.resourceUrl}/${this.getSubTaskIdentifier(subTask)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(subTask: PartialUpdateSubTask): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(subTask);
    return this.http
      .patch<RestSubTask>(`${this.resourceUrl}/${this.getSubTaskIdentifier(subTask)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestSubTask>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSubTask[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSubTaskIdentifier(subTask: Pick<ISubTask, 'id'>): number {
    return subTask.id;
  }

  compareSubTask(o1: Pick<ISubTask, 'id'> | null, o2: Pick<ISubTask, 'id'> | null): boolean {
    return o1 && o2 ? this.getSubTaskIdentifier(o1) === this.getSubTaskIdentifier(o2) : o1 === o2;
  }

  addSubTaskToCollectionIfMissing<Type extends Pick<ISubTask, 'id'>>(
    subTaskCollection: Type[],
    ...subTasksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const subTasks: Type[] = subTasksToCheck.filter(isPresent);
    if (subTasks.length > 0) {
      const subTaskCollectionIdentifiers = subTaskCollection.map(subTaskItem => this.getSubTaskIdentifier(subTaskItem));
      const subTasksToAdd = subTasks.filter(subTaskItem => {
        const subTaskIdentifier = this.getSubTaskIdentifier(subTaskItem);
        if (subTaskCollectionIdentifiers.includes(subTaskIdentifier)) {
          return false;
        }
        subTaskCollectionIdentifiers.push(subTaskIdentifier);
        return true;
      });
      return [...subTasksToAdd, ...subTaskCollection];
    }
    return subTaskCollection;
  }

  protected convertDateFromClient<T extends ISubTask | NewSubTask | PartialUpdateSubTask>(subTask: T): RestOf<T> {
    return {
      ...subTask,
      dueDate: subTask.dueDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restSubTask: RestSubTask): ISubTask {
    return {
      ...restSubTask,
      dueDate: restSubTask.dueDate ? dayjs(restSubTask.dueDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestSubTask>): HttpResponse<ISubTask> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestSubTask[]>): HttpResponse<ISubTask[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
