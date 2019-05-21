package mainProgram;

/**
 * Represents a musical scale
 */
public class Scale
{
	/** Scale id */
	public int id;
	
	/**
	 * Scale Constructor. 
	 * We typically do not construct a scale and use the class as an abstract class 
	 * but this is useful for the GUI dropdown elements.
	 * @param id Scale id. 0 to 11 where 0 is CMajor, 1 is C#Major, etc.
	 */
	public Scale(int id)
	{
		this.id = id;
		// We typically do not construct a scale and use the class as an abstract class
		// but this is useful for the GUI dropdown elements.
	}
	
	/**
	 * Convert the native scale integer to a Jmusic-friendly key signature
	 * @param settings Determines preferences for scales that have multiple key signatures.
	 * @param scaleId Chosen The scale id to convert (see constructor documentation too)
	 * @return JMusic scale enum
	 */
	public static JMusicScaleEnum getJMusicScaleEnum(SettingsBundle settings, int scaleId)
	{
		switch (scaleId)
		{
			case 0:
				return JMusicScaleEnum.C_MAJOR; // no flats, no sharps
			case 1:
				if (settings.preferDFlat) return JMusicScaleEnum.D_FLAT_MAJOR; // 5 flats
				else return JMusicScaleEnum.C_SHARP_MAJOR; // - 7 sharps
			case 2:
				return JMusicScaleEnum.D_MAJOR; //  2 sharps
			case 3:
				return JMusicScaleEnum.E_FLAT_MAJOR; // 3 flats
			case 4:
				return JMusicScaleEnum.E_MAJOR; // 4 sharps
			case 5:
				return JMusicScaleEnum.F_MAJOR; // 1 flat
			case 6:
				if (settings.preferGFlat) return JMusicScaleEnum.G_FLAT_MAJOR; // 6 flats
				else return JMusicScaleEnum.F_SHARP_MAJOR; // 6 sharps 
			case 7:
				return JMusicScaleEnum.G_MAJOR; // 1 sharp
			case 8:
				return JMusicScaleEnum.A_FLAT_MAJOR; // 4 flats
			case 9:
				return JMusicScaleEnum.A_MAJOR; // 3 sharps
			case 10:
				return JMusicScaleEnum.B_FLAT_MAJOR; // 2 flats
			case 11:
				if (settings.preferCFlat) return JMusicScaleEnum.C_FLAT_MAJOR; // 7 flats
				else return JMusicScaleEnum.B_MAJOR; // 5 sharps
			default:
				throw new AssertionError("Scale: Unexpected scale value in getJMusicScaleEnum");
		}
	};
	
	/**
	 * Get scale string
	 */
	public String toString()
	{
		return scaleToPrettyString(this.id);
	}
	
	/**
	 * Get scale string by scale id. Returns a plain ASCII string with no UTF characters unlike scaleToPrettyString
	 * @param scaleId Scale id
	 * @return Scale string
	 */
	public static String scaleToString(int scaleId)
	{
		switch (scaleId)
		{
			case 0:
				return "C_MAJOR";
			case 1:
				return "D_FLAT_MAJOR/C_SHARP_MAJOR";
			case 2:
				return "D_MAJOR";
			case 3:
				return "E_FLAT_MAJOR";
			case 4:
				return "E_MAJOR";
			case 5:
				return "F_MAJOR";
			case 6:
				return "G_FLAT_MAJOR/F_SHARP_MAJOR";
			case 7:
				return "G_MAJOR";
			case 8:
				return "A_FLAT_MAJOR";
			case 9:
				return "A_MAJOR";
			case 10:
				return "B_FLAT_MAJOR";
			case 11:
				return "C_FLAT_MAJOR/B_MAJOR";
			default:
				throw new AssertionError("Scale: Unexpected scale value in scaleToString");
		}
	};
	
	/**
	 * Get scale string by scale id. This is the pretty UTF version of scaleToString.
	 * @param scaleId Scale id
	 * @return Scale string
	 */
	public static String scaleToPrettyString(int scaleId)
	{
		switch (scaleId)
		{
			case 0:
				return "C Major";
			case 1:
				return "D\u266D Major/C\u266F Major";
			case 2:
				return "D Major";
			case 3:
				return "E\u266D Major";
			case 4:
				return "E Major";
			case 5:
				return "F Major";
			case 6:
				return "G\u266D Major/F\u266F Major";
			case 7:
				return "G Major";
			case 8:
				return "A\u266D Major";
			case 9:
				return "A Major";
			case 10:
				return "B\u266D Major";
			case 11:
				return "C\u266D Major/B Major";
			default:
				throw new AssertionError("Scale: Unexpected scale value in scaleToString");
		}
	};
}





