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

import org.l2jmobius.gameserver.data.xml.ClanShopData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.ClanShopProductHolder;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.AbstractItemPacket;

/**
 * @author Mobius
 */
public class ExPledgeItemList extends AbstractItemPacket
{
	final Player _player;
	
	public ExPledgeItemList(Player player)
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
		
		ServerPackets.EX_PLEDGE_ITEM_LIST.writeId(this);
		writeShort(ClanShopData.getInstance().getProducts().size()); // Product count.
		for (ClanShopProductHolder product : ClanShopData.getInstance().getProducts())
		{
			writeItem(product.getTradeItem());
			writeByte(_player.getClan().getLevel() < product.getClanLevel() ? 0 : 2); // 0 locked, 1 need activation, 2 available
			writeLong(product.getAdena()); // Purchase price: adena
			writeInt(product.getFame()); // Purchase price: fame
			writeByte(product.getClanLevel()); // Required pledge level
			writeByte(0); // Required pledge mastery
			writeLong(0); // Activation price: adena
			writeInt(0); // Activation price: reputation
			writeInt(0); // Time to deactivation
			writeInt(0); // Time to restock
			writeShort(0); // Current stock
			writeShort(0); // Total stock
		}
	}
}
