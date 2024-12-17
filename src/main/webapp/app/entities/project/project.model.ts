import dayjs from 'dayjs/esm';
import { Priority } from 'app/entities/enumerations/priority.model';

export interface IProject {
  id: number;
  name?: string | null;
  description?: string | null;
  priority?: keyof typeof Priority | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
}

export type NewProject = Omit<IProject, 'id'> & { id: null };
