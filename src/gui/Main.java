package gui;

import java.util.Collections;
import java.util.List;

import core.Sequence;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application{
	double sqSize = 10;
	int maxOverlap = 5;
	int mouldSize = 100;
	int uniqueFragmentSize = 30;
	double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;

	public static void main(String[] args) {


		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		AnchorPane root = new AnchorPane();
		root.setPadding(new Insets(20, 0, 20, 0));

		Sequence seq = Sequence.generator(mouldSize);
		List<Sequence> frags = seq.generateFixedSizedFragments(uniqueFragmentSize, maxOverlap);
		Collections.shuffle(frags); //shuffle the order of the fragments to add a bit of difficulty

		//draw mould sequence
		Group mould = new Group();

		for (int i = 0; i < seq.size(); i++) {
			double xPos = sqSize*(i+uniqueFragmentSize);
			Rectangle rectangle = new Rectangle(xPos, sqSize, sqSize, sqSize); //xpos, ypos, width, height
			rectangle.setFill(seq.get(i).getColor());
			mould.getChildren().add(rectangle);
		}

		root.getChildren().add(mould);

		//draw fragments
		for (int i = 0; i < frags.size(); i++) {
			double yPos = sqSize*(2*i+3);
			Sequence fragment = frags.get(i);
			Group fragmentDrawing = new Group();

			for (int j = 0; j < fragment.size(); j++) {
				double xPos = sqSize*(j+1);
				Rectangle rectangle = new Rectangle(xPos, yPos, sqSize, sqSize); //xpos, ypos, width, height
				rectangle.setFill(fragment.get(j).getColor());
				fragmentDrawing.getChildren().add(rectangle);
			}

			fragmentDrawing.setCursor(Cursor.MOVE);
			fragmentDrawing.setOnMousePressed(groupOnMousePressedEventHandler);
			fragmentDrawing.setOnMouseDragged(groupOnMouseDraggedEventHandler);
			root.getChildren().add(fragmentDrawing);
		}
		
		primaryStage.setScene(new Scene(root));
		primaryStage.sizeToScene();
		primaryStage.setMinWidth(sqSize*(mouldSize+2*uniqueFragmentSize)); //let enough space left and right of the mould sequence
		primaryStage.show();
	}
	
	 EventHandler<MouseEvent> groupOnMousePressedEventHandler = 
		        new EventHandler<MouseEvent>() {
		 
		        @Override
		        public void handle(MouseEvent t) {
		            orgSceneX = t.getSceneX();
		            orgSceneY = t.getSceneY();
		            orgTranslateX = ((Group)(t.getSource())).getTranslateX();
		            orgTranslateY = ((Group)(t.getSource())).getTranslateY();
		        }
		    };
		     
		    EventHandler<MouseEvent> groupOnMouseDraggedEventHandler = 
		        new EventHandler<MouseEvent>() {
		 
		        @Override
		        public void handle(MouseEvent t) {
		            double offsetX = t.getSceneX() - orgSceneX;
		            double offsetY = t.getSceneY() - orgSceneY;
		            double newTranslateX = orgTranslateX + offsetX;
		            double newTranslateY = orgTranslateY + offsetY;
		             
		            ((Group)(t.getSource())).setTranslateX(newTranslateX);
		            ((Group)(t.getSource())).setTranslateY(newTranslateY);
		        }
		    };
}
