import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IComic, Comic } from '../comic.model';

import { ComicService } from './comic.service';

describe('Comic Service', () => {
  let service: ComicService;
  let httpMock: HttpTestingController;
  let elemDefault: IComic;
  let expectedResult: IComic | IComic[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ComicService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      issueNumber: 0,
      location: 'AAAAAAA',
      title: 'AAAAAAA',
      description: 'AAAAAAA',
      thumbnailContentType: 'image/png',
      thumbnail: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Comic', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Comic()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Comic', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          issueNumber: 1,
          location: 'BBBBBB',
          title: 'BBBBBB',
          description: 'BBBBBB',
          thumbnail: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Comic', () => {
      const patchObject = Object.assign(
        {
          location: 'BBBBBB',
          description: 'BBBBBB',
        },
        new Comic()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Comic', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          issueNumber: 1,
          location: 'BBBBBB',
          title: 'BBBBBB',
          description: 'BBBBBB',
          thumbnail: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Comic', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addComicToCollectionIfMissing', () => {
      it('should add a Comic to an empty array', () => {
        const comic: IComic = { id: 123 };
        expectedResult = service.addComicToCollectionIfMissing([], comic);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(comic);
      });

      it('should not add a Comic to an array that contains it', () => {
        const comic: IComic = { id: 123 };
        const comicCollection: IComic[] = [
          {
            ...comic,
          },
          { id: 456 },
        ];
        expectedResult = service.addComicToCollectionIfMissing(comicCollection, comic);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Comic to an array that doesn't contain it", () => {
        const comic: IComic = { id: 123 };
        const comicCollection: IComic[] = [{ id: 456 }];
        expectedResult = service.addComicToCollectionIfMissing(comicCollection, comic);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(comic);
      });

      it('should add only unique Comic to an array', () => {
        const comicArray: IComic[] = [{ id: 123 }, { id: 456 }, { id: 79796 }];
        const comicCollection: IComic[] = [{ id: 123 }];
        expectedResult = service.addComicToCollectionIfMissing(comicCollection, ...comicArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const comic: IComic = { id: 123 };
        const comic2: IComic = { id: 456 };
        expectedResult = service.addComicToCollectionIfMissing([], comic, comic2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(comic);
        expect(expectedResult).toContain(comic2);
      });

      it('should accept null and undefined values', () => {
        const comic: IComic = { id: 123 };
        expectedResult = service.addComicToCollectionIfMissing([], null, comic, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(comic);
      });

      it('should return initial array if no Comic is added', () => {
        const comicCollection: IComic[] = [{ id: 123 }];
        expectedResult = service.addComicToCollectionIfMissing(comicCollection, undefined, null);
        expect(expectedResult).toEqual(comicCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
