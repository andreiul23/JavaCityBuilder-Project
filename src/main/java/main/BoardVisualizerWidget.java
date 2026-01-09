package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import boardPieces.*;

/*
 * BoardVisualizerWidget este responsabil pentru încapsularea unei instanțe a modelului și vizualizarea acesteia în interfața grafică
 */
public class BoardVisualizerWidget extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	/*
	 * Variabilă care modelează grosimea liniilor dintre fiecare element de pe tablă
	 */
	public static final int BOARD_THICKNESS = 1;
	private static final Color COL_HOUSE = new Color(34, 197, 94, 30);
	private static final Color COL_APT = new Color(34, 197, 94, 40);
	private static final Color COL_FACTORY = new Color(239, 68, 68, 30);
	private static final Color COL_RETAIL = new Color(59, 130, 246, 30);
	private static final Color COL_PARK = new Color(34, 197, 94, 50);
	private static final Color COL_ROAD = new Color(107, 114, 128, 40);
	private static final Color COL_WATER = new Color(59, 130, 246, 20);
	private static final Color COL_TRANS = new Color(255, 255, 255, 0);

	private transient Model model;
	/*
	 * Instanță Graphics2d necesară pentru a desena imaginile și grila în sine
	 */
	transient Graphics2D g2d;
	private int hoverX = -1;
	private int hoverY = -1;

	@SuppressWarnings("this-escape")
	public BoardVisualizerWidget(Model model) {
		/* Încapsulează modelul */
		this.model = model;
		
		/* Setează dimensiunea preferată și dimensiunea minimă */
		setPreferredSize(new Dimension(1050, 950));
		setMinimumSize(new Dimension(400, 400));
		
		/*
		 * Ne adăugăm ca ascultător de mouse, astfel încât să putem detecta când utilizatorul dă click
		 * pe o anumită parte a grilei
		 */
		addMouseListener(this);
		addMouseMotionListener(this);
		repaint();
	}

	/* Metodă care repictează întregul widget */
	public void paintComponent(Graphics g) {
		/* Apelează metoda super */
		super.paintComponent(g);
		/* Obține lățimea și înălțimea widget-ului nostru */
		int width = getWidth();
		int height = getHeight();
		
		// Evită împărțirea la zero
		if (width == 0 || height == 0) {
			return;
		}
		
		/*
		 * Modelează dimensiunea fiecărei celule folosind dimensiunile widget-ului nostru și
		 * câmpurile publice din Model care dictează dimensiunile tablei
		 */
		double eachCellXWidth = (width / (double) Model.BOARD_X);
		double eachCellYWidth = (height / (double) Model.BOARD_Y);
		/* Configurează instanța Graphics2D */
		g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(new Color(100, 100, 100));
		g2d.setStroke(new BasicStroke(BOARD_THICKNESS));

		/* Obține tabla o singură dată pentru a evita copieri defensive multiple */
		boardPieces.BoardPieceInterface[][] board = model.getBoard();

		/*
		 * Iterează peste matricea 2D de obiecte BoardPieceInterface pe care modelul
		 * o încapsulează
		 */
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[0].length; x++) {
			/* Desenează culoarea de fundal bazată pe tipul clădirii */
			Color bgColor = getColorForBuilding(board[y][x]);
			g2d.setColor(bgColor);
			g2d.fillRect((int) (x * eachCellXWidth), (int) (y * eachCellYWidth), (int) (eachCellXWidth),
					(int) (eachCellYWidth));
			
			/* Desenează întotdeauna dreptunghi în jurul marginii fiecărei celule */
			g2d.setColor(new Color(90, 90, 90, 180));
			g2d.drawRect((int) (x * eachCellXWidth), (int) (y * eachCellYWidth), (int) (eachCellXWidth),
					(int) (eachCellYWidth));
			/* Vizualizează piesa în interiorul celulei */
			g2d.drawImage(board[y][x].getPieceImage(), (int) (x * eachCellXWidth),
					(int) (y * eachCellYWidth), (int) (eachCellXWidth), (int) (eachCellYWidth), null);
		}
		}
		
		/* Desenează liniile grilei */
		g2d.setColor(new Color(70, 70, 70, 100));
		g2d.setStroke(new BasicStroke(0.5f));
		for (int y = 0; y <= Model.BOARD_Y; y++) {
			g2d.drawLine(0, (int) (y * eachCellYWidth), width, (int) (y * eachCellYWidth));
		}
		for (int x = 0; x <= Model.BOARD_X; x++) {
			g2d.drawLine((int) (x * eachCellXWidth), 0, (int) (x * eachCellXWidth), height);
		}

		g2d.dispose();

	}

	/*
	 * Metodă ajutătoare pentru a obține culoarea pentru tipul de clădire
	 */
	private Color getColorForBuilding(BoardPieceInterface piece) {
		if (piece instanceof HousePiece) {
			return COL_HOUSE; // Verde pentru rezidențial
		} else if (piece instanceof ApartmentPiece) {
			return COL_APT; // Verde mai închis
		} else if (piece instanceof FactoryPiece) {
			return COL_FACTORY; // Roșu pentru industrial
		} else if (piece instanceof RetailPiece) {
			return COL_RETAIL; // Albastru pentru comercial
		} else if (piece instanceof ParkPiece) {
			return COL_PARK; // Verde aprins pentru parcuri
		} else if (piece instanceof RoadPiece) {
			return COL_ROAD; // Gri pentru drumuri
		} else if (piece instanceof WaterPiece) {
			return COL_WATER; // Albastru deschis pentru apă
		} else {
			return COL_TRANS; // Transparent pentru iarbă
		}
	}

	/*
	 * Metodă ajutătoare care convertește o pereche ordonată într-o dimensiune
	 * Dimensiunea reprezintă perechea ordonată a grilei modelului în sine
	 * Apelează dimension.getWidth() și dimension.getHeight() pentru a obține rezultatul
	 */
	public Dimension convertPointToCoordinate(int x, int y) {
		/* Obține dimensiunile acestui widget */
		int width = getWidth();
		int height = getHeight();
		
		// Evită împărțirea la zero
		if (width == 0 || height == 0) {
			return new Dimension(0, 0);
		}
		
		/* Calculează lățimea și înălțimea celulei pe baza dimensiunilor */
		double eachCellXWidth = (width / (double) Model.BOARD_X);
		double eachCellYWidth = (height / (double) Model.BOARD_Y);
		/* Creează și returnează o nouă dimensiune */
		return new Dimension((int) (x / eachCellXWidth), (int) (y / eachCellYWidth));
	}

	@Override
	/*
	 * Când utilizatorul dă click pe grilă, trebuie să o transformăm într-o poziție pe tablă.
	 * Apoi trebuie să solicităm utilizatorului să construiască pe acea piesă, dacă este posibil
	 */
	public void mouseClicked(MouseEvent e) {
		/* Șir gol care va reprezenta alegerea construcției */
		String input;
		/*
		 * Creează o dimensiune astfel încât să putem converti coordonatele unui click în
		 * coordonate pe grilă
		 */
		Dimension temp = convertPointToCoordinate(e.getX(), e.getY());
		/* Creează matrice care modelează toate alegerile posibile pe care le are utilizatorul */
		String[] opters = this.model.getAvailableChoices((int) (temp.getWidth()), (int) (temp.getHeight()));
		/* Dacă este null, atunci utilizatorul nu poate construi nimic aici */
		if (opters == null) {
			JOptionPane.showMessageDialog(null, "Nu poți construi nimic aici", "Constructor",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		} else {
			input = (String) JOptionPane.showInputDialog(null, "Selectează Opțiune", "Loc Construcție",
					JOptionPane.QUESTION_MESSAGE, null, opters, opters[0]);
		}
		/* Asigură-te că nu au anulat */
		if (input == null) {
			return;
		}
		/* Analizează input-ul și apelează metoda modelului corespunzătoare */
		if (input.contains("Casă")) {
			tryConstruct(new HousePiece((int) (temp.getWidth()), ((int) (temp.getHeight()))));
		} else if (input.contains("Drum")) {
			tryConstruct(new RoadPiece((int) (temp.getWidth()), ((int) (temp.getHeight()))));
		} else if (input.contains("Apartament")) {
			tryConstruct(new ApartmentPiece((int) (temp.getWidth()), (int) (temp.getHeight())));
		} else if (input.contains("Demolare")) {
			this.model.demolish((int) (temp.getWidth()), (int) (temp.getHeight()));
		} else if (input.contains("Fabrică")) {
			tryConstruct(new FactoryPiece((int) (temp.getWidth()), (int) (temp.getHeight())));
		} else if (input.contains("Parc")) {
			tryConstruct(new ParkPiece((int) (temp.getWidth()), (int) (temp.getHeight())));
		} else if (input.contains("Retail")) {
			tryConstruct(new RetailPiece((int) (temp.getWidth()), (int) (temp.getHeight())));
		}

	}

	private void tryConstruct(BoardPieceInterface piece) {
		try {
			this.model.construct(piece);
		} catch (InsufficientFundsException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Fonduri Insuficiente", JOptionPane.ERROR_MESSAGE);
		} catch (PositionOccupiedException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Plasare Invalidă", JOptionPane.ERROR_MESSAGE);
		}
	}

	/*
	 * Metode pe care suntem obligați să le suprascriem ca parte a interfeței MouseListener,
	 * dar de care nu avem nevoie
	 */
	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Dimension temp = convertPointToCoordinate(e.getX(), e.getY());
		int tx = (int) temp.getWidth();
		int ty = (int) temp.getHeight();
		if (tx < 0 || ty < 0 || tx >= Model.BOARD_X || ty >= Model.BOARD_Y) {
			hoverX = -1;
			hoverY = -1;
		} else {
			hoverX = tx;
			hoverY = ty;
		}
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// Neutilizat
	}
}
