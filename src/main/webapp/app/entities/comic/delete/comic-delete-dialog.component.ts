import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IComic } from '../comic.model';
import { ComicService } from '../service/comic.service';

@Component({
  templateUrl: './comic-delete-dialog.component.html',
})
export class ComicDeleteDialogComponent {
  comic?: IComic;

  constructor(protected comicService: ComicService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.comicService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
