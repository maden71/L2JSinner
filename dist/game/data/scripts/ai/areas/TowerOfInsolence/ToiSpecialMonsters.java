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
package ai.areas.TowerOfInsolence;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;

import ai.AbstractNpcAI;

/**
 * Tower of Insolence Special Mobs AI.
 * @URL https://l2central.info/main/locations/special_zones/tower_of_insolence/
 * @author CostyKiller
 * @TODO Find and add fixed spawn locations for elite monsters.
 */
public class ToiSpecialMonsters extends AbstractNpcAI
{
	// Elite Monsters
	private static final int ELITE_MONSTER_FLOOR4 = 24863;
	private static final int ELITE_MONSTER_FLOOR5 = 24869;
	private static final int ELITE_MONSTER_FLOOR6 = 24875;
	private static final int ELITE_MONSTER_FLOOR7 = 24901;
	private static final int ELITE_MONSTER_FLOOR8 = 24907;
	private static final int ELITE_MONSTER_FLOOR9 = 24913;
	// Mimic Baium's Treasure
	private static final int MIMIC_TREASURE_FLOOR1 = 24887;
	private static final int MIMIC_TREASURE_FLOOR2 = 24888;
	private static final int MIMIC_TREASURE_FLOOR3 = 24889;
	private static final int MIMIC_TREASURE_FLOOR4 = 24890;
	private static final int MIMIC_TREASURE_FLOOR5 = 24891;
	private static final int MIMIC_TREASURE_FLOOR6 = 24892;
	private static final int MIMIC_TREASURE_FLOOR7 = 24893;
	private static final int MIMIC_TREASURE_FLOOR8 = 24894;
	private static final int MIMIC_TREASURE_FLOOR9 = 24895;
	// Elite Monsters Reward
	private static final int ENHANCED_RUNE = 81453;
	private static final int DROP_CHANCE = 25; // 25%
	// Misc
	private static final int ELITE_MONSTERS_SPAWN_CHANCE = 1; // 1%
	private static final int MIMIC_TREASURES_SPAWN_CHANCE = 2; // 2%
	// Trigger Monsters
	//@formatter:off
	private static final int[] TRIGGER_MOBS_FLOOR1 = {24550, 24551, 24552, 24553, 24554}; // Floor 1 Monsters
	private static final int[] TRIGGER_MOBS_FLOOR2 = {24556, 24557, 24558, 24559, 24560}; // Floor 2 Monsters
	private static final int[] TRIGGER_MOBS_FLOOR3 = {24562, 24563, 24564, 24565, 24566}; // Floor 3 Monsters
	private static final int[] TRIGGER_MOBS_FLOOR4 = {24858, 24859, 24860, 24861, 24862}; // Floor 4 Monsters
	private static final int[] TRIGGER_MOBS_FLOOR5 = {24864, 24865, 24866, 24867, 24868}; // Floor 5 Monsters
	private static final int[] TRIGGER_MOBS_FLOOR6 = {24870, 24871, 24872, 24873, 24874}; // Floor 6 Monsters
	private static final int[] TRIGGER_MOBS_FLOOR7 = {24896, 24897, 24898, 24899, 24900}; // Floor 7 Monsters
	private static final int[] TRIGGER_MOBS_FLOOR8 = {24902, 24903, 24904, 24905, 24906}; // Floor 8 Monsters
	private static final int[] TRIGGER_MOBS_FLOOR9 = {24908, 24909, 24910, 24911, 24912}; // Floor 9 Monsters
	//@formatter:on
	
