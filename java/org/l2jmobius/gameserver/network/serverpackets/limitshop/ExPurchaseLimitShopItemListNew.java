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
package org.l2jmobius.gameserver.network.serverpackets.limitshop;

import java.util.Collection;
import java.util.Collections;

import org.l2jmobius.gameserver.data.xml.LimitShopCraftData;
import org.l2jmobius.gameserver.data.xml.LimitShopData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.LimitShopProductHolder;
import org.l2jmobius.gameserver.model.variables.AccountVariables;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Mobius
 */
public class ExPurchaseLimitShopItemListNew extends ServerPacket
{
	private final int _shopType; // 3 Lcoin Store, 4 Special Craft, 100 Clan Shop
	private final Player _player;
	private final Collection<LimitShopProductHolder> _products;
	
	public ExPurchaseLimitShopItemListNew(int shopType, Player player)
	{
		_shopType = shopType;
		_player = player;
		switch (shopType)
		{
			case 3: // Normal Lcoin Shop
			{
				_products = LimitShopData.getInstance().getProducts();
				break;
			}
			case 4: // Lcoin Special Craft
			{
				_products = LimitShopCraftData.getInstance().getProducts();
				break;
			}
			default:
			{
				_products = Collections.emptyList();
			}
		}
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_PURCHASE_LIMIT_SHOP_ITEM_LIST_NEW.writeId(this);
		writeByte(_shopType);
		writeByte(1); // Page. (311)
		writeByte(1); // MaxPage. (311)
		writeInt(_products.size());
		for (LimitShopProductHolder product : _products)
		{
			writeInt(product.getId());
			writeInt(product.getProductionId());
			writeInt(product.getIngredientIds()[0]);
			writeInt(product.getIngredientIds()[1]);
			writeInt(product.getIngredientIds()[2]);
			writeInt(product.getIngredientIds()[3]); // 306
			writeInt(product.getIngredientIds()[4]); // 306
			writeLong(product.getIngredientQuantities()[0]);
			writeLong(product.getIngredientQuantities()[1]);
			writeLong(product.getIngredientQuantities()[2]);
			writeLong(product.getIngredientQuantities()[3]); // 306
			writeLong(product.getIngredientQuantities()[4]); // 306
			writeShort(product.getIngredientEnchants()[0]);
			writeShort(product.getIngredientEnchants()[1]);
			writeShort(product.getIngredientEnchants()[2]);
			writeShort(product.getIngredientEnchants()[3]); // 306
			writeShort(product.getIngredientEnchants()[4]); // 306
			// Check limits.
			if (product.getAccountDailyLimit() > 0) // Sale period.
			{
				if (_player.getAccountVariables().getInt(AccountVariables.LCOIN_SHOP_PRODUCT_DAILY_COUNT + product.getProductionId(), 0) >= product.getAccountDailyLimit())
				{
					writeInt(0);
				}
				else
				{
					writeInt(product.getAccountDailyLimit() - _player.getAccountVariables().getInt(AccountVariables.LCOIN_SHOP_PRODUCT_DAILY_COUNT + product.getProductionId(), 0));
				}
			}
			else if (product.getAccountBuyLimit() > 0) // Count limit.
			{
				if (_player.getAccountVariables().getInt(AccountVariables.LCOIN_SHOP_PRODUCT_COUNT + product.getProductionId(), 0) >= product.getAccountBuyLimit())
				{
					writeInt(0);
				}
				else
				{
					writeInt(product.getAccountBuyLimit() - _player.getAccountVariables().getInt(AccountVariables.LCOIN_SHOP_PRODUCT_COUNT + product.getProductionId(), 0));
				}
			}
			else // No account limits.
			{
				writeInt(1);
			}
			writeInt(0); // nRemainSec
			writeInt(0); // nRemainServerItemAmount
			writeShort(0); // sCircleNum (311)
		}
	}
}
