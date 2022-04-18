import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICharacters } from '../characters.model';
import { CharactersService } from '../service/characters.service';

@Component({
  templateUrl: './characters-delete-dialog.component.html',
})
export class CharactersDeleteDialogComponent {
  characters?: ICharacters;

  constructor(protected charactersService: CharactersService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.charactersService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
