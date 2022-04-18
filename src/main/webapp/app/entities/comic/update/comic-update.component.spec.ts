import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ComicService } from '../service/comic.service';
import { IComic, Comic } from '../comic.model';
import { ISeries } from 'app/entities/series/series.model';
import { SeriesService } from 'app/entities/series/service/series.service';

import { ComicUpdateComponent } from './comic-update.component';

describe('Comic Management Update Component', () => {
  let comp: ComicUpdateComponent;
  let fixture: ComponentFixture<ComicUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let comicService: ComicService;
  let seriesService: SeriesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ComicUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ComicUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ComicUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    comicService = TestBed.inject(ComicService);
    seriesService = TestBed.inject(SeriesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Series query and add missing value', () => {
      const comic: IComic = { id: 456 };
      const series: ISeries = { id: 83911 };
      comic.series = series;

      const seriesCollection: ISeries[] = [{ id: 3501 }];
      jest.spyOn(seriesService, 'query').mockReturnValue(of(new HttpResponse({ body: seriesCollection })));
      const additionalSeries = [series];
      const expectedCollection: ISeries[] = [...additionalSeries, ...seriesCollection];
      jest.spyOn(seriesService, 'addSeriesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ comic });
      comp.ngOnInit();

      expect(seriesService.query).toHaveBeenCalled();
      expect(seriesService.addSeriesToCollectionIfMissing).toHaveBeenCalledWith(seriesCollection, ...additionalSeries);
      expect(comp.seriesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const comic: IComic = { id: 456 };
      const series: ISeries = { id: 47907 };
      comic.series = series;

      activatedRoute.data = of({ comic });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(comic));
      expect(comp.seriesSharedCollection).toContain(series);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Comic>>();
      const comic = { id: 123 };
      jest.spyOn(comicService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ comic });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: comic }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(comicService.update).toHaveBeenCalledWith(comic);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Comic>>();
      const comic = new Comic();
      jest.spyOn(comicService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ comic });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: comic }));
      saveSubject.complete();

      // THEN
      expect(comicService.create).toHaveBeenCalledWith(comic);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Comic>>();
      const comic = { id: 123 };
      jest.spyOn(comicService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ comic });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(comicService.update).toHaveBeenCalledWith(comic);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackSeriesById', () => {
      it('Should return tracked Series primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackSeriesById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
