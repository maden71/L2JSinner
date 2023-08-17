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
 * @author Mobius
 */
public class ExActivateHomunculusResult extends ServerPacket
{
	private final boolean _activate;
	
	public ExActivateHomunculusResult(boolean activate)
	{
		_activate = activate;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_ACTIVATE_HOMUNCULUS_RESULT.writeId(this);
		writeInt(1); // success
		if (_activate)
		{
			writeByte(1); // activate
			writeInt(SystemMessageId.THE_RELATIONS_ARE_BEING_ESTABLISHED.getId());
		}
		else
		{
			writeByte(0); // activate
			writeInt(SystemMessageId.THE_RELATIONS_ARE_BROKEN.getId());
		}
	}
}
