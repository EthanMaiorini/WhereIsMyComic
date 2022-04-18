import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICharacters, Characters } from '../characters.model';
import { CharactersService } from '../service/characters.service';

@Injectable({ providedIn: 'root' })
export class CharactersRoutingResolveService implements Resolve<ICharacters> {
  constructor(protected service: CharactersService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICharacters> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((characters: HttpResponse<Characters>) => {
          if (characters.body) {
            return of(characters.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Characters());
  }
}
