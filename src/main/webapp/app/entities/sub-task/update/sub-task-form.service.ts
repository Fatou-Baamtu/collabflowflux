import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISubTask, NewSubTask } from '../sub-task.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISubTask for edit and NewSubTaskFormGroupInput for create.
 */
type SubTaskFormGroupInput = ISubTask | PartialWithRequiredKeyOf<NewSubTask>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISubTask | NewSubTask> = Omit<T, 'dueDate'> & {
  dueDate?: string | null;
};

type SubTaskFormRawValue = FormValueOf<ISubTask>;

type NewSubTaskFormRawValue = FormValueOf<NewSubTask>;

type SubTaskFormDefaults = Pick<NewSubTask, 'id' | 'dueDate'>;

type SubTaskFormGroupContent = {
  id: FormControl<SubTaskFormRawValue['id'] | NewSubTask['id']>;
  title: FormControl<SubTaskFormRawValue['title']>;
  description: FormControl<SubTaskFormRawValue['description']>;
  status: FormControl<SubTaskFormRawValue['status']>;
  dueDate: FormControl<SubTaskFormRawValue['dueDate']>;
  assignee: FormControl<SubTaskFormRawValue['assignee']>;
  task: FormControl<SubTaskFormRawValue['task']>;
};

export type SubTaskFormGroup = FormGroup<SubTaskFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SubTaskFormService {
  createSubTaskFormGroup(subTask: SubTaskFormGroupInput = { id: null }): SubTaskFormGroup {
    const subTaskRawValue = this.convertSubTaskToSubTaskRawValue({
      ...this.getFormDefaults(),
      ...subTask,
    });
    return new FormGroup<SubTaskFormGroupContent>({
      id: new FormControl(
        { value: subTaskRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(subTaskRawValue.title, {
        validators: [Validators.required],
      }),
      description: new FormControl(subTaskRawValue.description),
      status: new FormControl(subTaskRawValue.status, {
        validators: [Validators.required],
      }),
      dueDate: new FormControl(subTaskRawValue.dueDate),
      assignee: new FormControl(subTaskRawValue.assignee),
      task: new FormControl(subTaskRawValue.task),
    });
  }

  getSubTask(form: SubTaskFormGroup): ISubTask | NewSubTask {
    return this.convertSubTaskRawValueToSubTask(form.getRawValue() as SubTaskFormRawValue | NewSubTaskFormRawValue);
  }

  resetForm(form: SubTaskFormGroup, subTask: SubTaskFormGroupInput): void {
    const subTaskRawValue = this.convertSubTaskToSubTaskRawValue({ ...this.getFormDefaults(), ...subTask });
    form.reset(
      {
        ...subTaskRawValue,
        id: { value: subTaskRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SubTaskFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dueDate: currentTime,
    };
  }

  private convertSubTaskRawValueToSubTask(rawSubTask: SubTaskFormRawValue | NewSubTaskFormRawValue): ISubTask | NewSubTask {
    return {
      ...rawSubTask,
      dueDate: dayjs(rawSubTask.dueDate, DATE_TIME_FORMAT),
    };
  }

  private convertSubTaskToSubTaskRawValue(
    subTask: ISubTask | (Partial<NewSubTask> & SubTaskFormDefaults),
  ): SubTaskFormRawValue | PartialWithRequiredKeyOf<NewSubTaskFormRawValue> {
    return {
      ...subTask,
      dueDate: subTask.dueDate ? subTask.dueDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
