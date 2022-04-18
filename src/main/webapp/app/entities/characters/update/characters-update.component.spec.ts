import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CharactersService } from '../service/characters.service';
import { ICharacters, Characters } from '../characters.model';

import { CharactersUpdateComponent } from './characters-update.component';

describe('Characters Management Update Component', () => {
  let comp: CharactersUpdateComponent;
  let fixture: ComponentFixture<CharactersUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let charactersService: CharactersService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CharactersUpdateComponent],
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
      .overrideTemplate(CharactersUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CharactersUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    charactersService = TestBed.inject(CharactersService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const characters: ICharacters = { id: 456 };

      activatedRoute.data = of({ characters });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(characters));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Characters>>();
      const characters = { id: 123 };
      jest.spyOn(charactersService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ characters });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: characters }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(charactersService.update).toHaveBeenCalledWith(characters);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Characters>>();
      const characters = new Characters();
      jest.spyOn(charactersService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ characters });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: characters }));
      saveSubject.complete();

      // THEN
      expect(charactersService.create).toHaveBeenCalledWith(characters);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Characters>>();
      const characters = { id: 123 };
      jest.spyOn(charactersService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ characters });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(charactersService.update).toHaveBeenCalledWith(characters);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
