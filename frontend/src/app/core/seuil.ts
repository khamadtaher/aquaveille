/** Texte lisible d'une plage de seuils, ex. "≤ 50", "≥ 6", "6 – 9". */
export function formatSeuil(min: number | null, max: number | null): string {
  if (min != null && max != null) return `${min} – ${max}`;
  if (max != null) return `≤ ${max}`;
  if (min != null) return `≥ ${min}`;
  return '—';
}
