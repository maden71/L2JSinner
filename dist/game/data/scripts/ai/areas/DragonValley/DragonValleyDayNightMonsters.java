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
package ai.areas.DragonValley;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.taskmanager.GameTimeTaskManager;

import ai.AbstractNpcAI;

/**
 * Dragon Valley Area Mobs AI.
 * @info When you kill certain monsters within the zone at a certain time, special monsters will appear. The type of the monster depends on the time of day. Daytime: Behemoth Dragon Nighttime: Soul Hunter
 * @author CostyKiller
 */
public final class DragonValleyDayNightMonsters extends AbstractNpcAI
{
	// Trigger Mobs
	private static final int DRAGON_PELTAST = 24617; // Dragon Peltast
	private static final int DRAGON_OFFICER = 24618; // Dragon Officer
	// Special Mobs
	private static final int BEHEMOTH_DRAGON = 24619; // Behemoth Dragon
	private static final int SOUL_HUNTER = 24620; // Soul Hunter
	
	// Misc
	private static final int MOB_SPAWN_CHANCE = 1; // 1% chance to spawn
	
	private DragonValleyDayNightMonsters()
	{
		super();
		addKillId(DRAGON_PELTAST, DRAGON_OFFICER);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (getRandom(100) < MOB_SPAWN_CHANCE)
		{
			addSpawn(GameTimeTaskManager.getInstance().isNight() ? SOUL_HUNTER : BEHEMOTH_DRAGON, npc, true, 0, true);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new DragonValleyDayNightMonsters();
	}
}