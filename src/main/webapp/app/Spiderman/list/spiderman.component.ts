import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISeries } from '/Users/ethan/dev/dev2/myApp/src/main/webapp/app/entities/series/series.model';
import { SeriesService } from '/Users/ethan/dev/dev2/myApp/src/main/webapp/app/entities/series/service/series.service';

@Component({
  selector: 'jhi-spiderman',
  templateUrl: './spiderman.component.html',
})
export class SpidermanComponent implements OnInit {
  series?: ISeries[];
  isLoading = false;

  constructor(protected seriesService: SeriesService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.seriesService.getByCharacter().subscribe({
      next: (res: HttpResponse<ISeries[]>) => {
        this.isLoading = false;
        this.series = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: ISeries): number {
    return item.id!;
  }

}
