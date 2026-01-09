package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import boardPieces.*;

public class Model {

	public static final int BOARD_X = GameConfig.BOARD_WIDTH;
	public static final int BOARD_Y = GameConfig.BOARD_HEIGHT;
	public static double COST_TO_DEMOLISH = GameConfig.DEMOLISH_COST;

	private BoardPieceInterface[][] board;
	private double balance;
	private int day;
	private List<main.ModelObserver> observers;
	private double happiness;
	private int score;
	private List<String> achievedMilestones;
	private boolean randomEventsEnabled = true;
	private final SnapshotStack<GameState> stateHistory = new SnapshotStack<>(10);

	public Model() {
		/* Resetați costurile statice escaladante, astfel încât un joc nou să înceapă la prețurile de bază */
		HousePiece.costToConstruct = GameConfig.COST_HOUSE;
		ApartmentPiece.costToConstruct = GameConfig.COST_APARTMENT;
		FactoryPiece.costToConstruct = GameConfig.COST_FACTORY;
		RetailPiece.costToConstruct = GameConfig.COST_RETAIL;
		ParkPiece.costToConstruct = GameConfig.COST_PARK;
		RoadPiece.costToConstruct = GameConfig.COST_ROAD;
		COST_TO_DEMOLISH = GameConfig.DEMOLISH_COST;

		/* Setează valoarea implicită pentru variabilele de instanță */
		balance = GameConfig.STARTING_BALANCE;
		day = 1;
		happiness = 0.000;
		score = 0;
		achievedMilestones = new ArrayList<>();
		observers = new ArrayList<main.ModelObserver>();

		/* Generare Hartă */
		MapGenerator generator = new DefaultMapGenerator();
		this.board = generator.generateMap(BOARD_X, BOARD_Y);
		
		// Resetați costul drumului deoarece generatorul de hărți construiește drumuri
		RoadPiece.costToConstruct = GameConfig.COST_ROAD;
	}

	/* Returnează tabla, dar clonată */
	public synchronized BoardPieceInterface[][] getBoard() {
		BoardPieceInterface[][] copy = new BoardPieceInterface[board.length][board[0].length];
		for (int y = 0; y < board.length; y++) {
			System.arraycopy(board[y], 0, copy[y], 0, board[0].length);
		}
		return copy;
	}

	/* Acces sigur doar pentru citire la o singură piesă */
	public synchronized BoardPieceInterface getPieceAt(int x, int y) {
		if (y < 0 || y >= board.length || x < 0 || x >= board[0].length) {
			return null;
		}
		return board[y][x];
	}

	public synchronized double getBalance() {
		return balance;
	}

	public synchronized int getDay() {
		return day;
	}

	public synchronized int getScore() {
		return score;
	}

	public synchronized void addScore(int points) {
		score += points;
		notifyObservers(ModelObserver.EventTypes.BALANCE_CHANGED); // Folosește evenimentul existent pentru actualizarea UI
	}

	public synchronized boolean hasMilestone(String milestoneName) {
		return achievedMilestones.contains(milestoneName);
	}

	public synchronized void addMilestone(String milestoneName) {
		if (!achievedMilestones.contains(milestoneName)) {
			achievedMilestones.add(milestoneName);
			EventLog.getEventLog().addEntry("🏆 Realizare: " + milestoneName);
			addScore(50); // Acordă puncte pentru realizare
		}
	}

	public synchronized void addToBalance(double amount) {
		/* Oprește afișarea pe ecran după fiecare zi */
		if (amount == 0.0) {
			return;
		}
		balance += amount;
		notifyObservers(main.ModelObserver.EventTypes.BALANCE_CHANGED);
		EventLog.getEventLog().addEntry("Balanța a fost modificată cu: $" + View.round(amount, 2));
	}

