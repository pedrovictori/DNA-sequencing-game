package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sequence{
	char[] bases = {'a','c','g','t'};
	
	private String sequence;
	
	/**
	 * Creates a new Sequence object with the specified sequence.
	 * @param sequence
	 */
	public Sequence(String sequence) {
		this.sequence = sequence;
	}
	
	/**
	 * Creates a new Sequence object with a random sequence of the specified length
	 * @param length
	 */
	public Sequence(int length) {
		sequence = generator(length);
		System.out.println(sequence);//testing
	}
	
	private String generator(int length) {
		List<Character> list = new ArrayList<Character>();
		
		for(int i=0,c=0;i<length;i++,c++) { //create a list of acgt repeated until reaching the specified length
			if(c==4) {c=0;}
			list.add(bases[c]);
		}
		
		Collections.shuffle(list); //shuffle the sequence
		
		String string="";
		for (int i = 0; i < length; i++) { //convert List to string
			string += list.get(i);
		}
		
		return string; 
	}
	
	public List<String> generateFixedSizedFragments(int uniqueLength, int maxOverlapping){
		int nFrag = sequence.length()/uniqueLength;
		int lastFragLength = sequence.length()%uniqueLength;
		if (lastFragLength!=0) {nFrag++;} //cut a extra fragment that contains the rest of the target sequence, even if it's smaller than uniqueLength
		int[] overlappingLengths = Tools.randomIntegers((nFrag)*2, 0, maxOverlapping);
		
		String preZeroSeq = generator(maxOverlapping);
		String postEndSeq = generator(uniqueLength-lastFragLength+maxOverlapping);
		String extendedSeq = preZeroSeq+sequence+postEndSeq;
		int targetSeqStart = preZeroSeq.length();
		List<String> frags = new ArrayList<String>();
		
		for(int i=0,l=0;i<nFrag;i++,l+=2) {
			int startIndex = i*uniqueLength+targetSeqStart; //start the unique sequence fragment (without the overlapping parts) at 0 in the original sequence, 0+targetSeqStart in the extended sequence, which allows for extension of the first and last fragments
			int endIndex = startIndex+uniqueLength;
			int overlappingStartIndex = startIndex - overlappingLengths[l];
			int overlappingEndIndex = endIndex + overlappingLengths[l+1];
			String fragment = extendedSeq.substring(overlappingStartIndex, overlappingEndIndex);
			frags.add(fragment);
		}
		
		return frags;
	}
	
	public String getSequence() {
		return sequence;
	}
}
