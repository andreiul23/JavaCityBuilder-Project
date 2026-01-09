package boardPieces;

import java.awt.image.BufferedImage;

public class WaterPiece extends AbstractBoardPiece {
	private static final long serialVersionUID = 1L;
	private static final BufferedImage ICON = loadStaticImage("/boardPieces/WaterImage.png", "Water");

	public WaterPiece(int xPos, int yPos) {
		super(xPos, yPos, "Water Piece");
	}

	@Override
	public double getDailyIncome() {
		return 0;
	}

	@Override
	public int getNumResidents() {
		return 0;
	}

	@Override
	public double getCostToBuild() {
		return 10000000000.0;
	}

	@Override
	public BufferedImage getPieceImage() {
		return ICON;
	}

	@Override
	public void updateCost() {
		return;
	}

	@Override
	public int getNumEmployeePositions() {
		return 0;
	}

}
