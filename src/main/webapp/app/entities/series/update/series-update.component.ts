import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ISeries, Series } from '../series.model';
import { SeriesService } from '../service/series.service';
import { ICharacters } from 'app/entities/characters/characters.model';
import { CharactersService } from 'app/entities/characters/service/characters.service';

@Component({
  selector: 'jhi-series-update',
  templateUrl: './series-update.component.html',
})
export class SeriesUpdateComponent implements OnInit {
  isSaving = false;

  charactersSharedCollection: ICharacters[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    characters: [],
  });

  constructor(
    protected seriesService: SeriesService,
    protected charactersService: CharactersService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ series }) => {
      this.updateForm(series);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const series = this.createFromForm();
    if (series.id !== undefined) {
      this.subscribeToSaveResponse(this.seriesService.update(series));
    } else {
      this.subscribeToSaveResponse(this.seriesService.create(series));
    }
  }

  trackCharactersById(_index: number, item: ICharacters): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISeries>>): void {
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

  protected updateForm(series: ISeries): void {
    this.editForm.patchValue({
      id: series.id,
      name: series.name,
      characters: series.characters,
    });

    this.charactersSharedCollection = this.charactersService.addCharactersToCollectionIfMissing(
      this.charactersSharedCollection,
      series.characters
    );
  }

  protected loadRelationshipsOptions(): void {
    this.charactersService
      .query()
      .pipe(map((res: HttpResponse<ICharacters[]>) => res.body ?? []))
      .pipe(
        map((characters: ICharacters[]) =>
          this.charactersService.addCharactersToCollectionIfMissing(characters, this.editForm.get('characters')!.value)
        )
      )
      .subscribe((characters: ICharacters[]) => (this.charactersSharedCollection = characters));
  }

  protected createFromForm(): ISeries {
    return {
      ...new Series(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      characters: this.editForm.get(['characters'])!.value,
    };
  }
}
