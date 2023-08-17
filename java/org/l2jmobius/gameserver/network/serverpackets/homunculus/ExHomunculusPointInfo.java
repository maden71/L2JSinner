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
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExHomunculusPointInfo extends ServerPacket
{
	private final Player _player;
	
	public ExHomunculusPointInfo(Player player)
	{
		_player = player;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_HOMUNCULUS_POINT_INFO.writeId(this);
		writeInt(_player.getVariables().getInt(PlayerVariables.HOMUNCULUS_UPGRADE_POINTS, 0)); // points
		writeInt(_player.getVariables().getInt(PlayerVariables.HOMUNCULUS_KILLED_MOBS, 0)); // killed mobs
		writeInt(_player.getVariables().getInt(PlayerVariables.HOMUNCULUS_USED_KILL_CONVERT, 0)); // consumed basic kill points
		writeInt(_player.getVariables().getInt(PlayerVariables.HOMUNCULUS_USED_RESET_KILLS, 0)); // consumed reset kill points?
		writeInt(_player.getVariables().getInt(PlayerVariables.HOMUNCULUS_USED_VP_POINTS, 0)); // vp points
		writeInt(_player.getVariables().getInt(PlayerVariables.HOMUNCULUS_USED_VP_CONVERT, 0)); // consumed basic vp points
		writeInt(_player.getVariables().getInt(PlayerVariables.HOMUNCULUS_USED_RESET_VP, 0)); // consumed reset vp points
		writeInt(_player.getAvailableHomunculusSlotCount()); // 306
	}
}
