package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import core.Sequence.Base;
import javafx.collections.ModifiableObservableListBase;
import javafx.scene.paint.Color;

public class Sequence extends ModifiableObservableListBase<Base>{
	enum Base {
		A('a',Color.DEEPSKYBLUE),
		C('c',Color.RED),
		G('g',Color.LIMEGREEN),
		T('t',Color.GOLD);

		private char baseChar;
		private Color color;

		Base(char baseChar, Color color) {
			this.baseChar = baseChar;
			this.color = color;
		}
		
		public char getChar() {
			return baseChar;
		}
		
		public Color getColor() {
			return color;
		}
		
		static Base toBase(char c) {
			if(c=='a') return Base.A;
			else if(c=='c') return Base.C;
			else if(c=='g') return Base.G;
			else if(c=='t') return Base.T;
			else throw new IllegalArgumentException();
		}
	} 

	private List<Base> sequence;

	/**
	 * Creates a new Sequence object with the specified sequence.
	 * @param sequence
	 */
	public Sequence(String seqStr) {
		sequence =  stringToSequence(seqStr);
	}
	
	private Sequence (List<Base> sequence) {
		this.sequence = sequence;
	}
	
	/**
	 * Creates a new Sequence object with a random sequence of the specified length
	 * @param length
	 */
	public static Sequence generator(int length) {
		List<Base> list = new ArrayList<Base>();
		Base[] bases = Base.values();

		for(int i=0,c=0;i<length;i++,c++) { //create a list of acgt repeated until reaching the specified length
			if(c==4) {c=0;}
			list.add(bases[c]);
		}

		Collections.shuffle(list); //shuffle the sequence

		return new Sequence(list);
	}

	public List<Sequence> generateFixedSizedFragments(int uniqueLength, int maxOverlapping){
		int nFrag = sequence.size()/uniqueLength;
		int lastFragLength = sequence.size()%uniqueLength;
		if (lastFragLength!=0) {nFrag++;} //cut a extra fragment that contains the rest of the target sequence, even if it's smaller than uniqueLength
		int[] overlappingLengths = Tools.randomIntegers((nFrag)*2, 0, maxOverlapping);

		String preZeroSeq = generator(maxOverlapping).toString();
		String postEndSeq = generator(uniqueLength-lastFragLength+maxOverlapping).toString();
		String extendedSeq = preZeroSeq+sequence+postEndSeq;
		int targetSeqStart = preZeroSeq.length();
		List<Sequence> frags = new ArrayList<Sequence>();

		for(int i=0,l=0;i<nFrag;i++,l+=2) {
			int startIndex = i*uniqueLength+targetSeqStart; //start the unique sequence fragment (without the overlapping parts) at 0 in the original sequence, 0+targetSeqStart in the extended sequence, which allows for extension of the first and last fragments
			int endIndex = startIndex+uniqueLength;
			int overlappingStartIndex = startIndex - overlappingLengths[l];
			int overlappingEndIndex = endIndex + overlappingLengths[l+1];
			String fragment = extendedSeq.substring(overlappingStartIndex, overlappingEndIndex);
			frags.add(new Sequence(fragment));
		}

		return frags;
	}
	
	@Override
	public String toString() {
		return sequenceToString(sequence);
	}
	
	private static List<Base> stringToSequence (String sequence){
		char[] chars = sequence.toCharArray();
		List<Base> bases = new ArrayList<Base>();
		for (char c : chars) {
			bases.add(Base.toBase(c));
		}
		
		return bases;
	}
	
	public static String sequenceToString (List<Base> seq) {
		String string = "";
		for (Base base : seq) {
			string += base.getChar();
		}
		return string;
	}

	@Override
	protected void doAdd(int index, Base element) {
		sequence.add(index, element);
		
	}

	@Override
	protected Base doRemove(int index) {
		return sequence.remove(index);
	}

	@Override
	protected Base doSet(int index, Base element) {
		return sequence.set(index, element);
	}

	@Override
	public Base get(int index) {
		return sequence.get(index);
	}

	@Override
	public int size() {
		return sequence.size();
	}
}
