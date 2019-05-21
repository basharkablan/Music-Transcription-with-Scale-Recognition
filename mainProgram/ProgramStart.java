package mainProgram;

import javax.swing.UIManager;

import gui.MainFrame;

/**
 * Contains main function
 */
public class ProgramStart {

	/**
	 * Main Function
	 * @param args This parameter in not used
	 */
	public static void main(String[] args)
	{
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e1) {
			// Does not work on other operating systems.
		}
		
		TestingClass.runTests();
		MainFrame.run();
	}
}
