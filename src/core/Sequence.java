package core;

import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import core.Sequence.Base;

public class Sequence extends AbstractSequentialList<Base> {
	/**
	 * This enumeration is used to hold the char and color asociated with each base, and to provide methods to convert a char to base and vice versa.
	 */
	public enum Base {
		A('a',"#1a9641"),
		C('c',"#fdae61"),
		G('g',"#d7191c"),
		T('t',"#a6d96a");

		private char baseChar;
		private String color;

		Base(char baseChar, String color) {
			this.baseChar = baseChar;
			this.color = color;
		}
		
		public char getChar() {
			return baseChar;
		}
		
		public String getColor() {
			return color;
		}
		
		/**
		 * Static method that tries to convert a given char into the assigned base.
		 * @throws IllegalArgumentException if the given char is not a, c, g or t.
		 * @param c the char to be converted to a Base.
		 * @return the corresponding member of the enum Base.
		 */
		static Base toBase(char c) {
			if(c=='a') return Base.A;
			else if(c=='c') return Base.C;
			else if(c=='g') return Base.G;
			else if(c=='t') return Base.T;
			else {
				System.out.println(c);
				throw new IllegalArgumentException();
			}
		}
	} 

	private List<Base> sequence = new ArrayList<Base>();

	/**
	 * Creates a new Sequence object with the specified sequence.
	 * @param seqStr a String representing the desired sequence.
	 */
	public Sequence(String seqStr) {
		sequence =  stringToSequence(seqStr);
	}
	
	/**
	 * As per the recommendation in the Collection interface specification.
	 * @param sequence
	 */
	private Sequence (Collection<Base> sequence) {
		this.sequence.addAll(sequence);
	}
	
	/**
	 * As per the recommendation in the Collection interface specification.
	 */
	public Sequence() {}
	
	/**
	 * Creates a new Sequence object with a random sequence of the specified length.
	 * @param length the number of bases in the new sequence.
	 * @return the generated Sequence instance.
	 */
	public static Sequence generator(int length) {
		List<Base> list = new ArrayList<Base>();
		Base[] bases = Base.values();

		for(int i=0,c=0;i<length;i++,c++) { //create a list of acgt repeated until reaching the specified length
			if(c==4) {c=0;}
			list.add(bases[c]);
		}

		Collections.shuffle(list); //shuffle the sequence
		Sequence generated = new Sequence(list);
		System.out.println(generated.toString());//testing
		return generated;
	}
	
	/**
	 * Breaks down the sequence into fixed sized fragments, plus start and end sequences that overlap with the previous and next fragments.
	 * These overlapping parts are of a variable length, which is determined at random within a given range.
	 * @param uniqueLength the length in number of bases of the sequence region which is unique to each fragment. Every fragment has the same unique region length.
	 * @param minOverlapping the overlapping region's minimum length.
	 * @param maxOverlapping the overlapping region's maximum length.
	 * @return a List of Sequence, every member of the list is a fragment that includes the unique region and the two overlapping regions.
	 */
	public List<Sequence> generateFixedSizedFragments(int uniqueLength, int minOverlapping, int maxOverlapping){
		int nFrag = sequence.size()/uniqueLength;
		int lastFragLength = sequence.size()%uniqueLength;
		if (lastFragLength!=0) {nFrag++;} //cut a extra fragment that contains the rest of the target sequence, even if it's smaller than uniqueLength
		int[] overlappingLengths = tools.MathTools.genRandomIntegers((nFrag)*2, minOverlapping, maxOverlapping);

		Sequence preZeroSeq = generator(maxOverlapping);
		Sequence postEndSeq = generator(uniqueLength-lastFragLength+maxOverlapping);
		Sequence extendedSeq = new Sequence();
		extendedSeq.addAll(preZeroSeq);
		extendedSeq.addAll(this);
		extendedSeq.addAll(postEndSeq);
		
		int targetSeqStart = preZeroSeq.size();
		List<Sequence> frags = new ArrayList<Sequence>();

		for(int i=0,l=0;i<nFrag;i++,l+=2) {
			int startIndex = i*uniqueLength+targetSeqStart; //start the unique sequence fragment (without the overlapping parts) at 0 in the original sequence, 0+targetSeqStart in the extended sequence, which allows for extension of the first and last fragments
			int endIndex = startIndex+uniqueLength;
			int overlappingStartIndex = startIndex - overlappingLengths[l];
			int overlappingEndIndex = endIndex + overlappingLengths[l+1];
			Sequence fragment = new Sequence(extendedSeq.subList(overlappingStartIndex, overlappingEndIndex));
			System.out.println(fragment.toString());//testing
			frags.add(fragment);
		}

		return frags;
	}
	
	/**
	 * Applies the sequenceToString method to this instance.
	 */
	@Override
	public String toString() {
		return sequenceToString(this);
	}
	
	private static List<Base> stringToSequence (String sequence){
		char[] chars = sequence.trim().toCharArray();
		List<Base> bases = new ArrayList<Base>();
		for (char c : chars) {
			bases.add(Base.toBase(c));
		}
		
		return bases;
	}
	
	/**
	 * Exports a given Sequence as a String, each base represented by its assigned char.
	 * @param seq the Sequence instance to export.
	 * @return a String representation of the Sequence.
	 */
	public static String sequenceToString (Sequence seq) {
		String string = "";
		for (Base base : seq) {
			string += base.getChar();
		}
		return string;
	}

	@Override
	public int size() {
		return sequence.size();
	}

	@Override
	public ListIterator<Base> listIterator(int index) {
		return sequence.listIterator();
	}
	
	@Override
	public Base set(int index, Base base) {
		return sequence.set(index, base);
	}
	
	@Override
	public Base remove(int index) {
		return sequence.remove(index);
	}
	
	public void add(int index, Base base) {
		sequence.add(index, base);
	}
}
