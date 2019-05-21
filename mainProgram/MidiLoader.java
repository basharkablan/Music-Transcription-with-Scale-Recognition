package mainProgram;

import java.util.ArrayList;

import jm.JMC;
import jm.music.data.*;
import jm.util.*;

/**
 * Loads midi file and extracts notes from it. 
 * 
 * Note: This class coes not extract key signature data/
 * Scales and scale changes are always produced by ScaleGenerator and never extracted from a MIDI.
 */
public class MidiLoader implements JMC, NoteGenerator {
	
    /** Analyzed notes array */
    private ArrayList<Note> notes = new ArrayList<Note>();
    
    /** Chosen file for notes analysis */
    private String filename;
    
    /** MidiLoader default constructor */
	public MidiLoader()
	{
		this.filename = "";
	}
	
	/**
	 * MidiLoader constructor
	 * @param filename Input file for notes analysis
	 */
	public MidiLoader(String filename)
	{
		this.filename = filename;
	}
	
	/**
	 * Analyze input file, to get the notes use getNotes() function after this function.
	 */
	@Override
	public void analyse()
	{
		Score scr = new Score("Score");
		Read.midi(scr, this.filename);
		
		if(scr.getPartArray().length > 0)
		{
			Phrase[] phrArr = scr.getPartArray()[0].getPhraseArray();
			
			for(int i = 0; i < phrArr.length; i++)
				for(int j = 0; j < phrArr[i].getNoteArray().length; j++)
				{
					notes.add(phrArr[i].getNoteArray()[j]);
				}
		}
	}

	/**
	 * Get analyzed notes array. Must call analyze() first.
	 */
	@Override
	public Note[] getNotes()
	{
		Note[] arr = new Note[notes.size()];
		int i = 0;
		
		for(Note note : notes)
		{
			arr[i] = note;
			i++;
		}
		return arr;
	}

	/**
	 * Get no notes warning string
	 */
	@Override
	public String getNoNotesWarning() {
		return "This MIDI file is empty!";
	}
	
}
