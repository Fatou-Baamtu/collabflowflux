import dayjs from 'dayjs/esm';

import { IProject, NewProject } from './project.model';

export const sampleWithRequiredData: IProject = {
  id: 15554,
  name: 'prou',
  priority: 'NORMAL',
};

export const sampleWithPartialData: IProject = {
  id: 3681,
  name: 'vu que souvent',
  priority: 'NORMAL',
};

export const sampleWithFullData: IProject = {
  id: 546,
  name: 'coin-coin',
  description: 'blablabla avex serviable',
  priority: 'NORMAL',
  startDate: dayjs('2024-12-17T01:12'),
  endDate: dayjs('2024-12-17T06:55'),
};

export const sampleWithNewData: NewProject = {
  name: 'tendre',
  priority: 'LOW',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
