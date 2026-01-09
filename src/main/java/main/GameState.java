package main;

import java.io.Serializable;
import boardPieces.BoardPieceInterface;

/**
 * Instantaneu imutabil al stării jocului pentru suport sigur de anulare.
 * Captează toate câmpurile mutabile la un singur moment în timp.
 */
public class GameState implements Serializable {
	private static final long serialVersionUID = 1L;

	public final BoardPieceInterface[][] board;
	public final double balance;
	public final int day;
	public final double happiness;
	public final int score;
	public final double costToDemolish;
	public final boolean randomEventsEnabled;
	public final double houseCost;
	public final double apartmentCost;
	public final double factoryCost;
	public final double retailCost;
	public final double parkCost;
	public final double roadCost;

	public GameState(BoardPieceInterface[][] board, double balance, int day, 
			 double happiness, int score, double costToDemolish,
			 boolean randomEventsEnabled, double houseCost, double apartmentCost,
			 double factoryCost, double retailCost, double parkCost, double roadCost) {
		// Deep copy board
		this.board = new BoardPieceInterface[board.length][board[0].length];
		for (int y = 0; y < board.length; y++) {
			System.arraycopy(board[y], 0, this.board[y], 0, board[0].length);
		}
		this.balance = balance;
		this.day = day;
		this.happiness = happiness;
		this.score = score;
		this.costToDemolish = costToDemolish;
		this.randomEventsEnabled = randomEventsEnabled;
		this.houseCost = houseCost;
		this.apartmentCost = apartmentCost;
		this.factoryCost = factoryCost;
		this.retailCost = retailCost;
		this.parkCost = parkCost;
		this.roadCost = roadCost;
	}
}
