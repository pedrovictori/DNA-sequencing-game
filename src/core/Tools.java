package core;

import java.util.concurrent.ThreadLocalRandom;

public class Tools {
	
	//Suppress default constructor for noninstantiability.
	private Tools() {
		throw new AssertionError("Shouldn't instantiate this utility class");
	}
	
	public static int[] genRandomIntegers(int n, int min, int max){
	   	int[] numbers = new int[n];

	    for (int i = 0; i < n; i++)
	    {
	        numbers[i] = ThreadLocalRandom.current().nextInt(min, max + 1);
	    }
	   return numbers;
	}  

}
