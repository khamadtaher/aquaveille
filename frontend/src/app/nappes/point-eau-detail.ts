import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ApiService } from '../core/api.service';
import { Mesure, Parametre, PointEau } from '../core/models';
import { formatSeuil } from '../core/seuil';

interface DerniereValeur {
  parametre: Parametre;
  mesure: Mesure | undefined;
}

/** Fiche d'un point d'eau souterraine : nappe(s) captée(s), dernières valeurs et historique. */
@Component({
  selector: 'app-point-eau-detail',
  imports: [RouterLink, FormsModule, DatePipe, DecimalPipe],
  templateUrl: './point-eau-detail.html',
  styleUrls: ['../station/station-detail.scss', './point-eau-detail.scss'],
})
export class PointEauDetail implements OnInit {
  private readonly api = inject(ApiService);

  /** Identifiant BSS, lié automatiquement au paramètre de route :bssId. */
  readonly bssId = input.required<string>();

  protected readonly point = signal<PointEau | null>(null);
  protected readonly parametres = signal<Parametre[]>([]);
  protected readonly mesures = signal<Mesure[]>([]);
  protected readonly filtre = signal('');
  protected readonly erreur = signal<string | null>(null);
  protected readonly formatSeuil = formatSeuil;

  /** Paramètres ayant au moins une mesure sur ce point. */
  protected readonly dernieres = computed<DerniereValeur[]>(() =>
    this.parametres()
      .map((p) => ({ parametre: p, mesure: this.mesures().find((m) => m.codeParametre === p.code) }))
      .filter((d) => d.mesure !== undefined),
  );

  protected readonly mesuresFiltrees = computed(() => {
    const code = this.filtre();
    return code ? this.mesures().filter((m) => m.codeParametre === code) : this.mesures();
  });

  ngOnInit(): void {
    forkJoin({
      point: this.api.pointEau(this.bssId()),
      parametres: this.api.parametres(),
      mesures: this.api.mesuresNappe(this.bssId()),
    }).subscribe({
      next: ({ point, parametres, mesures }) => {
        this.point.set(point);
        this.parametres.set(parametres);
        this.mesures.set(mesures);
      },
      error: () => this.erreur.set("Point d'eau introuvable ou API indisponible."),
    });
  }
}
