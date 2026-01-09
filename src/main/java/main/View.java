package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.File;

public class View extends JPanel implements main.ModelObserver, ActionListener, LogObserver {
	private static final long serialVersionUID = 1L;
	/* Definire variabile de instanță */
	private JLabel moneyLabel;
	private JLabel dailyIncomeLabel;
	private JLabel populationLabel;
	private JLabel dayLabel;
	private JLabel happinessLabel;
	private JLabel unemploymentLabel;
	private JLabel scoreLabel;
	private JTextArea logLabel;

	private JButton nextDayButton;
	private JButton helpButton;
	private JButton playButton;
	private JButton settingsButton;
	private JButton undoButton;
	private JButton saveButton;
	private JButton loadButton;

	private transient Model model;
	private GridBagConstraints c;
	private BoardVisualizerWidget boardDrawer;
	private JFrame parentFrame;

	private boolean isAutoRunning;
	private transient BackgroundRunner brunner;
	private double autoRunDelayMs = 500;
	
	/* Câmpuri de animație pentru tranziții line */
	private javax.swing.Timer animationTimer;
	private double currentDisplayBalance;
	private double targetBalance;
	
	/* Câmpuri de animație puls pentru indicatorul de venit */
	private long lastIncomeUpdateTime = 0;
	private static final long INCOME_PULSE_DURATION = 600; // puls de 600ms

	/* Format număr pentru bani: întotdeauna 2 zecimale cu grupare */
	private static final java.text.DecimalFormat moneyFormat = new java.text.DecimalFormat("#,##0.00");

	/* Paletă de culori - Modern și Profesional cu Accente Vibrante */
	private static final Color BG_DARK = new Color(15, 18, 25);
	private static final Color PANEL_BG = new Color(26, 32, 44);
	private static final Color ACCENT_BLUE = new Color(59, 130, 246);
	private static final Color ACCENT_BLUE_BRIGHT = new Color(96, 165, 250);
	private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
	private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
	private static final Color TEXT_ACCENT = new Color(125, 211, 252);
	private static final Color BORDER_COLOR = new Color(51, 65, 85);
	private static final Color BORDER_HIGHLIGHT = new Color(71, 85, 105);
	private static final Color STAT_MONEY = new Color(16, 185, 129);
	private static final Color STAT_INCOME = new Color(251, 191, 36);
	private static final Color STAT_PEOPLE = new Color(139, 92, 246);
	private static final Color STAT_DAY = new Color(59, 130, 246);
	private static final Color STAT_HAPPINESS = new Color(249, 115, 22);
	private static final Color STAT_JOBS = new Color(236, 72, 153);
	private static final Color STAT_SCORE = new Color(234, 179, 8);

	@SuppressWarnings("this-escape")
	public View(Model model, JFrame parentFrame) {
		/* Încapsulează modelul */
		this.model = model;
		this.parentFrame = parentFrame;
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(12, 12, 12, 12);
		
		/* Setează Aspectul */
		setBackground(BG_DARK);
		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(1250, 800));

