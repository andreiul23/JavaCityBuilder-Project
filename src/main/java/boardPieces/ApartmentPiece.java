package boardPieces;

import java.awt.image.BufferedImage;

public class ApartmentPiece extends AbstractBoardPiece {
	private static final long serialVersionUID = 1L;
	private double income;
	private int residents;
	private static final BufferedImage ICON = loadStaticImage("/boardPieces/ApartmentImage.png", "Apt");
	public static double costToConstruct = main.GameConfig.COST_APARTMENT;

	public ApartmentPiece(int xPos, int yPos) {
		super(xPos, yPos, "Apartment Piece");
		this.income = Math.random() * 200.0 + 50.0; // $50–$250 pe zi
		this.residents = 20 + (int) (Math.random() * 11); // 20–30 rezidenți
	}

	@Override
	public double getDailyIncome() {
		return this.income;
	}

	@Override
	public int getNumResidents() {
		return this.residents;
	}

	@Override
	public double getCostToBuild() {
		/* Return the current escalating construction cost */
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
		return 0;
	}
}
