package mainProgram;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

import jm.gui.cpn.Notate;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

/**
 * This class is a collection of callbacks that are trigerred by MainFrame when buttons are clicked.
 * Currently callbackFileToNotation is the only callback. 
 */
public abstract class GuiCallbacks {
	
	/** Number of notes per notation line */
	private static int notesPerLine = 15;
	
	/**
	 * This method is called when hitting "analyze". It
	 * performs scale detection and shows the final music sheet. 
	 * @param The various settings to be used. Typically passed by the GUI class MainFrame.
	 */
	public static void callbackFileToNotation(SettingsBundle settings)
	{
		NoteGenerator noteGenerator;
		Score scr;
		Score playscr;
		
		if(settings.filePath.endsWith(".mid") || settings.filePath.endsWith(".midi"))
		{
			noteGenerator = new MidiLoader(settings.filePath);
		}
		else
		{
				noteGenerator = new TranscriptionLoader(settings.filePath);
		}
		
		try {
			noteGenerator.analyse();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Failed to read the file!\n" + e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
			return;
		} catch (UnsupportedAudioFileException e) {
			JOptionPane.showMessageDialog(null, "File not Supported!\n"+e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
			return;
		} catch (LineUnavailableException e) {
			JOptionPane.showMessageDialog(null, "Microphone unavailable\n"+e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		Note[] notes = noteGenerator.getNotes();
		if (notes.length == 0)
		{
			JOptionPane.showMessageDialog(null, noteGenerator.getNoNotesWarning(), "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		// Detect the scales and generate a scale array
		ScaleGenerator scaleGenerator = new ScaleGenerator(notes);
		scaleGenerator.analyze(settings);
		int[] intScales = scaleGenerator.getScales();
		
		NoteShifter.shift(notes, intScales, settings);
		
		JMusicScaleEnum[] jMusicScales = ScaleGenerator.getJMusicScalesFromIntScales(intScales, settings);
		
		notesPerLine = settings.notesPerLine;
		
		scr = notesToJMusicDisplay(notes, jMusicScales);
		
		playscr = notesToJmusicPlayerScore(notes);
		
		new Notate(scr, playscr);
	}
	
	/**
	 * Converts a notes array to a JMusic score object that is suitable for being played
	 * @param notes Input notes array
	 * @return Score object which contains the notes found in notes array
	 */
	public static Score notesToJmusicPlayerScore(Note[] notes)
	{
		if (notes.length == 0) return new Score();
		
		Score scr = new Score();
		Part prt = new Part();
		Phrase phr = new Phrase();
		
		//uncomment to remove time signature
		scr.setDenominator(0);
		scr.setNumerator(0);
		
		for(int i = 0; i < notes.length; i++)
		{
			/*// newline
			if (phr.length() == notesPerLine)
			{
				prt.add(phr);
				scr.addPart(prt);
				prt = new Part();
				phr = new Phrase();
			}*/
			
			phr.add(notes[i]);
		}
		
		//newline for leftovers
		/*if (phr.length() > 0)
		{
			prt.add(phr);
			scr.addPart(prt);
		}*/
		
		prt.add(phr);
		scr.addPart(prt);
		
		return scr;
	}
	
	/**
	 * Converts notes and scales arrays to a JMusic score object that can be displayed, both arrays should have the same length
	 * @param notes Input notes array
	 * @param scales Input scales array
	 * @return Score object which contains the notes found in notes array with corresponding scales
	 */
	public static Score notesToJMusicDisplay(Note[] notes, JMusicScaleEnum[] scales)
	{
		if (notes.length != scales.length) throw new AssertionError("Unexpected scale array length. notes length is " + notes.length + ", scale length is " + scales.length);
		if (notes.length == 0 || scales.length == 0) return new Score();
		Score scr = new Score();
		Part prt = new Part();
		Phrase phr = new Phrase();
		
		//uncomment to remove time signature
		scr.setDenominator(0);
		scr.setNumerator(0);
		
		JMusicScaleEnum prevScale = scales[0];
		prt.setKeySignature(scales[0].getValue());
		
		for(int i = 0; i < notes.length; i++)
		{
			// newline if line is full or if scale changed
			if ((phr.length() == notesPerLine) || (prevScale != scales[i]))
			{
				prt.add(phr);
				scr.addPart(prt);
				prt = new Part();
				phr = new Phrase();
				prt.setKeySignature(scales[i].getValue());
			}
			phr.add(notes[i]);
			prevScale = scales[i];
		}
		
		//newline for leftovers
		if (phr.length() > 0)
		{
			prt.add(phr);
			scr.addPart(prt);
		}
		return scr;
	}
	
}
