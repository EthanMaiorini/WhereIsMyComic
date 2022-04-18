import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ComicComponent } from '../list/comic.component';
import { ComicDetailComponent } from '../detail/comic-detail.component';
import { ComicUpdateComponent } from '../update/comic-update.component';
import { ComicRoutingResolveService } from './comic-routing-resolve.service';

const comicRoute: Routes = [
  {
    path: '',
    component: ComicComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ComicDetailComponent,
    resolve: {
      comic: ComicRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ComicUpdateComponent,
    resolve: {
      comic: ComicRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ComicUpdateComponent,
    resolve: {
      comic: ComicRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(comicRoute)],
  exports: [RouterModule],
})
export class ComicRoutingModule {}
