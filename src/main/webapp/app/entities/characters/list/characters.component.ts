import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICharacters } from '../characters.model';
import { CharactersService } from '../service/characters.service';
import { CharactersDeleteDialogComponent } from '../delete/characters-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-characters',
  templateUrl: './characters.component.html',
})
export class CharactersComponent implements OnInit {
  characters?: ICharacters[];
  isLoading = false;

  constructor(protected charactersService: CharactersService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.charactersService.query().subscribe({
      next: (res: HttpResponse<ICharacters[]>) => {
        this.isLoading = false;
        this.characters = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ICharacters): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(characters: ICharacters): void {
    const modalRef = this.modalService.open(CharactersDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.characters = characters;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
