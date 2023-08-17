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
package org.l2jmobius.gameserver.network.serverpackets.faction;

import org.l2jmobius.gameserver.enums.Faction;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mathael, Mobius
 */
public class ExFactionInfo extends ServerPacket
{
	private final Player _player;
	private final boolean _openDialog;
	
	public ExFactionInfo(Player player, boolean openDialog)
	{
		_player = player;
		_openDialog = openDialog;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_FACTION_INFO.writeId(this);
		writeInt(_player.getObjectId());
		writeByte(_openDialog);
		writeInt(Faction.values().length);
		for (Faction faction : Faction.values())
		{
			writeByte(faction.getId());
			writeShort(_player.getFactionLevel(faction));
			writeFloat(_player.getFactionProgress(faction));
		}
	}
}
