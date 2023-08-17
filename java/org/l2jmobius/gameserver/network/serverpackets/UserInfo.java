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

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.data.xml.ExperienceData;
import org.l2jmobius.gameserver.enums.AttributeType;
import org.l2jmobius.gameserver.enums.ItemGrade;
import org.l2jmobius.gameserver.enums.UserInfoType;
import org.l2jmobius.gameserver.instancemanager.CastleManager;
import org.l2jmobius.gameserver.instancemanager.CursedWeaponsManager;
import org.l2jmobius.gameserver.instancemanager.RankManager;
import org.l2jmobius.gameserver.model.Party;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author Sdw, UnAfraid
 */
public class UserInfo extends AbstractMaskPacket<UserInfoType>
{
	private Player _player;
	private int _relation;
	private int _runSpd;
	private int _walkSpd;
	private int _swimRunSpd;
	private int _swimWalkSpd;
	private final int _flRunSpd = 0;
	private final int _flWalkSpd = 0;
	private int _flyRunSpd;
	private int _flyWalkSpd;
	private double _moveMultiplier;
	private int _enchantLevel;
	private int _armorEnchant;
	private String _title;
	private final byte[] _masks = new byte[]
	{
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0x00
	};
	private int _initSize = 5;
	
	public UserInfo(Player player)
	{
		this(player, true);
	}
	
	public UserInfo(Player player, boolean addAll)
	{
		if (!player.isSubclassLocked()) // Changing class.
		{
			_player = player;
			_relation = calculateRelation(player);
			_moveMultiplier = player.getMovementSpeedMultiplier();
			_runSpd = (int) Math.round(player.getRunSpeed() / _moveMultiplier);
			_walkSpd = (int) Math.round(player.getWalkSpeed() / _moveMultiplier);
			_swimRunSpd = (int) Math.round(player.getSwimRunSpeed() / _moveMultiplier);
			_swimWalkSpd = (int) Math.round(player.getSwimWalkSpeed() / _moveMultiplier);
			_flyRunSpd = player.isFlying() ? _runSpd : 0;
			_flyWalkSpd = player.isFlying() ? _walkSpd : 0;
			_enchantLevel = player.getInventory().getWeaponEnchant();
			_armorEnchant = player.getInventory().getArmorMinEnchant();
			_title = player.getTitle();
			
			if (player.isGM() && player.isInvisible())
			{
				_title = "[Invisible]";
			}
			
			if (addAll)
			{
				addComponentType(UserInfoType.values());
			}
		}
	}
	
	@Override
	protected byte[] getMasks()
	{
		return _masks;
	}
	
	@Override
	protected void onNewMaskAdded(UserInfoType component)
	{
		calcBlockSize(component);
	}
	
	private void calcBlockSize(UserInfoType type)
	{
		switch (type)
		{
			case BASIC_INFO:
			{
				_initSize += type.getBlockLength() + (_player.getAppearance().getVisibleName().length() * 2);
				break;
			}
			case CLAN:
			{
				_initSize += type.getBlockLength() + (_title.length() * 2);
				break;
			}
			default:
			{
				_initSize += type.getBlockLength();
				break;
			}
		}
	}
	