	private ToiSpecialMonsters()
	{
		super();
		addKillId(ELITE_MONSTER_FLOOR4, ELITE_MONSTER_FLOOR5, ELITE_MONSTER_FLOOR6, ELITE_MONSTER_FLOOR7, ELITE_MONSTER_FLOOR8, ELITE_MONSTER_FLOOR9);
		addKillId(TRIGGER_MOBS_FLOOR1);
		addKillId(TRIGGER_MOBS_FLOOR2);
		addKillId(TRIGGER_MOBS_FLOOR3);
		addKillId(TRIGGER_MOBS_FLOOR4);
		addKillId(TRIGGER_MOBS_FLOOR5);
		addKillId(TRIGGER_MOBS_FLOOR6);
		addKillId(TRIGGER_MOBS_FLOOR7);
		addKillId(TRIGGER_MOBS_FLOOR8);
		addKillId(TRIGGER_MOBS_FLOOR9);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		switch (npc.getId())
		{
			case ELITE_MONSTER_FLOOR4:
			case ELITE_MONSTER_FLOOR5:
			case ELITE_MONSTER_FLOOR6:
			case ELITE_MONSTER_FLOOR7:
			case ELITE_MONSTER_FLOOR8:
			case ELITE_MONSTER_FLOOR9:
			{
				giveReward(npc, killer);
				break;
			}
			// Floor 1 Monsters
			case 24550:
			case 24551:
			case 24552:
			case 24553:
			case 24554:
			{
				if (getRandom(100) < MIMIC_TREASURES_SPAWN_CHANCE)
				{
					addSpawn(MIMIC_TREASURE_FLOOR1, npc, true, 0, true, npc.getInstanceId());
				}
				break;
			}
			// Floor 2 Monsters
			case 24556:
			case 24557:
			case 24558:
			case 24559:
			case 24560:
			{
				if (getRandom(100) < MIMIC_TREASURES_SPAWN_CHANCE)
				{
					addSpawn(MIMIC_TREASURE_FLOOR2, npc, true, 0, true, npc.getInstanceId());
				}
				break;
			}
			// Floor 3 Monsters
			case 24562:
			case 24563:
			case 24564:
			case 24565:
			case 24566:
			{
				if (getRandom(100) < MIMIC_TREASURES_SPAWN_CHANCE)
				{
					addSpawn(MIMIC_TREASURE_FLOOR3, npc, true, 0, true, npc.getInstanceId());
				}
				break;
			}
			// Floor 4 Monsters
			case 24858:
			case 24859:
			case 24860:
			case 24861:
			case 24862:
			{
				if (getRandom(100) < MIMIC_TREASURES_SPAWN_CHANCE)
				{
					addSpawn(MIMIC_TREASURE_FLOOR4, npc, true, 0, true, npc.getInstanceId());
				}
				if (getRandom(100) < ELITE_MONSTERS_SPAWN_CHANCE)
				{
					addSpawn(ELITE_MONSTER_FLOOR4, npc, true, 0, true, npc.getInstanceId());
				}
				break;
			}
			// Floor 5 Monsters
			case 24864:
			case 24865:
			case 24866:
			case 24867:
			case 24868:
			{
				if (getRandom(100) < MIMIC_TREASURES_SPAWN_CHANCE)
				{
					addSpawn(MIMIC_TREASURE_FLOOR5, npc, true, 0, true, npc.getInstanceId());
				}
				if (getRandom(100) < ELITE_MONSTERS_SPAWN_CHANCE)
				{
					addSpawn(ELITE_MONSTER_FLOOR5, npc, true, 0, true, npc.getInstanceId());
				}
				break;
			}
			// Floor 6 Monsters
			case 24870:
			case 24871:
			case 24872:
			case 24873:
			case 24874:
			{
				if (getRandom(100) < MIMIC_TREASURES_SPAWN_CHANCE)
				{
					addSpawn(MIMIC_TREASURE_FLOOR6, npc, true, 0, true, npc.getInstanceId());
				}
				if (getRandom(100) < ELITE_MONSTERS_SPAWN_CHANCE)
				{
					addSpawn(ELITE_MONSTER_FLOOR6, npc, true, 0, true, npc.getInstanceId());
				}
				break;
			}
			// Floor 7 Monsters
			case 24896:
			case 24897:
			case 24898:
			case 24899:
			case 24900:
			{
				if (getRandom(100) < MIMIC_TREASURES_SPAWN_CHANCE)
				{
					addSpawn(MIMIC_TREASURE_FLOOR7, npc, true, 0, true, npc.getInstanceId());
				}
				if (getRandom(100) < ELITE_MONSTERS_SPAWN_CHANCE)
				{
					addSpawn(ELITE_MONSTER_FLOOR7, npc, true, 0, true, npc.getInstanceId());
				}
				break;
			}
			// Floor 8 Monsters
			case 24902:
			case 24903:
			case 24904:
			case 24905:
			case 24906:
			{
				if (getRandom(100) < MIMIC_TREASURES_SPAWN_CHANCE)
				{
					addSpawn(MIMIC_TREASURE_FLOOR8, npc, true, 0, true, npc.getInstanceId());
				}
				if (getRandom(100) < ELITE_MONSTERS_SPAWN_CHANCE)
				{
					addSpawn(ELITE_MONSTER_FLOOR8, npc, true, 0, true, npc.getInstanceId());
				}
				break;
			}
			// Floor 9 Monsters
			case 24908:
			case 24909:
			case 24910:
			case 24911:
			case 24912:
			{
				if (getRandom(100) < MIMIC_TREASURES_SPAWN_CHANCE)
				{
					addSpawn(MIMIC_TREASURE_FLOOR9, npc, true, 0, true, npc.getInstanceId());
				}
				if (getRandom(100) < ELITE_MONSTERS_SPAWN_CHANCE)
				{
					addSpawn(ELITE_MONSTER_FLOOR9, npc, true, 0, true, npc.getInstanceId());
				}
				break;
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	private void giveReward(Npc npc, Player killer)
	{
		final Player player = getRandomPartyMember(killer);
		if ((getRandom(100) < DROP_CHANCE) && ((player.getParty() == null) || ((player.getParty() != null) && player.isInsideRadius3D(npc, Config.ALT_PARTY_RANGE))))
		{
			giveItems(player, ENHANCED_RUNE, 1);
			player.sendMessage("You obtained an Enhanced Rune from the Elite Monster!"); // Custom message.
		}
	}
	
	public static void main(String[] args)
	{
		new ToiSpecialMonsters();
	}
}
