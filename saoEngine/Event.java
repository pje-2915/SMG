package saoEngine;

public class Event
{
	private String _name;
	private int _id;
	private static int _highest = 0;
	
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
