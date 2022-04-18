import { ISeries } from 'app/entities/series/series.model';

export interface IComic {
  id?: number;
  issuenumber?: number | null;
  location?: string | null;
  title?: string | null;
  description?: string | null;
  thumbnailContentType?: string | null;
  thumbnail?: string | null;
  series?: ISeries | null;
}

export class Comic implements IComic {
  constructor(
    public id?: number,
    public issuenumber?: number | null,
    public location?: string | null,
    public title?: string | null,
    public description?: string | null,
    public thumbnailContentType?: string | null,
    public thumbnail?: string | null,
    public series?: ISeries | null
  ) {}
}

export function getComicIdentifier(comic: IComic): number | undefined {
  return comic.id;
}
