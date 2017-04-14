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
        
        //draw mould sequence
        List<Rectangle> seqDrawing = new ArrayList<Rectangle>();
        
        for (int i = 0; i < seq.size(); i++) {
        	double xPos = sqSize*(i+1);
			Rectangle rectangle = new Rectangle(xPos, sqSize, sqSize, sqSize); //xpos, ypos, width, height
        	rectangle.setFill(seq.get(i).getColor());
        	seqDrawing.add(rectangle);
		}
        
        root.getChildren().addAll(seqDrawing);
        
        
        List<Sequence> frags = seq.generateFixedSizedFragments(30, 5);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }
}
