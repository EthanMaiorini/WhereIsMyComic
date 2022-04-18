import { ISeries } from 'app/entities/series/series.model';
import { ICharacters } from 'app/entities/characters/characters.model';

export interface IComic {
  id?: number;
  issueNumber?: number | null;
  location?: string | null;
  title?: string | null;
  description?: string | null;
  thumbnailContentType?: string | null;
  thumbnail?: string | null;
  series?: ISeries | null;
  characters?: ICharacters | null;
}

export class Comic implements IComic {
  constructor(
    public id?: number,
    public issueNumber?: number | null,
    public location?: string | null,
    public title?: string | null,
    public description?: string | null,
    public thumbnailContentType?: string | null,
    public thumbnail?: string | null,
    public series?: ISeries | null,
    public characters?: ICharacters | null
  ) {}
}

export function getComicIdentifier(comic: IComic): number | undefined {
  return comic.id;
}
