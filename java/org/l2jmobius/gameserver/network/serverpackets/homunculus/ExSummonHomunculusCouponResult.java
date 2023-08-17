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
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author `NasSeKa`
 */
public class ExSummonHomunculusCouponResult extends ServerPacket
{
	private final int _slot;
	private final int _success;
	
	public ExSummonHomunculusCouponResult(int success, int slot)
	{
		_success = success;
		_slot = slot;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_SUMMON_HOMUNCULUS_COUPON_RESULT.writeId(this);
		writeInt(_success);
		writeInt(_slot); // homunculus slot
		writeInt(0); // keep or delete
	}
}
