import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { App } from './app';
import { formatSeuil } from './core/seuil';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  it('affiche la navigation', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const html = fixture.nativeElement as HTMLElement;
    expect(html.querySelector('.marque')?.textContent).toContain('AquaVeille');
    expect(html.querySelectorAll('nav a').length).toBe(3);
  });
});

describe('formatSeuil', () => {
  it('formate les différentes plages', () => {
    expect(formatSeuil(null, 50)).toBe('≤ 50');
    expect(formatSeuil(6, null)).toBe('≥ 6');
    expect(formatSeuil(6, 9)).toBe('6 – 9');
    expect(formatSeuil(null, null)).toBe('—');
  });
});
