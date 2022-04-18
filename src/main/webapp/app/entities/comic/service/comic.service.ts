import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IComic, getComicIdentifier } from '../comic.model';

export type EntityResponseType = HttpResponse<IComic>;
export type EntityArrayResponseType = HttpResponse<IComic[]>;

@Injectable({ providedIn: 'root' })
export class ComicService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/comics');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(comic: IComic): Observable<EntityResponseType> {
    return this.http.post<IComic>(this.resourceUrl, comic, { observe: 'response' });
  }

  update(comic: IComic): Observable<EntityResponseType> {
    return this.http.put<IComic>(`${this.resourceUrl}/${getComicIdentifier(comic) as number}`, comic, { observe: 'response' });
  }

  partialUpdate(comic: IComic): Observable<EntityResponseType> {
    return this.http.patch<IComic>(`${this.resourceUrl}/${getComicIdentifier(comic) as number}`, comic, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IComic>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IComic[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addComicToCollectionIfMissing(comicCollection: IComic[], ...comicsToCheck: (IComic | null | undefined)[]): IComic[] {
    const comics: IComic[] = comicsToCheck.filter(isPresent);
    if (comics.length > 0) {
      const comicCollectionIdentifiers = comicCollection.map(comicItem => getComicIdentifier(comicItem)!);
      const comicsToAdd = comics.filter(comicItem => {
        const comicIdentifier = getComicIdentifier(comicItem);
        if (comicIdentifier == null || comicCollectionIdentifiers.includes(comicIdentifier)) {
          return false;
        }
        comicCollectionIdentifiers.push(comicIdentifier);
        return true;
      });
      return [...comicsToAdd, ...comicCollection];
    }
    return comicCollection;
  }
}
