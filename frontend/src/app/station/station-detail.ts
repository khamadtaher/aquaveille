import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ApiService } from '../core/api.service';
import { Mesure, Parametre, Station } from '../core/models';
import { formatSeuil } from '../core/seuil';

interface DerniereValeur {
  parametre: Parametre;
  mesure: Mesure | undefined;
}

@Component({
  selector: 'app-station-detail',
  imports: [RouterLink, FormsModule, DatePipe, DecimalPipe],
  templateUrl: './station-detail.html',
  styleUrl: './station-detail.scss',
})
export class StationDetail implements OnInit {
  private readonly api = inject(ApiService);

  /** Code station, lié automatiquement au paramètre de route :code. */
  readonly code = input.required<string>();

  protected readonly station = signal<Station | null>(null);
  protected readonly parametres = signal<Parametre[]>([]);
  protected readonly mesures = signal<Mesure[]>([]);
  protected readonly filtre = signal('');
  protected readonly erreur = signal<string | null>(null);
  protected readonly formatSeuil = formatSeuil;

  /** Dernière valeur connue pour chaque paramètre suivi. */
  protected readonly dernieres = computed<DerniereValeur[]>(() =>
    this.parametres().map((p) => ({
      parametre: p,
      mesure: this.mesures().find((m) => m.codeParametre === p.code),
    })),
  );

  protected readonly mesuresFiltrees = computed(() => {
    const code = this.filtre();
    return code ? this.mesures().filter((m) => m.codeParametre === code) : this.mesures();
  });

  ngOnInit(): void {
    forkJoin({
      station: this.api.station(this.code()),
      parametres: this.api.parametres(),
      mesures: this.api.mesures(this.code()),
    }).subscribe({
      next: ({ station, parametres, mesures }) => {
        this.station.set(station);
        this.parametres.set(parametres);
        this.mesures.set(mesures);
      },
      error: () => this.erreur.set('Station introuvable ou API indisponible.'),
    });
  }
}
