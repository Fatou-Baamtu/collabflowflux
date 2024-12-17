import dayjs from 'dayjs/esm';

import { IComment, NewComment } from './comment.model';

export const sampleWithRequiredData: IComment = {
  id: 3923,
  content: '../fake-data/blob/hipster.txt',
  createdAt: dayjs('2024-12-17T07:50'),
};

export const sampleWithPartialData: IComment = {
  id: 9125,
  content: '../fake-data/blob/hipster.txt',
  createdAt: dayjs('2024-12-17T07:42'),
};

export const sampleWithFullData: IComment = {
  id: 26805,
  content: '../fake-data/blob/hipster.txt',
  createdAt: dayjs('2024-12-17T06:19'),
};

export const sampleWithNewData: NewComment = {
  content: '../fake-data/blob/hipster.txt',
  createdAt: dayjs('2024-12-16T21:20'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
