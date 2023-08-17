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
package ai.areas.Parnassus.Fioren;

import org.l2jmobius.gameserver.enums.Movie;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;

import ai.AbstractNpcAI;

/**
 * Fioren AI.
 * @author St3eT
 */
public class Fioren extends AbstractNpcAI
{
	// NPCs
	private static final int FIOREN = 33044;
	
	private Fioren()
	{
		addStartNpc(FIOREN);
		addTalkId(FIOREN);
		addFirstTalkId(FIOREN);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		if (event.equals("startMovie"))
		{
			playMovie(player, Movie.SI_BARLOG_STORY);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	public static void main(String[] args)
	{
		new Fioren();
	}
}