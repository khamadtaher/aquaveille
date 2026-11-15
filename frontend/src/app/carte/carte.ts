import {
  AfterViewInit, Component, ElementRef, OnDestroy, computed, inject, signal, viewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import * as L from 'leaflet';
import { ApiService } from '../core/api.service';
import { RapportImport, Station } from '../core/models';

@Component({
  selector: 'app-carte',
  imports: [FormsModule],
  templateUrl: './carte.html',
  styleUrl: './carte.scss',
})
export class Carte implements AfterViewInit, OnDestroy {
  private readonly api = inject(ApiService);
  private readonly router = inject(Router);
  private readonly conteneur = viewChild.required<ElementRef<HTMLDivElement>>('carte');

  private map?: L.Map;
  private calque = L.layerGroup();

  protected departement = '91';
  protected readonly stations = signal<Station[]>([]);
  protected readonly chargement = signal(false);
  protected readonly rapport = signal<RapportImport | null>(null);
  protected readonly erreur = signal<string | null>(null);
  protected readonly nbAlertes = computed(() => this.stations().filter((s) => s.enAlerte).length);

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
    this.api.stations(this.departement).subscribe({
      next: (stations) => {
        this.stations.set(stations);
        this.afficher(stations);
      },
      error: () => this.erreur.set("Impossible de joindre l'API. Le back-end est-il démarré ?"),
    });
  }

  protected importer(): void {
    this.chargement.set(true);
    this.rapport.set(null);
    this.erreur.set(null);
    this.api.importer(this.departement).subscribe({
      next: (rapport) => {
        this.rapport.set(rapport);
        this.chargement.set(false);
        this.charger();
      },
      error: () => {
        this.erreur.set("L'import a échoué. Vérifiez le code département et l'accès à Hub'Eau.");
        this.chargement.set(false);
      },
    });
  }

  private afficher(stations: Station[]): void {
    this.calque.clearLayers();
    for (const s of stations) {
      L.circleMarker([s.latitude, s.longitude], {
        radius: s.enAlerte ? 8 : 6,
        weight: 2,
        color: s.enAlerte ? '#b42318' : '#0f6d7a',
        fillColor: s.enAlerte ? '#f04438' : '#2bb3c0',
        fillOpacity: 0.85,
      })
        .bindTooltip(`<strong>${s.libelle}</strong><br>${s.coursEau ?? ''}`)
        .on('click', () => this.router.navigate(['/stations', s.code]))
        .addTo(this.calque);
    }
    if (stations.length > 0 && this.map) {
      const limites = L.latLngBounds(stations.map((s) => [s.latitude, s.longitude] as L.LatLngTuple));
      this.map.fitBounds(limites, { padding: [30, 30] });
    }
  }
}
