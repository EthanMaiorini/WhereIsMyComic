import { IComic } from 'app/entities/comic/comic.model';
import { ICharacters } from 'app/entities/characters/characters.model';

export interface ISeries {
  id?: number;
  name?: string | null;
  comics?: IComic[] | null;
  characters?: ICharacters | null;
}

export class Series implements ISeries {
  constructor(public id?: number, public name?: string | null, public comics?: IComic[] | null, public characters?: ICharacters | null) {}
}

export function getSeriesIdentifier(series: ISeries): number | undefined {
  return series.id;
}
