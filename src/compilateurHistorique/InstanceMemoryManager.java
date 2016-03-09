package compilateurHistorique;

import java.util.ArrayList;

/*   (C) Copyright 2015, Pierre-François Gimenez
 * 
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public class InstanceMemoryManager {

	private int indiceFirstAvailable = 0;
	private ArrayList<Instanciation> objects = new ArrayList<Instanciation>();
	private int limiteNbObjets = 1000000;
	private static InstanceMemoryManager instance;
	
	private InstanceMemoryManager()
	{}
	
	public void createInstanciation()
	{
		for(int i = 0; i < 200000; i++)
			objects.add(new Instanciation());
	}
	
	public static InstanceMemoryManager getMemoryManager()
	{
		if(instance == null)
			instance = new InstanceMemoryManager();
		return instance;
	}

	public Instanciation getObject()
	{
		Instanciation out;
		try {
			out = objects.get(indiceFirstAvailable);
		}
		catch(IndexOutOfBoundsException e)
		{
//			System.out.println("Pas assez de place ! "+indiceFirstAvailable);
			out = new Instanciation();
			if(objects.size() < limiteNbObjets)
				objects.add(out);
		}
		indiceFirstAvailable++;
		return out;
	}
	
	public void clearAll()
	{
		indiceFirstAvailable = 0;
	}
	
}