		/* Adaugă widget vizualizare tablă - focus principal */
		boardDrawer = new BoardVisualizerWidget(model);
		boardDrawer.setPreferredSize(new Dimension(1050, 900));
		boardDrawer.setBackground(new Color(241, 245, 249));
		boardDrawer.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(ACCENT_BLUE_BRIGHT, 3),
				BorderFactory.createLineBorder(BORDER_HIGHLIGHT, 1)
			),
			BorderFactory.createEmptyBorder(3, 3, 3, 3)
		));
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 8;
		c.gridwidth = 2;
		c.weightx = 0.7;
		c.weighty = 1.0;
		c.insets = new Insets(10, 16, 12, 12);
		this.add(boardDrawer, c);

		/* Creează panou statistici */
		JPanel statsPanel = createStatsPanel();
		c.gridx = 2;
		c.gridy = 0;
		c.gridheight = 4;
		c.gridwidth = 1;
		c.weightx = 0.15;
		c.weighty = 0.45;
		c.insets = new Insets(12, 6, 6, 6);
		this.add(statsPanel, c);

		/* Creează panou jurnal */
		JPanel logPanel = createLogPanel();
		c.gridx = 3;
		c.gridy = 0;
		c.gridheight = 4;
		c.gridwidth = 1;
		c.weightx = 0.15;
		c.weighty = 0.45;
		c.insets = new Insets(12, 6, 6, 12);
		this.add(logPanel, c);

		/* Creează panou controale întins pe lățime */
		JPanel controlsPanel = createControlsPanel();
		c.gridx = 2;
		c.gridy = 4;
		c.gridheight = 4;
		c.gridwidth = 2;
		c.weightx = 0.3;
		c.weighty = 0.4;
		c.insets = new Insets(6, 6, 12, 12);
		this.add(controlsPanel, c);

		/* Adaugă Vizualizarea ca ModelObserver */
		model.addObserver(this);
		/* Adaugă Vizualizarea ca Observer jurnal */
		EventLog.getEventLog().addObserver(this);
		
		/* Inițializează valorile de animație */
		currentDisplayBalance = model.getBalance();
		targetBalance = model.getBalance();
		
		/* Configurează timer animație pentru tranziții line */
		animationTimer = new javax.swing.Timer(16, evt -> updateAnimations());
		animationTimer.start();

		/* Scurtături tastatură și tooltips */
		setupKeyBindings();
			nextDayButton.setToolTipText("Ziua următoare (N)");
		playButton.setToolTipText("Redare/Pauză (P)");
		helpButton.setToolTipText("Vezi Prețuri (V)");
		settingsButton.setToolTipText("Setări (S)");

	}

	private void setupKeyBindings() {
		javax.swing.InputMap im = this.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
		javax.swing.ActionMap am = this.getActionMap();

		im.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, 0), "nextDay");
		am.put("nextDay", new javax.swing.AbstractAction() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				nextDayButton.doClick();
			}
		});

		im.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, 0), "playPause");
		am.put("playPause", new javax.swing.AbstractAction() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				playButton.doClick();
			}
		});

		im.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, 0), "viewPrices");
		am.put("viewPrices", new javax.swing.AbstractAction() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				helpButton.doClick();
			}
		});

		im.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, 0), "settings");
		am.put("settings", new javax.swing.AbstractAction() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				settingsButton.doClick();
			}
		});

		im.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, 0), "undo");
		am.put("undo", new javax.swing.AbstractAction() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				undoButton.doClick();
			}
		});

	}

	private JPanel createStatsPanel() {
		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(java.awt.Graphics g) {
				super.paintComponent(g);
				java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
				g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
					java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				
				/* Adaugă fundal gradient subtil */
				java.awt.GradientPaint gradient = new java.awt.GradientPaint(
					0, 0, PANEL_BG,
					0, getHeight(), new Color(28, 32, 42)
				);
				g2d.setPaint(gradient);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		panel.setLayout(new GridBagLayout());
		panel.setBackground(PANEL_BG);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1),
				new javax.swing.border.AbstractBorder() {
					@Override
					public void paintBorder(java.awt.Component c, java.awt.Graphics g, int x, int y, int w, int h) {
						java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
						g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
							java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
						g2d.setColor(new Color(0, 0, 0, 30));
						g2d.fillRect(x + 2, y + h - 3, w - 4, 3);
					}
					@Override
					public java.awt.Insets getBorderInsets(java.awt.Component c) {
						return new Insets(0, 0, 3, 0);
					}
				}
			),
			BorderFactory.createEmptyBorder(16, 14, 16, 14)
		));

		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(8, 0, 8, 0);
		gc.weightx = 1.0;
		gc.anchor = GridBagConstraints.WEST;

		/* Title */
		JLabel titleLabel = new JLabel("STATUS ORAȘ");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		titleLabel.setForeground(TEXT_PRIMARY);
		gc.gridy = 0;
		gc.insets = new Insets(0, 0, 10, 0);
		panel.add(titleLabel, gc);

		/* Statistici afișate ca carduri compacte */
		int[] gridys = {1, 2, 3, 4, 5, 6, 7};
		Color[] colors = {STAT_MONEY, STAT_INCOME, STAT_PEOPLE, STAT_DAY, STAT_HAPPINESS, STAT_JOBS, STAT_SCORE};
		String[] titles = {"Balanță", "Venit Zilnic", "Populație", "Ziua", "Fericire", "Șomaj", "Scor"};

		/* Balanță */
		moneyLabel = createStatLabel("$" + round(model.getBalance(), 2), STAT_MONEY, IconFactory.money());
		gc.gridy = gridys[0];
		gc.insets = new Insets(4, 0, 4, 0);
		panel.add(createStatRow(titles[0], moneyLabel, colors[0]), gc);

		/* Venit Zilnic */
		dailyIncomeLabel = createStatLabel("+$" + round(model.getDailyIncome(), 2), STAT_INCOME, IconFactory.income());
		gc.gridy = gridys[1];
		panel.add(createStatRow(titles[1], dailyIncomeLabel, colors[1]), gc);

		/* Populație */
		populationLabel = createStatLabel(model.getPopulation() + " cetățeni", STAT_PEOPLE, IconFactory.population());
		gc.gridy = gridys[2];
		panel.add(createStatRow(titles[2], populationLabel, colors[2]), gc);

		/* Zi */
		dayLabel = createStatLabel("Ziua " + model.getDay(), STAT_DAY, IconFactory.day());
		gc.gridy = gridys[3];
		panel.add(createStatRow(titles[3], dayLabel, colors[3]), gc);

		/* Fericire */
		happinessLabel = createStatLabel(round(model.getHappiness(), 1) + "%", STAT_HAPPINESS, IconFactory.happiness());
		gc.gridy = gridys[4];
		panel.add(createStatRow(titles[4], happinessLabel, colors[4]), gc);

		/* Șomaj */
		unemploymentLabel = createStatLabel(model.getUnemploymentRate(), STAT_JOBS, IconFactory.unemployment());
		gc.gridy = gridys[5];
		panel.add(createStatRow(titles[5], unemploymentLabel, colors[5]), gc);

		/* Scor */
		scoreLabel = createStatLabel(model.getScore() + " pct", STAT_SCORE, IconFactory.score());
		gc.gridy = gridys[6];
		gc.insets = new Insets(4, 0, 0, 0);
		panel.add(createStatRow(titles[6], scoreLabel, colors[6]), gc);

		return panel;
	}

	private JPanel createStatRow(String title, JLabel valueLabel, Color color) {
		JPanel row = new JPanel() {
			private boolean isHovered = false;
			
			@Override
			protected void paintComponent(java.awt.Graphics g) {
				super.paintComponent(g);
				java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
				g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
					java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				
				if (isHovered) {
					g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 15));
					g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
					
					g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80));
					g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
				}
			}
		};
		row.setLayout(new GridBagLayout());
		row.setBackground(PANEL_BG);
		row.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		row.setOpaque(false);
		
		/* Adaugă efect hover */
		row.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				try {
					java.lang.reflect.Field field = row.getClass().getDeclaredField("isHovered");
					field.setAccessible(true);
					field.setBoolean(row, true);
					row.repaint();
				} catch (Exception ex) { /* ignore */ }
			}
			
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				try {
					java.lang.reflect.Field field = row.getClass().getDeclaredField("isHovered");
					field.setAccessible(true);
					field.setBoolean(row, false);
					row.repaint();
				} catch (Exception ex) { /* ignore */ }
			}
		});

		GridBagConstraints gc = new GridBagConstraints();

		/* Titlu */
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		titleLabel.setForeground(TEXT_SECONDARY);
		gc.gridx = 0;
		gc.gridy = 0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weightx = 1.0;
		gc.anchor = GridBagConstraints.WEST;
		row.add(titleLabel, gc);

		/* Valoare cu indicator color */
		valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
		valueLabel.setForeground(color);
		valueLabel.setIconTextGap(6);
		gc.gridx = 0;
		gc.gridy = 1;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(2, 0, 0, 0);
		row.add(valueLabel, gc);
		
		/* Adaugă bară de progres pentru statistici bazate pe procente */
		if (title.equals("Fericire") || title.equals("Șomaj")) {
			JPanel progressBar = createProgressBar(color, title);
			gc.gridy = 2;
			gc.insets = new Insets(6, 0, 0, 0);
			gc.fill = GridBagConstraints.HORIZONTAL;
			row.add(progressBar, gc);
		}

		return row;
	}
	
	private JPanel createProgressBar(Color color, String statType) {
		JPanel progressBar = new JPanel() {
			@Override
			protected void paintComponent(java.awt.Graphics g) {
				super.paintComponent(g);
				java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
				g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
					java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				
				/* Bară fundal */
				g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
				
				/* Umplere progres */
				double percentage = 0;
				if (statType.equals("Fericire")) {
					percentage = model.getHappiness() / 100.0;
				} else if (statType.equals("Șomaj")) {
					String rate = model.getUnemploymentRate();
					try {
						percentage = Double.parseDouble(rate.replace("%", "")) / 100.0;
					} catch (Exception e) {
						percentage = 0;
					}
				}
				
				int fillWidth = (int) (getWidth() * percentage);
				java.awt.GradientPaint gradient = new java.awt.GradientPaint(
					0, 0, color,
					fillWidth, 0, new Color(color.getRed(), color.getGreen(), color.getBlue(), 180)
				);
				g2d.setPaint(gradient);
				g2d.fillRoundRect(0, 0, fillWidth, getHeight(), 4, 4);
			}
		};
		progressBar.setPreferredSize(new Dimension(0, 6));
		progressBar.setOpaque(false);
		return progressBar;
	}

	private JLabel createStatLabel(String text, Color color, javax.swing.Icon icon) {
		JLabel label = new JLabel(text);
		label.setFont(new Font("Segoe UI", Font.BOLD, 13));
		label.setForeground(color);
		label.setIcon(icon);
		label.setIconTextGap(6);
		return label;
	}

	private JPanel createControlsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBackground(PANEL_BG);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(BORDER_COLOR, 1),
			BorderFactory.createEmptyBorder(16, 14, 16, 14)
		));

		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(6, 4, 6, 4);
		gc.weightx = 1.0;

		/* Titlu */
		JLabel titleLabel = new JLabel("COMENZI");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		titleLabel.setForeground(TEXT_PRIMARY);
		gc.gridy = 0;
		gc.insets = new Insets(0, 0, 10, 0);
		gc.gridwidth = 3;
		panel.add(titleLabel, gc);

		/* Adaugă Buton Ziua Următoare */
		nextDayButton = new JButton("Ziua Următoare");
		nextDayButton.setActionCommand("NextDayButton");
		nextDayButton.addActionListener(this);
		stylePrimaryButton(nextDayButton);
		nextDayButton.setIcon(IconFactory.nextDay());
		gc.gridy = 1;
		gc.gridx = 0;
		gc.gridwidth = 1;
		gc.insets = new Insets(6, 4, 6, 4);
		gc.weightx = 0.33;
		panel.add(nextDayButton, gc);

		/* Adaugă buton redare */
		playButton = new JButton("Redare/Pauză");
		playButton.setActionCommand("PlayButton");
		playButton.addActionListener(this);
		stylePrimaryButton(playButton);
		playButton.setIcon(IconFactory.playPause());
		gc.gridx = 1;
		panel.add(playButton, gc);

		/* Adaugă buton ajutor */
		helpButton = new JButton("Vezi Prețuri");
		helpButton.setActionCommand("HelpButton");
		helpButton.addActionListener(this);
		stylePrimaryButton(helpButton);
		helpButton.setIcon(IconFactory.prices());
		gc.gridx = 2;
		panel.add(helpButton, gc);

		/* Adaugă buton setări */
		settingsButton = new JButton("Setări");
		settingsButton.setActionCommand("SettingsButton");
		settingsButton.addActionListener(this);
		stylePrimaryButton(settingsButton);
		settingsButton.setIcon(IconFactory.day());
		gc.gridy = 2;
		gc.gridx = 0;
		gc.gridwidth = 3;
		panel.add(settingsButton, gc);

		/* Adaugă buton anulare */
		undoButton = new JButton("Anulare");
		undoButton.setActionCommand("UndoButton");
		undoButton.addActionListener(this);
		stylePrimaryButton(undoButton);
		undoButton.setToolTipText("Apasă U pentru a anula");
		gc.gridy = 3;
		gc.gridx = 0;
		gc.gridwidth = 3;
		panel.add(undoButton, gc);

		/* Adaugă buton salvare */
		saveButton = new JButton("Salvează Joc");
		saveButton.setActionCommand("SaveButton");
		saveButton.addActionListener(this);
		stylePrimaryButton(saveButton);
		gc.gridy = 4;
		gc.gridx = 0;
		gc.gridwidth = 1;
		panel.add(saveButton, gc);

		/* Adaugă buton încărcare */
		loadButton = new JButton("Încarcă Joc");
		loadButton.setActionCommand("LoadButton");
		loadButton.addActionListener(this);
		stylePrimaryButton(loadButton);
		gc.gridy = 4;
		gc.gridx = 1;
		gc.gridwidth = 2;
		panel.add(loadButton, gc);

		return panel;
	}

	private JPanel createLogPanel() {
		JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(java.awt.Graphics g) {
				super.paintComponent(g);
				java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
				g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
					java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				
				/* Adaugă fundal gradient subtil */
				java.awt.GradientPaint gradient = new java.awt.GradientPaint(
					0, 0, PANEL_BG,
					0, getHeight(), new Color(28, 32, 42)
				);
				g2d.setPaint(gradient);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		panel.setLayout(new GridBagLayout());
		panel.setBackground(PANEL_BG);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1),
				new javax.swing.border.AbstractBorder() {
					@Override
					public void paintBorder(java.awt.Component c, java.awt.Graphics g, int x, int y, int w, int h) {
						java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
						g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
							java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
						g2d.setColor(new Color(0, 0, 0, 30));
						g2d.fillRect(x + 2, y + h - 3, w - 4, 3);
					}
					@Override
					public java.awt.Insets getBorderInsets(java.awt.Component c) {
						return new Insets(0, 0, 3, 0);
					}
				}
			),
			BorderFactory.createEmptyBorder(16, 14, 16, 14)
		));

		GridBagConstraints gc = new GridBagConstraints();

		/* Titlu */
		JLabel titleLabel = new JLabel("JURNAL EVENIMENTE");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		titleLabel.setForeground(TEXT_PRIMARY);
		gc.gridx = 0;
		gc.gridy = 0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weightx = 1.0;
		gc.weighty = 0;
		gc.insets = new Insets(0, 0, 12, 0);
		panel.add(titleLabel, gc);

		/* Linie separatoare */
		JPanel separator = new JPanel();
		separator.setBackground(BORDER_COLOR);
		separator.setPreferredSize(new Dimension(0, 1));
		gc.gridy = 1;
		gc.insets = new Insets(0, 0, 10, 0);
		panel.add(separator, gc);

		/* Configurează vizualizator jurnal */
		logLabel = new JTextArea(12, 26);
		logLabel.setEditable(false);
		logLabel.setBackground(new Color(17, 24, 39));
		logLabel.setForeground(new Color(156, 163, 175));
		logLabel.setFont(new Font("Consolas", Font.PLAIN, 11));
		logLabel.setLineWrap(true);
		logLabel.setWrapStyleWord(true);
		logLabel.setMargin(new Insets(10, 10, 10, 10));
		logLabel.setCaretColor(TEXT_ACCENT);
		
		JScrollPane scrollPane = new JScrollPane(logLabel);
		scrollPane.setBackground(new Color(17, 24, 39));
		scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = ACCENT_BLUE_BRIGHT;
				this.trackColor = new Color(17, 24, 39);
			}
			@Override
			protected javax.swing.JButton createDecreaseButton(int orientation) {
				return createZeroButton();
			}
			@Override
			protected javax.swing.JButton createIncreaseButton(int orientation) {
				return createZeroButton();
			}
			private javax.swing.JButton createZeroButton() {
				javax.swing.JButton button = new javax.swing.JButton();
				button.setPreferredSize(new Dimension(0, 0));
				return button;
			}
		});
		
		gc.gridy = 2;
		gc.fill = GridBagConstraints.BOTH;
		gc.weighty = 1.0;
		gc.insets = new Insets(0, 0, 0, 0);
		panel.add(scrollPane, gc);

		return panel;
	}

	private void stylePrimaryButton(JButton button) {
		button.setFont(new Font("Segoe UI", Font.BOLD, 12));
		button.setBackground(ACCENT_BLUE);
		button.setForeground(TEXT_PRIMARY);
		button.setFocusPainted(false);
		button.setContentAreaFilled(false);
		button.setOpaque(false);
		button.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
		button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		button.setPreferredSize(new Dimension(140, 40));
		button.setMinimumSize(new Dimension(140, 40));
		button.setMaximumSize(new Dimension(140, 40));
		button.setIconTextGap(8);
		
		/* Pictare personalizată pentru gradient și umbră */
		button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
			@Override
			public void paint(java.awt.Graphics g, javax.swing.JComponent c) {
				java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
				g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
					java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				
				javax.swing.AbstractButton b = (javax.swing.AbstractButton) c;
				javax.swing.ButtonModel model = b.getModel();
				
				/* Umbră */
				g2d.setColor(new Color(0, 0, 0, 40));
				g2d.fillRoundRect(2, 3, c.getWidth() - 4, c.getHeight() - 3, 8, 8);
				
				/* Fundal buton cu gradient */
				Color bgColor = model.isRollover() ? ACCENT_BLUE_BRIGHT : ACCENT_BLUE;
				Color bgColorDark = model.isRollover() ? new Color(37, 99, 235) : new Color(29, 78, 216);
				java.awt.GradientPaint gradient = new java.awt.GradientPaint(
					0, 0, bgColor,
					0, c.getHeight(), bgColorDark
				);
				g2d.setPaint(gradient);
				g2d.fillRoundRect(0, 0, c.getWidth() - 2, c.getHeight() - 3, 10, 10);
				
				/* Evidențiere lucioasă pe jumătatea superioară */
				g2d.setColor(new Color(255, 255, 255, model.isRollover() ? 30 : 20));
				g2d.fillRoundRect(0, 0, c.getWidth() - 2, c.getHeight() / 2, 10, 10);
				
				/* Margine */
				g2d.setColor(model.isRollover() ? new Color(147, 197, 253) : new Color(37, 99, 235));
				g2d.setStroke(new java.awt.BasicStroke(model.isRollover() ? 2 : 1));
				g2d.drawRoundRect(
					model.isRollover() ? 1 : 0,
					model.isRollover() ? 1 : 0,
					c.getWidth() - (model.isRollover() ? 4 : 2),
					c.getHeight() - (model.isRollover() ? 5 : 3),
					10, 10
				);
				
				super.paint(g, c);
			}
		});
		
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.repaint();
			}
			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.repaint();
			}
		});
	}

	@Override
	public void BalanceChanged() {
		targetBalance = model.getBalance();
		scoreLabel.setText(model.getScore() + " pct");
	}
	
	/* Metodă actualizare animație lină */
	private void updateAnimations() {
		/* Animează modificări balanță */
		if (Math.abs(currentDisplayBalance - targetBalance) > 0.01) {
			double diff = targetBalance - currentDisplayBalance;
			currentDisplayBalance += diff * 0.15; // Interpolare lină
			moneyLabel.setText("$" + round(currentDisplayBalance, 2));
		}
		
		/* Aplică efect puls etichetei venit dacă a fost actualizată recent */
		long timeSinceUpdate = System.currentTimeMillis() - lastIncomeUpdateTime;
		if (timeSinceUpdate < INCOME_PULSE_DURATION) {
			double pulseFactor = Math.sin((timeSinceUpdate / (double) INCOME_PULSE_DURATION) * Math.PI);
			int pulseSize = (int) (2 + pulseFactor * 3);
			dailyIncomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13 + pulseSize));
		} else {
			dailyIncomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
		}
		
		/* Repictează bare progres */
		repaint();
	}

	@Override
	public void PopulationChanged() {
		populationLabel.setText(model.getPopulation() + " cetățeni");
	}

	@Override
	public void DailyIncomeChanged() {
		dailyIncomeLabel.setText("+$" + round(model.getDailyIncome(), 2));
		lastIncomeUpdateTime = System.currentTimeMillis();
		dailyIncomeLabel.repaint();
	}

	@Override
	public void DayChanged() {
		dayLabel.setText("Ziua " + model.getDay());
	}

	@Override
	public void HappinessChanged() {
		happinessLabel.setText(round(model.getHappiness(), 1) + "%");
	}

	@Override
	public void UnemployementChanged() {
		unemploymentLabel.setText(model.getUnemploymentRate());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().contentEquals("NextDayButton")) {
			model.nextDay();
		} else if (e.getActionCommand().contentEquals("HelpButton")) {
			if (parentFrame != null) {
				PricesDialog dialog = new PricesDialog(parentFrame);
				dialog.setVisible(true);
			}
		} else if (e.getActionCommand().contentEquals("PlayButton")) {
			if (isAutoRunning) {
				isAutoRunning = false;
				if (brunner != null) {
					brunner.terminate();
					try {
						brunner.join(1000);
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					brunner = null;
				}
			} else {
				if (brunner == null || !brunner.isAlive()) {
					isAutoRunning = true;
					brunner = new BackgroundRunner(model, autoRunDelayMs);
					/* Marchează firul de execuție ca daemon astfel încât să se termine odată cu aplicația */
					brunner.setDaemon(true);
					brunner.start();
				}
			}
		} else if (e.getActionCommand().contentEquals("SettingsButton")) {
			JFrame owner = parentFrame != null ? parentFrame
					: (JFrame) SwingUtilities.getWindowAncestor(this);
			SettingsDialog dialog = new SettingsDialog(owner, model, this);
			dialog.setLocationRelativeTo(owner);
			dialog.setVisible(true);
		} else if (e.getActionCommand().contentEquals("UndoButton")) {
			model.undoLastAction();
		} else if (e.getActionCommand().contentEquals("SaveButton")) {
			handleSave();
		} else if (e.getActionCommand().contentEquals("LoadButton")) {
			handleLoad();
		}
	}

	@Override
	public void newLogEntry(String entry) {
		logLabel.append(entry + "\n");
		logLabel.setCaretPosition(logLabel.getDocument().getLength());
	}

	@Override
	public void BoardChanged() {
		boardDrawer.repaint();
	}

	public void updateAutoRunDelay(double delayMs) {
		this.autoRunDelayMs = delayMs;
		if (isAutoRunning && brunner != null) {
			brunner.setPauseDuration(delayMs);
		}
	}

	public double getAutoRunDelayMs() {
		return this.autoRunDelayMs;
	}

	/*
	 * Metodă internă ajutătoare folosită pentru rotunjirea double-urilor la afișarea elementelor UI
	 */
	public static String round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException("Places must be non-negative");
		if (places == 2) {
			return moneyFormat.format(value);
		}
		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.toPlainString();
	}

	private void handleSave() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Salvează Oraș");
		chooser.setSelectedFile(new File("city-save.dat"));
		int result = chooser.showSaveDialog(parentFrame);
		if (result == JFileChooser.APPROVE_OPTION) {
			try {
				model.saveToFile(chooser.getSelectedFile());
				JOptionPane.showMessageDialog(parentFrame, "Joc salvat.", "Salvat", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(parentFrame, "Eșec la salvare: " + ex.getMessage(), "Eroare",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void handleLoad() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Încarcă Oraș");
		int result = chooser.showOpenDialog(parentFrame);
		if (result == JFileChooser.APPROVE_OPTION) {
			try {
				model.loadFromFile(chooser.getSelectedFile());
				refreshAllStats();
				JOptionPane.showMessageDialog(parentFrame, "Joc încărcat.", "Încărcat", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(parentFrame, "Eșec la încărcare: " + ex.getMessage(), "Eroare",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void refreshAllStats() {
		BalanceChanged();
		PopulationChanged();
		DailyIncomeChanged();
		DayChanged();
		HappinessChanged();
		UnemployementChanged();
		boardDrawer.repaint();
	}

}
