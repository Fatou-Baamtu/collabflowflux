import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ITask } from 'app/entities/task/task.model';
import { TaskService } from 'app/entities/task/service/task.service';
import { ISubTask } from '../sub-task.model';
import { SubTaskService } from '../service/sub-task.service';
import { SubTaskFormService } from './sub-task-form.service';

import { SubTaskUpdateComponent } from './sub-task-update.component';

describe('SubTask Management Update Component', () => {
  let comp: SubTaskUpdateComponent;
  let fixture: ComponentFixture<SubTaskUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let subTaskFormService: SubTaskFormService;
  let subTaskService: SubTaskService;
  let userService: UserService;
  let taskService: TaskService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SubTaskUpdateComponent],
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
      .overrideTemplate(SubTaskUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SubTaskUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    subTaskFormService = TestBed.inject(SubTaskFormService);
    subTaskService = TestBed.inject(SubTaskService);
    userService = TestBed.inject(UserService);
    taskService = TestBed.inject(TaskService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const subTask: ISubTask = { id: 456 };
      const assignee: IUser = { id: 9657 };
      subTask.assignee = assignee;

      const userCollection: IUser[] = [{ id: 27556 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [assignee];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ subTask });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Task query and add missing value', () => {
      const subTask: ISubTask = { id: 456 };
      const task: ITask = { id: 17736 };
      subTask.task = task;

      const taskCollection: ITask[] = [{ id: 12596 }];
      jest.spyOn(taskService, 'query').mockReturnValue(of(new HttpResponse({ body: taskCollection })));
      const additionalTasks = [task];
      const expectedCollection: ITask[] = [...additionalTasks, ...taskCollection];
      jest.spyOn(taskService, 'addTaskToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ subTask });
      comp.ngOnInit();

      expect(taskService.query).toHaveBeenCalled();
      expect(taskService.addTaskToCollectionIfMissing).toHaveBeenCalledWith(
        taskCollection,
        ...additionalTasks.map(expect.objectContaining),
      );
      expect(comp.tasksSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const subTask: ISubTask = { id: 456 };
      const assignee: IUser = { id: 22505 };
      subTask.assignee = assignee;
      const task: ITask = { id: 6712 };
      subTask.task = task;

      activatedRoute.data = of({ subTask });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(assignee);
      expect(comp.tasksSharedCollection).toContain(task);
      expect(comp.subTask).toEqual(subTask);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISubTask>>();
      const subTask = { id: 123 };
      jest.spyOn(subTaskFormService, 'getSubTask').mockReturnValue(subTask);
      jest.spyOn(subTaskService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ subTask });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: subTask }));
      saveSubject.complete();

      // THEN
      expect(subTaskFormService.getSubTask).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(subTaskService.update).toHaveBeenCalledWith(expect.objectContaining(subTask));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISubTask>>();
      const subTask = { id: 123 };
      jest.spyOn(subTaskFormService, 'getSubTask').mockReturnValue({ id: null });
      jest.spyOn(subTaskService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ subTask: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: subTask }));
      saveSubject.complete();

      // THEN
      expect(subTaskFormService.getSubTask).toHaveBeenCalled();
      expect(subTaskService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISubTask>>();
      const subTask = { id: 123 };
      jest.spyOn(subTaskService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ subTask });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(subTaskService.update).toHaveBeenCalled();
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

    describe('compareTask', () => {
      it('Should forward to taskService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(taskService, 'compareTask');
        comp.compareTask(entity, entity2);
        expect(taskService.compareTask).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
