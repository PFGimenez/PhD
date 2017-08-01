/*   (C) Copyright 2017, Gimenez Pierre-François
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

package recommandation.parser;

/**
 * A small parser
 * @author Pierre-François Gimenez
 *
 */

public class ParserProcess
{
	private String[] args;
	private Integer index;
	private boolean verbose;
	
	public boolean hasNext()
	{
		return index < args.length;
	}
	
	@Override
	public ParserProcess clone()
	{
		return new ParserProcess(args, verbose, index);
	}
	
	public String read()
	{
		String out = args[index++];
		if(verbose)
		{
			StackTraceElement elem = Thread.currentThread().getStackTrace()[2];
			System.out.println("Lecture par "+elem.getClassName().substring(elem.getClassName().lastIndexOf(".") + 1) + ":" + elem.getLineNumber() + " (" + Thread.currentThread().getName()+") > "+out);
		}
		return out;
	}
	
	public ParserProcess(String[] args, boolean verbose)
	{
		this(args, verbose, 0);
	}
	
	private ParserProcess(String[] args, boolean verbose, int index)
	{
		this.verbose = verbose;
		this.args = args;
		this.index = index;
	}
}
