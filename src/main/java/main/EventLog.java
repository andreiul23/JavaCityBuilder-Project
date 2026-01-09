package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/* Jurnalul de evenimente este responsabil pentru încapsularea informațiilor despre modificările semnificative făcute în clasa model
 * Jurnalul de evenimente este afișat utilizatorului deoarece clasa View este un LogObserver
 * Jurnalul de evenimente a fost creat ca un singleton, însemnând că poate exista o singură instanță a clasei
 */
public class EventLog {
	/*
	 * Implementarea clasei se bazează pe menținerea unei liste de șiruri de caractere care
	 * reprezintă toate evenimentele care au avut loc în jurnal
	 */
	private List<String> events;
	/*
	 * Folosind șablonul de proiectare observator/observabil, trebuie să menținem o listă de
	 * LogObserveri
	 */
	private List<LogObserver> observers;
	/* Încapsulează o instanță a 'this' pentru a implementa șablonul de fabrică */
	private static EventLog eventLog;

	/* Constructor privat */
	private EventLog() {
		events = Collections.synchronizedList(new ArrayList<String>());
		observers = new CopyOnWriteArrayList<LogObserver>();
	}

	/*
	 * Metodă statică folosită pentru a accesa instanța singleton a jurnalului în
	 * locul unui constructor. Dacă nu există deja un jurnal, creăm unul și
	 * îl returnăm. Dacă un jurnal există deja, returnăm jurnalul care ar trebui
	 * să fie încapsulat în variabila eventLog
	 */
	public static EventLog getEventLog() {
		if (eventLog == null) {
			eventLog = new EventLog();
			return eventLog;
		} else {
			return eventLog;
		}
	}

	/* Adaugă o intrare în jurnal și notifică observatorii corespunzători */
	public void addEntry(String entry) {
		/* Verifică intrarea înainte de a adăuga în jurnal */
		if (entry == null) {
			throw new RuntimeException("Null string passed to log addEntry()");
		}
		events.add(entry);
		/*
		 * Acum că am modificat jurnalul, notificăm observatorii astfel încât interfața utilizator să se poată redessena
		 */
		notifyObservers(entry);
	}

	/* Metode Observabile */
	public void addObserver(LogObserver o) {
		observers.add(o);
	}

	public void removeObserver(LogObserver o) {
		observers.remove(o);
	}

	public void notifyObservers(String s) {
		for (LogObserver o : observers) {
			o.newLogEntry(s);
		}
	}

}
