package core;

import java.util.ArrayList;
import java.util.Collections;

public class Tools {
	
	//Suppress default constructor for noninstantiability.
	private Tools() {
		throw new AssertionError("Shouldn't instantiate this utility class");
	}
	
	/**
	 * Generates several random integers, without repetition.
	 * @param n the amount of randomly generated numbers needed. Needs to be smaller than the interval [min,max].
	 * @param min the minimum value, inclusive
	 * @param max the maximum value, inclusive
	 * @return An array of n random numbers picked from the interval [min,max]
	 */
	public static int[] randomIntegers (int n, int min, int max) throws AssertionError{
		if(n>(max-min)) {
			throw new AssertionError("n needs to be smaller than the interval");
		}

		else {
			int[] randomNumbers = new int[n];
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (int i=min; i<=max; i++) {
				list.add(new Integer(i));
			}

			Collections.shuffle(list);
			for (int i=0; i<n; i++) {
				randomNumbers[i] = list.get(i);
			}

			return randomNumbers;
		}
	}

}
