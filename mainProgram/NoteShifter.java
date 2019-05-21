package mainProgram;

import jm.music.data.Note;

/**
 * Handles scales and pitch shifting
 */
public abstract class NoteShifter {
	/**
	 * Shift notes depending on the input settings. if settings.shiftAction is 0, does nothing.
	 * If settings.shiftAction is 1, shifts all scales and notes by settings.shiftValue.
	 * If settings.shiftAction is 2, shifts all scales and notes such that the first scale 
	 * is the scale indicated by settings.targetScale.
	 * @param notes Input notes array. Will be shifted based on settings.
	 * @param scales Input scales array. Will be shifted based on settings.
	 * @param settings Chosen settings
	 */
	public static void shift(Note[] notes, int[] scales, SettingsBundle settings)
	{
		int shiftAction = settings.shiftAction;
		int delta = -5;
		
		// shift none
		if (shiftAction == 0)
		{
			return;
		}
		// shift pitch
		else if (shiftAction == 1)
		{
			if ((settings.shiftValue == -100) || (settings.targetScale != -100)) throw new AssertionError("NoteShifter - Impossible settings 1");
			delta = settings.shiftValue;
		}
		// shift to match a particular scale
		else if (shiftAction == 2)
		{
			if ((settings.shiftValue != -100) || (settings.targetScale == -100)) throw new AssertionError("NoteShifter - Impossible settings 2");
			delta = settings.targetScale - scales[0];
			if (delta > 6) delta -= 12;
			else if (delta < -6) delta += 12;
		}
		if (notes.length != scales.length) throw new AssertionError("Noteshifter - unexpected array sizes");
		if (delta == -5) throw new AssertionError("Noteshifter - Unexpected delta");

		for (int i = 0; i < notes.length; i++)
		{			
			scales[i] = (12 + scales[i] + delta) % 12;
			if (notes[i].isRest()) continue;
			notes[i].setPitch(notes[i].getPitch() + delta);
		}
	}
}
