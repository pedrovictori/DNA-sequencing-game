package core;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.distribution.NormalDistribution;

import toolbox.tools.MathTools;

public class Sequence extends AbstractList<Character> implements Sequenceable{
	
	/** 
	 * Stores the colors that represent each base.
	 */
	private static Map<Character,String> baseColors = new HashMap<Character,String>();
	
	static {
		baseColors.put('a',"#1a9641");
		baseColors.put('c',"#fdae61");
		baseColors.put('g',"#d7191c");
		baseColors.put('t',"#a6d96a");
	}
	
	private static final Character[] bases = {'a','c','g','t'};

	private List<Character> sequence = new ArrayList<Character>();

	/**
	 * Creates a new Sequence object with the specified sequence.
	 * @param seqStr a String representing the desired sequence.
	 */
	public Sequence(String seqStr) {
		for (char character : seqStr.toCharArray()) {
			sequence.add(character);
		}
	}

	/**
	 * As per the recommendation in the Collection interface specification.
	 * @param sequence
	 */
	private Sequence (Collection<Character> sequence) {
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
	public static Sequence generate(int length) {
		List<Character> list = new ArrayList<Character>();

		for(int i=0,c=0;i<length;i++,c++) { //create a list of acgt repeated until reaching the specified length
			if(c==4) {c=0;}
			list.add(bases[c]);
		}

		Collections.shuffle(list); //shuffle the sequence
		Sequence generated = new Sequence(list);
		System.out.println(generated.toString());//testing
		return generated;
	}
	
	public String getColor(int i) {
		return baseColors.get(sequence.get(i));
	}
	
	public List<Sequenceable> generateFixedSizedReads(int length, int poolSize){
		List<Sequenceable> reads = new ArrayList<Sequenceable>();
		int[] indexes = MathTools.genRandomUniqueIntegers(poolSize, 0, size()-length-1);

		for(int i=0;i<poolSize;i++){
			/* we could simply add a cast to Sequence, since Sequence itself is a subclass of List, 
			 * but this way we create a new object, independent from the mould */
			Sequence read = new Sequence(subList(indexes[i], indexes[i]+length));
			reads.add(read);
		}

		return reads;
	}
	
	public List<Sequenceable> generateVariableSizeReads(double mean, double sd, int poolSize) {
		List<Sequenceable> reads = new ArrayList<Sequenceable>();
		NormalDistribution nd = new NormalDistribution(mean, sd);
		Integer[] lengths = new Integer[poolSize];
	
		for(int i=0;i<poolSize;i++){
			lengths[i] = (int) nd.sample();		
		}
		
		int smallestLength = MathTools.findSmallest(Arrays.asList(lengths));
		int[] indexes = MathTools.genRandomUniqueIntegers(poolSize, 0, size()-smallestLength-1);
		
		for(int i=0;i<poolSize;i++){
			int endIndex =indexes[i] + lengths[i];
			
			/*if this read would overrun the sequence total length, cut it so it ends where the target ends.
			*we know that even when cut, this read won't be shorter than the minimum, because between the biggest 
			*possible index and the end of the target is a length equal to the smallest read length (see a couple of lines above)*/
			if(endIndex>=size()){
				endIndex = size()-1;
			}
			
			Sequence read = new Sequence(subList(indexes[i], endIndex));
			reads.add(read);
		}
		
		return reads;
	}

	public void introduceError(int percentage){
		if(percentage!=0){
			int nBasesToChange = size()*percentage/100; 
			int[] errorIndexes = MathTools.genRandomUniqueIntegers(nBasesToChange, 0, size()-1);
			System.out.println(Integer.toString(nBasesToChange));
			System.out.println(Integer.toString(errorIndexes.length));
			
			

			for(int i = 0; i<nBasesToChange; i++){
				set(errorIndexes[i], getAnotherBase(get(errorIndexes[i])));
			}
		}
	}
	
	private static Character getAnotherBase(Character c) {
		List<Character> characters = new ArrayList<Character>();
		for (Character character : bases) {
			characters.add(character);
		}
		characters.remove(c);
		Collections.shuffle(characters);
		return characters.get(0);
		
	}
	/**
	 * Applies the sequenceToString method to this instance.
	 */
	@Override
	public String toString() {
		return Sequenceable.sequenceToString(this);
	}

	@Override
	public int size() {
		return sequence.size();
	}

	@Override
	public Character set(int index, Character base) {
		return sequence.set(index, base);
	}

	@Override
	public Character remove(int index) {
		return sequence.remove(index);
	}

	public void add(int index, Character base) {
		sequence.add(index, base);
	}

	@Override
	public Character get(int index) {
		return sequence.get(index);
	}
}
