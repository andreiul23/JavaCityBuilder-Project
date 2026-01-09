package main;

import java.awt.Dimension;

import javax.swing.JFrame;

public class Runner {

	public static void main(String[] args) {
		boolean demoOnly = false;
		for (String arg : args) {
			if ("--demo-only".equalsIgnoreCase(arg)) {
				demoOnly = true;
			}
		}

		try {
			DemoScenarios.runAll();
			System.out.println("Demo scenarios completed successfully.");
		} catch (ScenarioExecutionException ex) {
			System.err.println("Demo scenarios failed: " + ex.getMessage());
		}

		if (demoOnly) {
			return;
		}

		javax.swing.SwingUtilities.invokeLater(() -> {
			/* Creează un cadru și adaugă vizualizarea la acesta */
			JFrame main_frame = new JFrame();
			main_frame.setTitle("City Builder - Manage Your Growing City");
			main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			/* Creează modelul și vizualizarea */
			Model model = new Model();
			View view = new View(model, main_frame);
			main_frame.setContentPane(view);

			/* Blochează dimensiunile */
			main_frame.setPreferredSize(new Dimension(1400, 900));
			main_frame.setResizable(true);
			main_frame.setMinimumSize(new Dimension(1200, 780));

			/* Face cadrul vizibil */
			main_frame.pack();
			main_frame.setLocationRelativeTo(null); // Centrează pe ecran
			main_frame.setVisible(true);
		});
	}

}
