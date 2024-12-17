import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IProject } from 'app/entities/project/project.model';
import { Status } from 'app/entities/enumerations/status.model';

export interface ITask {
  id: number;
  title?: string | null;
  description?: string | null;
  status?: keyof typeof Status | null;
  dueDate?: dayjs.Dayjs | null;
  isCompleted?: boolean | null;
  assignee?: Pick<IUser, 'id'> | null;
  project?: Pick<IProject, 'id'> | null;
}

export type NewTask = Omit<ITask, 'id'> & { id: null };
