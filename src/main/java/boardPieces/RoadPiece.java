package boardPieces;

import java.awt.image.BufferedImage;

public class RoadPiece extends AbstractBoardPiece {
	private static final long serialVersionUID = 1L;

	private static final BufferedImage ICON = loadStaticImage("/boardPieces/RoadImage.png", "Road");
	public static double costToConstruct = main.GameConfig.COST_ROAD;

	public RoadPiece(int xPos, int yPos) {
		super(xPos, yPos, "Road Piece");
	}

	@Override
	public double getDailyIncome() {
		/* Piesele goale nu ar trebui să genereze venituri */
		return 0.0;
	}

	@Override
	public int getNumResidents() {
		/* No one can live on empty pieces */
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
		return 0;
	}
}
