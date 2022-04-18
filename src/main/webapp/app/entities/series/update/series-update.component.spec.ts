import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SeriesService } from '../service/series.service';
import { ISeries, Series } from '../series.model';
import { ICharacters } from 'app/entities/characters/characters.model';
import { CharactersService } from 'app/entities/characters/service/characters.service';

import { SeriesUpdateComponent } from './series-update.component';

describe('Series Management Update Component', () => {
  let comp: SeriesUpdateComponent;
  let fixture: ComponentFixture<SeriesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let seriesService: SeriesService;
  let charactersService: CharactersService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SeriesUpdateComponent],
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
      .overrideTemplate(SeriesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SeriesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    seriesService = TestBed.inject(SeriesService);
    charactersService = TestBed.inject(CharactersService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Characters query and add missing value', () => {
      const series: ISeries = { id: 456 };
      const characters: ICharacters = { id: 70373 };
      series.characters = characters;

      const charactersCollection: ICharacters[] = [{ id: 69859 }];
      jest.spyOn(charactersService, 'query').mockReturnValue(of(new HttpResponse({ body: charactersCollection })));
      const additionalCharacters = [characters];
      const expectedCollection: ICharacters[] = [...additionalCharacters, ...charactersCollection];
      jest.spyOn(charactersService, 'addCharactersToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ series });
      comp.ngOnInit();

      expect(charactersService.query).toHaveBeenCalled();
      expect(charactersService.addCharactersToCollectionIfMissing).toHaveBeenCalledWith(charactersCollection, ...additionalCharacters);
      expect(comp.charactersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const series: ISeries = { id: 456 };
      const characters: ICharacters = { id: 60776 };
      series.characters = characters;

      activatedRoute.data = of({ series });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(series));
      expect(comp.charactersSharedCollection).toContain(characters);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Series>>();
      const series = { id: 123 };
      jest.spyOn(seriesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ series });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: series }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(seriesService.update).toHaveBeenCalledWith(series);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Series>>();
      const series = new Series();
      jest.spyOn(seriesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ series });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: series }));
      saveSubject.complete();

      // THEN
      expect(seriesService.create).toHaveBeenCalledWith(series);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Series>>();
      const series = { id: 123 };
      jest.spyOn(seriesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ series });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(seriesService.update).toHaveBeenCalledWith(series);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackCharactersById', () => {
      it('Should return tracked Characters primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackCharactersById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
