package gui;

import java.util.ArrayList;
import java.util.List;

import core.Sequence;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application{
	double sqSize = 10;
	
	public static void main(String[] args) {
		
		
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        AnchorPane root = new AnchorPane();
        
        Sequence seq = Sequence.generator(100);
        List<Sequence> frags = seq.generateFixedSizedFragments(30, 5);
        
        //draw mould sequence
        List<Rectangle> seqDrawing = new ArrayList<Rectangle>();
        
        for (int i = 0; i < seq.size(); i++) {
        	double xPos = sqSize*(i+1);
			Rectangle rectangle = new Rectangle(xPos, sqSize, sqSize, sqSize); //xpos, ypos, width, height
        	rectangle.setFill(seq.get(i).getColor());
        	seqDrawing.add(rectangle);
		}
        
        root.getChildren().addAll(seqDrawing);
        
        //draw fragments
        List<Rectangle> fragDrawing = new ArrayList<Rectangle>();
        
        for (int i = 0; i < frags.size(); i++) {
			double yPos = sqSize*(2*i+3);
			Sequence fragment = frags.get(i);
			
			for (int j = 0; j < fragment.size(); j++) {
				double xPos = sqSize*(j+1);
				Rectangle rectangle = new Rectangle(xPos, yPos, sqSize, sqSize); //xpos, ypos, width, height
	        	rectangle.setFill(fragment.get(j).getColor());
	        	fragDrawing.add(rectangle);
			}
		}
        
        root.getChildren().addAll(fragDrawing);
        
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.show();
    }
}
