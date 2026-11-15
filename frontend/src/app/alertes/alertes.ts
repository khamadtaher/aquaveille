import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { Alerte } from '../core/models';
import { formatSeuil } from '../core/seuil';

@Component({
  selector: 'app-alertes',
  imports: [RouterLink, DatePipe, DecimalPipe],
  templateUrl: './alertes.html',
  styleUrl: './alertes.scss',
})
export class Alertes implements OnInit {
  private readonly api = inject(ApiService);

  protected readonly alertes = signal<Alerte[]>([]);
  protected readonly erreur = signal<string | null>(null);
  protected readonly formatSeuil = formatSeuil;

  ngOnInit(): void {
    this.api.alertes().subscribe({
      next: (a) => this.alertes.set(a),
      error: () => this.erreur.set("Impossible de joindre l'API."),
    });
  }
}
