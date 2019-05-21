package mainProgram;

/**
 * This enum has Major scales values corresponding to JMusic
 */
public enum JMusicScaleEnum {
	
	EMPTY (-99),
	C_FLAT_MAJOR (-7),
	G_FLAT_MAJOR (-6),
	D_FLAT_MAJOR (-5),
	A_FLAT_MAJOR (-4),
	E_FLAT_MAJOR (-3),
	B_FLAT_MAJOR (-2),
	F_MAJOR (-1),
	C_MAJOR (0),
	G_MAJOR (1),
	D_MAJOR (2),
	A_MAJOR (3),
	E_MAJOR (4),
	B_MAJOR (5),
	F_SHARP_MAJOR (6),
	C_SHARP_MAJOR (7);
	
	/** Scale id */
	private final int id;
	
	/**
	 * Set enum scale id
	 * @param id Scale id
	 */
	JMusicScaleEnum(int id) { this.id = id; }
	
	/**
	 * Get scale value
	 * @return scale value
	 */
	public int getValue() { return this.id; };
	
}
