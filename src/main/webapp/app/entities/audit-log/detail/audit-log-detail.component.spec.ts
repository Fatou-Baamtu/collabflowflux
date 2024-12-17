import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { AuditLogDetailComponent } from './audit-log-detail.component';

describe('AuditLog Management Detail Component', () => {
  let comp: AuditLogDetailComponent;
  let fixture: ComponentFixture<AuditLogDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuditLogDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./audit-log-detail.component').then(m => m.AuditLogDetailComponent),
              resolve: { auditLog: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(AuditLogDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AuditLogDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load auditLog on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', AuditLogDetailComponent);

      // THEN
      expect(instance.auditLog()).toEqual(expect.objectContaining({ id: 123 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
