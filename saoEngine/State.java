package saoEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class State
{
	private String _name;
	private List<State> _state = new ArrayList<State>();
	private List<Transition> _transition = new ArrayList<Transition>();
	private int _currentSubStateIndex = 0;
	private int _myStateIndex = 0;
	private State _parent = null;
	
	// Graphics
	double _width = 40;
	double _height = 20;
	double _x = 0;
	double _y = 0;
	double _x_adjust = 0;
	double _y_adjust = 0;
	
	//==============================================================
	//======================= constructio ==========================
	//==============================================================
	
	State(String name)
	{
		_name = name;
	}

	State(State parent, String name)
	{
		_name = name;
		_parent = parent;
		_parent.register_state(this);
	}

	State duplicate(State parent, String name, boolean duplicate_transitions)
	{
		State copy = new State(parent, name);
		
		System.out.printf("Duplicating %s under parent %s\n", _name, copy._parent._name);
		
		// Need to change the parent for each immediate substate to new State
		for (int i = 0; i < _state.size(); i++)
		{
			State st = _state.get(i).duplicate(copy, _state.get(i)._name, true);
			// Constructor does the registration
		}
		
		// Duplicate each State in _state, and for these duplicate states also duplicate the transitions
		if(duplicate_transitions)
		{
			for (int i = 0; i < _transition.size(); i++)
			{
				// We duplicate transitions for substates, so we need the duplicated transitions to reference
				// the duplicated end states not the original end states.  We do this by finding the state in
				// the new list at the same position that the old state was in the old list.
				Transition tr = _transition.get(i).duplicate(copy);
				// Constructor does the registration
			}
		}

		return copy;
	}
	
	
	State duplicate(State parent, String name)
	{
		// Duplicate top level of a branch - don't copy transitions - only substates
		// Begin the recursive constructions
		return duplicate(parent, name, false);
	}

	//==============================================================
	//=================== customisation ======================
	//==============================================================

	// Register a substate
	public void register_state(State st)
	{
		_state.add(st);
		st.set_index(_state.size()-1);
	}

	public void register_transition(Transition tr)
	{
		_transition.add(tr);
	}

	public void add_transition(Event ev, State end)
	{
		Transition tr = new Transition(this, ev, end._myStateIndex);
		_transition.add(tr);
	}

	//==============================================================
	//=================== getters and setters ======================
	//==============================================================

	public void set_index(int index)
	{
		_myStateIndex = index;
	}
	
	public int get_index()
	{
		return _myStateIndex;
	}
		
	public String get_name()
	{
		return _name;
	}

	public String get_full_name()
	{
		String full_name = "";
		if(_parent != null) full_name = _parent.get_full_name() +  "#";
		full_name += _name;
		return full_name;
	}
	
	public State get_parent()
	{
		return _parent;
	}
	
	public State get_substate(int index)
	{
		State state = null;
		if(index >= 0 && index < _state.size())
		{
			state = _state.get(index);
		}
		return state;
	}
	
	public Transition get_transition(int index)
	{
		Transition transition = null;
		if(index >= 0 && index < _transition.size())
		{
			transition = _transition.get(index);
		}
		return transition;
	}
	
	public int get_subclass_count()
	{
		return _state.size();
	}
	
	public int get_transition_count()
	{
		return _transition.size();
	}
	
	//==============================================================
	//======================== execution ===========================
	//==============================================================
	
	public void switch_to_state(int state_index)
	{
		if(state_index >=0 && state_index < _state.size())
		{
			_currentSubStateIndex = state_index;
		}
	}
	
	public boolean event(Event ev)
	{
		// Try the current state to see if it has a suitable transition before we look
		// for generic transitions
		if(_state.size() == 0 || !_state.get(_currentSubStateIndex).event(ev))
		{
			for (int i = 0; i < _transition.size(); i++)
			{
				if(_transition.get(i).execute(ev))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean is_active()
	{
		boolean active = (_parent == null || _parent._currentSubStateIndex == _myStateIndex);
		active = active && (_parent == null || _parent.is_active());
		return active;
	}
	//==============================================================
	//======================== graphics ===========================
	//==============================================================

	public void calculate_coordinates()
	{
		double fudge1 = 1.3;
		double padding = 1.1;
		double label_height = 45;
		
		double min_x = 0;
		double min_y = 0;	
		double max_x = 0;
		double max_y = 0;	
		
		int r_count = _state.size();
		double max_radius = _width + padding;
		for (int i = 0; i < r_count; i++)
		{
			State r = _state.get(i);
			r.calculate_coordinates();
			int angle = (360/r_count)*i;
			double radius =  r._width * padding;
			max_radius = (radius > max_radius)? radius : max_radius;
			r._x = -radius * Math.cos(Math.toRadians(angle));
			r._y = -radius * Math.sin(Math.toRadians(angle));
			
			min_x = (r._x - 0.5 * r._width < min_x)? r._x - 0.5 * r._width: min_x;
			min_y = (r._y - 0.5 * r._height < min_y)? r._y - 0.5 * r._height: min_y;
			max_x = (r._x + 0.5 * r._width > max_x)? r._x + 0.5 * r._width : max_x;
			max_y = (r._y + 0.5 * r._height > max_y)? r._y + 0.5 * r._height : max_y;
		}	

		if(r_count > 0)
		{
			_width = (max_x - min_x) * fudge1;
			_height = (max_y - min_y) * fudge1 + label_height;
		
			_x_adjust = (min_x + max_x)/2;
			_y_adjust = (min_y + max_y)/2;
		}
		System.out.printf("%s\n",  _name);
		System.out.printf("_width = %f, _height = %f\n",  _width, _height);
		System.out.printf("_x_adjust = %f, _y_adjust = %f\n",  _x_adjust, _y_adjust);

	}
	
	public void draw_rectangles(double x, double y, GraphicsContext gc)
	{
		double x_left = (x + _x) - _width/2;
		double y_left = (y + _y) - _height/2;
		if(is_active())
		{
			gc.setStroke(Color.RED);
		}
		else
		{
			gc.setStroke(Color.BLACK);			
		}
        gc.strokeRoundRect(x_left, y_left, _width, _height, 10, 10);
        gc.strokeText(_name, x_left + 5, y_left + _height - 5, _width * 0.8);
		System.out.printf("%s : x=%f, y=%f, width=%f height=%f\n", _name, x + _x, y + _y, _width, _height);
		for (int i = 0; i < _state.size(); i++)
		{
			_state.get(i).draw_rectangles(x + _x - _x_adjust, y + _y - _y_adjust, gc);
		}
	}
	
}