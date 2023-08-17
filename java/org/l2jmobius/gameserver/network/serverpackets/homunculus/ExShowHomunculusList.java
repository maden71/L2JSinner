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

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.homunculus.Homunculus;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExShowHomunculusList extends ServerPacket
{
	private final Player _player;
	
	public ExShowHomunculusList(Player player)
	{
		_player = player;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_SHOW_HOMUNCULUS_LIST.writeId(this);
		int counter = 0;
		final int slotCount = _player.getAvailableHomunculusSlotCount();
		writeInt(slotCount);
		for (int i = 0; i <= slotCount; i++)
		{
			if (_player.getHomunculusList().get(i) != null)
			{
				final Homunculus homunculus = _player.getHomunculusList().get(i);
				writeInt(counter); // slot
				writeInt(homunculus.getId()); // homunculus id
				writeInt(homunculus.getType());
				writeByte(homunculus.isActive());
				writeInt(homunculus.getTemplate().getBasicSkillId());
				writeInt(homunculus.getSkillLevel1() > 0 ? homunculus.getTemplate().getSkillId1() : 0);
				writeInt(homunculus.getSkillLevel2() > 0 ? homunculus.getTemplate().getSkillId2() : 0);
				writeInt(homunculus.getSkillLevel3() > 0 ? homunculus.getTemplate().getSkillId3() : 0);
				writeInt(homunculus.getSkillLevel4() > 0 ? homunculus.getTemplate().getSkillId4() : 0);
				writeInt(homunculus.getSkillLevel5() > 0 ? homunculus.getTemplate().getSkillId5() : 0);
				writeInt(homunculus.getTemplate().getBasicSkillLevel());
				writeInt(homunculus.getSkillLevel1());
				writeInt(homunculus.getSkillLevel2());
				writeInt(homunculus.getSkillLevel3());
				writeInt(homunculus.getSkillLevel4());
				writeInt(homunculus.getSkillLevel5());
				writeInt(homunculus.getLevel());
				writeInt(homunculus.getExp());
				writeInt(homunculus.getHp());
				writeInt(homunculus.getAtk());
				writeInt(homunculus.getDef());
				writeInt(homunculus.getCritRate());
			}
			else
			{
				writeInt(counter); // slot
				writeInt(0); // homunculus id
				writeInt(0);
				writeByte(0);
				writeInt(0);
				for (int j = 1; j <= 5; j++)
				{
					writeInt(0);
				}
				writeInt(0);
				for (int j = 1; j <= 5; j++)
				{
					writeInt(0);
				}
				writeInt(0); // Level
				writeInt(0); // HP
				writeInt(0); // HP
				writeInt(0); // Attack
				writeInt(0); // Defence
				writeInt(0); // Critical
			}
			counter++;
		}
	}
}
