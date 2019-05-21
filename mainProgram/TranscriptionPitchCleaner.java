package mainProgram;

import java.util.ArrayList;

/** Transcription Post-processing used by TranscriptionLoader. The class automatically shifts the
 * input note sequence one or more octaves up or down if needed, making the JMusic display prettier
 * and suitable for different instruments. It also trims "stray notes" that are likely transcription errors.
 * It works as follows: Choose a pitch X, such that
 * the number of notes in the range [X, X+WINDOW_SIZE - 1] in the input note array is maximal.
 * It then removes any note outside of that range, and shifts the notes such that X is C4. */
public class TranscriptionPitchCleaner {

	// Algorithm:
	//1. array of pitch counters for every single pitch 0-119.
	//2. Choose a multiple of twelve pitch shift with the most notes inside the range 60-89
	//3. prune the notes outside that range
	
	/** Window Size */
	final static int WINDOW_SIZE = 26;
	
	/**
	 * Create clean pitch array
	 * @param pitches Pitch array
	 * @return Clean pitch array
	 */
	public static ArrayList<Integer> cleanPitches(ArrayList<Integer> pitches)
	{
		int[] pitchCnts = new int[120];
		for (int pitch : pitches)
		{
			if (pitch == -1) continue;
			if (pitch > 119) continue;
			pitchCnts[pitch]++;
		}
		
		int bestWindowPos = -1;
		int bestWindowSum = -1;
		int bestMinimalPitch = -1;
		int bestMaximalPitch = -1;
		for (int windowPos = -2; windowPos + WINDOW_SIZE - 1 < 120; windowPos+=12)
		{
			int minimalPitch = Math.max(windowPos, 0);
			int maximalPitch = Math.min(windowPos + WINDOW_SIZE - 1, 119);
			int windowSum = 0;
			for (int pitch = minimalPitch; pitch <= windowPos + WINDOW_SIZE -1; pitch++)
			{
				windowSum += pitchCnts[pitch];
			}
			System.out.print("TranscriptionCleaner - Trying [" + minimalPitch+"-"+maximalPitch+"]. Sum: " + windowSum);
			if (windowSum >= bestWindowSum)
			{
				System.out.println(". NEW BEST");
				bestWindowPos = windowPos;
				bestWindowSum = windowSum;
				bestMinimalPitch = minimalPitch;
				bestMaximalPitch = maximalPitch;
			}
			else
				System.out.println();
		}
		
		
		if (bestWindowPos == -1) throw new AssertionError("Unexpected bestWindowPos");
		
		System.out.println("TranscriptionCleaner - Chosen window [" + bestMinimalPitch+"-"+bestMaximalPitch+"]. Sum: " + bestWindowSum);
		
		ArrayList<Integer> prunedPitches = new ArrayList<Integer>();
		for (int pitch : pitches)
		{
			if (((pitch <= bestMaximalPitch) && (pitch >= bestMinimalPitch)))
				prunedPitches.add(pitch - bestWindowPos + 60 - 2);
			else if (pitch < 0)
				prunedPitches.add(-1);
		}
		
		return prunedPitches;
	}
}
