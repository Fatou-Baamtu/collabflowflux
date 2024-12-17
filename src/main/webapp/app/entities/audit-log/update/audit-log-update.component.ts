import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { EntityEnum } from 'app/entities/enumerations/entity-enum.model';
import { ActionEnum } from 'app/entities/enumerations/action-enum.model';
import { AuditLogService } from '../service/audit-log.service';
import { IAuditLog } from '../audit-log.model';
import { AuditLogFormGroup, AuditLogFormService } from './audit-log-form.service';

@Component({
  standalone: true,
  selector: 'jhi-audit-log-update',
  templateUrl: './audit-log-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class AuditLogUpdateComponent implements OnInit {
  isSaving = false;
  auditLog: IAuditLog | null = null;
  entityEnumValues = Object.keys(EntityEnum);
  actionEnumValues = Object.keys(ActionEnum);

  usersSharedCollection: IUser[] = [];

  protected auditLogService = inject(AuditLogService);
  protected auditLogFormService = inject(AuditLogFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AuditLogFormGroup = this.auditLogFormService.createAuditLogFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ auditLog }) => {
      this.auditLog = auditLog;
      if (auditLog) {
        this.updateForm(auditLog);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const auditLog = this.auditLogFormService.getAuditLog(this.editForm);
    if (auditLog.id !== null) {
      this.subscribeToSaveResponse(this.auditLogService.update(auditLog));
    } else {
      this.subscribeToSaveResponse(this.auditLogService.create(auditLog));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAuditLog>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(auditLog: IAuditLog): void {
    this.auditLog = auditLog;
    this.auditLogFormService.resetForm(this.editForm, auditLog);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, auditLog.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.auditLog?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
