package mainProgram;

import java.util.ArrayList;

import jm.music.data.Note;

/** This class is responsible for startup sanity tests and assertions. 
 * Used as a simple automatic testing platform. Can be removed in the production version. */
public class TestingClass implements jm.JMC {
	private static SettingsBundle settings = new SettingsBundle("", 0, 1, 4, 15, true, false, false, 0, -100, -100);

	/** Initiate the tests. May throw Runtime exceptions if errors fail. */
	public static void doTests()
	{
		testNotesInScales();
		testScaleGeneration();
		testTranscriptionLoaderMath();
	}
	
	private static void testNotesInScales()
	{
		Note n = new Note();
		for (int i = 0; i < 12; i++)
		{
			n.setPitch(i);
			int scaleId = i;
			if (!ScaleGenerator.noteInScale(n, scaleId))
			{
				String scaleName = Scale.getJMusicScaleEnum(settings, scaleId).toString();
				String noteName = n.getName();
				String expected =  noteName + " is in " + scaleName;
				String actual =  noteName + " is not in " + scaleName;
				errors.add(new TestFailDelayed("NoteInScale error", expected, actual));
			}
			testCount++;
		}
	}
	
	private static void testScaleGeneration()
	{
		{
			int[] notes =
				{
					C1, D7, E7, F7
				};
			JMusicScaleEnum[] expectedScales = {JMusicScaleEnum.C_MAJOR};
			int[] expectedLengths = {4};
			int[][] expectedDistances = 
				{
					{0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 1},
					{0, 1, 1, 0, 2, 0, 2, 0, 1, 1, 0, 2},
					{0, 2, 1, 1, 2, 0, 3, 0, 2, 1, 1, 2},
					{0, 2, 2, 1, 3, 0, 3, 1, 2, 2, 1, 3}
				};
			testScaleGeneration_single(notes, expectedScales, expectedLengths, expectedDistances);
		}
		{
			int[] notes =
				{
					CS1, DS4, F0, FS1
				};
			settings.preferDFlat = false;
			JMusicScaleEnum[] expectedScales = {JMusicScaleEnum.C_SHARP_MAJOR};
			int[] expectedLengths = {4};
			int[][] expectedDistances = 
				{
					{1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0},
					{2, 0, 1, 1, 0, 2, 0, 2, 0, 1, 1, 0},
					{2, 0, 2, 1, 1, 2, 0, 3, 0, 2, 1, 1},
					{3, 0, 2, 2, 1, 3, 0, 3, 1, 2, 2, 1}
				};
			testScaleGeneration_single(notes, expectedScales, expectedLengths, expectedDistances);
			settings.preferDFlat = true;
			JMusicScaleEnum[] expectedScales2 = {JMusicScaleEnum.D_FLAT_MAJOR};
			testScaleGeneration_single(notes, expectedScales2, expectedLengths, expectedDistances);
		}
		{
			int[] notes =
				{C7, D7, E7, C7, C7, D7, E7, C7, 
					CS7, FS7, CS7, FS7, CS7, FS7, CS7, FS7, G7, A7, D7, E7, FS7, CS7};
			JMusicScaleEnum[] expectedScales = {JMusicScaleEnum.C_MAJOR, JMusicScaleEnum.D_MAJOR};
			int[] expectedLengths = {8, 14};
			testScaleGeneration_single(notes, expectedScales, expectedLengths, null);
		}
	}
	
	public static void testTranscriptionLoaderMath()
	{
		testTranscriptionLoaderMath_single(440, jm.JMC.A4);
		testTranscriptionLoaderMath_single(439, jm.JMC.A4);
		testTranscriptionLoaderMath_single(442, jm.JMC.A4);
		testTranscriptionLoaderMath_single(261, jm.JMC.C4);
		testTranscriptionLoaderMath_single(262, jm.JMC.C4);
	}
	

	
	////////////////////////// Functions that perform individual tests.
	public static void testScaleGeneration_single(int[] rawNotes, JMusicScaleEnum[] scaleList, int[] lengthList, int[][] expectedDistance)
	{
		Note[] notes = rawNotesToNotes(rawNotes);
		if (scaleList.length != lengthList.length)
		{
			throw new TestFail("expectedLength length does not equal expectedScales length", 
					scaleList.length + "", lengthList.length + "");
		}
		JMusicScaleEnum[] expectedScales;
		
		{
			int sum = 0;
			for (int x : lengthList) sum += x;
			if (sum != rawNotes.length)
				throw new TestFail("expectedLength sum does not equal rawNote length", 
						rawNotes.length + "", sum + "");
			expectedScales = new JMusicScaleEnum[sum];
			
			int i = 0;
			for (int scaleIndex = 0; scaleIndex < scaleList.length; scaleIndex++)
			{
				for (int cnt = lengthList[scaleIndex]; cnt > 0; cnt--)
				{
					expectedScales[i++] = scaleList[scaleIndex];
				}
			}
		}
				
		ScaleGenerator gen = new ScaleGenerator(notes);
		gen.analyze(settings);
		JMusicScaleEnum[] actualScales = ScaleGenerator.getJMusicScalesFromIntScales(gen.getScales(), settings);
		if (expectedDistance != null)
		{
			testScaleGeneration_single_distanceTest(expectedDistance, gen.test_actualDistance);
		}
		testScaleGeneration_single_finalIOTest(expectedScales, actualScales);
	}
	
