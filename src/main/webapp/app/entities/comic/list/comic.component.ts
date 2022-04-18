import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IComic } from '../comic.model';
import { ComicService } from '../service/comic.service';
import { ComicDeleteDialogComponent } from '../delete/comic-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-comic',
  templateUrl: './comic.component.html',
})
export class ComicComponent implements OnInit {
  comics?: IComic[];
  isLoading = false;

  constructor(protected comicService: ComicService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.comicService.query().subscribe({
      next: (res: HttpResponse<IComic[]>) => {
        this.isLoading = false;
        this.comics = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IComic): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(comic: IComic): void {
    const modalRef = this.modalService.open(ComicDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.comic = comic;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
