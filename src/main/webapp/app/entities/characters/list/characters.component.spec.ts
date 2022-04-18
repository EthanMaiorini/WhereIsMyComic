import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { CharactersService } from '../service/characters.service';

import { CharactersComponent } from './characters.component';

describe('Characters Management Component', () => {
  let comp: CharactersComponent;
  let fixture: ComponentFixture<CharactersComponent>;
  let service: CharactersService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [CharactersComponent],
    })
      .overrideTemplate(CharactersComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CharactersComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CharactersService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.characters?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
