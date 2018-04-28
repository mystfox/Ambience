package vazkii.ambience;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;

public final class SongPicker {

    public static final String EVENT_MAIN_MENU = "mainMenu";
    public static final String EVENT_BOSS = "boss";
    public static final String EVENT_IN_NETHER = "nether";
    public static final String EVENT_IN_END = "end";
    public static final String EVENT_HORDE = "horde";
    public static final String EVENT_NIGHT = "night";
    public static final String EVENT_RAIN = "rain";
    public static final String EVENT_UNDERWATER = "underwater";
    public static final String EVENT_UNDERGROUND = "underground";
    public static final String EVENT_DEEP_UNDEGROUND = "deepUnderground";
    public static final String EVENT_HIGH_UP = "highUp";
    public static final String EVENT_VILLAGE = "village";
    public static final String EVENT_MINECART = "minecart";
    public static final String EVENT_BOAT = "boat";
    public static final String EVENT_HORSE = "horse";
    public static final String EVENT_PIG = "pig";
    public static final String EVENT_FISHING = "fishing";
    public static final String EVENT_DYING = "dying";
    public static final String EVENT_PUMPKIN_HEAD = "pumpkinHead";
    public static final String EVENT_GENERIC = "generic";

    public static final Map<String, String[]> eventMap = new HashMap();
    public static final Map<BiomeGenBase, String[]> biomeMap = new HashMap();
    public static final Map<BiomeDictionary.Type, String[]> primaryTagMap = new HashMap();
    public static final Map<BiomeDictionary.Type, String[]> secondaryTagMap = new HashMap();

    public static final Random rand = new Random();

    public static void reset() {
        eventMap.clear();
        biomeMap.clear();
        primaryTagMap.clear();
        secondaryTagMap.clear();
    }

    public static String[] getSongs() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        World world = mc.theWorld;

        if (player == null || world == null)
            return getSongsForEvent(EVENT_MAIN_MENU);

        int x = MathHelper.floor_double(player.posX);
        int y = MathHelper.floor_double(player.posY);
        int z = MathHelper.floor_double(player.posZ);

        AmbienceEventEvent event = new AmbienceEventEvent.Pre(world, x, y, z);
        MinecraftForge.EVENT_BUS.post(event);
        String[] eventr = getSongsForEvent(event.event);
        if (eventr != null)
            return eventr;

        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
            String[] songs = getSongsForEvent(EVENT_BOSS);
            if (songs != null)
                return songs;
        }

        float hp = player.getHealth();
        if (hp < 7) {
            String[] songs = getSongsForEvent(EVENT_DYING);
            if (songs != null)
                return songs;
        }

        int monsterCount = world.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getBoundingBox(player.posX - 16, player.posY - 8, player.posZ - 16, player.posX + 16, player.posY + 8, player.posZ + 16)).size();
        if (monsterCount > 5) {
            String[] songs = getSongsForEvent(EVENT_HORDE);
            if (songs != null)
                return songs;
        }

        if (player.fishEntity != null) {
            String[] songs = getSongsForEvent(EVENT_FISHING);
            if (songs != null)
                return songs;
        }

        ItemStack headItem = player.getEquipmentInSlot(4);
        if (headItem != null && headItem.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
            String[] songs = getSongsForEvent(EVENT_PUMPKIN_HEAD);
            if (songs != null)
                return songs;
        }
        int indimension = world.provider.dimensionId;

        if (indimension == -1) {
            String[] songs = getSongsForEvent(EVENT_IN_NETHER);
            if (songs != null)
                return songs;
        } else if (indimension == 1) {
            String[] songs = getSongsForEvent(EVENT_IN_END);
            if (songs != null)
                return songs;
        }

        Entity riding = player.ridingEntity;
        if (riding != null) {
            if (riding instanceof EntityMinecart) {
                String[] songs = getSongsForEvent(EVENT_MINECART);
                if (songs != null)
                    return songs;
            }
            if (riding instanceof EntityBoat) {
                String[] songs = getSongsForEvent(EVENT_BOAT);
                if (songs != null)
                    return songs;
            }
            if (riding instanceof EntityHorse) {
                String[] songs = getSongsForEvent(EVENT_HORSE);
                if (songs != null)
                    return songs;
            }
            if (riding instanceof EntityPig) {
                String[] songs = getSongsForEvent(EVENT_PIG);
                if (songs != null)
                    return songs;
            }
        }

        if (player.isInsideOfMaterial(Material.water)) {
            String[] songs = getSongsForEvent(EVENT_UNDERWATER);
            if (songs != null)
                return songs;
        }

        boolean underground = !world.canBlockSeeTheSky(x, y, z);
        if (underground) {
            if (y < 20) {
                String[] songs = getSongsForEvent(EVENT_DEEP_UNDEGROUND);
                if (songs != null)
                    return songs;
            }
            if (y < 55) {
                String[] songs = getSongsForEvent(EVENT_UNDERGROUND);
                if (songs != null)
                    return songs;
            }
        }

        if (world.isRaining()) {
            String[] songs = getSongsForEvent(EVENT_RAIN);
            if (songs != null)
                return songs;
        }

        if (y > 128) {
            String[] songs = getSongsForEvent(EVENT_HIGH_UP);
            if (songs != null)
                return songs;
        }

        long time = world.getWorldTime() % 24000;
        if (time > 13300 && time < 23200) {
            String[] songs = getSongsForEvent(EVENT_NIGHT);
            if (songs != null)
                return songs;
        }

        int villagerCount = world.getEntitiesWithinAABB(EntityVillager.class, AxisAlignedBB.getBoundingBox(player.posX - 30, player.posY - 8, player.posZ - 30, player.posX + 30, player.posY + 8, player.posZ + 30)).size();
        if (villagerCount > 3) {
            String[] songs = getSongsForEvent(EVENT_VILLAGE);
            if (songs != null)
                return songs;
        }


        event = new AmbienceEventEvent.Post(world, x, y, z);
        MinecraftForge.EVENT_BUS.post(event);
        eventr = getSongsForEvent(event.event);
        if (eventr != null)
            return eventr;

        if (world.blockExists(x, y, z)) {
            Chunk chunk = world.getChunkFromBlockCoords(x, z);
            BiomeGenBase biome = chunk.getBiomeGenForWorldCoords(x & 15, z & 15, world.getWorldChunkManager());
            if (biomeMap.containsKey(biome))
                return biomeMap.get(biome);

            BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(biome);
            for (Type t : types)
                if (primaryTagMap.containsKey(t))
                    return primaryTagMap.get(t);
            for (Type t : types)
                if (secondaryTagMap.containsKey(t))
                    return secondaryTagMap.get(t);
        }

        return getSongsForEvent(EVENT_GENERIC);
    }

    public static String getSongsString() {
        return StringUtils.join(getSongs(), ",");
    }

    public static String getRandomSong() {
        String[] songChoices = getSongs();

        return songChoices[rand.nextInt(songChoices.length)];
    }

    public static String[] getSongsForEvent(String event) {
        if (eventMap.containsKey(event))
            return eventMap.get(event);

        return null;
    }

    public static String getSongName(String song) {
        return song == null ? "" : song.replaceAll("([^A-Z])([A-Z])", "$1 $2");
    }
}
