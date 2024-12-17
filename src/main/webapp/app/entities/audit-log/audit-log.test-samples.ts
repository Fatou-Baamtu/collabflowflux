import dayjs from 'dayjs/esm';

import { IAuditLog, NewAuditLog } from './audit-log.model';

export const sampleWithRequiredData: IAuditLog = {
  id: 15759,
  entity: 'COMMENT',
  action: 'CREATED',
  timestamp: dayjs('2024-12-16T22:58'),
  entityId: 8046,
};

export const sampleWithPartialData: IAuditLog = {
  id: 744,
  entity: 'SUB_TASK',
  action: 'DELETED',
  timestamp: dayjs('2024-12-17T14:40'),
  entityId: 1443,
};

export const sampleWithFullData: IAuditLog = {
  id: 32353,
  entity: 'COMMENT',
  action: 'UPDATED',
  timestamp: dayjs('2024-12-16T23:31'),
  entityId: 1932,
};

export const sampleWithNewData: NewAuditLog = {
  entity: 'TASK',
  action: 'CREATED',
  timestamp: dayjs('2024-12-17T14:19'),
  entityId: 18216,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
