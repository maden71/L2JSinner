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
package org.l2jmobius.gameserver.network.clientpackets.herobook;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.commons.network.ReadablePacket;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.herobook.HeroBookManager;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.herobook.ExHeroBookCharge;
import org.l2jmobius.gameserver.network.serverpackets.herobook.ExHeroBookInfo;

/**
 * @author Index
 */
public class RequestHeroBookCharge implements ClientPacket
{
	private final Map<Integer, Long> _items = new HashMap<>();
	
	@Override
	public void read(ReadablePacket packet)
	{
		final int size = packet.readInt();
		for (int curr = 0; curr < size; curr++)
		{
			_items.put(packet.readInt(), packet.readLong());
		}
	}
	
	@Override
	public void run(GameClient client)
	{
		Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		final boolean isSuccess = new HeroBookManager().tryEnchant(player, _items);
		player.sendPacket(new ExHeroBookCharge(isSuccess));
		player.sendPacket(new ExHeroBookInfo(player.getHeroBookProgress()));
	}
}
