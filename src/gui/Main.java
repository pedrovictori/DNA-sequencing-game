package gui;

import core.Sequence;

public class Main {

	public static void main(String[] args) {
		Sequence seq = new Sequence(100); //testing
		
		System.out.println(String.join(", ",seq.generateFixedSizedFragments(30, 10))); //testing
	}

}
