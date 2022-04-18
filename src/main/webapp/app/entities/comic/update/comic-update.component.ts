import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IComic, Comic } from '../comic.model';
import { ComicService } from '../service/comic.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ISeries } from 'app/entities/series/series.model';
import { SeriesService } from 'app/entities/series/service/series.service';

@Component({
  selector: 'jhi-comic-update',
  templateUrl: './comic-update.component.html',
})
export class ComicUpdateComponent implements OnInit {
  isSaving = false;

  seriesSharedCollection: ISeries[] = [];

  editForm = this.fb.group({
    id: [],
    issuenumber: [],
    location: [],
    title: [],
    description: [],
    thumbnail: [],
    thumbnailContentType: [],
    series: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected comicService: ComicService,
    protected seriesService: SeriesService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ comic }) => {
      this.updateForm(comic);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('whereIsMyComicApp.error', { message: err.message })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const comic = this.createFromForm();
    if (comic.id !== undefined) {
      this.subscribeToSaveResponse(this.comicService.update(comic));
    } else {
      this.subscribeToSaveResponse(this.comicService.create(comic));
    }
  }

  trackSeriesById(_index: number, item: ISeries): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IComic>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(comic: IComic): void {
    this.editForm.patchValue({
      id: comic.id,
      issuenumber: comic.issuenumber,
      location: comic.location,
      title: comic.title,
      description: comic.description,
      thumbnail: comic.thumbnail,
      thumbnailContentType: comic.thumbnailContentType,
      series: comic.series,
    });

    this.seriesSharedCollection = this.seriesService.addSeriesToCollectionIfMissing(this.seriesSharedCollection, comic.series);
  }

  protected loadRelationshipsOptions(): void {
    this.seriesService
      .query()
      .pipe(map((res: HttpResponse<ISeries[]>) => res.body ?? []))
      .pipe(map((series: ISeries[]) => this.seriesService.addSeriesToCollectionIfMissing(series, this.editForm.get('series')!.value)))
      .subscribe((series: ISeries[]) => (this.seriesSharedCollection = series));
  }

  protected createFromForm(): IComic {
    return {
      ...new Comic(),
      id: this.editForm.get(['id'])!.value,
      issuenumber: this.editForm.get(['issuenumber'])!.value,
      location: this.editForm.get(['location'])!.value,
      title: this.editForm.get(['title'])!.value,
      description: this.editForm.get(['description'])!.value,
      thumbnailContentType: this.editForm.get(['thumbnailContentType'])!.value,
      thumbnail: this.editForm.get(['thumbnail'])!.value,
      series: this.editForm.get(['series'])!.value,
    };
  }
}
