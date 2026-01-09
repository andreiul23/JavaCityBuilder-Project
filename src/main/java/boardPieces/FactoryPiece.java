package boardPieces;

import java.awt.image.BufferedImage;

public class FactoryPiece extends AbstractBoardPiece {
	private static final long serialVersionUID = 1L;

	private double dailyIncome;
	private int employeePositions;
	public static double costToConstruct = main.GameConfig.COST_FACTORY;
	private static final BufferedImage ICON = loadStaticImage("/boardPieces/FactoryImage.png", "Factory");

	public FactoryPiece(int xPos, int yPos) {
		super(xPos, yPos, "Factory Piece");
		this.dailyIncome = Math.random() * 1000.0 + 500.0; // $500–$1500 pe zi
		this.employeePositions = 50 + (int) (Math.random() * 51); // 50–100 locuri de muncă
	}

	@Override
	public double getDailyIncome() {
		return this.dailyIncome;
	}

	@Override
	public int getNumResidents() {
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
