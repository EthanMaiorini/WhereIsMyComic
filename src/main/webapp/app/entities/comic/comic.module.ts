import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ComicComponent } from './list/comic.component';
import { ComicDetailComponent } from './detail/comic-detail.component';
import { ComicUpdateComponent } from './update/comic-update.component';
import { ComicDeleteDialogComponent } from './delete/comic-delete-dialog.component';
import { ComicRoutingModule } from './route/comic-routing.module';

@NgModule({
  imports: [SharedModule, ComicRoutingModule],
  declarations: [ComicComponent, ComicDetailComponent, ComicUpdateComponent, ComicDeleteDialogComponent],
  entryComponents: [ComicDeleteDialogComponent],
})
export class ComicModule {}
