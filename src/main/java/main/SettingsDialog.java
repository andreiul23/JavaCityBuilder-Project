package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;

public class SettingsDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private final transient Model model;
	private final transient View view;
	private final JSlider speedSlider;
	private final JCheckBox eventsCheckbox;

	@SuppressWarnings("this-escape")
	public SettingsDialog(JFrame owner, Model model, View view) {
		super(owner, "Setări", true);
		this.model = model;
		this.view = view;
		setLayout(new BorderLayout());

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(10, 10, 10, 10);
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1.0;

		eventsCheckbox = new JCheckBox("Activează evenimente aleatorii", model.isRandomEventsEnabled());
		panel.add(eventsCheckbox, gc);

		gc.gridy++;
		JLabel speedLabel = new JLabel("Viteză auto-rulare (ms între zile)");
		panel.add(speedLabel, gc);

		gc.gridy++;
		speedSlider = new JSlider(100, 1500, (int) view.getAutoRunDelayMs());
		speedSlider.setMajorTickSpacing(350);
		speedSlider.setMinorTickSpacing(50);
		speedSlider.setPaintTicks(true);
		speedSlider.setPaintLabels(true);
		panel.add(speedSlider, gc);

		add(panel, BorderLayout.CENTER);

		JPanel buttons = new JPanel();
		JButton saveBtn = new JButton("Salvează");
		JButton cancelBtn = new JButton("Anulează");
		buttons.add(saveBtn);
		buttons.add(cancelBtn);
		add(buttons, BorderLayout.SOUTH);

		ChangeListener syncTitle = e -> {
			setTitle("Setări - întârziere " + speedSlider.getValue() + " ms");
		};
		speedSlider.addChangeListener(syncTitle);
		syncTitle.stateChanged(null);

		saveBtn.addActionListener(e -> apply());
		cancelBtn.addActionListener(e -> dispose());

		setPreferredSize(new Dimension(360, 220));
		pack();
		if (owner != null) {
			setLocationRelativeTo(owner);
		} else {
			setLocationRelativeTo(null);
		}
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	private void apply() {
		boolean events = eventsCheckbox.isSelected();
		model.setRandomEventsEnabled(events);
		view.updateAutoRunDelay(speedSlider.getValue());
		dispose();
	}
}
