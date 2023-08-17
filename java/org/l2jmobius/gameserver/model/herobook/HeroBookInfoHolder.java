/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.model.herobook;

/**
 * @author Index
 */
public class HeroBookInfoHolder
{
	private int _currentLevel;
	private int _currentExp;
	
	public HeroBookInfoHolder()
	{
	}
	
	public int getCurrentLevel()
	{
		return _currentLevel;
	}
	
	public void setCurrentLevel(int currentLevel)
	{
		_currentLevel = currentLevel;
	}
	
	public int getCurrentExp()
	{
		return _currentExp;
	}
	
	public void setCurrentExp(int currentExp)
	{
		_currentExp = currentExp;
	}
}
