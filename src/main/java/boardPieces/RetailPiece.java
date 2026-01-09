package boardPieces;

import java.awt.image.BufferedImage;

public class RetailPiece extends AbstractBoardPiece {
	private static final long serialVersionUID = 1L;
	private double income;
	private int employeePositions;
	private static final BufferedImage ICON = loadStaticImage("/boardPieces/RetailImage.png", "Retail");
	public static double costToConstruct = main.GameConfig.COST_RETAIL;

	public RetailPiece(int xPos, int yPos) {
		super(xPos, yPos, "Retail Piece");
		this.income = Math.random() * 300.0 + 200.0; // $200–$500 pe zi
		this.employeePositions = 30 + (int) (Math.random() * 21); // 30–50 locuri de muncă
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
		return employeePositions;
	}
}
