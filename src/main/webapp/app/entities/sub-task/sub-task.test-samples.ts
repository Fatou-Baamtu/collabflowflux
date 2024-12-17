import dayjs from 'dayjs/esm';

import { ISubTask, NewSubTask } from './sub-task.model';

export const sampleWithRequiredData: ISubTask = {
  id: 3808,
  title: 'conseil municipal tranquille',
  status: 'TODO',
};

export const sampleWithPartialData: ISubTask = {
  id: 3943,
  title: 'biathlète',
  status: 'TODO',
  dueDate: dayjs('2024-12-16T21:33'),
};

export const sampleWithFullData: ISubTask = {
  id: 12640,
  title: 'mal patientèle',
  description: 'avant que cependant',
  status: 'IN_PROGRESS',
  dueDate: dayjs('2024-12-17T08:40'),
};

export const sampleWithNewData: NewSubTask = {
  title: 'désormais tard communauté étudiante',
  status: 'DONE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
