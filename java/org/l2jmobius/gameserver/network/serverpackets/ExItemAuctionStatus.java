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
package org.l2jmobius.gameserver.network.serverpackets;

import org.l2jmobius.gameserver.data.SpawnTable;
import org.l2jmobius.gameserver.instancemanager.ItemAuctionManager;
import org.l2jmobius.gameserver.model.Spawn;
import org.l2jmobius.gameserver.model.itemauction.ItemAuction;
import org.l2jmobius.gameserver.model.itemauction.ItemAuctionInstance;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Index, Gaikotsu
 */
public class ExItemAuctionStatus extends ServerPacket
{
	private static final int AUCTION_MANAGER = 34328;
	
	private int _x = 0;
	private int _y = 0;
	private int _z = 0;
	private int _status = 0;
	
	public ExItemAuctionStatus()
	{
		final Spawn spawn = SpawnTable.getInstance().getAnySpawn(AUCTION_MANAGER);
		if (spawn != null)
		{
			_x = spawn.getX();
			_y = spawn.getY();
			_z = spawn.getZ();
			final ItemAuctionInstance manager = ItemAuctionManager.getInstance().getManagerInstance(AUCTION_MANAGER);
			if (manager != null)
			{
				final ItemAuction auction = manager.getCurrentAuction();
				if (auction != null)
				{
					_status = 1;
				}
			}
		}
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_ITEM_AUCTION_STATUS.writeId(this);
		writeInt(_x);
		writeInt(_y);
		writeInt(_z);
		writeInt(0);
		writeShort(_status);
	}
}
