import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'characters',
        data: { pageTitle: 'Characters' },
        loadChildren: () => import('./characters/characters.module').then(m => m.CharactersModule),
      },
      {
        path: 'series',
        data: { pageTitle: 'Series' },
        loadChildren: () => import('./series/series.module').then(m => m.SeriesModule),
      },
      {
        path: 'comic',
        data: { pageTitle: 'Comics' },
        loadChildren: () => import('./comic/comic.module').then(m => m.ComicModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
