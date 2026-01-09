package boardPieces;

import java.awt.image.BufferedImage;

public class GrassPiece extends AbstractBoardPiece {
	private static final long serialVersionUID = 1L;
	private static final BufferedImage ICON = loadStaticImage("/boardPieces/GrassImage.png", "Grass");

	public GrassPiece(int xPos, int yPos) {
		super(xPos, yPos, "Grass Piece");
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
		return 0;
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
