package core;

import java.util.concurrent.ThreadLocalRandom;

public class Tools {
	
	//Suppress default constructor for noninstantiability.
	private Tools() {
		throw new AssertionError("Shouldn't instantiate this utility class");
	}
	
	/**
	 * Returns an array of non unique random integers in a given range.
	 * @param n the number of random integers to return.
	 * @param min the lower limit of the range, inclusive.
	 * @param max the upper limit of the range, inclusive.
	 * @return an int array with the specified number of random integers.
	 */
	public static int[] genRandomIntegers(int n, int min, int max){
	   	int[] numbers = new int[n];

	    for (int i = 0; i < n; i++)
	    {
	        numbers[i] = ThreadLocalRandom.current().nextInt(min, max + 1);
	    }
	   return numbers;
	}  

}
