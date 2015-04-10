package br4cp;

import java.util.ArrayList;

public class VarPoidsId {

	public ArrayList<int[]> var= new ArrayList<int[]>();
	public ArrayList<Structure> poid= new ArrayList<Structure>();
	public ArrayList<Integer> id= new ArrayList<Integer>();
	public int memoryId;
	public boolean available = false;
	
	public VarPoidsId(int memoryId)
	{
		this.memoryId = memoryId;
	}

	public void clear()
	{
		var.clear();
		poid.clear();
		id.clear();
	}
	
}
