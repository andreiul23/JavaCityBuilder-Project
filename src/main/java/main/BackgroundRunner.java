package main;

public class BackgroundRunner extends Thread {

	/* Variabile de instanță */
	private boolean isDone;
	private Model model;
	private double pauseDuration;

	/* Constructor */
	public BackgroundRunner(Model model, double pauseDuration) {
		/* Setează valorile implicite */
		this.isDone = false;
		this.model = model;

		/* Validare intrare: pauseDuration ar trebui să fie 100-1500ms conform SettingsDialog */
		if (pauseDuration < 100 || pauseDuration > 1500) {
			this.pauseDuration = 500;
		} else {
			this.pauseDuration = pauseDuration;
		}
	}

	/* Încheie terminarea firului de execuție */
	public void terminate() {
		isDone = true;
		this.interrupt();
	}

	public synchronized void setPauseDuration(double pauseDuration) {
		if (pauseDuration < 100 || pauseDuration > 1500) {
			this.pauseDuration = 500;
		} else {
			this.pauseDuration = pauseDuration;
		}
	}

	public synchronized double getPauseDuration() {
		return this.pauseDuration;
	}

	public void run() {
		/* Buclă infinită, cât timp este posibil */
		while (!isDone && !Thread.currentThread().isInterrupted()) {
			/* Suspendă firul de execuție pentru durata corectă */
			try {
				Thread.sleep((long) getPauseDuration());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
			/* Efectuează o mișcare, apoi repetă bucla */
			model.nextDay();
		}
	}

}