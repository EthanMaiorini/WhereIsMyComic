import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CharactersComponent } from '../list/characters.component';
import { CharactersDetailComponent } from '../detail/characters-detail.component';
import { CharactersUpdateComponent } from '../update/characters-update.component';
import { CharactersRoutingResolveService } from './characters-routing-resolve.service';

const charactersRoute: Routes = [
  {
    path: '',
    component: CharactersComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CharactersDetailComponent,
    resolve: {
      characters: CharactersRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CharactersUpdateComponent,
    resolve: {
      characters: CharactersRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CharactersUpdateComponent,
    resolve: {
      characters: CharactersRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(charactersRoute)],
  exports: [RouterModule],
})
export class CharactersRoutingModule {}
