package br4cp;

import java.util.ArrayList;

public class VarPoidsId {

	public ArrayList<VarPoidIdElement> triplet = new ArrayList<VarPoidIdElement>();
	public int memoryId;
	public boolean available = false;
	
	public VarPoidsId(int memoryId)
	{
		this.memoryId = memoryId;
	}

	public void clear()
	{
		triplet.clear();
	}
	
}
