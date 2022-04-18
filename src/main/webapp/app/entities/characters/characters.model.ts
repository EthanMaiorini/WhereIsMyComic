import { ISeries } from 'app/entities/series/series.model';
import { IComic } from 'app/entities/comic/comic.model';

export interface ICharacters {
  id?: number;
  fullName?: string | null;
  description?: string | null;
  thumbnailContentType?: string | null;
  thumbnail?: string | null;
  series?: ISeries[] | null;
  comics?: IComic[] | null;
}

export class Characters implements ICharacters {
  constructor(
    public id?: number,
    public fullName?: string | null,
    public description?: string | null,
    public thumbnailContentType?: string | null,
    public thumbnail?: string | null,
    public series?: ISeries[] | null,
    public comics?: IComic[] | null
  ) {}
}

export function getCharactersIdentifier(characters: ICharacters): number | undefined {
  return characters.id;
}
