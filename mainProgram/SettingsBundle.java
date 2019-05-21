package mainProgram;

/** This class stores the settings from the GUI. When a user clicks "analyze"
 * this class is instantiated. The instance is passed to any object
 * interested in reading any settings. Such as filepath, algorithm parameters, display parameters, etc. */
public class SettingsBundle {
	/** Input file path */
	public String filePath;
	
	// algorithm settings
	/** Matching scale cost */
	public int costMatch;
	/** Accidental in scale cost */
	public int costAccidental;
	/** Scale change cost */
	public int costScaleChange;
	/** Number of notes per notation line */
	public int notesPerLine;
	
	// scale preference settings
	/** Corresponds to preferred scales in MainFrame */
	public boolean preferDFlat;
	/** Corresponds to preferred scales in MainFrame */
	public boolean preferGFlat;
	/** Corresponds to preferred scales in MainFrame */
	public boolean preferCFlat;
	
	// shifting settings
	/** Corresponds to pitch shift in MainFrame. 
	 * 0 - No shift, 
	 * 1 - shift by shift value, 
	 * 2 - shift by custom scale
	 */
	public int shiftAction;
	/** Value to shift pitch by. relevant only if shiftAction is 1. */
	public int shiftValue;
	/** Scale to shift to. relevant only if shiftAction is 2. */
	public int targetScale;
	
	/**
	 * SettingsBundle Constructor
	 * @param filePath Input file path (Read by NoteGenerator implementations)
	 * @param costMatch Matching scale cost (Read by ScaleGenerator)
	 * @param costAccidental Accidental in scale cost (Read by ScaleGenerator)
	 * @param costScaleChange Scale change cost (Read by ScaleGenerator)
	 * @param notesPerLine Number of notes per notation line (Read by GuiCallbacks and eventually affects Notate)
	 * @param preferDFlat Prefer D-Flat scale over C-Sharp (Read by Scale via ScaleGenerator)
	 * @param preferGFlat G-Flat scale over F-Sharp (Read by Scale via ScaleGenerator)
	 * @param preferCFlat C-Flat scale over B (Read by Scale via ScaleGenerator)
	 * @param shiftAction 0 - No shift, 1 - shift by shift value, 2 - shift by custom scale (Read by NoteShifter)
	 * @param shiftValue Value to shift pitch by. relevant only if shiftAction is 1. (Read by NoteShifter)
	 * @param targetScale Scale to shift to. relevant only if shiftAction is 2. (Read by NoteShifter)
	 */
	public SettingsBundle(String filePath, int costMatch, int costAccidental, int costScaleChange, int notesPerLine,
			boolean preferDFlat, boolean preferGFlat, boolean preferCFlat, int shiftAction, int shiftValue, int targetScale)
	{
		this.filePath = filePath;
		
		this.costMatch = costMatch;
		this.costAccidental = costAccidental;
		this.costScaleChange = costScaleChange;
		this.notesPerLine = notesPerLine;
		
		this.preferDFlat = preferDFlat;
		this.preferGFlat = preferGFlat;
		this.preferCFlat = preferCFlat;
		
		this.shiftAction = shiftAction;
		// shift none
		if (shiftAction == 0)
		{
			this.shiftValue = -100;
			this.targetScale = -100;
		}
		// shift pitch
		else if (shiftAction == 1)
		{
			this.shiftValue = shiftValue;
			this.targetScale = -100;
		}
		// shift to match a particular scale
		else if (shiftAction == 2)
		{
			this.shiftValue = -100;
			this.targetScale = targetScale;
		}
	}
}