	public synchronized double getDailyIncome() {
		double employmentMultiplier = this.getEmploymentMultiplier();
		double runningTotal = 0.0;
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[0].length; x++) {
				runningTotal += board[y][x].getDailyIncome() * employmentMultiplier;
			}
		}
		return runningTotal;
	}

	public synchronized int getPopulation() {
		int runningTotal = 0;
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[0].length; x++) {
				runningTotal += board[y][x].getNumResidents();
			}
		}
		return runningTotal;
	}

	public synchronized boolean undoLastAction() {
		GameState snap = stateHistory.pollSnapshot();
		if (snap != null) {
			restoreStateSnapshot(snap);
			EventLog.getEventLog().addEntry("⏪ Anulare ultima acțiune");
			notifyObservers(ModelObserver.EventTypes.BALANCE_CHANGED);
			notifyObservers(ModelObserver.EventTypes.BOARD_CHANGED);
			notifyObservers(ModelObserver.EventTypes.POPULATION_CHANGED);
			notifyObservers(ModelObserver.EventTypes.DAILYINCOME_CHANGED);
			notifyObservers(ModelObserver.EventTypes.HAPPINESS_CHANGED);
			notifyObservers(ModelObserver.EventTypes.UNEMPLOYEMENT_CHANGED);
			return true;
		}
		return false;
	}

	public synchronized void saveToFile(File file) throws SaveLoadException {
		if (file == null) {
			throw new SaveLoadException("File cannot be null");
		}
		GameState snap = new GameState(board, balance, day, happiness, score, COST_TO_DEMOLISH,
				randomEventsEnabled, HousePiece.costToConstruct, ApartmentPiece.costToConstruct,
				FactoryPiece.costToConstruct, RetailPiece.costToConstruct, ParkPiece.costToConstruct,
				RoadPiece.costToConstruct);
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
			oos.writeObject(snap);
		} catch (IOException ex) {
			throw new SaveLoadException("Failed to save file: " + file.getName(), ex);
		}
	}

	public synchronized void loadFromFile(File file) throws SaveLoadException {
		if (file == null || !file.exists()) {
			throw new SaveLoadException("Save file not found");
		}
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
			Object obj = ois.readObject();
			if (!(obj instanceof GameState)) {
				throw new SaveLoadException("Invalid save file format");
			}
			GameState snap = (GameState) obj;
			restoreStateSnapshot(snap);
		} catch (IOException | ClassNotFoundException ex) {
			throw new SaveLoadException("Failed to load save file", ex);
		}
		EventLog.getEventLog().addEntry("💾 Game loaded from " + file.getName());
		notifyObservers(ModelObserver.EventTypes.BALANCE_CHANGED);
		notifyObservers(ModelObserver.EventTypes.DAY_CHANGED);
		notifyObservers(ModelObserver.EventTypes.BOARD_CHANGED);
		notifyObservers(ModelObserver.EventTypes.DAILYINCOME_CHANGED);
		notifyObservers(ModelObserver.EventTypes.POPULATION_CHANGED);
		notifyObservers(ModelObserver.EventTypes.HAPPINESS_CHANGED);
		notifyObservers(ModelObserver.EventTypes.UNEMPLOYEMENT_CHANGED);
	}

	private synchronized void restoreStateSnapshot(GameState snap) {
		if (snap == null) return;
		for (int y = 0; y < board.length; y++) {
			System.arraycopy(snap.board[y], 0, board[y], 0, board[0].length);
		}
		this.balance = snap.balance;
		this.day = snap.day;
		this.happiness = snap.happiness;
		this.score = snap.score;
		COST_TO_DEMOLISH = snap.costToDemolish;
		this.randomEventsEnabled = snap.randomEventsEnabled;
		HousePiece.costToConstruct = snap.houseCost;
		ApartmentPiece.costToConstruct = snap.apartmentCost;
		FactoryPiece.costToConstruct = snap.factoryCost;
		RetailPiece.costToConstruct = snap.retailCost;
		ParkPiece.costToConstruct = snap.parkCost;
		RoadPiece.costToConstruct = snap.roadCost;
	}

	private synchronized void captureStateSnapshot() {
		GameState snap = new GameState(board, balance, day, happiness, score, COST_TO_DEMOLISH,
				randomEventsEnabled, HousePiece.costToConstruct, ApartmentPiece.costToConstruct,
				FactoryPiece.costToConstruct, RetailPiece.costToConstruct, ParkPiece.costToConstruct,
				RoadPiece.costToConstruct);
		stateHistory.pushSnapshot(snap);
	}

	public synchronized String[] getAvailableChoices(int x, int y) {
		/* Creează o listă goală */
		List<String> potentialOptions = new ArrayList<String>();
		/* Dacă ceva a fost deja construit aici, permite doar demolarea */
		if (!(board[y][x] instanceof GrassPiece) && this.getBalance() >= COST_TO_DEMOLISH
				&& !(board[y][x] instanceof WaterPiece) && !(board[y][x] instanceof RoadPiece)) {
			potentialOptions.add("Demolare: $" + COST_TO_DEMOLISH);
			return potentialOptions.toArray(new String[potentialOptions.size()]);

		} else if (!(board[y][x] instanceof GrassPiece)) {
			return null;
		}

		/* Adaugă Drum - construibil pe iarbă oriunde */
		if (this.getBalance() >= RoadPiece.costToConstruct) {
			potentialOptions.add("Drum: $" + View.round(RoadPiece.costToConstruct, 2));
		}
		
		/* Adaugă Casă - necesită atingerea drumului SAU iarbă care atinge drumul */
		if (this.getBalance() >= HousePiece.costToConstruct && (isPieceTouchingRoad(x, y) || isPieceTouchingGrass(x, y))) {
			potentialOptions.add("Casă: $" + View.round(HousePiece.costToConstruct, 2));
		}
		
		/* Adaugă Apartament - necesită atingerea drumului */
		if (this.getBalance() >= ApartmentPiece.costToConstruct && isPieceTouchingRoad(x, y)) {
			potentialOptions.add("Apartament: $" + View.round(ApartmentPiece.costToConstruct, 2));
		}
		
		/* Adaugă Fabrică - necesită atingerea atât a drumului cât și a apei */
		if (this.getBalance() >= FactoryPiece.costToConstruct && isPieceTouchingWater(x, y)
				&& isPieceTouchingRoad(x, y)) {
			potentialOptions.add("Fabrică: $" + View.round(FactoryPiece.costToConstruct, 2));
		}
		
		/* Adaugă Parc - necesită atingerea apei */
		if (this.getBalance() >= ParkPiece.costToConstruct && isPieceTouchingWater(x, y)) {
			potentialOptions.add("Parc: $" + View.round(ParkPiece.costToConstruct, 2));
		}
		
		/* Adaugă Retail - necesită atingerea drumului */
		if (this.getBalance() >= RetailPiece.costToConstruct && isPieceTouchingRoad(x, y)) {
			potentialOptions.add("Retail: $" + View.round(RetailPiece.costToConstruct, 2));
		}
		
		/* Sortează opțiunile după cost crescător pentru o experiență mai bună a utilizatorului */
		if (potentialOptions.size() != 0) {
			potentialOptions.sort((a, b) -> {
				double ca = extractCost(a);
				double cb = extractCost(b);
				return Double.compare(ca, cb);
			});
			return potentialOptions.toArray(new String[potentialOptions.size()]);
		} else {
			return null;
		}
	}

	private synchronized void validatePlacement(BoardPieceInterface piece)
			throws InsufficientFundsException, PositionOccupiedException {
		if (piece == null) {
			throw new IllegalArgumentException("Piece cannot be null");
		}

		int x = piece.getXPosition();
		int y = piece.getYPosition();

		/* Verificare limite */
		if (x < 0 || x >= BOARD_X || y < 0 || y >= BOARD_Y) {
			throw new PositionOccupiedException("Plasarea este în afara limitelor.");
		}

		/* Verificare ocupare */
		if (!(board[y][x] instanceof GrassPiece)) {
			throw new PositionOccupiedException("Acel loc este deja ocupat.");
		}

		/* Verificare fonduri */
		double cost = piece.getCostToBuild();
		if (this.balance < cost) {
			throw new InsufficientFundsException(
					"Necesită $" + View.round(cost, 2) + " dar ai $" + View.round(this.balance, 2));
		}

		/* Regulile de plasare reflectă getAvailableChoices */
		if (piece instanceof HousePiece) {
			if (!(isPieceTouchingRoad(x, y) || isPieceTouchingGrass(x, y))) {
				throw new PositionOccupiedException("Casa trebuie să atingă drumul sau iarbă lângă drum.");
			}
		} else if (piece instanceof ApartmentPiece) {
			if (!isPieceTouchingRoad(x, y)) {
				throw new PositionOccupiedException("Apartamentul trebuie să atingă un drum.");
			}
		} else if (piece instanceof FactoryPiece) {
			if (!(isPieceTouchingRoad(x, y) && isPieceTouchingWater(x, y))) {
				throw new PositionOccupiedException("Fabrica trebuie să atingă atât drumul, cât și apa.");
			}
		} else if (piece instanceof ParkPiece) {
			if (!isPieceTouchingWater(x, y)) {
				throw new PositionOccupiedException("Parcul trebuie să atingă apa.");
			}
		} else if (piece instanceof RetailPiece) {
			if (!isPieceTouchingRoad(x, y)) {
				throw new PositionOccupiedException("Retailul trebuie să atingă un drum.");
			}
		}
	}

	public synchronized void construct(BoardPieceInterface piece)
			throws InsufficientFundsException, PositionOccupiedException {
		validatePlacement(piece);
		captureStateSnapshot();
		/* Scade soldul */
		balance -= piece.getCostToBuild();
		/* Actualizează Tabla */
		board[piece.getYPosition()][piece.getXPosition()] = piece;
		/* Adaugă Notificare Jurnal */
		EventLog.getEventLog().addEntry(
				piece.getPieceName() + " construit la (" + piece.getXPosition() + ", " + piece.getYPosition() + ").");
		/* Face piesa mai scumpă de construit */
		piece.updateCost();
		/* Acordă puncte pentru construcție */
		addScore(10);
		/* Verifică realizările */
		checkMilestones();
		/* Notify observers */
		notifyObservers(ModelObserver.EventTypes.BALANCE_CHANGED);
		notifyObservers(ModelObserver.EventTypes.DAILYINCOME_CHANGED);
		notifyObservers(ModelObserver.EventTypes.POPULATION_CHANGED);
		notifyObservers(ModelObserver.EventTypes.BOARD_CHANGED);
		notifyObservers(ModelObserver.EventTypes.UNEMPLOYEMENT_CHANGED);
		recomputeHappiness();
	}

	public synchronized void demolish(int x, int y) {
		captureStateSnapshot();
		board[y][x] = new GrassPiece(x, y);
		/* Încasează utilizatorul */
		this.addToBalance(-COST_TO_DEMOLISH);
		/* Face mai scumpă demolarea următorului loc */
		COST_TO_DEMOLISH *= 2;
		/* Scrie în jurnal */
		EventLog.getEventLog().addEntry("Clădire demolată la (" + x + ", " + y + ")");
		/* Notifică Observatorii */
		notifyObservers(ModelObserver.EventTypes.BOARD_CHANGED);
		notifyObservers(ModelObserver.EventTypes.POPULATION_CHANGED);
		notifyObservers(ModelObserver.EventTypes.DAILYINCOME_CHANGED);
		notifyObservers(ModelObserver.EventTypes.UNEMPLOYEMENT_CHANGED);
		recomputeHappiness();
	}

	public synchronized void nextDay() {
		EventLog.getEventLog().addEntry("Ziua " + day++ + " s-a încheiat.");
		this.balance += this.getDailyIncome();
		notifyObservers(ModelObserver.EventTypes.BALANCE_CHANGED);
		notifyObservers(ModelObserver.EventTypes.DAY_CHANGED);
		notifyObservers(ModelObserver.EventTypes.BOARD_CHANGED);
		
		/* Adaugă bonus de fericire */
		double happinessBonus = this.getHappiness() * 20;
		this.addToBalance(happinessBonus);
		
		/* Adaugă bonusuri de eficiență */
		double employmentMultiplier = this.getEmploymentMultiplier();
		if (employmentMultiplier >= 0.95) {
			double employmentBonus = this.getPopulation() * 0.5;
			this.addToBalance(employmentBonus);
			EventLog.getEventLog().addEntry("💼 Bonus de angajare: +$" + View.round(employmentBonus, 2));
		}
		
		/* Adaugă bonus de diversitate - recompensă pentru a avea diferite tipuri de clădiri */
		int diversity = countBuildingTypes();
		if (diversity >= 5) {
			double diversityBonus = diversity * 50.0;
			this.addToBalance(diversityBonus);
			EventLog.getEventLog().addEntry("🏘️ Bonus de diversitate: +$" + View.round(diversityBonus, 2));
		}
		
		/* Evenimente aleatorii ocazionale în oraș pentru a menține jocul animat */
		if (randomEventsEnabled) {
			applyRandomEvent();
		}
		
		recomputeHappiness();
		checkMilestones();
	}

	private synchronized boolean isPieceTouchingRoad(int x, int y) {
		/* Testează chiar mai sus, dacă este posibil */
		if (y != 0 && board[y - 1][x] instanceof RoadPiece) {
			return true;
		}
		/* Testează chiar mai jos, dacă este posibil */
		if (y != BOARD_Y - 1 && board[y + 1][x] instanceof RoadPiece) {
			return true;
		}
		/* Testează locul din dreapta, dacă este posibil */
		if (x != BOARD_X - 1 && board[y][x + 1] instanceof RoadPiece) {
			return true;
		}
		/* Testează locul din stânga, dacă este posibil */
		if (x != 0 && board[y][x - 1] instanceof RoadPiece) {
			return true;
		}
		/* Dacă niciunul dintre cazuri nu funcționează, atunci nu */
		return false;
	}

	private synchronized boolean isPieceTouchingWater(int x, int y) {
		/* Testează chiar mai sus, dacă este posibil */
		if (y != 0 && board[y - 1][x] instanceof WaterPiece) {
			return true;
		}
		/* Testează chiar mai jos, dacă este posibil */
		if (y != BOARD_Y - 1 && board[y + 1][x] instanceof WaterPiece) {
			return true;
		}
		/* Testează locul din dreapta, dacă este posibil */
		if (x != BOARD_X - 1 && board[y][x + 1] instanceof WaterPiece) {
			return true;
		}
		/* Testează locul din stânga, dacă este posibil */
		if (x != 0 && board[y][x - 1] instanceof WaterPiece) {
			return true;
		}
		/* Dacă niciunul dintre cazuri nu funcționează, atunci nu */
		return false;
	}

	private synchronized boolean isPieceTouchingGrass(int x, int y) {
		/* Testează chiar mai sus, dacă este posibil */
		if (y != 0 && board[y - 1][x] instanceof GrassPiece) {
			return true;
		}
		/* Testează chiar mai jos, dacă este posibil */
		if (y != BOARD_Y - 1 && board[y + 1][x] instanceof GrassPiece) {
			return true;
		}
		/* Testează locul din dreapta, dacă este posibil */
		if (x != BOARD_X - 1 && board[y][x + 1] instanceof GrassPiece) {
			return true;
		}
		/* Testează locul din stânga, dacă este posibil */
		if (x != 0 && board[y][x - 1] instanceof GrassPiece) {
			return true;
		}
		/* Dacă niciunul dintre cazuri nu funcționează, atunci nu */
		return false;
	}

	private synchronized boolean isPieceTouchingFactory(int x, int y) {
		/* Testează chiar mai sus, dacă este posibil */
		if (y != 0 && board[y - 1][x] instanceof FactoryPiece) {
			return true;
		}
		/* Testează chiar mai jos, dacă este posibil */
		if (y != BOARD_Y - 1 && board[y + 1][x] instanceof FactoryPiece) {
			return true;
		}
		/* Testează locul din dreapta, dacă este posibil */
		if (x != BOARD_X - 1 && board[y][x + 1] instanceof FactoryPiece) {
			return true;
		}
		/* Testează locul din stânga, dacă este posibil */
		if (x != 0 && board[y][x - 1] instanceof FactoryPiece) {
			return true;
		}
		/* Dacă niciunul dintre cazuri nu funcționează, atunci nu */
		return false;
	}

	private synchronized boolean isPieceTouchingPark(int x, int y) {
		/* Testează chiar mai sus, dacă este posibil */
		if (y != 0 && board[y - 1][x] instanceof ParkPiece) {
			return true;
		}
		/* Testează chiar mai jos, dacă este posibil */
		if (y != BOARD_Y - 1 && board[y + 1][x] instanceof ParkPiece) {
			return true;
		}
		/* Testează locul din dreapta, dacă este posibil */
		if (x != BOARD_X - 1 && board[y][x + 1] instanceof ParkPiece) {
			return true;
		}
		/* Testează locul din stânga, dacă este posibil */
		if (x != 0 && board[y][x - 1] instanceof ParkPiece) {
			return true;
		}
		/* Dacă niciunul dintre cazuri nu funcționează, atunci nu */
		return false;
	}

	/* Observable Methods */
	public void addObserver(main.ModelObserver o) {
		observers.add(o);
	}

	public void removeObserver(main.ModelObserver o) {
		observers.remove(o);
	}

	public void notifyObservers(main.ModelObserver.EventTypes eventType) {
		/* Ordonează actualizările observatorilor în Swing EDT pentru a evita problemele de threading */
		Runnable r = () -> {
			for (main.ModelObserver o : observers) {
				switch (eventType) {
				case BALANCE_CHANGED:
					o.BalanceChanged();
					break;
				case DAILYINCOME_CHANGED:
					o.DailyIncomeChanged();
					break;
				case DAY_CHANGED:
					o.DayChanged();
					break;
				case POPULATION_CHANGED:
					o.PopulationChanged();
					break;
				case BOARD_CHANGED:
					o.BoardChanged();
					break;
				case HAPPINESS_CHANGED:
					o.HappinessChanged();
					break;
				case UNEMPLOYEMENT_CHANGED:
					o.UnemployementChanged();
					break;
				}
			}
		};
		if (javax.swing.SwingUtilities.isEventDispatchThread()) {
			r.run();
		} else {
			javax.swing.SwingUtilities.invokeLater(r);
		}
	}

	public synchronized boolean isRandomEventsEnabled() {
		return randomEventsEnabled;
	}

	public synchronized void setRandomEventsEnabled(boolean enabled) {
		this.randomEventsEnabled = enabled;
	}

	public synchronized double getHappiness() {
		return happiness;
	}

	public synchronized void recomputeHappiness() {
		/*
		 * Every person should add 0.01 to total happiness But, if the dwelling is
		 * touching a factory, it should decrease total happiness by 10 If the dwelling
		 * is touching water or a park, then it should increase happiness by 0.05/person
		 */
		this.happiness = 0.0;
		for (int y = 0; y < this.board.length; y++) {
			for (int x = 0; x < this.board[0].length; x++) {
				if (this.isPieceTouchingFactory(x, y) && board[y][x].getNumResidents() != 0) {
					this.happiness -= 10;
				} else if (this.isPieceTouchingPark(x, y) || this.isPieceTouchingWater(x, y)) {
					this.happiness += board[y][x].getNumResidents() * 0.05;
				} else {
					this.happiness += board[y][x].getNumResidents() * 0.01;
				}
			}
		}
		/* Notify observers */
		notifyObservers(ModelObserver.EventTypes.HAPPINESS_CHANGED);
	}

	/*
	 * Returns the current unemployment rate, formatted as a string Unemployment =
	 * (TotalWorkers - TotalJobs)/(TotalWorkers) * 100
	 */
	public synchronized String getUnemploymentRate() {
		int totalJobs = 0;
		for (int y = 0; y < this.board.length; y++) {
			for (int x = 0; x < this.board[0].length; x++) {
				totalJobs += board[y][x].getNumEmployeePositions();
			}
		}

		if (totalJobs >= this.getPopulation()) {
			return "0 %";
		} else {
			return (View.round(((double) (this.getPopulation() - totalJobs) / (double) (this.getPopulation())) * 100, 2)
					+ "%");
		}

	}

	/*
	 * Used for computing daily income Returns the amount of the population that is
	 * employed on the range (0,1) 0 indicates no employment at all 1 indicates that
	 * every citizen is employed
	 */
	private synchronized double getEmploymentMultiplier() {
		int totalJobs = 0;
		for (int y = 0; y < this.board.length; y++) {
			for (int x = 0; x < this.board[0].length; x++) {
				totalJobs += board[y][x].getNumEmployeePositions();
			}
		}
		if (totalJobs >= this.getPopulation()) {
			return 1.0000;
		} else {
			return 1.0 - (double) (this.getPopulation() - totalJobs) / (double) (this.getPopulation());
		}

	}

	private synchronized void checkMilestones() {
		int population = getPopulation();
		int buildingCount = countBuildings();
		double dailyIncome = getDailyIncome();
		
		/* Realizări populație */
		if (population >= 100 && !hasMilestone("Fondator de Oraș: 100 populație")) {
			addMilestone("Fondator de Oraș: 100 populație");
			addToBalance(500);
		}
		if (population >= 500 && !hasMilestone("Oraș în Creștere: 500 populație")) {
			addMilestone("Oraș în Creștere: 500 populație");
			addToBalance(1500);
		}
		if (population >= 1000 && !hasMilestone("Metropolă Prosperă: 1000 populație")) {
			addMilestone("Metropolă Prosperă: 1000 populație");
			addToBalance(3000);
		}
		
		/* Realizări venit */
		if (dailyIncome >= 100 && !hasMilestone("Profitabil: venit zilnic $100+")) {
			addMilestone("Profitabil: venit zilnic $100+");
		}
		if (dailyIncome >= 500 && !hasMilestone("Foarte Profitabil: venit zilnic $500+")) {
			addMilestone("Foarte Profitabil: venit zilnic $500+");
		}
		
		/* Realizări diversitate clădiri */
		if (buildingCount >= 10 && !hasMilestone("Dezvoltator: 10 clădiri diferite")) {
			addMilestone("Dezvoltator: 10 clădiri diferite");
		}
		if (buildingCount >= 25 && !hasMilestone("Maestru Constructor: 25 clădiri")) {
			addMilestone("Maestru Constructor: 25 clădiri");
		}
		
		/* Realizări zile */
		if (day >= 10 && !hasMilestone("Un Deceniu: 10 zile supraviețuite")) {
			addMilestone("Un Deceniu: 10 zile supraviețuite");
		}
		if (day >= 30 && !hasMilestone("O Lună: 30 zile supraviețuite")) {
			addMilestone("O Lună: 30 zile supraviețuite");
		}
	}

	private synchronized int countBuildings() {
		int count = 0;
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[0].length; x++) {
				if (!(board[y][x] instanceof GrassPiece) && !(board[y][x] instanceof WaterPiece) && !(board[y][x] instanceof RoadPiece)) {
					count++;
				}
			}
		}
		return count;
	}

	private synchronized int countBuildingTypes() {
		boolean hasHouse = false;
		boolean hasApartment = false;
		boolean hasFactory = false;
		boolean hasPark = false;
		boolean hasRetail = false;
		boolean hasRoad = false;
		
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[0].length; x++) {
				BoardPieceInterface piece = board[y][x];
				if (piece instanceof HousePiece) hasHouse = true;
				else if (piece instanceof ApartmentPiece) hasApartment = true;
				else if (piece instanceof FactoryPiece) hasFactory = true;
				else if (piece instanceof ParkPiece) hasPark = true;
				else if (piece instanceof RetailPiece) hasRetail = true;
				else if (piece instanceof RoadPiece) hasRoad = true;
			}
		}
		
		int count = 0;
		if (hasHouse) count++;
		if (hasApartment) count++;
		if (hasFactory) count++;
		if (hasPark) count++;
		if (hasRetail) count++;
		if (hasRoad) count++;
		
		return count;
	}

	/* Ajutor pentru a analiza costul din șirurile de opțiuni precum "Road: $1,000" */
	private static double extractCost(String option) {
		if (option == null || option.isEmpty()) {
			return Double.MAX_VALUE;
		}
		int idx = option.lastIndexOf('$');
		if (idx >= 0 && idx + 1 < option.length()) {
			String num = option.substring(idx + 1).replaceAll("[^\\d.]", "").trim();
			if (!num.isEmpty()) {
				try {
					return Double.parseDouble(num);
				} catch (NumberFormatException e) {
					return Double.MAX_VALUE;
				}
			}
		}
		return Double.MAX_VALUE;
	}

	private synchronized void applyRandomEvent() {
		// ~8% șansă pe zi
		if (ThreadLocalRandom.current().nextDouble() < 0.08) {
			int eventType = ThreadLocalRandom.current().nextInt(4);
			switch (eventType) {
				case 0: { // Rambursare taxe
					double bonus = 100 + ThreadLocalRandom.current().nextDouble(50, 250);
					this.addToBalance(bonus);
					EventLog.getEventLog().addEntry("🏛️ Rambursare taxe: +$" + View.round(bonus, 2));
					break;
				}
				case 1: { // Costuri de întreținere
					double cost = 50 + ThreadLocalRandom.current().nextDouble(50, 200);
					this.addToBalance(-cost);
					EventLog.getEventLog().addEntry("🛠️ Cheltuieli de întreținere: -$" + View.round(cost, 2));
					break;
				}
				case 2: { // Festival comunitar
					this.happiness += 2.0; // small bump
					notifyObservers(ModelObserver.EventTypes.HAPPINESS_CHANGED);
					EventLog.getEventLog().addEntry("🎉 Festivalul comunitar a crescut fericirea!");
					break;
				}
				case 3: { // Furtună
					this.happiness = Math.max(0.0, this.happiness - 1.0);
					notifyObservers(ModelObserver.EventTypes.HAPPINESS_CHANGED);
					EventLog.getEventLog().addEntry("🌧️ Furtuna a scăzut fericirea.");
					break;
				}
			}
		}
	}
}
