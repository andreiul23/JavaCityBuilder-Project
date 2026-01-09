package main;

public interface LogObserver {
	/*
	 * Un observator de jurnal trebuie să știe când a fost adăugat ceva în jurnal. În acest
	 * joc, View servește ca un LogObserver. Vizualizarea vrea să știe ce este adăugat
	 * în jurnal pentru a-și putea actualiza interfața utilizator în mod corespunzător. 'entry"
	 * reprezintă noul șir de caractere care a fost adăugat în jurnal.
	 */
	public void newLogEntry(String entry);
}
