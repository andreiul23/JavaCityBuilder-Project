package main;

import java.util.Comparator;
import java.util.List;
import boardPieces.BoardPieceInterface;

/**
 * Utilitare ajutătoare pentru lucrul cu piese de tablă folosind generice și comparatori.
 */
public final class PieceUtils {
	private PieceUtils() {}

	public static final Comparator<BoardPieceInterface> BY_INCOME_DESC =
			Comparator.comparingDouble(BoardPieceInterface::getDailyIncome).reversed()
					.thenComparing(BoardPieceInterface::getPieceName, String.CASE_INSENSITIVE_ORDER);

	public static final Comparator<BoardPieceInterface> BY_EMPLOYMENT_DESC =
			Comparator.comparingInt(BoardPieceInterface::getNumEmployeePositions).reversed()
					.thenComparing(BoardPieceInterface::getPieceName, String.CASE_INSENSITIVE_ORDER);

	public static <T extends BoardPieceInterface> List<T> sortByCost(List<T> pieces) {
		if (pieces == null) {
			return List.of();
		}
		pieces.sort(Comparator.naturalOrder());
		return pieces;
	}
}
