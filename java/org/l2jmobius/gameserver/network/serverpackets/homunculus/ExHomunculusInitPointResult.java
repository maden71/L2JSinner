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
package org.l2jmobius.gameserver.network.serverpackets.homunculus;

import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author nexvill
 */
public class ExHomunculusInitPointResult extends ServerPacket
{
	private final boolean _success;
	private final int _type;
	
	public ExHomunculusInitPointResult(boolean success, int type)
	{
		_success = success;
		_type = type;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_HOMUNCULUS_INIT_POINT_RESULT.writeId(this);
		if (_success)
		{
			writeInt(1); // success
			writeInt(_type); // init type
			writeInt(SystemMessageId.THE_RECEIVED_UPGRADE_POINTS_ARE_RESET.getId());
		}
		else
		{
			writeInt(0);
			writeInt(_type);
			writeInt(SystemMessageId.NOT_ENOUGH_ITEMS_FOR_RESETTING.getId());
		}
	}
}