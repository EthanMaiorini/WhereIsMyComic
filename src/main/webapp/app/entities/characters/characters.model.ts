import { ISeries } from 'app/entities/series/series.model';

export interface ICharacters {
  id?: number;
  fullname?: string | null;
  description?: string | null;
  thumbnailContentType?: string | null;
  thumbnail?: string | null;
  series?: ISeries[] | null;
}

export class Characters implements ICharacters {
  constructor(
    public id?: number,
    public fullname?: string | null,
    public description?: string | null,
    public thumbnailContentType?: string | null,
    public thumbnail?: string | null,
    public series?: ISeries[] | null
  ) {}
}

export function getCharactersIdentifier(characters: ICharacters): number | undefined {
  return characters.id;
}
