import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '19031594-59a9-4b43-a8b2-875708388890',
};

export const sampleWithPartialData: IAuthority = {
  name: '8d7ab46c-4c62-44a5-9266-63c0fda75f62',
};

export const sampleWithFullData: IAuthority = {
  name: 'c3d90c47-700b-4890-b4f8-0e603f53cd7b',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
