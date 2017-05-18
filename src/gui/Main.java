package gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import core.Sequence;
import core.Sequence.Base;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application{
	double sqSize = 10;
	int maxOverlap = 5;
	int minOverlap = 1;
	int mouldSize = 100;
	int uniqueFragmentSize = 30;
	double orgSceneX, orgSceneY;
	double orgTranslateX, orgTranslateY;
	String showButtonText = "Show mould sequence";
	String hideButtonText = "Hide mould sequence";
	VBox fragmentVBox;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		//gui settings
		AnchorPane root = new AnchorPane();
		VBox mainVBox = new VBox(sqSize);
		fragmentVBox = new VBox(sqSize); //use sqSize as spacing value between children
		mainVBox.setPadding(new Insets(20,sqSize*uniqueFragmentSize,20,sqSize*uniqueFragmentSize)); //leave plenty of space right and left
		root.setPadding(new Insets(20));
		DropShadow highlight = new DropShadow(sqSize, Color.BLACK);

		//generate sequences
		Sequence seq = Sequence.generator(mouldSize);
		List<Sequence> frags = seq.generateFixedSizedFragments(uniqueFragmentSize,minOverlap, maxOverlap);
		Collections.shuffle(frags); //shuffle the order of the fragments to add a bit of difficulty

		//draw mould sequence
		Group mould = new Group();

		for (int i = 0; i < seq.size(); i++) {
			double xPos = sqSize*(i+uniqueFragmentSize);
			Rectangle rectangle = new Rectangle(xPos, 0, sqSize, sqSize); //xpos, ypos, width, height
			rectangle.setFill(Color.web(seq.get(i).getColor()));
			mould.getChildren().add(rectangle);
		}

		mainVBox.getChildren().add(mould);
		mould.setVisible(false); //start hidden

		//draw fragments
		for (int i = 0; i < frags.size(); i++) {
			Sequence fragment = frags.get(i);
			Group fragmentDrawing = new Group();

			for (int j = 0; j < fragment.size(); j++) {
				double xPos = sqSize*(j+1);
				Rectangle rectangle = new Rectangle(xPos, 0, sqSize, sqSize); //xpos, ypos, width, height
				rectangle.setFill(Color.web(fragment.get(j).getColor()));
				fragmentDrawing.getChildren().add(rectangle);
			}

			//testing
			Text text = new Text(Integer.toString(i));
			fragmentDrawing.getChildren().add(text);

			fragmentDrawing.setEffect(highlight);
			fragmentDrawing.setCursor(Cursor.MOVE);
			fragmentDrawing.setOnMousePressed(groupOnMousePressedEventHandler);
			fragmentDrawing.setOnMouseDragged(groupOnMouseDraggedEventHandler);
			fragmentDrawing.setOnMouseReleased(groupOnMouseRelesedEventHandler);
			fragmentVBox.getChildren().add(fragmentDrawing);
		}

		mainVBox.getChildren().add(fragmentVBox);

		//add button to hide mould
		ImageView ivShow = new ImageView(new Image("/res/show.png", 30, 30, true, true));		
		ImageView ivHide = new ImageView(new Image("/res/hide.png", 30, 30, true, true));

		Button bShow = new Button(null,ivShow); //no text, just the icon

		bShow.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e) {
				boolean isVisible = mould.isVisible();
				if (isVisible) { //hide it and give option to show again
					mould.setVisible(false);
					bShow.setGraphic(ivShow);
				}
				else { //show it and give option to hide it again
					mould.setVisible(true);
					bShow.setGraphic(ivHide);
				}
			}
		});

		//create legend
		GridPane legend = new GridPane();
		legend.setHgap(10);
		legend.setVgap(10);
		Base[] bases = Base.values();

		for (int i = 0; i < bases.length; i++) {
			Text text = new Text(String.valueOf(bases[i].getChar()));
			Rectangle rectangle = new Rectangle(sqSize, sqSize);
			rectangle.setFill(Color.web(bases[i].getColor()));
			legend.add(text, 0, i);
			legend.add(rectangle, 1, i);			
		}

		//adding everything to view
		VBox rightColumn = new VBox(10.);
		rightColumn.getChildren().add(bShow);
		rightColumn.getChildren().add(legend);
		rightColumn.setPadding(new Insets(20));

		root.getChildren().addAll(mainVBox,rightColumn);
		AnchorPane.setTopAnchor(mainVBox, sqSize); //use sqSize as offset from the top
		AnchorPane.setRightAnchor(rightColumn, 20.0);

		primaryStage.setScene(new Scene(root));
		primaryStage.sizeToScene();
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
			double newTranslateX = orgTranslateX + offsetX;

			double nSq = Math.round((newTranslateX)/sqSize);
			newTranslateX = sqSize*nSq;
			((Group)(t.getSource())).setTranslateX(newTranslateX);
		}
	};

	EventHandler<MouseEvent> groupOnMouseRelesedEventHandler =
			new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent t) {
			double offsetY = t.getSceneY() - orgSceneY;
			double newTranslateY = orgTranslateY + offsetY;

			int nRows = (int) Math.round(newTranslateY/(sqSize*4));
			int currentIndex = fragmentVBox.getChildren().indexOf((Group) t.getSource());
			int newIndex = currentIndex+nRows;
			int size = fragmentVBox.getChildren().size();
			if(Math.abs(nRows) >= 1) {
				if(newIndex<0) newIndex = 0;
				else if(newIndex >= size) newIndex = size - 1;

				List<Node> fragments = new ArrayList<Node>(fragmentVBox.getChildren());
				fragmentVBox.getChildren().clear();
				fragments.add(newIndex, fragments.remove(currentIndex));

				fragmentVBox.getChildren().addAll(fragments);

			}
		}
	};
}
