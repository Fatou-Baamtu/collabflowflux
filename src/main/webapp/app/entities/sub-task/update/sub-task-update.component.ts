import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ITask } from 'app/entities/task/task.model';
import { TaskService } from 'app/entities/task/service/task.service';
import { Status } from 'app/entities/enumerations/status.model';
import { SubTaskService } from '../service/sub-task.service';
import { ISubTask } from '../sub-task.model';
import { SubTaskFormGroup, SubTaskFormService } from './sub-task-form.service';

@Component({
  standalone: true,
  selector: 'jhi-sub-task-update',
  templateUrl: './sub-task-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SubTaskUpdateComponent implements OnInit {
  isSaving = false;
  subTask: ISubTask | null = null;
  statusValues = Object.keys(Status);

  usersSharedCollection: IUser[] = [];
  tasksSharedCollection: ITask[] = [];

  protected subTaskService = inject(SubTaskService);
  protected subTaskFormService = inject(SubTaskFormService);
  protected userService = inject(UserService);
  protected taskService = inject(TaskService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: SubTaskFormGroup = this.subTaskFormService.createSubTaskFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareTask = (o1: ITask | null, o2: ITask | null): boolean => this.taskService.compareTask(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ subTask }) => {
      this.subTask = subTask;
      if (subTask) {
        this.updateForm(subTask);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const subTask = this.subTaskFormService.getSubTask(this.editForm);
    if (subTask.id !== null) {
      this.subscribeToSaveResponse(this.subTaskService.update(subTask));
    } else {
      this.subscribeToSaveResponse(this.subTaskService.create(subTask));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISubTask>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(subTask: ISubTask): void {
    this.subTask = subTask;
    this.subTaskFormService.resetForm(this.editForm, subTask);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, subTask.assignee);
    this.tasksSharedCollection = this.taskService.addTaskToCollectionIfMissing<ITask>(this.tasksSharedCollection, subTask.task);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.subTask?.assignee)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.taskService
      .query()
      .pipe(map((res: HttpResponse<ITask[]>) => res.body ?? []))
      .pipe(map((tasks: ITask[]) => this.taskService.addTaskToCollectionIfMissing<ITask>(tasks, this.subTask?.task)))
      .subscribe((tasks: ITask[]) => (this.tasksSharedCollection = tasks));
  }
}
