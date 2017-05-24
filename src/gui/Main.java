package gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import core.Sequence;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sliders.LabelledSlider;

public class Main extends Application{
	@FXML VBox vbSeq;
	@FXML LabelledSlider lsTargetLength;
	@FXML LabelledSlider lsPoolSize;
	@FXML LabelledSlider lsReadLength;
	@FXML LabelledSlider lsError;
	@FXML Button bGenerate;
	@FXML Button bShow;


	double sqSize = 10;
	double orgSceneX, orgSceneY;
	double orgTranslateX, orgTranslateY;
	String showButtonText = "Show mould sequence";
	String hideButtonText = "Hide mould sequence";
	VBox fragmentVBox;

	Group mould;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
			Scene scene = new Scene(root,800,800);
			primaryStage.setScene(scene);

			primaryStage.show();
		} 

		catch(Exception e) {
			e.printStackTrace();
		}
	}

	@FXML public void initialize(){
		//show/hide button
		ImageView ivShow = new ImageView(new Image("/res/show.png", 30, 30, true, true));		
		ImageView ivHide = new ImageView(new Image("/res/hide.png", 30, 30, true, true));

		bShow.setGraphic(ivShow);

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

		//adjust max sliders' values according to target sequence length
		lsTargetLength.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {	
				lsPoolSize.setMax((double) new_val);
				lsReadLength.setMax((double)new_val/2);
			}
		});
	}

	private void generateSequences(int targetSize, int poolSize, int readsLength, int error) {
		DropShadow highlight = new DropShadow(sqSize, Color.BLACK);
		vbSeq.setSpacing(sqSize);
		fragmentVBox = new VBox(sqSize); //use sqSize as spacing value between children

		//generate sequences
		Sequence seq = Sequence.generator(targetSize);
		List<Sequence> reads = seq.generateFixedSizedReads(readsLength, poolSize);
		Collections.shuffle(reads); //shuffle the order of the fragments to add a bit of difficulty

		//draw mould sequence
		mould = new Group();

		for (int i = 0; i < seq.size(); i++) {
			double xPos = sqSize*(i+readsLength);
			Rectangle rectangle = new Rectangle(xPos, 0, sqSize, sqSize); //xpos, ypos, width, height
			rectangle.setFill(Color.web(seq.get(i).getColor()));
			mould.getChildren().add(rectangle);
		}

		vbSeq.getChildren().add(mould);
		mould.setVisible(false); //start hidden

		//introduce error and draw fragments
		for (int i = 0; i < reads.size(); i++) {
			Sequence read = reads.get(i);
			read.introduceError(error);
			Group fragmentDrawing = new Group();

			for (int j = 0; j < read.size(); j++) {
				double xPos = sqSize*(j+1);
				Rectangle rectangle = new Rectangle(xPos, 0, sqSize, sqSize); //xpos, ypos, width, height
				rectangle.setFill(Color.web(read.get(j).getColor()));
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

		vbSeq.getChildren().add(fragmentVBox);
	}

	@FXML protected void onGenerateButton(ActionEvent event) {
		//clear previous case
		vbSeq.getChildren().clear();
		
		//get settings
		int targetSize = lsTargetLength.getValue().intValue();
		int poolSize = lsPoolSize.getValue().intValue();
		int readsLength = lsReadLength.getValue().intValue();
		int error = lsError.getValue().intValue();

		generateSequences(targetSize, poolSize, readsLength,error);
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
