import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Alerte, Mesure, Parametre, PointEau, RapportImport, Station } from './models';

/**
 * Accès à l'API Spring Boot.
 * En local, /api est redirigé vers le port 8080 (proxy.conf.json ou nginx).
 * Sur GitHub Pages, l'URL complète de l'API est fournie par environment.github-pages.ts.
 */
@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly base = environment.apiUrl;

  stations(departement?: string): Observable<Station[]> {
    const params = departement ? new HttpParams().set('departement', departement) : undefined;
    return this.http.get<Station[]>(`${this.base}/stations`, { params });
  }

  station(code: string): Observable<Station> {
    return this.http.get<Station>(`${this.base}/stations/${code}`);
  }

  mesures(code: string, parametre?: string): Observable<Mesure[]> {
    const params = parametre ? new HttpParams().set('parametre', parametre) : undefined;
    return this.http.get<Mesure[]>(`${this.base}/stations/${code}/mesures`, { params });
  }

  parametres(): Observable<Parametre[]> {
    return this.http.get<Parametre[]>(`${this.base}/parametres`);
  }

  alertes(jours = 365): Observable<Alerte[]> {
    return this.http.get<Alerte[]>(`${this.base}/alertes`, { params: { jours } });
  }

  importer(departement: string): Observable<RapportImport> {
    return this.http.post<RapportImport>(`${this.base}/import/${departement}`, null);
  }

  // --- Eaux souterraines ---

  pointsEau(departement?: string): Observable<PointEau[]> {
    const params = departement ? new HttpParams().set('departement', departement) : undefined;
    return this.http.get<PointEau[]>(`${this.base}/nappes/points`, { params });
  }

  pointEau(bssId: string): Observable<PointEau> {
    return this.http.get<PointEau>(`${this.base}/nappes/points/${bssId}`);
  }

  mesuresNappe(bssId: string, parametre?: string): Observable<Mesure[]> {
    const params = parametre ? new HttpParams().set('parametre', parametre) : undefined;
    return this.http.get<Mesure[]>(`${this.base}/nappes/points/${bssId}/mesures`, { params });
  }

  importerNappes(departement: string): Observable<RapportImport> {
    return this.http.post<RapportImport>(`${this.base}/nappes/import/${departement}`, null);
  }
}
