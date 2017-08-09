package core;

import java.util.List;

public interface Sequenceable extends List<Character>{
	public List<Sequenceable> generateFixedSizedReads(int length, int poolSize);
		
	public List<Sequenceable> generateVariableSizeReads(double mean, double sd, int poolSize);
		
	public void introduceError(int percentage);
	
	/**
	 * Exports a given Sequence as a String.
	 * @param seq the Sequence instance to export.
	 * @return a String representation of the Sequence.
	 */
	public static String sequenceToString (Sequence seq) {
		String string = "";
		for (Character character : seq) {
			string += character;
		}
		return string;
	}
	
	public String getColor(int i);
	
}
