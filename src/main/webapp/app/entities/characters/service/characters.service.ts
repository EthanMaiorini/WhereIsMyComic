import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICharacters, getCharactersIdentifier } from '../characters.model';

export type EntityResponseType = HttpResponse<ICharacters>;
export type EntityArrayResponseType = HttpResponse<ICharacters[]>;

@Injectable({ providedIn: 'root' })
export class CharactersService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/characters');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(characters: ICharacters): Observable<EntityResponseType> {
    return this.http.post<ICharacters>(this.resourceUrl, characters, { observe: 'response' });
  }

  update(characters: ICharacters): Observable<EntityResponseType> {
    return this.http.put<ICharacters>(`${this.resourceUrl}/${getCharactersIdentifier(characters) as number}`, characters, {
      observe: 'response',
    });
  }

  partialUpdate(characters: ICharacters): Observable<EntityResponseType> {
    return this.http.patch<ICharacters>(`${this.resourceUrl}/${getCharactersIdentifier(characters) as number}`, characters, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICharacters>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICharacters[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCharactersToCollectionIfMissing(
    charactersCollection: ICharacters[],
    ...charactersToCheck: (ICharacters | null | undefined)[]
  ): ICharacters[] {
    const characters: ICharacters[] = charactersToCheck.filter(isPresent);
    if (characters.length > 0) {
      const charactersCollectionIdentifiers = charactersCollection.map(charactersItem => getCharactersIdentifier(charactersItem)!);
      const charactersToAdd = characters.filter(charactersItem => {
        const charactersIdentifier = getCharactersIdentifier(charactersItem);
        if (charactersIdentifier == null || charactersCollectionIdentifiers.includes(charactersIdentifier)) {
          return false;
        }
        charactersCollectionIdentifiers.push(charactersIdentifier);
        return true;
      });
      return [...charactersToAdd, ...charactersCollection];
    }
    return charactersCollection;
  }
}
