package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sequence{
	char[] bases = {'a','c','g','t'};
	
	private char[] sequence;
	
	/**
	 * Creates a new Sequence object with the specified sequence.
	 * @param sequence
	 */
	public Sequence(String sequence) {
		this.sequence = sequence.toCharArray();
	}
	
	/**
	 * Creates a new Sequence object with a random sequence of the specified length
	 * @param length
	 */
	public Sequence(int length) {
		sequence = generator(length);
		System.out.println(new String(sequence));//testing
	}
	
	private char[] generator(int length) {
		char[] seq = new char[length];
		
		List<Character> list = new ArrayList<Character>();
		
		for(int i=0,c=0;i<length;i++,c++) { //create a list of acgt repeated until reaching the specified length
			if(c==4) {c=0;}
			list.add(bases[c]);
		}
		
		Collections.shuffle(list); //shuffles the sequence
		
		for (int i = 0; i < length; i++) { //convert List to char[]
			seq[i] = list.get(i);
		}
		
		return seq;
	}
	
	public char[] getSequence() {
		return sequence;
	}
}
