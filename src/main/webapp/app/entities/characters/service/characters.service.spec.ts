import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICharacters, Characters } from '../characters.model';

import { CharactersService } from './characters.service';

describe('Characters Service', () => {
  let service: CharactersService;
  let httpMock: HttpTestingController;
  let elemDefault: ICharacters;
  let expectedResult: ICharacters | ICharacters[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CharactersService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      fullname: 'AAAAAAA',
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

    it('should create a Characters', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Characters()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Characters', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          fullname: 'BBBBBB',
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

    it('should partial update a Characters', () => {
      const patchObject = Object.assign(
        {
          fullname: 'BBBBBB',
          description: 'BBBBBB',
        },
        new Characters()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Characters', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          fullname: 'BBBBBB',
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

    it('should delete a Characters', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCharactersToCollectionIfMissing', () => {
      it('should add a Characters to an empty array', () => {
        const characters: ICharacters = { id: 123 };
        expectedResult = service.addCharactersToCollectionIfMissing([], characters);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(characters);
      });

      it('should not add a Characters to an array that contains it', () => {
        const characters: ICharacters = { id: 123 };
        const charactersCollection: ICharacters[] = [
          {
            ...characters,
          },
          { id: 456 },
        ];
        expectedResult = service.addCharactersToCollectionIfMissing(charactersCollection, characters);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Characters to an array that doesn't contain it", () => {
        const characters: ICharacters = { id: 123 };
        const charactersCollection: ICharacters[] = [{ id: 456 }];
        expectedResult = service.addCharactersToCollectionIfMissing(charactersCollection, characters);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(characters);
      });

      it('should add only unique Characters to an array', () => {
        const charactersArray: ICharacters[] = [{ id: 123 }, { id: 456 }, { id: 538 }];
        const charactersCollection: ICharacters[] = [{ id: 123 }];
        expectedResult = service.addCharactersToCollectionIfMissing(charactersCollection, ...charactersArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const characters: ICharacters = { id: 123 };
        const characters2: ICharacters = { id: 456 };
        expectedResult = service.addCharactersToCollectionIfMissing([], characters, characters2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(characters);
        expect(expectedResult).toContain(characters2);
      });

      it('should accept null and undefined values', () => {
        const characters: ICharacters = { id: 123 };
        expectedResult = service.addCharactersToCollectionIfMissing([], null, characters, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(characters);
      });

      it('should return initial array if no Characters is added', () => {
        const charactersCollection: ICharacters[] = [{ id: 123 }];
        expectedResult = service.addCharactersToCollectionIfMissing(charactersCollection, undefined, null);
        expect(expectedResult).toEqual(charactersCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
