import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./carte/carte').then((m) => m.Carte), title: 'AquaVeille · Carte' },
  {
    path: 'stations/:code',
    loadComponent: () => import('./station/station-detail').then((m) => m.StationDetail),
    title: 'AquaVeille · Station',
  },
  { path: 'alertes', loadComponent: () => import('./alertes/alertes').then((m) => m.Alertes), title: 'AquaVeille · Alertes' },
  { path: '**', redirectTo: '' },
];
