/*

<This Java Class is part of the jMusic API version 1.65, March 2017.>

Copyright (C) 2000 Andrew Brown and Andrew Sorensen

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or any
later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

*/ 

package jm.gui.cpn; 

import java.awt.event.*;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import gui.OutputFrame;

import java.awt.*;

import jm.music.data.*;
import jm.util.Play;
import jm.util.Read;
import jm.util.Write;
import jm.JMC;

/**
* This class displays a frame with a common practice notation display
* of the score passed to it.
* The parameter and add data by text attributes only work on the first stave.
* Some GPL changes for jMusic CPN Written by Al Christians 
* (achrist@easystreet.com).
* Contributed by Trillium Resources Corporation, Oregon's
* leading provider of unvarnished software.
* @author Andrew Brown
*/

public class Notate extends OutputFrame implements 
                ActionListener, 
                WindowListener, JMC {
    private Score score;
    private Score playScore;
    //private Stave stave;
    //private Phrase phrase;
    private Phrase[] phraseArray;
    private Stave[] staveArray;
    private int scrollHeight = 130, locationX = 0, locationY = 0;
    private Dialog keyDialog, timeDialog;
    private MenuItem keySig, open, openJmXml, openjm, play, stop, delete, clear, 
                    newStave, close, timeSig, saveJmXml, saveJM, saveMidi, quit,
                    trebleStave, bassStave, pianoStave, grandStave, automaticStave,
                    
                    // Some menu options added
                    appendMidiFile, 
                    insertMidiFile,
                    setParameters,
                    playAll,
                    playMeasure,
                    repeatAll,
                    repeatMeasure,
                    stopPlay,
                    earTrain,
                    addNotes,
                    adjustTiming, viewDetails, viewTitle,
                    viewZoom, barNumbers,
                    helpItem;
    				
    public boolean timeToStop;                 				
    // the panel for all the stave panels to go in to
    private Panel scoreBG;
    // the constraints for the scoreBG layout
    private GridBagConstraints constraints;
    private GridBagLayout layout;
    // The scoreBg goes into this scroll pane to enable navigation
    private ScrollPane scroll;

    private String lastFileName   = "*.mid";
    private String lastDirectory  = "";
    private String fileNameFilter = "*.mid";

    private boolean     zoomed;
    private Phrase      beforeZoom = new Phrase();
    private Phrase      afterZoom = new Phrase();
    /* The height of the notate window */
    private int height = 0;
    private int width = 700;
    
    private boolean playing = false;
    
    private HelpScreen hs;
    
    public Notate() {
        this(new Phrase(), 0, 0);
        clearZoom();
    }
    
    public Notate(int locX, int locY) {
        this(new Phrase(), locX, locY);
        clearZoom();
    }
    
    public Notate(Phrase phr) {
        this(phr, 0, 0);
        clearZoom();
    }
    
    private void clearZoom() {
        zoomed = false;
    }        

    public Notate(Phrase phrase, int locX, int locY) {
        super("CPN: "+ phrase.getTitle());
        clearZoom();
        this.score = new Score(new Part(phrase));
        locationX = locX;
        locationY = locY;
        score = new Score(new Part(phrase));
        init();
    }
    
    public Notate(Score score, int locX, int locY) {
        super("CPN: "+ score.getTitle());
        clearZoom();
        this.score = score;
        locationX = locX;
        locationY = locY;
        init();
    }
    
    public Notate(Score score, Score playScore) {
        super("CPN: "+ score.getTitle());
        clearZoom();
        this.score = score;
        this.playScore = playScore;
        init();
    }
    
    public void init() {
        addWindowListener(this);
        // menus
        MenuBar menus = new MenuBar();
        Menu edit     = new Menu("File", true);
        //Menu features = new Menu("Tools", true);
        Menu player   = new Menu("Play", true);
        //Menu view     = new Menu("View", true);
        Menu help     = new Menu("Help", true);
        
        close = new MenuItem("Close");
        close.addActionListener(this);
        edit.add(close);
        
        edit.add("-");
        
        quit = new MenuItem("Quit");
        quit.addActionListener(this);
        edit.add(quit);
        
        //------
        
        playAll = new MenuItem("Play All");
        playAll.addActionListener(this);
        player.add(playAll);
        
        stopPlay = new MenuItem("Stop Playback");
        stopPlay.addActionListener(this);
        player.add(stopPlay);
        
        //-------
        
        helpItem = new MenuItem("Help");
        helpItem.addActionListener(this);
        help.add(helpItem);
        
        
        //-------
        menus.add(edit);
        //menus.add(features);
        menus.add(player);
        //menus.add(view);
        menus.add(help);
        this.setMenuBar(menus);
        
        // components
        scroll = new ScrollPane(1);
       
        scroll.getHAdjustable().setUnitIncrement(10);
        scroll.getVAdjustable().setUnitIncrement(10);
        
        scoreBG = new Panel();
        layout = new GridBagLayout();
        scoreBG.setLayout(layout); //new GridLayout(score.size(), 1));
        constraints = new GridBagConstraints();
        setupConstraints();
        
        scroll.add(scoreBG);
        this.add(scroll); 
        
        setupArrays();
        makeAppropriateStaves();
               
        this.pack();
        this.setLocation(locationX, locationY);
        setExtendedState(MAXIMIZED_BOTH);
        this.show();
    }
    
    private void setupArrays() {
        // set up arrays
        phraseArray = new Phrase[score.size()];
        staveArray = new Stave[score.size()];

        for (int i=0; i<staveArray.length; i++) {
            phraseArray[i] = score.getPart(i).getPhrase(0);
            staveArray[i] = new PianoStave();
            staveArray[i].setKeySignature(score.getPart(i).getKeySignature());
            staveArray[i].setMetre(score.getNumerator());
            staveArray[i].setBarNumbers(true);
        }
    }
    
    private void setupConstraints() {
        constraints.weightx = 100;
        constraints.weighty = 0;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        //constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
    }
    
    private void calcHeight() {
        // work out the height
        height = 0;
        for (int i=0; i<staveArray.length; i++) {
            height += staveArray[i].getSize().height;
        }

    }
    
    private void makeAppropriateStaves(){
        Stave[] tempStaveArray  = new Stave[staveArray.length];
        for(int i=0; i<score.size(); i++) {
            Phrase currentPhrase = score.getPart(i).getPhrase(0);
            tempStaveArray[i] = new PianoStave();
        }
        updateAllStaves(tempStaveArray);

    }   

    private void makeTrebleStave() {
        Stave[] tempStaveArray  = new Stave[score.size()];
        for(int i=0; i<staveArray.length; i++) {
             tempStaveArray[i] = new TrebleStave();
        }
        updateAllStaves(tempStaveArray);
    }
    
    private void updateAllStaves(Stave[] tempStaveArray) {
        int gridyVal = 0;
        int gridheightVal = 0;
        int totalHeight = 0;
        scoreBG.removeAll();
        for(int i=0; i<staveArray.length; i++) {
            // store current phrase parameters in new stave object
            tempStaveArray[i].setKeySignature(staveArray[i].getKeySignature());
            tempStaveArray[i].setMetre(staveArray[i].getMetre());
            tempStaveArray[i].setBarNumbers(staveArray[i].getBarNumbers());
            tempStaveArray[i].setPhrase(phraseArray[i]);
            // create new stave panel
            staveArray[i] = tempStaveArray[i];
            tempStaveArray[i] = null;
            // set and add constraints
            constraints.gridy = gridyVal;
            if(staveArray[i].getClass().isInstance(new TrebleStave()) || 
                staveArray[i].getClass().isInstance(new BassStave())) {
                    gridheightVal = 1;
            } else if(staveArray[i].getClass().isInstance(new PianoStave())) {
                gridheightVal = 2;
            } else {
                gridheightVal = 3;
            }
            constraints.gridheight = gridheightVal;
            // add to display
            scoreBG.add(staveArray[i], constraints);
            gridyVal += gridheightVal;
            totalHeight += staveArray[i].getPanelHeight();
        }
        //calcHeight();
        scroll.setSize(new Dimension(width, totalHeight));
        // check window size against screen
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        this.setSize(new Dimension(this.width, Math.min(d.height-40, totalHeight+40)));
        //this.setResizable(true);
        this.pack();
    }        

    private void makeBassStave() {
        Stave[] tempStaveArray  = new Stave[score.size()];
        for(int i=0; i<staveArray.length; i++) {
             tempStaveArray[i] = new BassStave();
        }
        updateAllStaves(tempStaveArray);
    }
    
    private void makePianoStave() {
        Stave[] tempStaveArray  = new Stave[score.size()];
        for(int i=0; i<tempStaveArray.length; i++) {
             tempStaveArray[i] = new PianoStave();
        }
        updateAllStaves(tempStaveArray);
    }
        
    private void makeGrandStave() {
        Stave[] tempStaveArray  = new Stave[score.size()];
        for(int i=0; i<tempStaveArray.length; i++) {
             tempStaveArray[i] = new GrandStave();
        }
        updateAllStaves(tempStaveArray);
    }
    
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == close) dispose();
        
        else if(e.getSource() == quit) System.exit(0);
        
        else if( e.getSource() == playAll )  {
        	if(this.playing)
        	{
            	Play.stopMidi(); // stop a single playback
            	Play.stopMidiCycle(); // stop a cycle playback
        	}
            Play.midi(playScore);
            playing = true;
        }
        
        else if(e.getSource() == stopPlay) {
        	
        	Play.stopMidi(); // stop a single playback
        	Play.stopMidiCycle(); // stop a cycle playback
        	playing = false;
        }
        
        else if(e.getSource() == helpItem)
        {
        	if(hs == null)
        	{
		    	hs = new HelpScreen();
		    	hs.setAlwaysOnTop(true);
		    	hs.setVisible(true);
        	}
        	else
        		hs.setVisible(true);
        }
                  
    }
    
    /**
    * Dialog to import a MIDI file
    */
     public void openMidi() {
        Score s = new Score();
        FileDialog loadMidi = new FileDialog(this, "Select a MIDI file.", FileDialog.LOAD);
        loadMidi.setDirectory( lastDirectory );
        loadMidi.setFile( lastFileName );
        loadMidi.show();
        String fileName = loadMidi.getFile();
        if (fileName != null) {
            lastFileName = fileName;
            lastDirectory = loadMidi.getDirectory();                        
            Read.midi(s, lastDirectory + fileName);  
            setNewScore(s);
        }
    }
     
     public Score midiToScore() {
         Score s = new Score();
         FileDialog loadMidi = new FileDialog(this, "Select a MIDI file.", FileDialog.LOAD);
         loadMidi.setDirectory( lastDirectory );
         loadMidi.setFile( lastFileName );
         loadMidi.show();
         String fileName = loadMidi.getFile();
         if (fileName != null) {
             lastFileName = fileName;
             lastDirectory = loadMidi.getDirectory();                        
              
             return s;
         }
         return null;
     }
    
    
    private void setNewScore(Score score) {
        this.score = score;
        // set up arrays
        setupArrays();        
        makeAppropriateStaves();
    }
    
    /**
     * Dialog to import a jm file
     */
     
     public void openJM() {
        FileDialog loadjm = new FileDialog(this, "Select a jm score file.", FileDialog.LOAD);
        loadjm.setDirectory( lastDirectory );
        loadjm.show();
        String fileName = loadjm.getFile();
        if (fileName != null) {
            Score s = new Score();
            lastDirectory = loadjm.getDirectory();  
            Read.jm(s, lastDirectory + fileName);
            setNewScore(s);
        }
    }
    
    /**
     * Dialog to import a jm XML file
     */
     
     public void openJMXML() {
        FileDialog loadjmxml = new FileDialog(this, "Select a jMusic XML score file.", FileDialog.LOAD);
        loadjmxml.setDirectory( lastDirectory );
        loadjmxml.show();
        String fileName = loadjmxml.getFile();
        if (fileName != null) {
            Score s = new Score();
            lastDirectory = loadjmxml.getDirectory(); 
            Read.xml(s, lastDirectory + fileName);
            setNewScore(s);
        }
    }
    

    
    /**
     * Dialog to save phrase as a MIDI file.
     */
    public void saveMidi() {
		JFileChooser fc = new JFileChooser() {
        	@Override
            public void approveSelection()
        	{
	            File f = getSelectedFile();
	            if(f.exists() && getDialogType() == SAVE_DIALOG)
	            {
	                int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
	                switch(result)
	                {
	                    case JOptionPane.YES_OPTION:
	                        super.approveSelection();
	                        Write.midi(playScore, f.getAbsolutePath());
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
	            if(f.getParentFile().exists())
	            	Write.midi(playScore, f.getPath() + ".mid");
	            else
	            	JOptionPane.showMessageDialog(this, "File not saved !", "Error", JOptionPane.ERROR_MESSAGE);
	        }
        };
        
        fc.setFileFilter(new FileFilter() {
        	
		    public String getDescription() {
		        return "*.mid - Midi File";
		    }
		    
		    public boolean accept(File f) {
		        if (f.isDirectory()) {
		            return true;
		        } else {
		            return (f.getName().toLowerCase().endsWith(".mid") ||
		            		f.getName().toLowerCase().endsWith(".midi"));
		        }
		    }
		});
        
        fc.showSaveDialog(this);
        
    }
    
    /**
     * Dialog to save score as a jMusic serialized jm file.
     */
    public void saveJM() {
        FileDialog fd = new FileDialog(this, "Save as a jm file...",FileDialog.SAVE);
                fd.show();
                            
        //write a MIDI file to disk
        if ( fd.getFile() != null) {
            Write.jm(score, fd.getDirectory()+fd.getFile());
        }
    }
    
    /**
     * Dialog to save score as a jMusic XML file.
     */
    public void saveJMXML() {
        FileDialog fd = new FileDialog(this, "Save as a jMusic XML file...",FileDialog.SAVE);
                fd.show();
                            
        //write an XML file to disk
        if ( fd.getFile() != null) {
            Write.xml(score, fd.getDirectory()+fd.getFile());
        }
    }

    
    /**
    * Get the first phrase from a MIDI file.
    */
    public Phrase readMidiPhrase() {
        FileDialog loadMidi = new FileDialog(this, "Select a MIDI file.", FileDialog.LOAD);
        loadMidi.show();
        String fileName = loadMidi.getFile();
        Phrase phr = new Phrase(0.0);
        Score scr = new Score();
        if (fileName != null) {
            Read.midi(scr, loadMidi.getDirectory() + fileName); 
        }
        scr.clean();
        if (scr.size() > 0 && scr.getPart(0).size() > 0) phr = scr.getPart(0).getPhrase(0);
        //System.out.println("Size = " + phr.size());
        return phr;
    }
    
    private Score getLastMeasure() {
        double beats = phraseArray[0].getNumerator();
        double endTime = score.getEndTime();
        int numbOfCompleteBars = (int)(endTime / beats);
        double startOflastBar = beats * numbOfCompleteBars;
        if (startOflastBar == endTime) startOflastBar -= beats;
        Score oneBar = score.copy(startOflastBar, endTime);
        
        for(int i=0; i<oneBar.size();i++){
            oneBar.getPart(i).getPhrase(0).setStartTime(0.0);
        }
        return oneBar;
    }
    
    private static double getRhythmAdjustment(
                        double  beats,
                        double  beatIncrement ) {
        double increments;
        increments = beats/beatIncrement;
        double tolerance;                                   
        tolerance = 0.00001;
        double answer;  
        answer = 0.0;
        double n;
        n = Math.floor(increments);
        while(( Math.floor(increments+tolerance) > n ) 
                && (tolerance > 0.00000000000001)) {
            answer = tolerance;
            tolerance = tolerance / 2;
        }                        
        return answer * beatIncrement;                            
    }                            
    
    private static void adjustTimeValues(Phrase phr) {
        int i;
        double t, dt, st;
        for( i = 0; i < phr.size(); ++i) {        
            t  = phr.getNote(i).getDuration();
            dt = getRhythmAdjustment( t, 1.0 / 256.0 ); 
            phr.getNote(i).setDuration(t+dt);
        }
        
        st = 0.0;
        for( i = 0; i < phr.size(); ++i) {        
            t  = phr.getNote(i).getDuration();
            st = st + t;
            dt = getRhythmAdjustment( st, 1.0 ); 
            phr.getNote(i).setDuration(t+dt);
            st = st + dt;
        }
    }  
    
    /**
    * Toggle the phrase title display
    */
    public void toggleDisplayTitle() {
        for(int i=0; i<staveArray.length; i++) {
            staveArray[i].setDisplayTitle(!staveArray[i].getDisplayTitle());
        }
    }
    
    /**
     * Invoked when a window has been opened.
     */
    public void windowOpened(WindowEvent e) {
    }

    /**
     * Invoked when a window is in the process of being closed.
     * The close operation can be overridden at this point.
     */
    public void windowClosing(WindowEvent e) {
        if(e.getSource() == this) dispose();
        if(e.getSource() == keyDialog) keyDialog.dispose();
        if(e.getSource() == timeDialog) timeDialog.dispose();
        if(hs != null) hs.dispose();
    }

    

    /**
     * Invoked when a window has been closed.
     */
    public void windowClosed(WindowEvent e) {
    	Play.stopMidi(); // stop a single playback
    	Play.stopMidiCycle(); // stop a cycle playback
    	playing = false;
    }

    /**
     * Invoked when a window is iconified.
     */
    public void windowIconified(WindowEvent e) {
    }

    /**
     * Invoked when a window is de-iconified.
     */
    public void windowDeiconified(WindowEvent e) {
    }

    /**
     * Invoked when a window is activated.
     */
    public void windowActivated(WindowEvent e) {
    }

    /**
     * Invoked when a window is de-activated.
     */
    public void windowDeactivated(WindowEvent e) {
    }
}
