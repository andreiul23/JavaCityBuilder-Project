package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import boardPieces.BoardPieceInterface;
import boardPieces.HousePiece;
import boardPieces.RoadPiece;

/**
 * Scenarii demo scriptate executate din main() pentru a valida fluxurile de bază.
 */
public final class DemoScenarios {
	private DemoScenarios() {}

	public static void runAll() throws ScenarioExecutionException {
		Model demoModel = new Model();
		try {
			BoardPieceInterface starterHouse = new HousePiece(1, 10);
			demoModel.construct(starterHouse);
			BoardPieceInterface roadsideHouse = new HousePiece(1, 11);
			demoModel.construct(roadsideHouse);
		} catch (InsufficientFundsException | PositionOccupiedException ex) {
			throw new ScenarioExecutionException("Placement demo failed: " + ex.getMessage(), ex);
		}

		/* Demo sortare și generice */
		List<BoardPieceInterface> sorted = PieceUtils.sortByCost(new ArrayList<>(List.of(
				new HousePiece(2, 10), new RoadPiece(3, 10), new HousePiece(2, 11))));
		if (sorted.isEmpty()) {
			throw new ScenarioExecutionException("Sorting scenario produced no results");
		}

		/* Plasare invalidă intenționată pentru a arăta fluxul de capturare */
		try {
			demoModel.construct(new RoadPiece(0, 0));
			throw new ScenarioExecutionException("Expected placement rejection not triggered");
		} catch (PositionOccupiedException | InsufficientFundsException expected) {
			EventLog.getEventLog().addEntry("✅ Expected failure caught in demo: " + expected.getMessage());
		}

		/* Test rapid de salvare și încărcare folosind fișier temporar */
		try {
			File tmp = File.createTempFile("city-demo", ".dat");
			demoModel.saveToFile(tmp);
			demoModel.loadFromFile(tmp);
			tmp.deleteOnExit();
		} catch (IOException | SaveLoadException ex) {
			throw new ScenarioExecutionException("Save/load demo failed: " + ex.getMessage(), ex);
		}

		EventLog.getEventLog().addEntry("🎯 Demo scenarios completed successfully");
	}
}
