import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { ComicService } from '../service/comic.service';

import { ComicComponent } from './comic.component';

describe('Comic Management Component', () => {
  let comp: ComicComponent;
  let fixture: ComponentFixture<ComicComponent>;
  let service: ComicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ComicComponent],
    })
      .overrideTemplate(ComicComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ComicComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ComicService);

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
    expect(comp.comics?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
