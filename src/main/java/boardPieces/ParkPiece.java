package boardPieces;

import java.awt.image.BufferedImage;

public class ParkPiece extends AbstractBoardPiece {
	private static final long serialVersionUID = 1L;
	private double income;
	private static final BufferedImage ICON = loadStaticImage("/boardPieces/ParkImage.png", "Park");
	public static double costToConstruct = main.GameConfig.COST_PARK;

	public ParkPiece(int xPos, int yPos) {
		super(xPos, yPos, "Park Piece");
		this.income = 0.0; // Parcurile se concentrează pe fericire, nu pe venit
	}

	@Override
	public double getDailyIncome() {
		return income;
	}

	@Override
	public int getNumResidents() {
		/* No one can live in a park */
		return 0;
	}

	@Override
	public double getCostToBuild() {
		return costToConstruct;
	}

	@Override
	public BufferedImage getPieceImage() {
		return ICON;
	}

	@Override
	public void updateCost() {
		/* Make it more expensive to construct the next one (10% increment) */
		costToConstruct *= 1.1;
	}

	@Override
	public int getNumEmployeePositions() {
		return 25;
	}
}
