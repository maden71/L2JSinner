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

import org.l2jmobius.gameserver.data.xml.ClanMasteryData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.holders.ClanMasteryHolder;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.AbstractItemPacket;

/**
 * @author Mobius
 */
public class ExPledgeMasteryInfo extends AbstractItemPacket
{
	final Player _player;
	
	public ExPledgeMasteryInfo(Player player)
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
		
		ServerPackets.EX_PLEDGE_MASTERY_INFO.writeId(this);
		writeInt(clan.getUsedDevelopmentPoints()); // Consumed development points
		writeInt(clan.getTotalDevelopmentPoints()); // Total development points
		writeInt(16); // Mastery count
		for (ClanMasteryHolder mastery : ClanMasteryData.getInstance().getMasteries())
		{
			if (mastery.getId() < 17)
			{
				final int id = mastery.getId();
				writeInt(id); // Mastery
				writeInt(0); // ?
				boolean available = true;
				if (clan.getLevel() < mastery.getClanLevel())
				{
					available = false;
				}
				else
				{
					final int previous = mastery.getPreviousMastery();
					final int previousAlt = mastery.getPreviousMasteryAlt();
					if (previousAlt > 0)
					{
						available = clan.hasMastery(previous) || clan.hasMastery(previousAlt);
					}
					else if (previous > 0)
					{
						available = clan.hasMastery(previous);
					}
				}
				writeByte(clan.hasMastery(id) ? 2 : available ? 1 : 0); // Availability.
			}
		}
	}
}
