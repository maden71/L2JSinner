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
package org.l2jmobius.gameserver.network.serverpackets.ceremonyofchaos;

import java.util.Collection;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExCuriousHouseMemberList extends ServerPacket
{
	private final int _id;
	private final int _maxPlayers;
	private final Collection<Player> _players;
	
	public ExCuriousHouseMemberList(int id, int maxPlayers, Collection<Player> players)
	{
		_id = id;
		_maxPlayers = maxPlayers;
		_players = players;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_CURIOUS_HOUSE_MEMBER_LIST.writeId(this);
		writeInt(_id);
		writeInt(_maxPlayers);
		writeInt(_players.size());
		int pos = 0;
		for (Player player : _players)
		{
			writeInt(player.getObjectId());
			writeInt(pos++);
			writeInt(player.getMaxHp());
			writeInt(player.getMaxCp());
			writeInt((int) player.getCurrentHp());
			writeInt((int) player.getCurrentCp());
		}
	}
}
