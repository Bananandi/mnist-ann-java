package ziffererkennungsnetz;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import net.miginfocom.swing.MigLayout;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.ChangeEvent;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class UI extends JFrame {

	private Netzwerk netz;
	Daten daten;
	double[] input = new double[784];
	double[] output = new double[10];
	double[] zieloutput = new double[10];
	double trefferquote = 0.0;

	Thread thread;

	private JPanel contentPane;
	private JLabel lblNetzErstellen;
	private JLabel lblLernrate;
	private JLabel lblNeuronenzahlHiddenlayer;
	private JLabel labelNeuronHidden;
	private JLabel labelLernrate;
	private JLabel lblAnzahlTrainingsdaten;
	private JLabel lblAnzahlEpochen;
	private JSlider sliderAnzTraindaten;
	private JLabel labelAnzTraindaten;
	private JSlider sliderAnzEpochen;
	private JLabel labelAnzEpochen;
	private JButton btnTrainieren;
	private JProgressBar progressBar;
	private JLabel lblNetzAusprobieren;
	private JLabel lblTrefferquote;
	private JSlider sliderNeuronHidden;
	private JSlider sliderLernrate;
	private JButton btnAbbrechen;
	private JPanel panel;
	private JLabel lblZifferneingabe;
	private JButton btnBerechnen;
	private JButton btnLeeren;
	private JLabel lblZiffer;
	private JLabel lblQuote;
	private JLabel lblZiffer0;
	private JLabel lblZiffer1;
	private JLabel lblZiffer2;
	private JLabel lblZiffer3;
	private JLabel lblZiffer4;
	private JLabel lblZiffer5;
	private JLabel lblZiffer6;
	private JLabel lblZiffer7;
	private JLabel lblZiffer8;
	private JLabel lblZiffer9;

	private class Training extends Thread {
		public void run() {
			btnTrainieren.setEnabled(false);
			btnBerechnen.setEnabled(false);
			btnRueckwerts.setEnabled(false);
			btnLaden.setEnabled(false);
			btnSpeichern.setEnabled(false);
			btnAbbrechen.setVisible(true);
			progressBar.setVisible(true);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			netz = new Netzwerk(784, Integer.valueOf(labelNeuronHidden.getText()), 10,
					Double.valueOf(labelLernrate.getText()));

			int teiler = Integer.valueOf(labelAnzEpochen.getText()) * Integer.valueOf(labelAnzTraindaten.getText())
					/ 100;
			int durchlaeufe = 0;
			int richtig = 0;

			try {

				for (int e = 0; e < Integer.valueOf(labelAnzEpochen.getText()); e++) {
					// eine Epoche trainieren
					daten = new Daten("mnist_train.csv");
					for (int t = 0; t < Integer.valueOf(labelAnzTraindaten.getText()); t++) {
						if (isInterrupted())
							return;
						daten.lesen(input, zieloutput);
						netz.trainieren(input, zieloutput);

						if (durchlaeufe != teiler) {
							durchlaeufe++;
						} else {
							progressBar.setValue(progressBar.getValue() + 1);
							durchlaeufe = 0;
						}
					}
					daten.close();
				}

				// testen
				daten = new Daten("mnist_test.csv");
				for (int t = 0; t < 10000; t++) {
					if (isInterrupted())
						return;
					daten.lesen(input, zieloutput);
					int zielziffer = 0;
					for (int o = 0; o < 10; o++) {
						if (zieloutput[o] > zieloutput[zielziffer]) {
							zielziffer = o;
						}
					}

					output = netz.durchlauf(input);

					int ziffer = 0;
					for (int o = 0; o < 10; o++) {
						if (output[o] > output[ziffer]) {
							ziffer = o;
						}
					}

					if (ziffer == zielziffer)
						richtig++;
				}
				daten.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				System.out.println("Beim Trainieren ist etwas schief gegangen!");
			} finally {
				trefferquote = richtig / 100.0;
				lblQuote.setText(trefferquote + " %");
				btnTrainieren.setEnabled(true);
				btnBerechnen.setEnabled(true);
				btnRueckwerts.setEnabled(true);
				btnLaden.setEnabled(true);
				btnSpeichern.setEnabled(true);
				btnAbbrechen.setVisible(false);
				progressBar.setValue(0);
				progressBar.setVisible(false);
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
	};

	// ERDM: Speichern des gesamten Zustands in einem File
	private File verzeichnis = null;
	private JButton btnLaden;
	private JButton btnSpeichern;
	private JLabel lblAktivittenDerOutputneuronen;
	private JButton btnRueckwerts;
	private JTextField textFieldZiffer;
	private JLabel labelZiffer0;
	private JLabel labelZiffer2;
	private JLabel labelZiffer4;
	private JLabel labelZiffer6;
	private JLabel labelZiffer8;
	private JLabel labelZiffer1;
	private JLabel labelZiffer3;
	private JLabel labelZiffer5;
	private JLabel labelZiffer7;
	private JLabel labelZiffer9;

	void speichern() {
		if (netz == null) {
			JOptionPane.showMessageDialog(null, "Bitte erst ein Netz trainieren oder laden", "Achtung",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		final JFileChooser fc = new JFileChooser() {
			@Override
			public void approveSelection() {
				File f = getSelectedFile();
				if (f.exists() && getDialogType() == SAVE_DIALOG) {
					int result = JOptionPane.showConfirmDialog(this, "File existiert bereits, Ueberschreiben?",
							"File existiert", JOptionPane.YES_NO_CANCEL_OPTION);
					switch (result) {
					case JOptionPane.YES_OPTION:
						super.approveSelection();
						return;
					case JOptionPane.NO_OPTION:
						return;
					case JOptionPane.CLOSED_OPTION:
						return;
					case JOptionPane.CANCEL_OPTION:
						cancelSelection();
						return;
					}
				}
				super.approveSelection();
			}
		};

		fc.setDialogTitle("Speichern des aktuellen Netzes");
		// fc.setFileFilter( new FileNameExtensionFilter("Netz-Dateien (*.net)", ".net")
		// );
		if (verzeichnis != null) {
			fc.setCurrentDirectory(verzeichnis);
		}

		int returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			verzeichnis = fc.getCurrentDirectory();
			File f = fc.getSelectedFile();
			try {
				ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(f));
				outputStream.writeInt(sliderNeuronHidden.getValue());
				outputStream.writeInt(sliderLernrate.getValue());
				outputStream.writeInt(sliderAnzTraindaten.getValue());
				outputStream.writeInt(sliderAnzEpochen.getValue());
				outputStream.writeDouble(trefferquote);
				netz.speichern(outputStream);
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// ERDM: Einlesen des gesamten Zustands aus einem File
	void lesen() {
		final JFileChooser fc = new JFileChooser();

		fc.setDialogTitle("Einlesen eines Netzes");
		// fc.setFileFilter( new FileNameExtensionFilter("Netz-Dateien (*.net)", "net")
		// );
		if (verzeichnis != null) {
			fc.setCurrentDirectory(verzeichnis);
		}

		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			verzeichnis = fc.getCurrentDirectory();
			File f = getSelectedFileWithExtension(fc);
			try {
				ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(f));
				sliderNeuronHidden.setValue(inputStream.readInt());
				sliderLernrate.setValue(inputStream.readInt());
				sliderAnzTraindaten.setValue(inputStream.readInt());
				sliderAnzEpochen.setValue(inputStream.readInt());
				trefferquote = inputStream.readDouble();
				lblQuote.setText(trefferquote + " %");
				if (netz == null) { // gibt's das Netz nicht - ein neues erstellen - Werte uninteressant
					netz = new Netzwerk(1, 1, 1, 0.2);
				}
				netz.lesen(inputStream);
				inputStream.close();
				btnBerechnen.setEnabled(true);
				btnRueckwerts.setEnabled(true);
				btnSpeichern.setEnabled(true);
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Ermitteln des eingegebenen Files in einem JFileChooser. Fehlt die Endung,
	 * wird automatisch .net angehängt. Von
	 * https://stackoverflow.com/questions/8713968/add-txt-extension-in-jfilechooser?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
	 */
	public static File getSelectedFileWithExtension(JFileChooser c) {
		File file = c.getSelectedFile();
		if (c.getFileFilter() instanceof FileNameExtensionFilter) {
			String[] exts = ((FileNameExtensionFilter) c.getFileFilter()).getExtensions();
			String nameLower = file.getName().toLowerCase();
			for (String ext : exts) { // check if it already has a valid extension
				if (nameLower.endsWith('.' + ext.toLowerCase())) {
					return file; // if yes, return as-is
				}
			}
			// if not, append the first extension from the selected filter
			file = new File(file.toString() + '.' + exts[0]);
		}
		return file;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UI frame = new UI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public UI() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(UI.class.getResource("/ziffererkennungsnetz/KI_icon4.png")));
		setBackground(Color.BLACK);
		setTitle("K\u00FCnstliches Neuronales Netz zur Erkennung mausgeschriebener Ziffern");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(20, 20, 850, 900);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(220, 220, 220));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[grow,fill][42.00,grow,fill][pref!,grow,fill][250.00,grow,fill][299.00,grow,fill]", "[fill][grow][grow][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill]"));

		lblNetzErstellen = new JLabel("Netz erstellen");
		lblNetzErstellen.setForeground(new Color(0, 0, 0));
		lblNetzErstellen.setBackground(new Color(220, 220, 220));
		lblNetzErstellen.setFont(new Font("Tahoma", Font.BOLD, 25));
		contentPane.add(lblNetzErstellen, "cell 0 0,grow");

		lblNetzAusprobieren = new JLabel("Netz ausprobieren");
		lblNetzAusprobieren.setBackground(new Color(220, 220, 220));
		lblNetzAusprobieren.setForeground(new Color(0, 0, 0));
		lblNetzAusprobieren.setFont(new Font("Tahoma", Font.BOLD, 25));
		contentPane.add(lblNetzAusprobieren, "cell 3 0,grow");

		sliderNeuronHidden = new JSlider();
		sliderNeuronHidden.setForeground(new Color(0, 0, 0));
		sliderNeuronHidden.setBackground(new Color(192, 192, 192));
		sliderNeuronHidden.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		sliderNeuronHidden.setFont(new Font("Tahoma", Font.PLAIN, 18));
		sliderNeuronHidden.setMaximum(1000);
		sliderNeuronHidden.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (labelNeuronHidden != null) {
					labelNeuronHidden.setText("" + sliderNeuronHidden.getValue());
				}
			}
		});

		lblNeuronenzahlHiddenlayer = new JLabel("Neuronenzahl 2. Schicht:");
		lblNeuronenzahlHiddenlayer.setBackground(new Color(220, 220, 220));
		lblNeuronenzahlHiddenlayer.setForeground(new Color(0, 0, 0));
		lblNeuronenzahlHiddenlayer.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblNeuronenzahlHiddenlayer, "cell 0 3,grow");

		lblZifferneingabe = new JLabel("Zifferneingabe:");
		lblZifferneingabe.setBackground(new Color(220, 220, 220));
		lblZifferneingabe.setForeground(new Color(0, 0, 0));
		lblZifferneingabe.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblZifferneingabe, "cell 3 3,grow");
		sliderNeuronHidden.setToolTipText("1 - 1000 Neuronen im Hiddenlayer einstellen");
		sliderNeuronHidden.setValue(100);
		sliderNeuronHidden.setMinorTickSpacing(1);
		sliderNeuronHidden.setMinimum(1);
		sliderNeuronHidden.setMajorTickSpacing(1);
		contentPane.add(sliderNeuronHidden, "cell 0 4,grow");

		labelNeuronHidden = new JLabel("100");
		labelNeuronHidden.setBackground(new Color(220, 220, 220));
		labelNeuronHidden.setForeground(new Color(0, 0, 0));
		labelNeuronHidden.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(labelNeuronHidden, "cell 1 4,grow");

		panel = new Zeichenblock(28, 28, 12);
		panel.setBackground(new Color(220, 220, 220));
		contentPane.add(panel, "cell 3 4 2 7,grow");

		sliderLernrate = new JSlider();
		sliderLernrate.setForeground(new Color(0, 0, 0));
		sliderLernrate.setBackground(new Color(192, 192, 192));
		sliderLernrate.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		sliderLernrate.setFont(new Font("Tahoma", Font.PLAIN, 18));
		sliderLernrate.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (labelLernrate != null) {
					labelLernrate.setText("" + (double) sliderLernrate.getValue() / 100);
				}
			}
		});

		lblLernrate = new JLabel("Lernrate:");
		lblLernrate.setBackground(new Color(220, 220, 220));
		lblLernrate.setForeground(new Color(0, 0, 0));
		lblLernrate.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblLernrate, "cell 0 5,grow");
		sliderLernrate.setValue(20);
		sliderLernrate.setToolTipText("Lernfaktor von 0.00 - 1.00 einstellen");
		sliderLernrate.setMinorTickSpacing(1);
		sliderLernrate.setMinimum(1);
		sliderLernrate.setMajorTickSpacing(1);
		contentPane.add(sliderLernrate, "cell 0 6,grow");

		labelLernrate = new JLabel("0.2");
		labelLernrate.setBackground(new Color(220, 220, 220));
		labelLernrate.setForeground(new Color(0, 0, 0));
		labelLernrate.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(labelLernrate, "cell 1 6,grow");

		lblAnzahlTrainingsdaten = new JLabel("Anzahl Trainingsdaten:");
		lblAnzahlTrainingsdaten.setBackground(new Color(220, 220, 220));
		lblAnzahlTrainingsdaten.setForeground(new Color(0, 0, 0));
		lblAnzahlTrainingsdaten.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblAnzahlTrainingsdaten, "cell 0 7,grow");

		sliderAnzTraindaten = new JSlider();
		sliderAnzTraindaten.setForeground(new Color(0, 0, 0));
		sliderAnzTraindaten.setBackground(new Color(192, 192, 192));
		sliderAnzTraindaten.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		sliderAnzTraindaten.setFont(new Font("Tahoma", Font.PLAIN, 18));
		sliderAnzTraindaten.setToolTipText("1 - 60 000 Trainingsbilder verwenden");
		sliderAnzTraindaten.setMaximum(60000);
		sliderAnzTraindaten.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (labelAnzTraindaten != null) {
					labelAnzTraindaten.setText("" + sliderAnzTraindaten.getValue());
				}
			}
		});
		sliderAnzTraindaten.setValue(60000);
		sliderAnzTraindaten.setMinimum(1);
		sliderAnzTraindaten.setMinorTickSpacing(1);
		sliderAnzTraindaten.setMajorTickSpacing(1);
		contentPane.add(sliderAnzTraindaten, "cell 0 8,grow");

		labelAnzTraindaten = new JLabel("60000");
		labelAnzTraindaten.setBackground(new Color(220, 220, 220));
		labelAnzTraindaten.setForeground(new Color(0, 0, 0));
		labelAnzTraindaten.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(labelAnzTraindaten, "cell 1 8,grow");

		lblAnzahlEpochen = new JLabel("Anzahl Epochen:");
		lblAnzahlEpochen.setBackground(new Color(220, 220, 220));
		lblAnzahlEpochen.setForeground(new Color(0, 0, 0));
		lblAnzahlEpochen.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblAnzahlEpochen, "cell 0 9,grow");

		sliderAnzEpochen = new JSlider();
		sliderAnzEpochen.setForeground(new Color(0, 0, 0));
		sliderAnzEpochen.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		sliderAnzEpochen.setBackground(new Color(192, 192, 192));
		sliderAnzEpochen.setFont(new Font("Tahoma", Font.PLAIN, 18));
		sliderAnzEpochen.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (labelAnzEpochen != null) {
					labelAnzEpochen.setText("" + sliderAnzEpochen.getValue());
				}
			}
		});
		sliderAnzEpochen.setToolTipText("1 - 100 mal mit Trainingsdaten trainieren");
		sliderAnzEpochen.setValue(1);
		sliderAnzEpochen.setMinimum(1);
		sliderAnzEpochen.setMinorTickSpacing(1);
		sliderAnzEpochen.setMajorTickSpacing(1);
		contentPane.add(sliderAnzEpochen, "cell 0 10,grow");

		labelAnzEpochen = new JLabel("1");
		labelAnzEpochen.setBackground(new Color(220, 220, 220));
		labelAnzEpochen.setForeground(new Color(0, 0, 0));
		labelAnzEpochen.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(labelAnzEpochen, "cell 1 10,grow");

		btnTrainieren = new JButton("Netz erstellen und trainieren");
		btnTrainieren.setBackground(new Color(192, 192, 192));
		btnTrainieren.setForeground(new Color(0, 0, 0));
		btnTrainieren.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnTrainieren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread = new Training();
				thread.start();
			}
		});
		contentPane.add(btnTrainieren, "flowy,cell 0 11,grow");

		progressBar = new JProgressBar();
		progressBar.setBackground(new Color(192, 192, 192));
		progressBar.setForeground(new Color(0, 0, 0));
		progressBar.setFont(new Font("Tahoma", Font.PLAIN, 18));
		progressBar.setToolTipText("Der Trainigsprozess kann einige Minuten in Anpruch nehmen.");
		progressBar.setStringPainted(true);
		progressBar.setVisible(false);

		lblZiffer = new JLabel("Dies ist eine:   ");
		lblZiffer.setBackground(new Color(220, 220, 220));
		lblZiffer.setForeground(new Color(0, 0, 0));
		lblZiffer.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblZiffer, "flowx,cell 3 11,grow");
		contentPane.add(progressBar, "flowx,cell 0 12,grow");

		btnAbbrechen = new JButton("Abbrechen");
		btnAbbrechen.setBackground(new Color(192, 192, 192));
		btnAbbrechen.setForeground(new Color(0, 0, 0));
		btnAbbrechen.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnAbbrechen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thread.interrupt();
			}
		});
		btnAbbrechen.setVisible(false);
		contentPane.add(btnAbbrechen, "cell 0 12,grow");

		btnLaden = new JButton("Laden");
		btnLaden.setBackground(new Color(192, 192, 192));
		btnLaden.setForeground(new Color(0, 0, 0));
		btnLaden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lesen();
			}
		});

		btnLeeren = new JButton("Leeren");
		btnLeeren.setBackground(new Color(192, 192, 192));
		btnLeeren.setForeground(new Color(0, 0, 0));
		btnLeeren.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnLeeren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((Zeichenblock) panel).clear();
			}
		});
		contentPane.add(btnLeeren, "cell 4 12,grow");

		lblTrefferquote = new JLabel("Trefferquote:");
		lblTrefferquote.setBackground(new Color(220, 220, 220));
		lblTrefferquote.setForeground(new Color(0, 0, 0));
		lblTrefferquote.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblTrefferquote, "flowx,cell 0 14,grow");

		lblAktivittenDerOutputneuronen = new JLabel("Aktivit\u00E4ten der Ausgabeneuronen:");
		lblAktivittenDerOutputneuronen.setBackground(new Color(220, 220, 220));
		lblAktivittenDerOutputneuronen.setForeground(new Color(0, 0, 0));
		lblAktivittenDerOutputneuronen.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblAktivittenDerOutputneuronen, "cell 3 14,alignx left,growy");
		btnLaden.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(btnLaden, "flowx,cell 0 15");

		lblZiffer0 = new JLabel("0:");
		lblZiffer0.setBackground(new Color(220, 220, 220));
		lblZiffer0.setForeground(new Color(0, 0, 0));
		lblZiffer0.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblZiffer0, "flowx,cell 3 15,grow");

		lblZiffer1 = new JLabel("1:");
		lblZiffer1.setBackground(new Color(220, 220, 220));
		lblZiffer1.setForeground(new Color(0, 0, 0));
		lblZiffer1.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblZiffer1, "flowx,cell 4 15,grow");

		lblZiffer2 = new JLabel("2:");
		lblZiffer2.setBackground(new Color(220, 220, 220));
		lblZiffer2.setForeground(new Color(0, 0, 0));
		lblZiffer2.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblZiffer2, "flowx,cell 3 16,grow");

		lblZiffer3 = new JLabel("3:");
		lblZiffer3.setBackground(new Color(220, 220, 220));
		lblZiffer3.setForeground(new Color(0, 0, 0));
		lblZiffer3.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblZiffer3, "flowx,cell 4 16,grow");

		lblZiffer4 = new JLabel("4:");
		lblZiffer4.setBackground(new Color(220, 220, 220));
		lblZiffer4.setForeground(new Color(0, 0, 0));
		lblZiffer4.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblZiffer4, "flowx,cell 3 17,grow");

		lblZiffer5 = new JLabel("5:");
		lblZiffer5.setBackground(new Color(220, 220, 220));
		lblZiffer5.setForeground(new Color(0, 0, 0));
		lblZiffer5.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblZiffer5, "flowx,cell 4 17,grow");

		lblZiffer6 = new JLabel("6:");
		lblZiffer6.setBackground(new Color(220, 220, 220));
		lblZiffer6.setForeground(new Color(0, 0, 0));
		lblZiffer6.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblZiffer6, "flowx,cell 3 18,grow");

		lblZiffer7 = new JLabel("7:");
		lblZiffer7.setBackground(new Color(220, 220, 220));
		lblZiffer7.setForeground(new Color(0, 0, 0));
		lblZiffer7.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblZiffer7, "flowx,cell 4 18,grow");

		lblZiffer8 = new JLabel("8:");
		lblZiffer8.setBackground(new Color(220, 220, 220));
		lblZiffer8.setForeground(new Color(0, 0, 0));
		lblZiffer8.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblZiffer8, "flowx,cell 3 19,grow");

		btnBerechnen = new JButton("Berechnen");
		btnBerechnen.setEnabled(false);
		btnBerechnen.setForeground(new Color(0, 0, 0));
		btnBerechnen.setBackground(new Color(192, 192, 192));
		btnBerechnen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					output = netz.durchlauf(((Zeichenblock) panel).ausgeben(input));
					int ziffer = 0;
					double summe = 0;
					for (int o = 0; o < 10; o++) {
						if (output[o] > output[ziffer]) {
							ziffer = o;
						}
						summe += output[o];

					textFieldZiffer.setText(Integer.toString(ziffer));
					labelZiffer0.setText(Math.round(100 / summe * output[0] * 10) / 10.0 + " %");
					labelZiffer1.setText(Math.round(100 / summe * output[1] * 10) / 10.0 + " %");
					labelZiffer2.setText(Math.round(100 / summe * output[2] * 10) / 10.0 + " %");
					labelZiffer3.setText(Math.round(100 / summe * output[3] * 10) / 10.0 + " %");
					labelZiffer4.setText(Math.round(100 / summe * output[4] * 10) / 10.0 + " %");
					labelZiffer5.setText(Math.round(100 / summe * output[5] * 10) / 10.0 + " %");
					labelZiffer6.setText(Math.round(100 / summe * output[6] * 10) / 10.0 + " %");
					labelZiffer7.setText(Math.round(100 / summe * output[7] * 10) / 10.0 + " %");
					labelZiffer8.setText(Math.round(100 / summe * output[8] * 10) / 10.0 + " %");
					labelZiffer9.setText(Math.round(100 / summe * output[9] * 10) / 10.0 + " %");
				}
			}
		});
		btnBerechnen.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(btnBerechnen, "flowx,cell 3 12,grow");

		btnSpeichern = new JButton("Speichern");
		btnSpeichern.setEnabled(false);
		btnSpeichern.setForeground(new Color(0, 0, 0));
		btnSpeichern.setBackground(new Color(192, 192, 192));
		btnSpeichern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				speichern();
			}
		});
		btnSpeichern.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(btnSpeichern, "cell 0 15");

		lblQuote = new JLabel("");
		lblQuote.setBackground(new Color(220, 220, 220));
		lblQuote.setForeground(new Color(0, 0, 0));
		lblQuote.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblQuote, "cell 0 14,grow");

		lblZiffer9 = new JLabel("9:");
		lblZiffer9.setBackground(new Color(220, 220, 220));
		lblZiffer9.setForeground(new Color(0, 0, 0));
		lblZiffer9.setFont(new Font("Tahoma", Font.PLAIN, 18));
		contentPane.add(lblZiffer9, "flowx,cell 4 19,grow");

		btnRueckwerts = new JButton("R\u00FCckwerts");
		btnRueckwerts.setEnabled(false);
		btnRueckwerts.setBackground(new Color(192, 192, 192));
		btnRueckwerts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (lblQuote.getText() != "") {
					int ziffer = 0;
					try {
						ziffer = Integer.parseInt(textFieldZiffer.getText());
						if(ziffer > 9)	System.out.println("Bitte nur Ziffern von 0 bis 9 eingeben.");
					}catch(NumberFormatException e1){
						System.out.println("Bitte nur Ziffern von 0 bis 9 eingeben.");
					}
					for(int i=0; i<10; i++) {
						if(ziffer == i)	output[i] = 0.99; 
						else	output[i] = 0.01;
					}
					((Zeichenblock) panel).rueckwerts(netz.rueckwerts(output));
				}
			}
		});
		btnRueckwerts.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnRueckwerts.setForeground(new Color(0, 0, 0));
		contentPane.add(btnRueckwerts, "cell 3 12,grow");
		
		textFieldZiffer = new JTextField();
		textFieldZiffer.setText("0");
		textFieldZiffer.setBorder(null);
		textFieldZiffer.setFont(new Font("Tahoma", Font.BOLD, 22));
		textFieldZiffer.setBackground(new Color(220, 220, 220));
		textFieldZiffer.setForeground(new Color(0, 0, 0));
		contentPane.add(textFieldZiffer, "cell 3 11,alignx right,growy");
		textFieldZiffer.setColumns(10);
		
		labelZiffer0 = new JLabel("0.0 %");
		labelZiffer0.setFont(new Font("Tahoma", Font.PLAIN, 18));
		labelZiffer0.setForeground(new Color(0, 0, 0));
		labelZiffer0.setBackground(new Color(220, 220, 220));
		contentPane.add(labelZiffer0, "cell 3 15,grow");
		
		labelZiffer2 = new JLabel("0.0 %");
		labelZiffer2.setForeground(new Color(0, 0, 0));
		labelZiffer2.setFont(new Font("Tahoma", Font.PLAIN, 18));
		labelZiffer2.setBackground(new Color(220, 220, 220));
		contentPane.add(labelZiffer2, "cell 3 16,grow");
		
		labelZiffer4 = new JLabel("0.0 %");
		labelZiffer4.setForeground(new Color(0, 0, 0));
		labelZiffer4.setFont(new Font("Tahoma", Font.PLAIN, 18));
		labelZiffer4.setBackground(new Color(220, 220, 220));
		contentPane.add(labelZiffer4, "cell 3 17,grow");
		
		labelZiffer6 = new JLabel("0.0 %");
		labelZiffer6.setForeground(new Color(0, 0, 0));
		labelZiffer6.setFont(new Font("Tahoma", Font.PLAIN, 18));
		labelZiffer6.setBackground(new Color(220, 220, 220));
		contentPane.add(labelZiffer6, "cell 3 18,grow");
		
		labelZiffer8 = new JLabel("0.0 %");
		labelZiffer8.setForeground(new Color(0, 0, 0));
		labelZiffer8.setFont(new Font("Tahoma", Font.PLAIN, 18));
		labelZiffer8.setBackground(new Color(220, 220, 220));
		contentPane.add(labelZiffer8, "cell 3 19,grow");
		
		labelZiffer1 = new JLabel("0.0 %");
		labelZiffer1.setForeground(new Color(0, 0, 0));
		labelZiffer1.setFont(new Font("Tahoma", Font.PLAIN, 18));
		labelZiffer1.setBackground(new Color(220, 220, 220));
		contentPane.add(labelZiffer1, "cell 4 15,grow");
		
		labelZiffer3 = new JLabel("0.0 %");
		labelZiffer3.setForeground(new Color(0, 0, 0));
		labelZiffer3.setFont(new Font("Tahoma", Font.PLAIN, 18));
		labelZiffer3.setBackground(new Color(220, 220, 220));
		contentPane.add(labelZiffer3, "cell 4 16,grow");
		
		labelZiffer5 = new JLabel("0.0 %");
		labelZiffer5.setForeground(new Color(0, 0, 0));
		labelZiffer5.setFont(new Font("Tahoma", Font.PLAIN, 18));
		labelZiffer5.setBackground(new Color(220, 220, 220));
		contentPane.add(labelZiffer5, "cell 4 17,grow");
		
		labelZiffer7 = new JLabel("0.0 %");
		labelZiffer7.setForeground(new Color(0, 0, 0));
		labelZiffer7.setFont(new Font("Tahoma", Font.PLAIN, 18));
		labelZiffer7.setBackground(new Color(220, 220, 220));
		contentPane.add(labelZiffer7, "cell 4 18,grow");
		
		labelZiffer9 = new JLabel("0.0 %");
		labelZiffer9.setForeground(new Color(0, 0, 0));
		labelZiffer9.setFont(new Font("Tahoma", Font.PLAIN, 18));
		labelZiffer9.setBackground(new Color(220, 220, 220));
		contentPane.add(labelZiffer9, "cell 4 19,grow");
	}
}