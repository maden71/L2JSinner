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

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.homunculus.Homunculus;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author nexvill
 */
public class ExEnchantHomunculusSkillResult extends ServerPacket
{
	private final Player _player;
	private final int _slot;
	private final int _skillNumber;
	
	public ExEnchantHomunculusSkillResult(Player player, int slot, int skillNumber)
	{
		_player = player;
		_slot = slot;
		_skillNumber = skillNumber;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_ENCHANT_HOMUNCULUS_SKILL_RESULT.writeId(this);
		final int playerNumber = Rnd.get(1, 6);
		final int homunculusNumber = Rnd.get(1, 6);
		final int systemNumber = Rnd.get(1, 6);
		final Homunculus homunculus = _player.getHomunculusList().get(_slot);
		int boundLevel = 1;
		if ((playerNumber == homunculusNumber) && (playerNumber == systemNumber))
		{
			boundLevel = 3;
		}
		else if ((playerNumber == homunculusNumber) || (playerNumber == systemNumber) || (homunculusNumber == systemNumber))
		{
			boundLevel = 2;
		}
		switch (_skillNumber)
		{
			case 1:
			{
				homunculus.setSkillLevel1(boundLevel);
				break;
			}
			case 2:
			{
				homunculus.setSkillLevel2(boundLevel);
				break;
			}
			case 3:
			{
				homunculus.setSkillLevel3(boundLevel);
				break;
			}
			case 4:
			{
				homunculus.setSkillLevel4(boundLevel);
				break;
			}
			case 5:
			{
				homunculus.setSkillLevel5(boundLevel);
				break;
			}
		}
		_player.getHomunculusList().update(homunculus);
		_player.getHomunculusList().refreshStats(true);
		writeInt(boundLevel); // skill bound level result
		writeInt(homunculus.getId()); // homunculus id? random value on JP
		writeInt(_slot); // slot
		writeInt(_skillNumber); // skill number
		writeInt(playerNumber); // player number
		writeInt(homunculusNumber); // homunculus number
		writeInt(systemNumber); // system number
	}
}