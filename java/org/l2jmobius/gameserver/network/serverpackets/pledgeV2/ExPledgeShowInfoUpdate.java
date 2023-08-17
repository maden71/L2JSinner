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
package org.l2jmobius.gameserver.network.serverpackets.pledgeV2;

import org.l2jmobius.gameserver.data.xml.ClanLevelData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.AbstractItemPacket;

/**
 * @author Mobius
 */
public class ExPledgeShowInfoUpdate extends AbstractItemPacket
{
	final Player _player;
	
	public ExPledgeShowInfoUpdate(Player player)
	{
		_player = player;
	}
	
	@Override
	public void write()
	{
		final Clan clan = _player.getClan();
		if (clan == null)
		{
			return;
		}
		
		ServerPackets.EX_PLEDGE_SHOW_INFO_UPDATE.writeId(this);
		writeInt(clan.getId()); // Clan ID
		writeInt(ClanLevelData.getLevelRequirement(clan.getLevel())); // Next level cost
		writeInt(ClanLevelData.getCommonMemberLimit(clan.getLevel())); // Max pledge members
		writeInt(ClanLevelData.getEliteMemberLimit(clan.getLevel())); // Max elite members
	}
}
