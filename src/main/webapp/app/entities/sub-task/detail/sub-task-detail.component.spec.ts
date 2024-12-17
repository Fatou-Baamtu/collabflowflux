import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { SubTaskDetailComponent } from './sub-task-detail.component';

describe('SubTask Management Detail Component', () => {
  let comp: SubTaskDetailComponent;
  let fixture: ComponentFixture<SubTaskDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubTaskDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./sub-task-detail.component').then(m => m.SubTaskDetailComponent),
              resolve: { subTask: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(SubTaskDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubTaskDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load subTask on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SubTaskDetailComponent);

      // THEN
      expect(instance.subTask()).toEqual(expect.objectContaining({ id: 123 }));
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
