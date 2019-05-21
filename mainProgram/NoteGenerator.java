package mainProgram;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import jm.music.data.Note;

/**
 * NoteGenerator interface. Any class that can generate notes should implement this.
 * Currently: MidiLoader and TranscriptionLoader
 */
public interface NoteGenerator
{
	/**
	 * Analyze input file, to get results use getNotes() function
	 */
	public void analyse() throws IOException, UnsupportedAudioFileException, LineUnavailableException;
	/**
	 * Get analyzed notes array
	 */
	public Note[] getNotes();
	/**
	 * Get no notes warning string
	 */
	public String getNoNotesWarning();
}
