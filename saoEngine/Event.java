package saoEngine;

public class Event
{
	String _name;
	int _id;
	static int _highest = 0;
	
	public Event(){}
	public Event(String name)
	{
		_id = _highest++;
		_name = name;
	}
	
	public int get_id()
	{
		return _id;
	}
	
	public String get_name()
	{
		return _name;
	}
}
