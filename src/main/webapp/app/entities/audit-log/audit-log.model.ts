import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { EntityEnum } from 'app/entities/enumerations/entity-enum.model';
import { ActionEnum } from 'app/entities/enumerations/action-enum.model';

export interface IAuditLog {
  id: number;
  entity?: keyof typeof EntityEnum | null;
  action?: keyof typeof ActionEnum | null;
  timestamp?: dayjs.Dayjs | null;
  entityId?: number | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewAuditLog = Omit<IAuditLog, 'id'> & { id: null };
