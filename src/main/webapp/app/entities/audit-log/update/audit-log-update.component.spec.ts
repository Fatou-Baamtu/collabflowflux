import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { AuditLogService } from '../service/audit-log.service';
import { IAuditLog } from '../audit-log.model';
import { AuditLogFormService } from './audit-log-form.service';

import { AuditLogUpdateComponent } from './audit-log-update.component';

describe('AuditLog Management Update Component', () => {
  let comp: AuditLogUpdateComponent;
  let fixture: ComponentFixture<AuditLogUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let auditLogFormService: AuditLogFormService;
  let auditLogService: AuditLogService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AuditLogUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(AuditLogUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AuditLogUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    auditLogFormService = TestBed.inject(AuditLogFormService);
    auditLogService = TestBed.inject(AuditLogService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const auditLog: IAuditLog = { id: 456 };
      const user: IUser = { id: 20668 };
      auditLog.user = user;

      const userCollection: IUser[] = [{ id: 8116 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ auditLog });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const auditLog: IAuditLog = { id: 456 };
      const user: IUser = { id: 27328 };
      auditLog.user = user;

      activatedRoute.data = of({ auditLog });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.auditLog).toEqual(auditLog);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAuditLog>>();
      const auditLog = { id: 123 };
      jest.spyOn(auditLogFormService, 'getAuditLog').mockReturnValue(auditLog);
      jest.spyOn(auditLogService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ auditLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: auditLog }));
      saveSubject.complete();

      // THEN
      expect(auditLogFormService.getAuditLog).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(auditLogService.update).toHaveBeenCalledWith(expect.objectContaining(auditLog));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAuditLog>>();
      const auditLog = { id: 123 };
      jest.spyOn(auditLogFormService, 'getAuditLog').mockReturnValue({ id: null });
      jest.spyOn(auditLogService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ auditLog: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: auditLog }));
      saveSubject.complete();

      // THEN
      expect(auditLogFormService.getAuditLog).toHaveBeenCalled();
      expect(auditLogService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAuditLog>>();
      const auditLog = { id: 123 };
      jest.spyOn(auditLogService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ auditLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(auditLogService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
