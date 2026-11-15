import {
  AfterViewInit, Component, ElementRef, OnDestroy, computed, inject, signal, viewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import * as L from 'leaflet';
import { ApiService } from '../core/api.service';
import { PointEau, RapportImport } from '../core/models';

/** Carte des points d'eau souterraine (forages, puits, sources) d'un département. */
@Component({
  selector: 'app-carte-nappes',
  imports: [FormsModule],
  templateUrl: './carte-nappes.html',
  styleUrl: '../carte/carte.scss',
})
export class CarteNappes implements AfterViewInit, OnDestroy {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  private readonly conteneur = viewChild.required<ElementRef<HTMLDivElement>>('carte');

  private map?: L.Map;
  private calque = L.layerGroup();

  protected departement = '91';
  protected readonly points = signal<PointEau[]>([]);
  protected readonly chargement = signal(false);
  protected readonly rapport = signal<RapportImport | null>(null);
  protected readonly erreur = signal<string | null>(null);
  protected readonly nbAlertes = computed(() => this.points().filter((p) => p.enAlerte).length);
  protected readonly nbMulticouches = computed(() => this.points().filter((p) => p.entites.length > 1).length);

  ngAfterViewInit(): void {
    this.map = L.map(this.conteneur().nativeElement, { zoomControl: true }).setView([46.6, 2.4], 6);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      attribution: '&copy; contributeurs OpenStreetMap',
    }).addTo(this.map);
    this.calque.addTo(this.map);
    this.charger();
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }

  protected charger(): void {
    this.erreur.set(null);
    this.api.pointsEau(this.departement).subscribe({
      next: (points) => {
        this.points.set(points);
        this.afficher(points);
      },
      error: () => this.erreur.set("Impossible de joindre l'API. Le serveur peut mettre une minute à se réveiller."),
    });
  }

  protected importer(): void {
    this.chargement.set(true);
    this.rapport.set(null);
    this.erreur.set(null);
    this.api.importerNappes(this.departement).subscribe({
      next: (rapport) => {
        this.rapport.set(rapport);
        this.chargement.set(false);
        this.charger();
      },
      error: () => {
        this.erreur.set("L'import a échoué. Vérifiez le code département, ou réessayez dans une minute.");
        this.chargement.set(false);
      },
    });
  }

  private afficher(points: PointEau[]): void {
    this.calque.clearLayers();
    for (const p of points) {
      const multicouche = p.entites.length > 1;
      const nappes = p.entites.length === 0
        ? 'Nappe non renseignée'
        : multicouche
          ? `Multicouche : ${p.entites.length} nappes`
          : (p.entites[0].nom ?? p.entites[0].code);
      L.circleMarker([p.latitude, p.longitude], {
        radius: p.enAlerte ? 8 : 6,
        weight: 2,
        dashArray: multicouche ? '3 3' : undefined,
        color: p.enAlerte ? '#b42318' : '#5b3fa0',
        fillColor: p.enAlerte ? '#f04438' : '#9b87d6',
        fillOpacity: 0.85,
      })
        .bindTooltip(
          `<strong>${p.libelle ?? p.codeBss ?? p.bssId}</strong><br>` +
            `${p.nature ?? 'Point d\'eau'}${p.profondeur != null ? ` · ${p.profondeur} m` : ''}<br>${nappes}`,
        )
        .on('click', () => this.router.navigate(['/nappes', p.bssId]))
        .addTo(this.calque);
    }
    if (points.length > 0 && this.map) {
      const limites = L.latLngBounds(points.map((p) => [p.latitude, p.longitude] as L.LatLngTuple));
      this.map.fitBounds(limites, { padding: [30, 30] });
    }
  }
}
