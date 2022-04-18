import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISeries } from '../series.model';
import { SeriesService } from '../service/series.service';
import { SeriesDeleteDialogComponent } from '../delete/series-delete-dialog.component';

@Component({
  selector: 'jhi-series',
  templateUrl: './series.component.html',
})
export class SeriesComponent implements OnInit {
  series?: ISeries[];
  isLoading = false;

  constructor(protected seriesService: SeriesService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.seriesService.query().subscribe({
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

  delete(series: ISeries): void {
    const modalRef = this.modalService.open(SeriesDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.series = series;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
