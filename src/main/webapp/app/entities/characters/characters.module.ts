import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CharactersComponent } from './list/characters.component';
import { CharactersDetailComponent } from './detail/characters-detail.component';
import { CharactersUpdateComponent } from './update/characters-update.component';
import { CharactersDeleteDialogComponent } from './delete/characters-delete-dialog.component';
import { CharactersRoutingModule } from './route/characters-routing.module';

@NgModule({
  imports: [SharedModule, CharactersRoutingModule],
  declarations: [CharactersComponent, CharactersDetailComponent, CharactersUpdateComponent, CharactersDeleteDialogComponent],
  entryComponents: [CharactersDeleteDialogComponent],
})
export class CharactersModule {}
