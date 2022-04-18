import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SeriesDetailComponent } from './series-detail.component';

describe('Series Management Detail Component', () => {
  let comp: SeriesDetailComponent;
  let fixture: ComponentFixture<SeriesDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SeriesDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ series: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SeriesDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SeriesDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load series on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.series).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
