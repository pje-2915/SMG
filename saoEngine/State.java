package saoEngine;

import java.util.ArrayList;
import java.util.List;

public class State
{
	String _name;
	List<State> _state = new ArrayList<State>();
	List<Transition> _transition = new ArrayList<Transition>();
	int _currentSubStateIndex = 0;
	int _myStateIndex = 0;
	State _parent = null;
	
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
	
	public void set_index(int index)
	{
		_myStateIndex = index;
	}
	
	public int get_index()
	{
		return _myStateIndex;
	}
	
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
	
	public void switch_to_state(int state_index)
	{
		if(state_index >=0 && state_index < _state.size())
		{
			_currentSubStateIndex = state_index;
		}
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
}