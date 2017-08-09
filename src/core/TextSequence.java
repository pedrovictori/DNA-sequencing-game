package core;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.distribution.NormalDistribution;

import toolbox.tools.MathTools;

public class TextSequence extends AbstractList<Character> implements Sequenceable{
	private List<Character> sequence = new ArrayList<Character>();


	/**
	 * Creates a new Sequence object with the specified sequence.
	 * @param seqStr a String representing the desired sequence.
	 */
	public TextSequence(String seqStr) {
		char[] chars = seqStr.trim().toCharArray();
		sequence = new ArrayList<Character>();
		for (char c : chars) {
			sequence.add(c);
		}
	}

	/**
	 * As per the recommendation in the Collection interface specification.
	 * @param sequence
	 */
	private TextSequence (Collection<Character> sequence) {
		this.sequence.addAll(sequence);
	}

	/**
	 * As per the recommendation in the Collection interface specification.
	 */
	public TextSequence() {}
	
	public List<Sequenceable> generateFixedSizedReads(int length, int poolSize){
		List<Sequenceable> reads = new ArrayList<Sequenceable>();
		int[] indexes = MathTools.genRandomUniqueIntegers(poolSize, 0, size()-length-1);

		for(int i=0;i<poolSize;i++){
			/* we could simply add a cast to Sequence, since Sequence itself is a subclass of List, 
			 * but this way we create a new object, independent from the mould */
			TextSequence read = new TextSequence(subList(indexes[i], indexes[i]+length));
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
			
			Sequenceable read = new TextSequence(subList(indexes[i], endIndex));
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
				set(errorIndexes[i], getAnotherLetter(get(errorIndexes[i])));
			}
		}
	}
	
	private static Character getAnotherLetter(char c) {
		String letters = "abcdefghijklmnopqrstuvwxyz";
		
		int index = MathTools.genRandomIntegers(1, 0, letters.length()-1)[0];
		if(letters.charAt(index) == c) {
			index++;
			if(index== letters.length()) {
				index = 0;
			}
		}
		return letters.charAt(index);
		
	}
	
	@Override
	public Character get(int index) {
		return sequence.get(index);
	}

	@Override
	public int size() {
		return sequence.size();
	}
	
	@Override
	public Character set(int index, Character character) {
		return sequence.set(index, character);
	}

	@Override
	public Character remove(int index) {
		return sequence.remove(index);
	}

	public void add(int index, Character character) {
		sequence.add(index, character);
	}
	
}