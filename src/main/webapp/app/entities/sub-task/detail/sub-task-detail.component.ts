import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ISubTask } from '../sub-task.model';

@Component({
  standalone: true,
  selector: 'jhi-sub-task-detail',
  templateUrl: './sub-task-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class SubTaskDetailComponent {
  subTask = input<ISubTask | null>(null);

  previousState(): void {
    window.history.back();
  }
}
