package mainProgram;

import jm.music.data.Note;

/** Generates a scale array based on the input note array. 
 * Uses a graph based algorithm. For details see our paper. */
public class ScaleGenerator {
	private final Note[] originalNotes;
	private int[] generatedScales;

	public int[][] test_actualDistance; // do not use. Only used for unit tests via TestingClass.
	/** ScaleGenerator constructor.
	 * @param notes The notes that will be analyzed when calling "analyze". */
	public ScaleGenerator(Note[] notes)
	{
		this.originalNotes = notes;
	}
	
	/** Analyzes the note array and generate a scale array.
	 * Must call this before getScales and getNotes. settings.costMatch, settings.costAccidental, and settings.costScaleChange
	 * are read from the settings bundle and determine the path-finding cost parameters.
	 * settings.preferDFlat, settings.preferGFlat, and settings.preferCFlat are read to determine
	 * which key signature is to be used in cases where scales have multiple key signatures.
	 * @param settings The settings bundle. Used for determining cost parameters. */
	
	public void analyze(SettingsBundle settings)
	{
		if (this.originalNotes.length == 0)
		{
			this.generatedScales = new int[0];
			return;
		}
		int[][] distance = calculateDistances(settings, this.originalNotes);
		int[] intScales = traceBackPath(settings, this.originalNotes, distance);
		this.generatedScales = intScales;
		// this.correctedNotes = correctNotes(this.originalNotes, intScales, this.generatedScales);
		
		test_actualDistance = distance;
	}
	

	/** Returns the scale array. Must call analyze() first. */
	public int[] getScales()
	{
		return this.generatedScales;
	}
	
	/** private functions **/
	
	/////////// Pathfinding algorithm subfunctions
	
	/** Calculates all distance based on topological sorting pathfinding.
	 * The graph is already topologically sorted by design. */
	private int[][] calculateDistances(final SettingsBundle settings, final Note[] notes)
	{
		// First column
		final int[][] distance = new int[notes.length][12];
		for (int y = 0; y < 12; y++) // for each graph cell...
		{
			if (noteInScale(notes[0], y))
			{
				distance[0][y] = settings.costMatch;
			}
			else
			{
				distance[0][y] = settings.costAccidental;
			}
		}
		
		// Rest of the columns
		for (int x = 1; x < notes.length; x++)
		for (int y = 0; y < 12; y++)
		{
			distance[x][y] = findMinInColumn(x - 1, x, y, new Compare() {
				public int compare(int x1, int y1, int x2, int y2)
				{
					return distance[x1][y1] + calculateCost(settings, x1, y1, x2, y2);
				}
			}).minCompareResult;
		}
		
		return distance;
	}
	
	/** Trace back the best path
	* Deduce the way back based on distance cost arithmetic. This
	* is an alternative to the "parent pointers" typically used in pathfinding.
	* The main advantage is that in the future we can derive multiple
	* optimal paths easily if this is ever needed. */
	private  int[] traceBackPath(final SettingsBundle settings, final Note[] notes, final int[][] distance)
	{
		final int[] scales = new int[notes.length];

		// find minimum distance at rightmost column
		MinResult cell = findMinInColumn(originalNotes.length - 1, -1, -1, new Compare() {
			public int compare(int x1, int y1, int x2, int y2)
			{
				// x2, y2 unused.
				return distance[x1][y1];
			}
		});

		scales[originalNotes.length -1] = cell.y;
		
		// trace back till the leftmost column
		for (int x = originalNotes.length - 2; x >= 0; x--) // exclude rightmost column
		{
			int failCount = 0;
			for (int y = 0; y < 12; y++) // for each graph cell (reversed)...
			{
				if (distance[x][y] + calculateCost(settings, x, y, cell.x, cell.y) == distance[cell.x][cell.y])
				{
					cell.x = x;
					cell.y = y;
					scales[x] = cell.y;
					break;
				}
				else
				{
					failCount++;
					if (failCount == 12) throw new AssertionError("Couldn't trace back the path. This should not happen. ");
				}
				
			}
		}
		
		return scales;
	}
	
	public static JMusicScaleEnum[] getJMusicScalesFromIntScales(int[] scales, SettingsBundle settings)
	{
		final JMusicScaleEnum[] generatedScales = new JMusicScaleEnum[scales.length];
		for (int i = 0; i < scales.length; i++)
		{
			generatedScales[i] = Scale.getJMusicScaleEnum(settings, scales[i]);
		}
		return generatedScales;
	}
	
	/////////// Column scanning helper function
	
	/** Callable interface for findMinInColumn*/
	private interface Compare {
		public int compare(int x1, int y1, int x2, int y2);
	}
	
	/** Allows findMinInColumn to return 3 values. */
	private class MinResult
	{
		public int minCompareResult;
		public int x;
		public int y;
	}
	/** For each cell in colX, the cell is compared with the cell [cellX][cellY] using the
	callable c. Returns the cell in colX whose comparison yielded the smallest value. */
	private MinResult findMinInColumn(int colX, int cellX, int cellY, Compare callable)
	{
		MinResult res = new MinResult();
		res.x = colX;
		res.y = 0;
		res.minCompareResult = callable.compare(colX, 0, cellX, cellY);
		for (int colY = 0; colY < 12; colY++)
		{
			int val = callable.compare(colX, colY, cellX, cellY);
			if (val < res.minCompareResult)
			{
				res.x = colX;
				res.y = colY;
				res.minCompareResult = val;
			}
		}
		return res;
	}
	
	/////////// Other functions
	
	/** Calculate Cost from [prevX][prevY] to [x][Y]. Assumes prevX = x - 1 */
	private int calculateCost(SettingsBundle settings, int prevX, int prevY, int x, int y)
	{
		if (prevX != x - 1) throw new AssertionError("Illegal calculateCost parameters: " + prevX + "," + prevY + "," + x + "," + y);
		int cost = 0;
		if (y != prevY) cost += settings.costScaleChange;
		if (noteInScale(originalNotes[x], y))
		{
			cost += settings.costMatch;
		}
		else
		{
			cost += settings.costAccidental;
		}
		return cost;
	}
	
	public static boolean noteInScale(Note note, int scalePitch)
	{
		if (note.isRest()) return true;
		return noteInScale(note.getPitch(), scalePitch);
	}
	
	public static boolean noteInScale(int pitch, int scalePitch)
	{
		int pitchDelta = (12 + pitch - scalePitch) % 12;
		switch (pitchDelta)
		{
			case 0:
			case 2:
			case 4:
			case 5:
			case 7:
			case 9:
			case 11:
				return true;
		}
		return false;
	}
}
