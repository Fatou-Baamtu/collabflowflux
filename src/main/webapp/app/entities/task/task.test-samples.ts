import dayjs from 'dayjs/esm';

import { ITask, NewTask } from './task.model';

export const sampleWithRequiredData: ITask = {
  id: 1377,
  title: 'vouh chut',
  status: 'TODO',
};

export const sampleWithPartialData: ITask = {
  id: 23275,
  title: 'premièrement',
  status: 'DONE',
};

export const sampleWithFullData: ITask = {
  id: 29920,
  title: 'coupable',
  description: 'main-d’œuvre',
  status: 'TODO',
  dueDate: dayjs('2024-12-17T11:47'),
  isCompleted: true,
};

export const sampleWithNewData: NewTask = {
  title: 'retrouver',
  status: 'DONE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
