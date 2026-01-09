package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import boardPieces.ApartmentPiece;
import boardPieces.FactoryPiece;
import boardPieces.HousePiece;
import boardPieces.ParkPiece;
import boardPieces.RetailPiece;
import boardPieces.RoadPiece;

public class PricesDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	/* Paletă de culori - Îmbunătățită */
	private static final Color BG_DARK = new Color(20, 23, 28);
	private static final Color PANEL_BG = new Color(30, 35, 44);
	private static final Color ACCENT_BLUE = new Color(66, 150, 245);
	private static final Color TEXT_SECONDARY = new Color(175, 185, 195);
	private static final Color BORDER_COLOR = new Color(50, 70, 95);

	@SuppressWarnings("this-escape")
	public PricesDialog(JFrame parent) {
		super(parent, "Prețuri Clădiri & Informații", true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBackground(BG_DARK);

		/* Creează panoul principal */
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBackground(BG_DARK);

		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(16, 16, 16, 16);
		gc.weightx = 1.0;

		/* Titlu cu accent */
		JLabel titleLabel = new JLabel("PREȚURI CLĂDIRI & DETALII");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
		titleLabel.setForeground(ACCENT_BLUE);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		gc.gridy = 0;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.insets = new Insets(0, 16, 24, 16);
		mainPanel.add(titleLabel, gc);
		
		/* Separator titlu */
		JPanel titleSeparator = new JPanel();
		titleSeparator.setBackground(ACCENT_BLUE);
		titleSeparator.setPreferredSize(new Dimension(0, 2));
		gc.gridy = 1;
		gc.gridwidth = GridBagConstraints.REMAINDER;
		gc.insets = new Insets(0, 16, 12, 16);
		gc.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(titleSeparator, gc);

		/* Adaugă informații despre clădire într-o grilă cu 2 coloane */
		gc.gridwidth = 1;
		gc.gridy = 2;
		gc.gridx = 0;
		gc.insets = new Insets(10, 0, 10, 10);
		mainPanel.add(createBuildingPanel("Casă", "$" + View.round(HousePiece.costToConstruct, 2), "4-6 locuitori", new Color(76, 175, 80)), gc);

		gc.gridx = 1;
		gc.insets = new Insets(10, 10, 10, 0);
		mainPanel.add(createBuildingPanel("Apartament", "$" + View.round(ApartmentPiece.costToConstruct, 2), "20-30 locuitori", new Color(156, 39, 176)), gc);

		gc.gridy = 3;
		gc.gridx = 0;
		gc.insets = new Insets(10, 0, 10, 10);
		mainPanel.add(createBuildingPanel("Fabrică", "$" + View.round(FactoryPiece.costToConstruct, 2), "50-100 locuri de muncă", new Color(244, 67, 54)), gc);

		gc.gridx = 1;
		gc.insets = new Insets(10, 10, 10, 0);
		mainPanel.add(createBuildingPanel("Retail", "$" + View.round(RetailPiece.costToConstruct, 2), "30-50 locuri de muncă", new Color(255, 152, 0)), gc);

		gc.gridy = 4;
		gc.gridx = 0;
		gc.insets = new Insets(10, 0, 10, 10);
		mainPanel.add(createBuildingPanel("Parc", "$" + View.round(ParkPiece.costToConstruct, 2), "Crește fericirea", new Color(255, 193, 7)), gc);

		gc.gridx = 1;
		gc.insets = new Insets(10, 10, 10, 0);
		mainPanel.add(createBuildingPanel("Drum", "$" + View.round(RoadPiece.costToConstruct, 2), "Infrastructură", new Color(158, 158, 158)), gc);

		/* Cost demolare */
		gc.gridy = 5;
		gc.gridx = 0;
		gc.gridwidth = 2;
		gc.insets = new Insets(18, 0, 14, 0);
		mainPanel.add(createDemolishPanel(), gc);

		/* Adaugă secțiunea de informații */
		gc.gridy = 6;
		gc.insets = new Insets(0, 0, 0, 0);
		mainPanel.add(createInfoPanel(), gc);

		setContentPane(new JScrollPane(mainPanel));
		
		setSize(780, 750);
		setLocationRelativeTo(parent);
		setResizable(false);
	}

	private JPanel createBuildingPanel(String name, String cost, String info, Color accentColor) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBackground(PANEL_BG);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(BORDER_COLOR, 1),
			BorderFactory.createEmptyBorder(14, 14, 14, 14)
		));
		panel.setPreferredSize(new Dimension(360, 120));

		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(5, 0, 5, 0);
		gc.weightx = 1.0;

		/* Nume clădire cu subliniere de accent */
		JPanel namePanel = new JPanel();
		namePanel.setBackground(PANEL_BG);
		namePanel.setLayout(new GridBagLayout());
		GridBagConstraints ngc = new GridBagConstraints();
		
		JLabel nameLabel = new JLabel(name);
		nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
		nameLabel.setForeground(accentColor);
		if ("Casă".equals(name) || "Apartament".equals(name)) {
			nameLabel.setIcon(IconFactory.population());
		} else if ("Fabrică".equals(name) || "Retail".equals(name)) {
			nameLabel.setIcon(IconFactory.unemployment());
		} else if ("Parc".equals(name)) {
			nameLabel.setIcon(IconFactory.happiness());
		} else {
			nameLabel.setIcon(IconFactory.prices());
		}
		nameLabel.setIconTextGap(8);
		ngc.gridx = 0;
		ngc.gridy = 0;
		ngc.fill = GridBagConstraints.HORIZONTAL;
		ngc.weightx = 1.0;
		ngc.anchor = GridBagConstraints.WEST;
		namePanel.add(nameLabel, ngc);
		
		JPanel underline = new JPanel();
		underline.setBackground(accentColor);
		underline.setPreferredSize(new Dimension(0, 2));
		ngc.gridy = 1;
		ngc.insets = new Insets(6, 0, 0, 0);
		ngc.fill = GridBagConstraints.HORIZONTAL;
		namePanel.add(underline, ngc);
		
		gc.gridy = 0;
		panel.add(namePanel, gc);

		/* Cost */
		JLabel costLabel = new JLabel("Cost: " + cost);
		costLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
		costLabel.setForeground(new Color(76, 175, 80));
		costLabel.setIcon(IconFactory.money());
		costLabel.setIconTextGap(8);
		gc.gridy = 1;
		gc.insets = new Insets(8, 0, 4, 0);
		panel.add(costLabel, gc);

		/* Info */
		JLabel infoLabel = new JLabel(info);
		infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		infoLabel.setForeground(TEXT_SECONDARY);
		if ("Crește fericirea".equals(info)) {
			infoLabel.setIcon(IconFactory.happiness());
		} else if (info.contains("locuitori")) {
			infoLabel.setIcon(IconFactory.population());
		} else if (info.contains("locuri de muncă")) {
			infoLabel.setIcon(IconFactory.unemployment());
		} else {
			infoLabel.setIcon(IconFactory.prices());
		}
		infoLabel.setIconTextGap(8);
		gc.gridy = 2;
		gc.insets = new Insets(4, 0, 0, 0);
		panel.add(infoLabel, gc);

		return panel;
	}

	private JPanel createDemolishPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBackground(PANEL_BG);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(BORDER_COLOR, 1),
			BorderFactory.createEmptyBorder(14, 14, 14, 14)
		));

		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(6, 0, 6, 0);
		gc.weightx = 1.0;

		JLabel titleLabel = new JLabel("DEMOLARE");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
		titleLabel.setForeground(new Color(255, 193, 7));
		gc.gridy = 0;
		panel.add(titleLabel, gc);

		JLabel demolishLabel = new JLabel("Cost: $1,250 per clădire (crește odată cu utilizarea)");
		demolishLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		demolishLabel.setForeground(TEXT_SECONDARY);
		demolishLabel.setIcon(IconFactory.nextDay());
		demolishLabel.setIconTextGap(8);
		gc.gridy = 1;
		gc.insets = new Insets(6, 0, 0, 0);
		panel.add(demolishLabel, gc);

		return panel;
	}

	private JPanel createInfoPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBackground(PANEL_BG);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(BORDER_COLOR, 1),
			BorderFactory.createEmptyBorder(14, 14, 14, 14)
		));

		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(5, 0, 5, 0);
		gc.weightx = 1.0;

		JLabel titleLabel = new JLabel("SFATURI JOC");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
		titleLabel.setForeground(ACCENT_BLUE);
		gc.gridy = 0;
		panel.add(titleLabel, gc);

		String[] tips = {
			"• Echilibrează clădirile rezidențiale și comerciale pentru creștere",
			"• Parcurile cresc fericirea; fabricile cresc șomajul",
			"• Drumurile conectează clădirile și îmbunătățesc valoarea",
			"• Monitorizează venitul zilnic pentru a evita falimentul",
			"• Populație mai mare = venit mai mare dar nevoi mai multe"
		};

		for (int i = 0; i < tips.length; i++) {
			JLabel tipLabel = new JLabel(tips[i]);
			tipLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			tipLabel.setForeground(TEXT_SECONDARY);
			gc.gridy = i + 1;
			gc.insets = new Insets(4, 0, 4, 0);
			panel.add(tipLabel, gc);
		}

		return panel;
	}
}
