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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.l2jmobius.gameserver.data.xml.RecipeData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.RecipeHolder;
import org.l2jmobius.gameserver.network.ServerPackets;

public class RecipeShopManageList extends ServerPacket
{
	private final Player _seller;
	private final boolean _isDwarven;
	private final Collection<RecipeHolder> _recipes;
	private List<Entry<Integer, Long>> _manufacture;
	
	public RecipeShopManageList(Player seller, boolean isDwarven)
	{
		_seller = seller;
		_isDwarven = isDwarven;
		_recipes = (isDwarven && (_seller.getCreateItemLevel() > 0)) ? _seller.getDwarvenRecipeBook() : _seller.getCommonRecipeBook();
		if (_seller.hasManufactureShop())
		{
			_manufacture = new ArrayList<>();
			for (Entry<Integer, Long> item : _seller.getManufactureItems().entrySet())
			{
				final RecipeHolder recipe = RecipeData.getInstance().getRecipe(item.getKey());
				if (((recipe != null) && (recipe.isDwarvenRecipe() == _isDwarven)) && seller.hasRecipeList(recipe.getId()))
				{
					_manufacture.add(item);
				}
			}
		}
	}
	
	@Override
	public void write()
	{
		ServerPackets.RECIPE_SHOP_MANAGE_LIST.writeId(this);
		writeInt(_seller.getObjectId());
		writeInt((int) _seller.getAdena());
		writeInt(!_isDwarven);
		if ((_recipes == null) || _recipes.isEmpty())
		{
			writeInt(0);
		}
		else
		{
			writeInt(_recipes.size()); // number of items in recipe book
			int count = 1;
			for (RecipeHolder recipe : _recipes)
			{
				writeInt(recipe.getId());
				writeInt(count++);
			}
		}
		if ((_manufacture == null) || _manufacture.isEmpty())
		{
			writeInt(0);
		}
		else
		{
			writeInt(_manufacture.size());
			for (Entry<Integer, Long> item : _manufacture)
			{
				writeInt(item.getKey());
				writeInt(0); // CanCraft?
				writeLong(item.getValue());
			}
		}
	}
}
