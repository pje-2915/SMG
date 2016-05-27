
package saoEngine;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
 
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
        btn.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
            	simulate();
            }
        });
        
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }
    
    public void simulate()
    {
		// red -> amber
		traffic_light.event(swap);
		// amber -> amber
		traffic_light.event(tick);
		// amber-> green
		traffic_light.event(swap);
		// green#on -> green#off
		traffic_light.event(flick);
		// green#off -> green#on
		traffic_light.event(flick);
		// green -> red
		traffic_light.event(swap);
		// red -> blue
		traffic_light.event(bswap);
		// blue#on -> blue#off
		traffic_light.event(flick);
		// blue#off -> blue#on
		traffic_light.event(flick);
		// blue -> red
		traffic_light.event(bswap);
		// red -> amber
		traffic_light.event(swap);
    }
}