	private static void testScaleGeneration_single_distanceTest(int[][] expected, int[][] actual)
	{
		if ((expected.length != actual.length) || (expected[0].length != actual[0].length))
		{
			errors.add(new TestFailDelayed("Distance array size unexpected. Expected " +
					"[" + expected.length + ", " + expected[0].length + "] " +					
					"but actual is[" + actual.length + ", " + actual[0].length + "]", array2dToString(expected), array2dToString(actual)));
		}
		else 
		{
			boolean errorFlag = false;
			for (int x = 0; x < expected.length; x++)
			{
				for (int y = 0; y < expected[0].length; y++)
				{
					if (errorFlag == true) break;
					if (expected[x][y] != actual[x][y])
					{
						errors.add(new TestFailDelayed("Distance array value mismatch starting from [" + x +", " + y + "]. Expected " + expected[x][y] +
								"but actual is " + actual[x][y] + ". Full Arrays below. ", array2dToString(expected), array2dToString(actual)));
						errorFlag = true;
					}
				}
				if (errorFlag == true) break;
			}
		}
		testCount++;
	}
	
	private static void testScaleGeneration_single_finalIOTest(JMusicScaleEnum[] expectedScales, JMusicScaleEnum[] actualScales)
	{
		for (int i = 0; i < expectedScales.length; i++)
		{
			if (expectedScales[i] != actualScales[i])
			{
				errors.add(new TestFailDelayed("Scale mismatch starting at note #" + (i + 1),
						scaleArrToString(expectedScales), scaleArrToString(actualScales)));
				break;
			}
		}
		testCount++;
	}
	
	private static void testTranscriptionLoaderMath_single(float freq, int expectedPitch)
	{
		int actualPitch = TranscriptionLoader.freqToPitch(freq);
		if (expectedPitch != actualPitch)
			errors.add(new TestFailDelayed("freqToPitch " + freq, 
					expectedPitch + "", actualPitch + ""));
		testCount++;
	}
	//////////////////////////
	
	private static ArrayList<TestFailDelayed> errors = new ArrayList<TestFailDelayed>();
	private static int testCount = 0;
	
	@SuppressWarnings("serial")
	public static class TestFail extends AssertionError {
		public TestFail(String str, String expected, String got)
		{
			super ("Bad test parameters. " + str + "\nExpected: " + expected + "\nGot: " + got);
		}
	}
	
	public static class TestFailDelayed {
		String toPrint;
		public TestFailDelayed(String str, String expected, String got)
		{
			this.toPrint = str + "\nExpected:\n" + expected + "\nActual:\n" + got;
		}
		public String toString()
		{
			return this.toPrint;
		}
	}
	
	public static void runTests()
	{
		doTests();
		int failCount = errors.size();
		String failCountStr;
		if (failCount == 0) failCountStr = "0";
		else failCountStr = "*" + failCount + "*";
		
		int successCount = testCount - failCount;
			
		int cnt = 0;
		for (TestFailDelayed err : errors)
		{
			System.out.println("Failure #" + (++cnt));
			System.out.println(err);
			System.out.println();
		}
		
		if (failCount > 0)
		{
			System.out.print("TESTING FAILURE: ");
			System.out.println(successCount + " tests succeeded and " + failCountStr + " tests failed. Total tests performed: " + testCount);
		}
	}
	
	private static String scaleArrToString(JMusicScaleEnum[] scales)
	{
		String str = "";
		for (JMusicScaleEnum scale : scales)
		{
			if (scale == null) str += "NULL,";
			else str += scale.toString() + ",";
		}
		return str;
	}
	
	private static String array2dToString(int[][] arr)
	{
		String str = "";
		for (int y = 0; y < arr[0].length; y++)
		{
			for (int x = 0; x < arr.length; x++)
			{
				str += arr[x][y] + ",";
			}
			str += "\n";
		}
		return str;
	}
	
	private static Note[] rawNotesToNotes(int[] rawNotes)
	{
		Note[] notes = new Note[rawNotes.length];
		for (int i = 0; i < rawNotes.length; i++)
		{
			notes[i] = new Note(rawNotes[i], 1);
		}
		return notes;
	}
}
