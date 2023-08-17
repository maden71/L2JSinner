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
package org.l2jmobius.gameserver.network.serverpackets.prison;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Fakee
 */
public class ExPrisonUserInfo extends ServerPacket
{
	private final int _prisonType;
	private final int _itemAmount;
	private final int _remainTime;
	
	public ExPrisonUserInfo(Player player, int prisonType)
	{
		_prisonType = prisonType;
		if (_prisonType == 3)
		{
			_itemAmount = player.getVariables().getInt(PlayerVariables.PRISON_3_POINTS, 0);
		}
		else if (_prisonType == 2)
		{
			_itemAmount = player.getVariables().getInt(PlayerVariables.PRISON_2_POINTS, 0);
		}
		else
		{
			_itemAmount = 0;
		}
		_remainTime = (int) ((player.getVariables().getLong(PlayerVariables.PRISON_WAIT_TIME, 0) - System.currentTimeMillis()) / 1000);
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_PRISON_USER_INFO.writeId(this);
		writeByte(_prisonType);
		writeInt(_itemAmount);
		writeInt(_remainTime);
	}
}
