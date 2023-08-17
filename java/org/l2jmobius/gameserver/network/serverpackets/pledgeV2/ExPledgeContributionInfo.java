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

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExPledgeContributionInfo extends ServerPacket
{
	private final Player _player;
	
	public ExPledgeContributionInfo(Player player)
	{
		_player = player;
	}
	
	@Override
	public void write()
	{
		if (_player.getClan() == null)
		{
			return;
		}
		
		ServerPackets.EX_PLEDGE_CONTRIBUTION_INFO.writeId(this);
		writeInt(_player.getClanContribution());
		writeInt(_player.getClanContribution());
		writeInt(Config.CLAN_CONTRIBUTION_REQUIRED);
		writeInt(-1);
		writeInt(0);
		writeInt(Config.CLAN_CONTRIBUTION_FAME_REWARD);
	}
}
