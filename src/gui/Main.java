package gui;

import java.util.ArrayList;
import java.util.Arrays;
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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
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
	@FXML ToggleGroup tgReadLength;
	@FXML LabelledSlider lsError;
	@FXML Button bGenerate;
	@FXML Button bShow;
	@FXML RadioButton rbV;
	@FXML RadioButton rbF;
	@FXML GridPane grid;
	@FXML TitledPane tpOptions;
	
	double sqSize = 10;
	double orgSceneX, orgSceneY;
	double orgTranslateX, orgTranslateY;
	Group draggedBar;
	String showButtonText = "Show mould sequence";
	String hideButtonText = "Hide mould sequence";
	VBox fragmentVBox;
	
	Group mould;
	VBox readsBox;
	List<LabelledSlider> readSliders = new ArrayList<LabelledSlider>();
	Boolean isReadsLengthFixed;

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

		tgReadLength.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			public void changed(ObservableValue<? extends Toggle> ov,
					Toggle old_toggle, Toggle new_toggle) {
				grid.getChildren().remove(readsBox);

				if(new_toggle==rbF){
					readsBox = createFixedLengthGUI();
				}

				else{
					readsBox = createVariableLengthGUI();
				}

				GridPane.setConstraints(readsBox, 1, 4);

				grid.getChildren().add(readsBox);			
			}
		});

		//adjust max sliders' values according to target sequence length
		lsTargetLength.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {	
				lsPoolSize.setMax((double) new_val);

				if(isReadsLengthFixed != null) {
					if(isReadsLengthFixed){
						readSliders.get(0).setMax((double)new_val/2);
					}

					else {
						readSliders.get(1).setMax((double)new_val/2);
					}
				}
			}
		});
	}

	private VBox createFixedLengthGUI(){
		Label label = new Label("Read length");
		double targetLength = lsTargetLength.getValue();
		double max = targetLength/2;
		double value = (max-10)/2;
		LabelledSlider slider = new LabelledSlider(10, max, value);
		VBox box = new VBox(5, label,slider);
		readSliders.clear();
		readSliders.add(slider);
		isReadsLengthFixed = true;
		return box;
	}

	private VBox createVariableLengthGUI(){		
		double targetLength = lsTargetLength.getValue();
		double max = targetLength/2;
		double value = (max-10)/2;
		double maxSc = (max - value)/2;

		Label labelMin = new Label("Minimum length");
		LabelledSlider sliderMin = new LabelledSlider(10, value, 10);

		Label labelMax = new Label("Maximum length");
		LabelledSlider sliderMax = new LabelledSlider(10, max, value);

		Label labelSc = new Label("Scale");
		LabelledSlider sliderSc = new LabelledSlider(5, maxSc, maxSc/2);

		//maximum scale value depends on maximum and minimum length values
		sliderMin.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {	
				double newMaxSc = (sliderMax.getValue() - sliderMin.getValue())/2;
				sliderSc.setMax(newMaxSc);
			}
		});

		sliderMax.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {	
				sliderMin.setMax((double)new_val);
				double newMaxSc = (sliderMax.getValue() - sliderMin.getValue())/2;
				sliderSc.setMax(newMaxSc);
			}
		});

		VBox box = new VBox(5, labelMin, sliderMin, labelMax, sliderMax, labelSc, sliderSc);
		readSliders.clear();
		readSliders.addAll(Arrays.asList(sliderMin,sliderMax,sliderSc));

		isReadsLengthFixed = false;
		return box;
	}

	private void drawSequences(Sequence seq, List<Sequence> reads, int error) {
		vbSeq.setSpacing(sqSize);
		fragmentVBox = new VBox(sqSize/2); //use sqSize as spacing value between children


		Collections.shuffle(reads); //shuffle the order of the fragments to add a bit of difficulty

		//draw mould sequence
		mould = new Group();

		for (int i = 0; i < seq.size(); i++) {
			double xPos = sqSize*i;
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
			Group bar = new Group();
			
			//adding number
			String number = Integer.toString(i);
			int margin = 3-number.length();
			Rectangle space = new Rectangle(0,0,sqSize*margin,sqSize);
			space.setFill(Color.TRANSPARENT);
			Text text = new Text(sqSize*margin, 10, number);	
			bar.getChildren().addAll(space,text);
			
			for (int j = 0; j < read.size(); j++) {
				double xPos = sqSize*(j+3);
				Rectangle rectangle = new Rectangle(xPos, 0, sqSize, sqSize); //xpos, ypos, width, height
				rectangle.setFill(Color.web(read.get(j).getColor()));
				bar.getChildren().add(rectangle);
			}
			
			prepareBar(bar);
			fragmentVBox.getChildren().add(bar);
		}

		vbSeq.getChildren().add(fragmentVBox);
	}
	
	private void prepareBar(Group bar) {
		DropShadow highlight = new DropShadow(sqSize, Color.BLACK);
		bar.setEffect(highlight);
		bar.setCursor(Cursor.MOVE);
		bar.setOnMousePressed(groupOnMousePressedEventHandler);
		bar.setOnMouseDragged(groupOnMouseDraggedEventHandler);
		bar.setOnMouseReleased(groupOnMouseRelesedEventHandler);
	}

	@FXML protected void onGenerateButton(ActionEvent event) {
		//clear previous case
		vbSeq.getChildren().clear();
		
		//collapse options pane
		tpOptions.setExpanded(false);

		//get settings
		int targetSize = lsTargetLength.getValue().intValue();
		int poolSize = lsPoolSize.getValue().intValue();
		int error = lsError.getValue().intValue();

		//generate target
		Sequence seq = Sequence.generator(targetSize);
		
		List<Sequence> reads;
		
		//fixed or variable length?
		if(isReadsLengthFixed){
			int readsLength = readSliders.get(0).getValue().intValue();
			reads = seq.generateFixedSizedReads(readsLength, poolSize);
		}
		
		else{
			double min = readSliders.get(0).getValue().intValue();
			double max = readSliders.get(1).getValue().intValue();
			//TODO implement scale
			
			reads= seq.generateVariableSizeReads((max+min)/2, (max-min)/2, poolSize);
		}
		
		drawSequences(seq,reads,error);
	}

	EventHandler<MouseEvent> groupOnMousePressedEventHandler = 
			new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent t) {		
			orgSceneX = t.getSceneX();
			orgSceneY = t.getSceneY();
			draggedBar = (Group) t.getSource();
			orgTranslateX = draggedBar.getTranslateX();
			orgTranslateY = draggedBar.getTranslateY();
			
		}
	};

	EventHandler<MouseEvent> groupOnMouseDraggedEventHandler = 
			new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent t) {
			double offsetX = t.getSceneX() - orgSceneX;
			double newTranslateX = orgTranslateX + offsetX;
			
			double offsetY = t.getSceneY() - orgSceneY;
			double newTranslateY = orgTranslateY + offsetY;

			double nSq = Math.round((newTranslateX)/sqSize);
			newTranslateX = sqSize*nSq;
			
			draggedBar.setTranslateX(newTranslateX); //move copy
			draggedBar.setTranslateY(newTranslateY);
		}
	};

	EventHandler<MouseEvent> groupOnMouseRelesedEventHandler =
			new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent t) {
			double offsetY = t.getSceneY() - orgSceneY;
			double newTranslateY = orgTranslateY + offsetY;
			
			int nRows = (int) Math.round(newTranslateY/(sqSize*2+1));
			int currentIndex = fragmentVBox.getChildren().indexOf(draggedBar);
			int newIndex = currentIndex+nRows;
			int size = fragmentVBox.getChildren().size();
			if(Math.abs(nRows) >= 1) {
				if(newIndex<0) newIndex = 0;
				else if(newIndex >= size) newIndex = size - 1;

				List<Node> fragments = new ArrayList<Node>(fragmentVBox.getChildren());
				fragmentVBox.getChildren().clear();
				fragments.remove(currentIndex);
				Group copy = new Group(draggedBar.getChildren());
				prepareBar(copy);
				fragments.add(newIndex,copy) ;
				fragmentVBox.getChildren().addAll(fragments);

			}
		}
	};

}
