import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { ITask } from 'app/entities/task/task.model';
import { Status } from 'app/entities/enumerations/status.model';

export interface ISubTask {
  id: number;
  title?: string | null;
  description?: string | null;
  status?: keyof typeof Status | null;
  dueDate?: dayjs.Dayjs | null;
  assignee?: Pick<IUser, 'id'> | null;
  task?: Pick<ITask, 'id'> | null;
}

export type NewSubTask = Omit<ISubTask, 'id'> & { id: null };
