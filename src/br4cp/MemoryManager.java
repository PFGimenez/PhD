package br4cp;

import java.util.ArrayList;

public class MemoryManager {

	private int indiceFirstAvailable;
	private ArrayList<VarPoidsId> objects = new ArrayList<VarPoidsId>();
	private static MemoryManager instance;
	
	private MemoryManager()
	{
	}
	
	public static MemoryManager getMemoryManager()
	{
		if(instance == null)
			instance = new MemoryManager();
		return instance;
	}

	public VarPoidsId getObject()
	{
		VarPoidsId out;
		try {
			out = objects.get(indiceFirstAvailable);
			if(!out.available)
				System.out.println("Intégrité rompue dans le MemoryManager: object déjà utilisé");
			out.available = false;

		}
		catch(IndexOutOfBoundsException e)
		{
			out = new VarPoidsId(indiceFirstAvailable);
			objects.add(out);
		}
		indiceFirstAvailable++;
		return out;
	}
	
	public void destroyObject(VarPoidsId object)
	{
		if(object.available)
			System.out.println("Intégrité rompue dans le MemoryManager: objet à détruire déjà détruit");
		indiceFirstAvailable--;
		VarPoidsId tmp = objects.get(indiceFirstAvailable);
		objects.set(indiceFirstAvailable, object);
		tmp.memoryId = object.memoryId;
		objects.set(object.memoryId, tmp);
		object.memoryId = indiceFirstAvailable;
		object.clear();
		object.available = true;
//		System.out.println(indiceFirstAvailable);
	}
	
}
