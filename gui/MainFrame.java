package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;

import java.awt.Dimension;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.filechooser.FileFilter;

import mainProgram.GuiCallbacks;
import mainProgram.Scale;
import mainProgram.SettingsBundle;

import java.awt.BorderLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Image;

import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.border.BevelBorder;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JScrollPane;

/**
 * MainFrame GUI, contains load file, settings and help 
 */
public class MainFrame {

	
	// ----- MainFrame
	/** MainFrame Frame */
	private JFrame frmMain;
	// ----- MainFrame
	
	// ----- Tabs
	/** Tabs container */
	private JTabbedPane tabbedPane;
	
	/** Load file tab */
	private JPanel tabLoadFile;
	/** Settings tab */
	private JPanel tabSettings;
	/** Help tab */
	private JPanel tabHelp;
	// ----- Tabs
	
	
	// ----- Load File Tab
	/** Transcription button */
	private JButton btnTranscribe;
	/** Browse file button */
	private JButton btnBrowse;
	/** Exit button */
	private JButton btnExit;
	
	/** Background image label */
	private JLabel lblImage; 
	/** Choose file path label */
	private JLabel lblFilePath;
	
	/** Chosen file path textField */
	private JTextField textFilePath;
	
	/** File chooser object */
	private JFileChooser fileChooser;
	
	/** Path of background image */
	private String imgPath = "/res/Notes.png";
	// ----- Load File Tab
	
	
	// ----- Settings Tab
	/** Match cost value */
	private JTextField textFieldMatch;
	/** Accidental cost value */
	private JTextField textFieldAccidental;
	/** Scale change cost value */
	private JTextField textFieldScaleChange;
	
	/** Prefer D-Flat scale over C-Sharp */
	private JRadioButton rdbtnPreferDFlat;
	/** Prefer G-Flat scale over F-Sharp */
	private JRadioButton rdbtnPreferGFlat;
	/** Prefer C-Flat scale over B */
	private JRadioButton rdbtnPreferCFlat;
	/** Prefer F-Sharp scale over G-Flat */
	private JRadioButton rdbtnPreferFSharp;
	/** Prefer C-Sharp scale over D-Flat */
	private JRadioButton rdbtnPreferCSharp;
	/** Prefer B scale over C-Flat */
	private JRadioButton rdbtnPreferB;
	/** Scale recognition only */
	private JRadioButton rdbtnNone;
	/** Shift recognized scale by x */
	private JRadioButton rdbtnPitchShift;
	/** Change recognized scale to custom scale */
	private JRadioButton rdbtnScaleCustom;
	
	/** Choose custom scale to change to */
	private JComboBox comboBoxScales;
	/** Choose pitch shift amount */
	private JComboBox comboBoxShift;
	private JScrollPane scrollPane;
	private JTextArea txtrTodo;
	private JTextField textFieldNotesPerLine;
	// ----- Help Tab
	
	/**
	 * Start MainFrame GUI thread
	 */
	public static void run() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window.frmMain.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMain = new JFrame();
		frmMain.setMinimumSize(new Dimension(610, 410));
		frmMain.setTitle("Music Transcription With Scale Recognition");
		frmMain.setBounds(100, 100, 600, 400);
		frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		fileChooser.setFileFilter(new FileFilter() {
			
		    public String getDescription() {
		        return "Supported sound file (*.mid, *.wav)";
		    }
		    
		    public boolean accept(File f) {
		        if (f.isDirectory()) {
		            return true;
		        } else {
		            return (f.getName().toLowerCase().endsWith(".mid") ||
		            		f.getName().toLowerCase().endsWith(".mp3"));
		        }
		    }
		});
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		btnTranscribe = new JButton("Analyze");
		btnTranscribe.setToolTipText("Runs the algorithm and produces the output sheet music");
		btnTranscribe.setEnabled(false);
		
