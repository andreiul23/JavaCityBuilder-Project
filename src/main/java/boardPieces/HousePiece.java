package boardPieces;

import java.awt.image.BufferedImage;

public class HousePiece extends AbstractBoardPiece {
	private static final long serialVersionUID = 1L;
	private double income;
	private int residents;
	private static final BufferedImage ICON = loadStaticImage("/boardPieces/HouseImage.png", "House");
	public static double costToConstruct = main.GameConfig.COST_HOUSE;

	public HousePiece(int xPos, int yPos) {
		super(xPos, yPos, "House Piece");
		this.income = Math.random() * 40 + 10;
		this.residents = (int) (1 + (4 * Math.random()));
	}

	@Override
	public double getDailyIncome() {
		return income;
	}

	@Override
	public int getNumResidents() {
		return residents;
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
		/* Face construirea următoarei piese mai scumpă (incrementare de 10%) */
		costToConstruct *= 1.1;
	}

	@Override
	public int getNumEmployeePositions() {
		return 0;
	}
}
