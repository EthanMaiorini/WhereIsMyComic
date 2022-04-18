import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISeries } from '../series.model';

@Component({
  selector: 'jhi-series-detail',
  templateUrl: './series-detail.component.html',
})
export class SeriesDetailComponent implements OnInit {
  series: ISeries | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ series }) => {
      this.series = series;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
