package saoEngine;

public class Transition
{
	State _start_state;
	int _end_state_index;
	int _event_id;
	
	public Transition(){}
	
	public Transition(State start, Event ev, int end_index)
	{
		_start_state = start;
		_end_state_index = end_index;
		if(ev != null) _event_id = ev.get_id();
		start.register_transition(this);
	}

	public boolean execute(Event ev)
	{
		if(ev.get_id() != _event_id) return false;
		System.out.printf("%s : transition from %s to %s\n",ev.get_name(), _start_state.get_full_name(), 
				_start_state.get_parent().get_substate(_end_state_index).get_full_name());
		_start_state.get_parent().switch_to_state(_end_state_index);
		return true;
	}
	
	public Transition duplicate(State start)
	{
		Transition tr = new Transition(start, null, _end_state_index);
		tr._event_id = _event_id;
		return tr;
	}
}
