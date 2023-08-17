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

import java.util.Set;

import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.holders.SkillHolder;

/**
 * @author Index
 */
public class HeroBookLevelHolder
{
	private final int _level;
	private final int _exp;
	private final Set<ItemHolder> _items;
	private final Set<SkillHolder> _skills;
	private final long _commission;
	
	public HeroBookLevelHolder(int level, int exp, Set<ItemHolder> items, Set<SkillHolder> skills, long commission)
	{
		_level = level;
		_exp = exp;
		_items = items;
		_skills = skills;
		_commission = commission;
	}
	
	public int getLevel()
	{
		return _level;
	}
	
	public int getExp()
	{
		return _exp;
	}
	
	public Set<ItemHolder> getItems()
	{
		return _items;
	}
	
	public Set<SkillHolder> getSkills()
	{
		return _skills;
	}
	
	public long getCommission()
	{
		return _commission;
	}
}
