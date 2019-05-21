package mainProgram;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import jm.music.data.Note;
import jm.music.data.Rest;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;

 /**
 * Loads a WAV file and transcribes it. Returns a Note array. 
 * Internally, it uses the third party TarsosDSP's YIN pitch estimation algorithm. This class
 * is the only place TarsosDSP is used. TranscriptionPitchCleaner is a custom post-processing algorithm
 * for cleaning up the transcription slightly.
 */
public class TranscriptionLoader implements NoteGenerator {
	
	/** TranscriptionLoader default constructor */
	public TranscriptionLoader()
	{
		this.filePath = "";
	}
	
	/** Chosen file for notes analysis */
	private String filePath;
	
	/**
	 * TranscriptionLoader constructor
	 * @param filePath Input file for notes analysis
	 */
	public TranscriptionLoader(String filePath)
	{
		this.filePath = filePath;
	}
	
	/**
	 * Transcribes the input file. Must be called before getNotes()
	 */
	@Override
	public void analyse() throws IOException, UnsupportedAudioFileException, LineUnavailableException
	{
		boolean fromMicrophone = false;
		
		System.out.print("Transcription begin. Frequencies: ");
		if (fromMicrophone)
		{
			PitchDetectionHandler handler = new PitchDetectionHandler() {
		        @Override
		        public void handlePitch(PitchDetectionResult pitchDetectionResult,
		                AudioEvent audioEvent) {
		        	double time = audioEvent.getTimeStamp();
		        	float freq = pitchDetectionResult.getPitch(); // Not the JMusic kind of pitch. This is in HZ and is actually frequency.
		        	processSingleFrequency(time, freq);
		        }
		    };
		    AudioDispatcher adp = AudioDispatcherFactory.fromDefaultMicrophone(2048, 0);
		    adp.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, 44100, 2048, handler));
		    adp.run();
		}
		else
		{
			PitchDetectionHandler handler = new PitchDetectionHandler() {
				// Inspired by: https://stackoverflow.com/questions/31231813/tarsosdsp-pitch-analysis-for-dummies
		        @Override
		        public void handlePitch(PitchDetectionResult pitchDetectionResult,
		                AudioEvent audioEvent) {
		        	double time = audioEvent.getTimeStamp();
		        	float freq = pitchDetectionResult.getPitch(); // Not the JMusic kind of pitch. This is in HZ and is actually frequency.
		        	processSingleFrequency(time, freq);
		            // http://pages.mtu.edu/~suits/notefreqs.html
		        }
		    };
		    AudioDispatcher adp;
		    File f = new File(this.filePath);
			adp = AudioDispatcherFactory.fromFile(f, 2048, 0);
			adp.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, getSampleRate(f), 2048, handler));
		    adp.run();
		}
		
		System.out.println();
		System.out.println("total freq samples " + rawPitches.size());
		printIntArray("Raw Pitches", rawPitches);
	    ArrayList<Integer> pitches = rawPitchesToMergedPitches();
	    printIntArray("Merged Pitches", pitches);
	    rawPitches = null;
	    timestamps = null;
	    pitches = TranscriptionPitchCleaner.cleanPitches(pitches);
	    printIntArray("Cleaned and shifted pitches", pitches);
	    pitchesToNotes(pitches);
	    System.out.println("Transcription has ended");
	}
	
	
	private static float getSampleRate(File f) throws UnsupportedAudioFileException, IOException
	{
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(f);
		AudioFormat audioFormat = audioInputStream.getFormat();
		return audioFormat.getSampleRate();
	}
	private ArrayList<Integer> rawPitches = new ArrayList<Integer>();
	private ArrayList<Double> timestamps = new ArrayList<Double>();
	
	/** Analyzed notes array */
	private Note[] notes;
	
	public void processSingleFrequency(double time, float freq)
	{
		System.out.print(freq +",");
		int pitch = freqToPitch(freq);
		rawPitches.add(pitch);
		timestamps.add(time);	
	}
	
	private ArrayList<Integer> rawPitchesToMergedPitches()
	{
		ArrayList<Integer> pitches = new ArrayList<Integer>();
		int lastPitch = -1;
		for (int i = 0; i < rawPitches.size() - 3; i++)
		{
			int p = rawPitches.get(i);
			if (p < 0)
			{
				if (i < rawPitches.size() - 7 &&  p == rawPitches.get(i + 1) && p == rawPitches.get(i + 2) && p == rawPitches.get(i + 3) &&
						p == rawPitches.get(i + 4) && p == rawPitches.get(i + 5) && p == rawPitches.get(i + 6) &&
						p == rawPitches.get(i + 7))
				{
					if ((p != lastPitch) && pitches.size() != 0)
					{
						pitches.add(-1);
					}
				}
				lastPitch = -1;
			}
			else if (p == rawPitches.get(i + 1) && p == rawPitches.get(i + 2) && p == rawPitches.get(i + 3))   
			{
				int currentPitch = p;
				if (currentPitch != lastPitch)
				{
					pitches.add(currentPitch);
					lastPitch = currentPitch;
				}
			}
			
		}
		if (pitches.get(pitches.size() - 1) == -1) pitches.remove(pitches.size() - 1); // remove last rest if present
		return pitches;
	}
	
	private void pitchesToNotes(ArrayList<Integer> pitches)
	{
		notes = new Note[pitches.size()];
		for (int i = 0; i < notes.length; i++)
		{
			if (pitches.get(i) < 0)
			{
				notes[i] = new Rest();
			}
			else
				notes[i] = new Note(pitches.get(i), 1);
		}
	}
	
	private void printIntArray(String description, ArrayList<Integer> arr)
	{
		System.out.print("Transcription - " + description + ": ");
		for (int item : arr) System.out.print(item + ",");
		System.out.println();
	}
	
	public static int freqToPitch(float f)
	{
		if (f<= 0) return -1;
		//F = 2 ^ (12/n) * a4
		// where n = pitch, F = frequency, a4 = 440hz
		// and where n=0 means a4.
		// Therefore, n = 12 log2(f/a). We also need to shift 0 to c4, therefore +9. JMUSIC c4 is 60, therefore +60
		final int a4 = 440;
		int n = (int) (Math.round(12 * (Math.log(f/a4)/Math.log(2)))) + 69;
		
		// in JMusic, C0 = 12, C4 = 60, A4, 69
		return n;
	}
	
	/**
	 * Get analyzed notes array
	 */
	@Override
	public Note[] getNotes()
	{
		return notes;
		//return notes.toArray(new Note[notes.size()]);
	}

	/**
	 * Get no notes warning string
	 */
	@Override
	public String getNoNotesWarning() {
		return "No notes were detected.\nMake sure the recording is monophonic (one instrument, one note at a time) and not too noisy.";
	}
	
}
