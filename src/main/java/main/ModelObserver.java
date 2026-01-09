package main;

public interface ModelObserver {
	/*
	 * O enumerare pentru a reprezenta tipurile de evenimente care se pot întâmpla în model.
	 * Enumerarea este ținută în interfață dar este folosită în metoda notifyObserver a clasei
	 * model. În exemplul nostru, vizualizarea observă modelul astfel încât să știe
	 * când să actualizeze diverse elemente UI.
	 */
	enum EventTypes {
		BALANCE_CHANGED, POPULATION_CHANGED, DAILYINCOME_CHANGED, DAY_CHANGED, BOARD_CHANGED, HAPPINESS_CHANGED,
		UNEMPLOYEMENT_CHANGED
	};

	/*
	 * Anunță observatorul că soldul s-a schimbat, de obicei din cauza construcției
	 * sau a unei noi zile.
	 */
	public void BalanceChanged();

	/*
	 * Anunță observatorul că populația s-a schimbat, de obicei din cauza
	 * construcției sau demolării.
	 */
	public void PopulationChanged();

	/*
	 * Anunță observatorul că venitul zilnic total s-a schimbat, de obicei din cauza
	 * construcției sau demolării.
	 */
	public void DailyIncomeChanged();

	/*
	 * Anunță observatorul că ziua s-a schimbat, de obicei deoarece utilizatorul a apăsat
	 * butonul 'următoarea zi'.
	 */
	public void DayChanged();

	/*
	 * Anunță observatorul că una sau mai multe poziții de pe tablă s-au schimbat, de obicei
	 * din cauza construcției sau demolării.
	 */
	public void BoardChanged();

	/*
	 * Anunță observatorul că fericirea generală a jocului s-a schimbat.
	 */
	public void HappinessChanged();

	/*
	 * Anunță observatorul că rata șomajului s-a schimbat.
	 */
	public void UnemployementChanged();
}
