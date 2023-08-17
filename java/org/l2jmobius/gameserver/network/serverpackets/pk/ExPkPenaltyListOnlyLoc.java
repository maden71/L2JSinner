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
package org.l2jmobius.gameserver.network.serverpackets.pk;

import java.util.Set;

import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExPkPenaltyListOnlyLoc extends ServerPacket
{
	public ExPkPenaltyListOnlyLoc()
	{
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_PK_PENALTY_LIST_ONLY_LOC.writeId(this);
		final Set<Player> players = World.getInstance().getPkPlayers();
		writeInt(World.getInstance().getLastPkTime());
		writeInt(players.size());
		for (Player player : players)
		{
			writeInt(player.getObjectId());
			writeInt(player.getX());
			writeInt(player.getY());
			writeInt(player.getZ());
		}
	}
}
