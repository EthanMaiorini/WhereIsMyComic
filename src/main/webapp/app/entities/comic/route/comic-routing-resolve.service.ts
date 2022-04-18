import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IComic, Comic } from '../comic.model';
import { ComicService } from '../service/comic.service';

@Injectable({ providedIn: 'root' })
export class ComicRoutingResolveService implements Resolve<IComic> {
  constructor(protected service: ComicService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IComic> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((comic: HttpResponse<Comic>) => {
          if (comic.body) {
            return of(comic.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Comic());
  }
}
