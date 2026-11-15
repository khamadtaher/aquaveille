import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./carte/carte').then((m) => m.Carte), title: 'AquaVeille · Carte' },
  {
    path: 'stations/:code',
    loadComponent: () => import('./station/station-detail').then((m) => m.StationDetail),
    title: 'AquaVeille · Station',
  },
  {
    path: 'nappes',
    loadComponent: () => import('./nappes/carte-nappes').then((m) => m.CarteNappes),
    title: 'AquaVeille · Eaux souterraines',
  },
  {
    path: 'nappes/:bssId',
    loadComponent: () => import('./nappes/point-eau-detail').then((m) => m.PointEauDetail),
    title: "AquaVeille · Point d'eau",
  },
  { path: 'alertes', loadComponent: () => import('./alertes/alertes').then((m) => m.Alertes), title: 'AquaVeille · Alertes' },
  { path: '**', redirectTo: '' },
];
