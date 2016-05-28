
package saoEngine;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import javafx.util.Duration;
 
public class StateMachineDriver extends Application
{
	static State traffic_light;
	static State red;
	static State amber;
	static State green;
	static State blue;

	static Event swap;
	static Event tick;
	static Event flick;
	static Event bswap;

	static int test_counter = 0;

	public static void main(String[] args)
    {        
		traffic_light = new State("TrafficLight");
		
		red = new State(traffic_light, "Red");
		amber = new State(traffic_light, "Amber");
		green = new State(traffic_light, "Green");
		State on = new State(green, "On");
		State off = new State(green, "Off");
		
		swap = new Event("Swap");
		tick = new Event("Tick");
		flick = new Event("Flick");
		bswap = new Event("Blue-swap");
		
		red.add_transition(swap, amber);
		amber.add_transition(tick, amber);
		amber.add_transition(swap, green);
		green.add_transition(swap, red);
		on.add_transition(flick, off);
		off.add_transition(flick, on);
		
		// Add blue, and Duplicate green behaviour for blue
		State blue = green.duplicate(green.get_parent(), "blue");
		// Add some transitions to get in and out of blue
		red.add_transition(bswap, blue);
		blue.add_transition(bswap, red);
		
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage)
    {
        primaryStage.setTitle("States-As-Objects Engine");
        Button btn = new Button();
        btn.setText("Run Animation");        
        Group root = new Group();
        Canvas canvas = new Canvas(600, 500);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        AnimationTimer timer = new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
            }
        };
        
        btn.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
            	timeline.play();
            	timer.start();;
            }
        });
        
        EventHandler onFinished = new EventHandler<ActionEvent>()
		{
			public void handle(ActionEvent t)
			{
				simulate(gc);
			}
		};
		
        Duration duration = Duration.millis(1000);
		KeyFrame keyFrame = new KeyFrame(duration, onFinished);
		timeline.getKeyFrames().add(keyFrame);

        show_diagram(gc);
        root.getChildren().add(canvas);
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 600, 500));
        primaryStage.show();
    }
    
    public void show_diagram(GraphicsContext gc)
    {
    	traffic_light.calculate_coordinates();
    	traffic_light.draw_rectangles(300,250, gc); // TODO
    }
   
    public void simulate(GraphicsContext gc)
    {
    	Event test_events[] =
    		{
    				swap, tick, swap, flick, flick, swap, bswap, flick, flick, bswap, swap
    		};
    	
    	traffic_light.event(test_events[test_counter]);
    	if(++test_counter == test_events.length) test_counter = 0;
    	traffic_light.draw_rectangles(300,250, gc); // TODO
    }
}







