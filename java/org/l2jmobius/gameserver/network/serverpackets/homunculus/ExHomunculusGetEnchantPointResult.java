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
public class ExHomunculusGetEnchantPointResult extends ServerPacket
{
	private final int _enchantType;
	
	public ExHomunculusGetEnchantPointResult(int enchantType)
	{
		_enchantType = enchantType;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_HOMUNCULUS_GET_ENCHANT_POINT_RESULT.writeId(this);
		if (_enchantType != 2)
		{
			writeInt(1); // success
			writeInt(_enchantType);
			writeInt(SystemMessageId.YOU_VE_OBTAINED_UPGRADE_POINTS.getId());
		}
		else
		{
			writeInt(1);
			writeInt(_enchantType);
			writeInt(SystemMessageId.VP_ADDED.getId());
		}
	}
}