	@Override
	public void write()
	{
		if (_player == null)
		{
			return;
		}
		
		ServerPackets.USER_INFO.writeId(this);
		writeInt(_player.getObjectId());
		writeInt(_initSize);
		writeShort(29); // 362 - 29
		writeBytes(_masks);
		if (containsMask(UserInfoType.RELATION))
		{
			writeInt(_relation);
		}
		if (containsMask(UserInfoType.BASIC_INFO))
		{
			writeShort(23 + (_player.getAppearance().getVisibleName().length() * 2));
			writeSizedString(_player.getName());
			writeByte(_player.isGM() ? 1 : 0);
			writeByte(_player.getRace().ordinal());
			writeByte(_player.getAppearance().isFemale() ? 1 : 0);
			writeInt(_player.isDeathKnight() && _player.isSubClassActive() ? 0 : _player.getBaseTemplate().getClassId().getRootClassId().getId());
			writeInt(_player.getClassId().getId());
			writeInt(_player.getLevel()); // 270
			writeInt(_player.getClassId().getId()); // 286
		}
		if (containsMask(UserInfoType.BASE_STATS))
		{
			writeShort(18);
			writeShort(_player.getSTR());
			writeShort(_player.getDEX());
			writeShort(_player.getCON());
			writeShort(_player.getINT());
			writeShort(_player.getWIT());
			writeShort(_player.getMEN());
			writeShort(_player.getLUC());
			writeShort(_player.getCHA());
		}
		if (containsMask(UserInfoType.MAX_HPCPMP))
		{
			writeShort(14);
			writeInt(_player.getMaxHp());
			writeInt(_player.getMaxMp());
			writeInt(_player.getMaxCp());
		}
		if (containsMask(UserInfoType.CURRENT_HPMPCP_EXP_SP))
		{
			writeShort(38);
			writeInt((int) Math.round(_player.getCurrentHp()));
			writeInt((int) Math.round(_player.getCurrentMp()));
			writeInt((int) Math.round(_player.getCurrentCp()));
			writeLong(_player.getSp());
			writeLong(_player.getExp());
			writeDouble((float) (_player.getExp() - ExperienceData.getInstance().getExpForLevel(_player.getLevel())) / (ExperienceData.getInstance().getExpForLevel(_player.getLevel() + 1) - ExperienceData.getInstance().getExpForLevel(_player.getLevel())));
		}
		if (containsMask(UserInfoType.ENCHANTLEVEL))
		{
			writeShort(5); // 338
			writeByte(_enchantLevel);
			writeByte(_armorEnchant);
			writeByte(0); // 338 - cBackEnchant?
		}
		if (containsMask(UserInfoType.APPAREANCE))
		{
			writeShort(19); // 338
			writeInt(_player.getVisualHair());
			writeInt(_player.getVisualHairColor());
			writeInt(_player.getVisualFace());
			writeByte(_player.isHairAccessoryEnabled() ? 1 : 0);
			writeInt(_player.getVisualHairColor() + 1); // 338 - DK color.
		}
		if (containsMask(UserInfoType.STATUS))
		{
			writeShort(6);
			writeByte(_player.getMountType().ordinal());
			writeByte(_player.getPrivateStoreType().getId());
			writeByte(_player.getCrystallizeGrade() != ItemGrade.NONE ? 1 : 0);
			writeByte(_player.getAbilityPoints() - _player.getAbilityPointsUsed());
		}
		if (containsMask(UserInfoType.STATS))
		{
			writeShort(64); // 270
			writeShort(_player.getActiveWeaponItem() != null ? 40 : 20);
			writeInt(_player.getPAtk());
			writeInt(_player.getPAtkSpd());
			writeInt(_player.getPDef());
			writeInt(_player.getEvasionRate());
			writeInt(_player.getAccuracy());
			writeInt(_player.getCriticalHit());
			writeInt(_player.getMAtk());
			writeInt(_player.getMAtkSpd());
			writeInt(_player.getPAtkSpd()); // Seems like atk speed - 1
			writeInt(_player.getMagicEvasionRate());
			writeInt(_player.getMDef());
			writeInt(_player.getMagicAccuracy());
			writeInt(_player.getMCriticalHit());
			writeInt(_player.getStat().getWeaponBonusPAtk()); // 270
			writeInt(_player.getStat().getWeaponBonusMAtk()); // 270
		}
		if (containsMask(UserInfoType.ELEMENTALS))
		{
			writeShort(14);
			writeShort(_player.getDefenseElementValue(AttributeType.FIRE));
			writeShort(_player.getDefenseElementValue(AttributeType.WATER));
			writeShort(_player.getDefenseElementValue(AttributeType.WIND));
			writeShort(_player.getDefenseElementValue(AttributeType.EARTH));
			writeShort(_player.getDefenseElementValue(AttributeType.HOLY));
			writeShort(_player.getDefenseElementValue(AttributeType.DARK));
		}
		if (containsMask(UserInfoType.POSITION))
		{
			writeShort(18);
			writeInt(_player.getX());
			writeInt(_player.getY());
			writeInt(_player.getZ());
			writeInt(_player.isInVehicle() ? _player.getVehicle().getObjectId() : 0);
		}
		if (containsMask(UserInfoType.SPEED))
		{
			writeShort(18);
			writeShort(_runSpd);
			writeShort(_walkSpd);
			writeShort(_swimRunSpd);
			writeShort(_swimWalkSpd);
			writeShort(_flRunSpd);
			writeShort(_flWalkSpd);
			writeShort(_flyRunSpd);
			writeShort(_flyWalkSpd);
		}
		if (containsMask(UserInfoType.MULTIPLIER))
		{
			writeShort(18);
			writeDouble(_moveMultiplier);
			writeDouble(_player.getAttackSpeedMultiplier());
		}
		if (containsMask(UserInfoType.COL_RADIUS_HEIGHT))
		{
			writeShort(18);
			writeDouble(_player.getCollisionRadius());
			writeDouble(_player.getCollisionHeight());
		}
		if (containsMask(UserInfoType.ATK_ELEMENTAL))
		{
			writeShort(5);
			final AttributeType attackAttribute = _player.getAttackElement();
			writeByte(attackAttribute.getClientId());
			writeShort(_player.getAttackElementValue(attackAttribute));
		}
		if (containsMask(UserInfoType.CLAN))
		{
			writeShort(32 + (_title.length() * 2));
			writeSizedString(_title);
			writeShort(_player.getPledgeType());
			writeInt(_player.getClanId());
			writeInt(_player.getClanCrestLargeId());
			writeInt(_player.getClanCrestId());
			writeInt(_player.getClanPrivileges().getBitmask());
			writeByte(_player.isClanLeader() ? 1 : 0);
			writeInt(_player.getAllyId());
			writeInt(_player.getAllyCrestId());
			writeByte(_player.isInMatchingRoom() ? 1 : 0);
		}
		if (containsMask(UserInfoType.SOCIAL))
		{
			writeShort(30); // 228
			writeByte(_player.getPvpFlag());
			writeInt(_player.getReputation()); // Reputation
			writeByte(_player.getNobleLevel());
			writeByte(_player.isLegend() ? 4 : _player.isHero() || (_player.isGM() && Config.GM_HERO_AURA) ? 2 : 0); // 152 - Value for enabled changed to 2? 4 = legend
			writeByte(_player.getPledgeClass());
			writeInt(_player.getPkKills());
			writeInt(_player.getPvpKills());
			writeShort(_player.getRecomLeft());
			writeShort(_player.getRecomHave());
			// AFK animation.
			if ((_player.getClan() != null) && (CastleManager.getInstance().getCastleByOwner(_player.getClan()) != null)) // 196
			{
				writeInt(_player.isClanLeader() ? 100 : 101);
			}
			else
			{
				writeInt(0);
			}
			writeInt(0); // 228
		}
		if (containsMask(UserInfoType.VITA_FAME))
		{
			writeShort(19); // 196
			writeInt(_player.getVitalityPoints());
			writeByte(0); // Vita Bonus
			writeInt(_player.getFame());
			writeInt(_player.getRaidbossPoints());
			writeByte(0); // 196
			writeShort(_player.getSymbolSealPoints()); // Henna Seal Engraving Gauge
			writeByte(0); // 196
		}
		if (containsMask(UserInfoType.SLOTS))
		{
			writeShort(12); // 152
			writeByte(_player.getInventory().getTalismanSlots());
			writeByte(_player.getInventory().getBroochJewelSlots());
			writeByte(_player.getTeam().getId());
			writeInt(0);
			if (_player.getInventory().getAgathionSlots() > 0)
			{
				writeByte(1); // Charm slots
				writeByte(_player.getInventory().getAgathionSlots() - 1);
			}
			else
			{
				writeByte(0); // Charm slots
				writeByte(0);
			}
			writeByte(_player.getInventory().getArtifactSlots()); // Artifact set slots // 152
		}
		if (containsMask(UserInfoType.MOVEMENTS))
		{
			writeShort(4);
			writeByte(_player.isInsideZone(ZoneId.WATER) ? 1 : _player.isFlyingMounted() ? 2 : 0);
			writeByte(_player.isRunning() ? 1 : 0);
		}
		if (containsMask(UserInfoType.COLOR))
		{
			writeShort(10);
			writeInt(_player.getAppearance().getNameColor());
			writeInt(_player.getAppearance().getTitleColor());
		}
		if (containsMask(UserInfoType.INVENTORY_LIMIT))
		{
			writeShort(13);
			writeShort(0);
			writeShort(0);
			writeShort(_player.getInventoryLimit());
			writeByte(_player.isCursedWeaponEquipped() ? CursedWeaponsManager.getInstance().getLevel(_player.getCursedWeaponEquippedId()) : 0);
			writeByte(0); // 196
			writeByte(0); // 196
			writeByte(0); // 196
			writeByte(0); // 196
		}
		if (containsMask(UserInfoType.TRUE_HERO))
		{
			writeShort(9);
			writeInt(0);
			writeShort(0);
			writeByte(_player.isTrueHero() ? 100 : 0);
		}
		if (containsMask(UserInfoType.ATT_SPIRITS)) // 152
		{
			writeShort(26);
			writeInt(-1);
			writeInt(0);
			writeInt(0);
			writeInt(0);
			writeInt(0);
			writeInt(0);
		}
		if (containsMask(UserInfoType.RANKING)) // 196
		{
			writeShort(6);
			writeInt(RankManager.getInstance().getPlayerGlobalRank(_player) == 1 ? 1 : RankManager.getInstance().getPlayerRaceRank(_player) == 1 ? 2 : RankManager.getInstance().getPlayerClassRank(_player) == 1 ? 4 : 0);
		}
		if (containsMask(UserInfoType.STAT_POINTS)) // 235
		{
			writeShort(16);
			writeShort(0); // Usable points?
			writeShort(0); // STR points
			writeShort(0); // DEX points
			writeShort(0); // CON points
			writeShort(0); // INT points
			writeShort(0); // WIT points
			writeShort(0); // MEN points
		}
		if (containsMask(UserInfoType.STAT_ABILITIES)) // 235
		{
			writeShort(18);
			writeShort(0); // STR additional
			writeShort(0); // DEX additional
			writeShort(0); // CON additional
			writeShort(0); // INT additional
			writeShort(0); // WIT additional
			writeShort(0); // MEN additional
			writeShort(0); // ?
			writeShort(0); // ?
		}
		if (containsMask(UserInfoType.ELIXIR_USED)) // 286
		{
			writeShort(1);
			writeShort(0);
		}
		
		if (containsMask(UserInfoType.VANGUARD_MOUNT)) // 362
		{
			writeByte(0); // 362 - Vanguard mount.
		}
		
		// Send exp bonus change.
		if (containsMask(UserInfoType.VITA_FAME))
		{
			_player.sendUserBoostStat();
		}
	}
	
	private int calculateRelation(Player player)
	{
		int relation = 0;
		final Party party = player.getParty();
		final Clan clan = player.getClan();
		if (party != null)
		{
			relation |= 8; // Party member
			if (party.getLeader() == _player)
			{
				relation |= 16; // Party leader
			}
		}
		if (clan != null)
		{
			
			if (player.getSiegeState() == 1)
			{
				relation |= 256; // Clan member
			}
			else if (player.getSiegeState() == 2)
			{
				relation |= 32; // Clan member
			}
			if (clan.getLeaderId() == player.getObjectId())
			{
				relation |= 64; // Clan leader
			}
		}
		if (player.getSiegeState() != 0)
		{
			relation |= 128; // In siege
		}
		return relation;
	}
}