		btnExit = new JButton("Exit");
		btnExit.setToolTipText("Closes the program entirely");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnExit.setMinimumSize(new Dimension(83, 23));
		btnExit.setMaximumSize(new Dimension(83, 23));
		btnExit.setPreferredSize(new Dimension(83, 23));
		GroupLayout groupLayout = new GroupLayout(frmMain.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(167)
					.addComponent(btnTranscribe, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
					.addGap(50)
					.addComponent(btnExit, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
					.addGap(151))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnExit, GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
						.addComponent(btnTranscribe, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
					.addGap(17))
		);
		
		tabLoadFile = new JPanel();
		tabbedPane.addTab("Load File", null, tabLoadFile, null);
		tabLoadFile.setLayout(new BorderLayout(0, 0));
		
		JPanel panelFilePath = new JPanel();
		tabLoadFile.add(panelFilePath);
		
		
		btnBrowse = new JButton("Browse..");
		btnBrowse.setToolTipText("Selects a WAV or a MIDI file for analysis");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int result = fileChooser.showOpenDialog(tabLoadFile);
				
				if(result == JFileChooser.APPROVE_OPTION)
				{
					textFilePath.setText(fileChooser.getSelectedFile().getPath());
					btnTranscribe.setEnabled(true);
				}
			}
		});
		
		textFilePath = new JTextField();
		textFilePath.setToolTipText("This is the filepath of the selected file");
		textFilePath.setEditable(false);
		textFilePath.setColumns(10);
		
		lblFilePath = new JLabel("File Path:");
		
		
		lblImage = new JLabel("");
		lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblImage.setIcon(new ImageIcon(MainFrame.class.getResource(imgPath)));
		lblImage.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		lblImage.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				ImageIcon MyImage = new ImageIcon(MainFrame.class.getResource(imgPath));
				Image img = MyImage.getImage();
				Image newImage = img.getScaledInstance(lblImage.getWidth(), lblImage.getHeight(), Image.SCALE_SMOOTH);
				ImageIcon image = new ImageIcon(newImage);
				lblImage.setIcon(image);
			}
		});
		
		
		
		GroupLayout gl_panelFilePath = new GroupLayout(panelFilePath);
		gl_panelFilePath.setHorizontalGroup(
			gl_panelFilePath.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelFilePath.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelFilePath.createParallelGroup(Alignment.LEADING)
						.addComponent(lblImage, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 559, Short.MAX_VALUE)
						.addGroup(gl_panelFilePath.createSequentialGroup()
							.addComponent(lblFilePath)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textFilePath, GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
							.addGap(10)
							.addComponent(btnBrowse)))
					.addContainerGap())
		);
		gl_panelFilePath.setVerticalGroup(
			gl_panelFilePath.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelFilePath.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblImage, GroupLayout.PREFERRED_SIZE, 235, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelFilePath.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblFilePath)
						.addComponent(textFilePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowse))
					.addContainerGap())
		);
		panelFilePath.setLayout(gl_panelFilePath);
		
		tabSettings = new JPanel();
		tabbedPane.addTab("Settings", null, tabSettings, null);
		tabSettings.setLayout(new BorderLayout(0, 0));
		
		JPanel panetSettings = new JPanel();
		tabSettings.add(panetSettings, BorderLayout.CENTER);
		
		String matchTooltip = "Selects the pathfinding penalty of a non-accidental note. See our paper for details. Usually should be 0.";
		String accidentalTooltip = "Selects the pathfinding penalty of finding an accidental. See our paper for details. Usually should be 1.";
		String scaleChangeTooltip = "Selects the pathfinding penalty of switching a scale. Higher values discourage scale switching, lower values encourage it. See our paper for details.";
				
		JLabel lblMatch = new JLabel("Match :");
		lblMatch.setToolTipText(matchTooltip);
		
		JLabel lblAccidental = new JLabel("Accidental :");
		lblAccidental.setToolTipText(accidentalTooltip);
		
		JLabel lblScaleChange = new JLabel("Scale Change :");
		lblScaleChange.setToolTipText(scaleChangeTooltip);
		
		textFieldMatch = new JTextField();
		textFieldMatch.setToolTipText(matchTooltip);
		textFieldMatch.setText("0");
		textFieldMatch.setColumns(10);
		
		textFieldAccidental = new JTextField();
		textFieldAccidental.setToolTipText(accidentalTooltip);
		textFieldAccidental.setText("1");
		textFieldAccidental.setColumns(10);
		
		textFieldScaleChange = new JTextField();
		textFieldScaleChange.setToolTipText(scaleChangeTooltip);
		textFieldScaleChange.setText("6");
		textFieldScaleChange.setColumns(10);
		
		JLabel lblCosts = new JLabel("Costs :");
		lblCosts.setToolTipText("This section allows you to specify the pathfinder penalties. The pathfinder selects key signature sequences that give the least penalties (shortest path)");
		lblCosts.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		rdbtnPreferDFlat = new JRadioButton("D\u266D Major");
		rdbtnPreferGFlat = new JRadioButton("G\u266D Major");
		rdbtnPreferCFlat = new JRadioButton("C\u266D Major");
		rdbtnPreferFSharp = new JRadioButton("F\u266F Major");
		rdbtnPreferB = new JRadioButton("B Major");
		rdbtnPreferCSharp = new JRadioButton("C\u266F Major");

		String prefferedKeySignatures = "Some key signatures are equivalent and the " +
		"difference is a matter of style. This section allows you to choose the preferred ones.";
		
		rdbtnPreferDFlat.setToolTipText(prefferedKeySignatures); 
		rdbtnPreferGFlat.setToolTipText(prefferedKeySignatures); 
		rdbtnPreferCFlat.setToolTipText(prefferedKeySignatures); 
		rdbtnPreferFSharp.setToolTipText(prefferedKeySignatures); 
		rdbtnPreferB.setToolTipText(prefferedKeySignatures); 
		rdbtnPreferCSharp.setToolTipText(prefferedKeySignatures); 

		ButtonGroup rbgroup1 = new ButtonGroup();
		rbgroup1.add(rdbtnPreferDFlat);
		rbgroup1.add(rdbtnPreferCSharp);
		
		ButtonGroup rbgroup2 = new ButtonGroup();
		rbgroup2.add(rdbtnPreferFSharp);
		rbgroup2.add(rdbtnPreferGFlat);
		
		ButtonGroup rbgroup3 = new ButtonGroup();
		rbgroup3.add(rdbtnPreferB);
		rbgroup3.add(rdbtnPreferCFlat);
		
		JLabel lblPrefers = new JLabel("Preferred key signatures :");
		lblPrefers.setToolTipText(prefferedKeySignatures);
		lblPrefers.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		rdbtnPreferDFlat.setSelected(true);
		rdbtnPreferFSharp.setSelected(true);
		rdbtnPreferB.setSelected(true);

		
		JLabel lblScale = new JLabel("Pitch Shift :");
		lblScale.setToolTipText("This section allows you to optionally shift the original pitch.");
		lblScale.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		comboBoxScales = new JComboBox();
		//comboBoxScales.setModel(new DefaultComboBoxModel(new String[] {"C\u266D Major", "G\u266D Major", "D\u266D Major", "A\u266D Major", "E\u266D Major", "B\u266D Major", "F Major", "C Major", "G Major", "D Major", "A Major", "E Major", "B Major", "F\u266F Major", "C\u266F Major"}));
		Scale[] allScales = new Scale[12];
		for (int i = 0; i < allScales.length; i++)
		{
			allScales[i] = new Scale(i);
		}
		String customPitchShift = "Shifts a pitch by the specified amount";
		String customScaleTooltip = "Auto-shifts pitch so that the first detected scale in the original composition is shifted into the specified scale";
		
		comboBoxScales.setModel(new DefaultComboBoxModel(allScales));
		comboBoxScales.setToolTipText(customScaleTooltip);
		comboBoxScales.setEnabled(false);
		
		comboBoxShift = new JComboBox();
		comboBoxShift.setModel(new DefaultComboBoxModel(new String[] {"-11", "-10", "-9", "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"}));
		comboBoxShift.setSelectedIndex(11);
		comboBoxShift.setEnabled(false);
		comboBoxShift.setToolTipText(customPitchShift);
		
		rdbtnNone = new JRadioButton("None");
		rdbtnNone.setToolTipText("Disables pitch shifting. This is the default.");
		rdbtnPitchShift = new JRadioButton("Pitch Shift :");
		rdbtnPitchShift.setToolTipText(customPitchShift);
		rdbtnScaleCustom = new JRadioButton("Custom Scale :");
		rdbtnScaleCustom.setToolTipText(customScaleTooltip);
		
		rdbtnNone.setSelected(true);
		
		ItemListener itemListener = new ItemListener() {
		      public void itemStateChanged(ItemEvent itemEvent) {
		    	  if(itemEvent.getSource() == rdbtnNone)
		    	  {
		    		  if(itemEvent.getStateChange() == ItemEvent.SELECTED)
		    		  {
		    			  comboBoxShift.setEnabled(false);
		    			  comboBoxScales.setEnabled(false);
		    		  }
		    	  }
		    	  else if(itemEvent.getSource() == rdbtnPitchShift)
		    	  {
		    		  if(itemEvent.getStateChange() == ItemEvent.SELECTED)
		    		  {
		    			  comboBoxShift.setEnabled(true);
		    			  comboBoxScales.setEnabled(false);
		    		  }
		    	  }
		    	  else if(itemEvent.getSource() == rdbtnScaleCustom)
		    	  {
		    		  if(itemEvent.getStateChange() == ItemEvent.SELECTED)
		    		  {
		    			  comboBoxShift.setEnabled(false);
		    			  comboBoxScales.setEnabled(true);
		    		  }
		    	  }
		      }
		};
		
		rdbtnNone.addItemListener(itemListener);
		rdbtnPitchShift.addItemListener(itemListener);
		rdbtnScaleCustom.addItemListener(itemListener);
		
		
		ButtonGroup rbgScales = new ButtonGroup();
		rbgScales.add(rdbtnNone);
		rbgScales.add(rdbtnPitchShift);
		rbgScales.add(rdbtnScaleCustom);
		
		JLabel lblOutput = new JLabel("Output:");
		lblOutput.setToolTipText("This section allows you to optionally shift the original pitch.");
		lblOutput.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		JLabel lblNotesPerLine = new JLabel("Notes per line :");
		lblNotesPerLine.setToolTipText("Number of notes showing in the output, per notation line.\r\nMinimum of 4, and Maximum of 50.");
		
		textFieldNotesPerLine = new JTextField();
		textFieldNotesPerLine.setToolTipText("Number of notes showing in the output, per notation line.\r\nMinimum of 4, and Maximum of 50.");
		textFieldNotesPerLine.setText("15");
		textFieldNotesPerLine.setColumns(10);
		
		GroupLayout gl_panetSettings = new GroupLayout(panetSettings);
		gl_panetSettings.setHorizontalGroup(
			gl_panetSettings.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panetSettings.createSequentialGroup()
					.addGroup(gl_panetSettings.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panetSettings.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panetSettings.createParallelGroup(Alignment.LEADING)
								.addComponent(lblCosts)
								.addGroup(gl_panetSettings.createSequentialGroup()
									.addGap(10)
									.addGroup(gl_panetSettings.createParallelGroup(Alignment.LEADING)
										.addComponent(lblAccidental)
										.addGroup(gl_panetSettings.createSequentialGroup()
											.addGroup(gl_panetSettings.createParallelGroup(Alignment.LEADING)
												.addComponent(lblScaleChange)
												.addComponent(lblMatch))
											.addGap(10)
											.addGroup(gl_panetSettings.createParallelGroup(Alignment.LEADING, false)
												.addComponent(textFieldMatch)
												.addComponent(textFieldAccidental)
												.addComponent(textFieldScaleChange, GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)))))
								.addComponent(lblPrefers))
							.addGap(24)
							.addGroup(gl_panetSettings.createParallelGroup(Alignment.LEADING)
								.addComponent(lblOutput, GroupLayout.PREFERRED_SIZE, 178, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblScale, GroupLayout.PREFERRED_SIZE, 178, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_panetSettings.createSequentialGroup()
									.addGap(10)
									.addGroup(gl_panetSettings.createParallelGroup(Alignment.LEADING, false)
										.addComponent(rdbtnNone)
										.addGroup(gl_panetSettings.createSequentialGroup()
											.addGroup(gl_panetSettings.createParallelGroup(Alignment.LEADING)
												.addComponent(rdbtnScaleCustom)
												.addComponent(rdbtnPitchShift))
											.addPreferredGap(ComponentPlacement.RELATED)
											.addGroup(gl_panetSettings.createParallelGroup(Alignment.LEADING, false)
												.addComponent(comboBoxScales, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(comboBoxShift, 0, 127, Short.MAX_VALUE)))
										.addGroup(Alignment.TRAILING, gl_panetSettings.createSequentialGroup()
											.addGap(9)
											.addComponent(lblNotesPerLine, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(textFieldNotesPerLine, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE))))))
						.addGroup(gl_panetSettings.createSequentialGroup()
							.addGap(56)
							.addGroup(gl_panetSettings.createParallelGroup(Alignment.LEADING, false)
								.addComponent(rdbtnPreferFSharp, GroupLayout.PREFERRED_SIZE, 49, Short.MAX_VALUE)
								.addComponent(rdbtnPreferB, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(rdbtnPreferDFlat, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panetSettings.createParallelGroup(Alignment.LEADING, false)
								.addComponent(rdbtnPreferCFlat, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(rdbtnPreferGFlat, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(rdbtnPreferCSharp, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
					.addGap(26))
		);
		gl_panetSettings.setVerticalGroup(
			gl_panetSettings.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panetSettings.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panetSettings.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCosts)
						.addComponent(lblScale, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panetSettings.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblMatch)
						.addComponent(textFieldMatch, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(rdbtnNone))
					.addGroup(gl_panetSettings.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panetSettings.createSequentialGroup()
							.addGap(18)
							.addGroup(gl_panetSettings.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblAccidental)
								.addComponent(textFieldAccidental, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(18)
							.addGroup(gl_panetSettings.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblScaleChange)
								.addComponent(textFieldScaleChange, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(18)
							.addGroup(gl_panetSettings.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblPrefers, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblOutput, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE))
							.addGap(14)
							.addGroup(gl_panetSettings.createParallelGroup(Alignment.BASELINE)
								.addComponent(rdbtnPreferDFlat)
								.addComponent(rdbtnPreferCSharp)
								.addComponent(textFieldNotesPerLine, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNotesPerLine))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panetSettings.createParallelGroup(Alignment.BASELINE)
								.addComponent(rdbtnPreferFSharp)
								.addComponent(rdbtnPreferGFlat))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panetSettings.createParallelGroup(Alignment.BASELINE)
								.addComponent(rdbtnPreferB)
								.addComponent(rdbtnPreferCFlat)))
						.addGroup(gl_panetSettings.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panetSettings.createParallelGroup(Alignment.BASELINE)
								.addComponent(rdbtnPitchShift)
								.addComponent(comboBoxShift, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panetSettings.createParallelGroup(Alignment.BASELINE)
								.addComponent(rdbtnScaleCustom)
								.addComponent(comboBoxScales, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap(25, Short.MAX_VALUE))
		);
		panetSettings.setLayout(gl_panetSettings);
		
		tabHelp = new JPanel();
		tabbedPane.addTab("Help", null, tabHelp, null);
		tabHelp.setLayout(new BorderLayout(0, 0));
		
		JPanel panelHelp = new JPanel();
		tabHelp.add(panelHelp, BorderLayout.CENTER);
		panelHelp.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		panelHelp.add(scrollPane, BorderLayout.CENTER);
		
		txtrTodo = new JTextArea();
		txtrTodo.setWrapStyleWord(true);
		txtrTodo.setText("== INTRO & FEATURES ==\r\nThis program analyzes a WAV or a MIDI file and outputs a sheet music with the proper key signature detected. In case of compositions with varying signatures, the program is capable of detecting where the key signature changes and will produce output accordingly. Sheet music is rendered with JMusic.\r\n\r\n== LIMITATIONS ==\r\nOnly monophonic music is supported. WAV files are transcribed with TarsosDSP. Transcription is not 100% accurate due to inherent unsolved computer science problems.\r\n\r\n== TABS & WINDOWS==\r\nUse the 'settings' tab to tweak the program parameters, and the 'load file' tab to select a file. Click 'analyze' to run the algorithm and make the output window appear.\r\n\r\n== OUTPUT WINDOW ==\r\nAfter clicking 'analyze' the output music sheet along with proper key signatures, will appear in a new window.\r\n\r\n== FURTHER HELP==\r\nFor specific help, most widgets, textboxes, and controls have a help tooltip which appears when hoverinxg your cursor over them.\r\n\r\n== CREDITS==\r\nThis is our final project. Ort Braude, 2018 Winter. Special thanks to our supervisor Dr. Orly Yahlom. Programmed by Bashar Kablan and Safwat Halaby.");
		txtrTodo.setLineWrap(true);
		txtrTodo.setEditable(false);
		scrollPane.setViewportView(txtrTodo);
		frmMain.getContentPane().setLayout(groupLayout);
		
		btnTranscribe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if(fileChooser.getSelectedFile() != null && fileChooser.getSelectedFile().exists())
				{
					int shiftValue;
					int costMatch;
					int costAccidental;
					int costScaleChange;
					int notesPerLine;
					
					try {
						costMatch = Integer.parseInt(textFieldMatch.getText());
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Match cost should be an integer.", "Error", JOptionPane.ERROR_MESSAGE); 
						return;
					}
					
					try {
						costAccidental = Integer.parseInt(textFieldAccidental.getText());
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Accidental cost should be an integer.", "Error", JOptionPane.ERROR_MESSAGE); 
						return;
					}
					
					try {
						costScaleChange = Integer.parseInt(textFieldScaleChange.getText());
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Scale change cost should be an integer.", "Error", JOptionPane.ERROR_MESSAGE); 
						return;
					}
					
					try {
						notesPerLine = Integer.parseInt(textFieldNotesPerLine.getText());
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Notes per line should be an integer.", "Error", JOptionPane.ERROR_MESSAGE); 
						return;
					}
					
					if(costMatch < 0)
					{
						JOptionPane.showMessageDialog(null, "Match cost should have a non-negative value.", "Error", JOptionPane.ERROR_MESSAGE); 
						return;
					}
					if(costAccidental < 0)
					{
						JOptionPane.showMessageDialog(null, "Accidental cost should have a non-negative value.", "Error", JOptionPane.ERROR_MESSAGE); 
						return;
					}
					if(costScaleChange < 0)
					{
						JOptionPane.showMessageDialog(null, "Scale change cost should have a non-negative value.", "Error", JOptionPane.ERROR_MESSAGE); 
						return;
					}
					if(notesPerLine < 4 || notesPerLine > 50)
					{
						JOptionPane.showMessageDialog(null, "Notes per line should have a value between 4 and 50.", "Error", JOptionPane.ERROR_MESSAGE); 
						return;
					}
					
					try {
						shiftValue = Integer.parseInt((String)comboBoxShift.getSelectedItem());
					} catch (Exception e)
					{
						throw new RuntimeException("A scaleShift item has a non number");
					}
					int scaleChange = ((Scale)comboBoxScales.getSelectedItem()).id;
					int shiftAction = 0; 
					if (rdbtnPitchShift.isSelected()) shiftAction = 1;
					else if (rdbtnScaleCustom.isSelected()) shiftAction = 2;
					
					SettingsBundle settingsBundle = new SettingsBundle(fileChooser.getSelectedFile().getPath(),
							costMatch, costAccidental, costScaleChange, notesPerLine,
							rdbtnPreferDFlat.isSelected(), rdbtnPreferGFlat.isSelected(), rdbtnPreferCFlat.isSelected(),
							shiftAction, shiftValue, scaleChange);
					GuiCallbacks.callbackFileToNotation(settingsBundle);
				}
			}
		});
		
	}
}
