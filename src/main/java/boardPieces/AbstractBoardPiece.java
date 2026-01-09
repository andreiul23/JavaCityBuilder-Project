package boardPieces;

import java.awt.image.BufferedImage;

/* O implementare generală a interfeței BoardPieceInterface
 * Responsabilă pentru încapsularea numelui piesei și a coordonatelor sale X/Y în model
 * Restul modelelor rămân abstracte
 */
public abstract class AbstractBoardPiece implements boardPieces.BoardPieceInterface {
	private static final long serialVersionUID = 1L;
	/* Variabile de instanță pentru reținerea Poziției X, Poziției Y și Numelui */
	private int xPos;
	private int yPos;
	private String pieceName;

	/* Constructor */
	public AbstractBoardPiece(int xPos, int yPos, String pieceName) {
		/* Presupunem că inputul valid este transmis constructorului */
		this.xPos = xPos;
		this.yPos = yPos;
		this.pieceName = pieceName;
	}

	@Override
	public int getXPosition() {
		return this.xPos;
	}

	@Override
	public int getYPosition() {
		return this.yPos;
	}

	@Override
	public String getPieceName() {
		return this.pieceName;
	}

	@Override
	public int compareTo(BoardPieceInterface other) {
		if (other == null) {
			return 1;
		}
		int byCost = Double.compare(this.getCostToBuild(), other.getCostToBuild());
		if (byCost != 0) {
			return byCost;
		}
		int byIncome = Double.compare(this.getDailyIncome(), other.getDailyIncome());
		if (byIncome != 0) {
			return byIncome;
		}
		return this.getPieceName().compareToIgnoreCase(other.getPieceName());
	}

	/* Metodă ajutătoare pentru încărcarea imaginilor cu revenire la substituent (bazată pe instanță) */
	protected BufferedImage loadImage(String resourcePath) {
		return loadStaticImage(resourcePath, pieceName);
	}

	/* Ajutor static pentru a evita avertismentele de tip this-escape din constructor */
	protected static BufferedImage loadStaticImage(String resourcePath, String label) {
		BufferedImage img = main.AssetManager.getImage(resourcePath);
		if (img != null) {
			return img;
		} else {
			return createPlaceholderImage(label);
		}
	}

	/* Metodă ajutătoare pentru crearea unei imagini substituent atunci când fișierul lipsește */
	protected static BufferedImage createPlaceholderImage(String label) {
		BufferedImage placeholder = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
		java.awt.Graphics2D g2d = placeholder.createGraphics();
		g2d.setColor(new java.awt.Color(100, 100, 100));
		g2d.fillRect(0, 0, 64, 64);
		g2d.setColor(java.awt.Color.WHITE);
		g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 9));
		String text = label.length() > 5 ? label.substring(0, 5) : label;
		java.awt.FontMetrics fm = g2d.getFontMetrics();
		int x = (64 - fm.stringWidth(text)) / 2;
		int y = (64 - fm.getHeight()) / 2 + fm.getAscent();
		g2d.drawString(text, x, y);
		g2d.dispose();
		return placeholder;
	}

	/* Metode Abstracte */
	public abstract double getDailyIncome();

	public abstract int getNumResidents();

	public abstract double getCostToBuild();

	public abstract BufferedImage getPieceImage();

	public abstract void updateCost();

	public abstract int getNumEmployeePositions();

}
