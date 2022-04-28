import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';
import { ICharacters } from 'app/entities/characters/characters.model';
import {CharactersService} from 'app/entities/characters/service/characters.service';
import {Characters} from 'app/entities/characters/characters.model'

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  account: Account | null = null;
  characters?: ICharacters[];
  isLoading = false;

  private readonly destroy$ = new Subject<void>();

  constructor(private accountService: AccountService, private router: Router, private charactersService:CharactersService ) {}

  ngOnInit(): void {
    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => (this.account = account));
//      this.charactersService.findAll(characters => (this.characters = findAll()));
       this.getCharacters();
  }


  login(): void {
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  getCharacters():void{
  this.isLoading = true;
    this.charactersService.query().subscribe({
     next: (res: HttpResponse<ICharacters[]>) => {
      this.isLoading = false;
      this.characters = res.body ?? [];},
       error: () => { this.isLoading = false; },
     });
  }
}
