package com.etsuni.fallout;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

import static com.etsuni.fallout.Fallout.plugin;

public class Arena {

    private int countdown = 0;

    public Boolean createArena(Player player, String arenaName, String block) {
        com.sk89q.worldedit.entity.Player actor = BukkitAdapter.adapt(player);
        SessionManager manager = WorldEdit.getInstance().getSessionManager();
        LocalSession localSession = manager.get(actor);
        Region region;
        World selectionWorld = localSession.getSelectionWorld();
        Configuration config = plugin.getArenasConfig();

        if(config.getConfigurationSection("arenas") != null &&
        config.getConfigurationSection("arenas").getKeys(false).contains(arenaName)) {
            return false;
        }

        try{
            if(selectionWorld == null) {
                throw new IncompleteRegionException();
            }
            region = localSession.getSelection(selectionWorld);
        } catch (IncompleteRegionException ex) {
            actor.printError(TextComponent.of("Please make a selection first."));
            return false;
        }

        ConfigurationSection section = config.createSection("arenas." + arenaName);
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        Location maxLocation = new Location(player.getLocation().getWorld(), max.getX(), max.getY(), max.getZ());
        Location minLocation = new Location(player.getLocation().getWorld(), min.getX(), min.getY(), min.getZ());
        section.set("block_type", block);
        section.set("world", player.getLocation().getWorld().getName());
        section.createSection("region").set("max", maxLocation.getBlock().getLocation().toVector());
        config.set("arenas." + arenaName + ".region." + "min", minLocation.getBlock().getLocation().toVector());
        plugin.saveCfg();

        return true;
    }

    public Boolean resetArena(String name) {
        Configuration config = plugin.getArenasConfig();

        if(config.getConfigurationSection("arenas") == null || !config.getConfigurationSection("arenas").getKeys(false).contains(name)) {
            return false;
        }

        if(FalloutGames.getInstance().getGames().containsKey(name)) {
            return false;
        }

        Vector maxLocation = config.getVector("arenas." + name + ".region.max");
        Vector minLocation = config.getVector("arenas." + name + ".region.min");
        org.bukkit.World bukkitWorld = Bukkit.getWorld(config.getString("arenas." + name + ".world"));
        World world = BukkitAdapter.adapt(bukkitWorld);
        BlockVector3 max = BlockVector3.at(maxLocation.getX(), maxLocation.getY(), maxLocation.getZ());
        BlockVector3 min = BlockVector3.at(minLocation.getX(), minLocation.getY(), minLocation.getZ());
        CuboidRegion region = new CuboidRegion(world, min, max);
        RandomPattern pattern = new RandomPattern();
        BlockState type = BukkitAdapter.adapt(Material.getMaterial(
                config.getString("arenas." + name+".block_type").toUpperCase()).createBlockData());
        pattern.add(type, 1.0);

        try(EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            editSession.setBlocks(region, pattern);
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public Boolean startArena(String name, Long decayTime) {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        Configuration config = plugin.getArenasConfig();


        if(config.getConfigurationSection("arenas") == null || !config.getConfigurationSection("arenas").getKeys(false).contains(name)) {
            return false;
        }

        org.bukkit.World bukkitWorld = Bukkit.getWorld(Objects.requireNonNull(config.getString("arenas." + name + ".world")));

        if(FalloutGames.getInstance().getGames().containsKey(name)) {
            return false;
        }

        countdown = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            int count = 3;
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(player.getWorld().equals(bukkitWorld)) {
                        if(count == 0) {
                            player.sendTitle(ChatColor.GREEN + "Go!", null, 0, 10, 0);
                        } else if(count > 0) {
                            player.sendTitle(ChatColor.GREEN + "" + count, null, 0, 10, 0);
                        }
                    }
                }

                if(count == 0) {
                    startFallout(name, decayTime);
                    scheduler.cancelTask(countdown);
                }
                count--;
            }
        },0,20);


        return true;
    }

    public void startFallout(String name, Long decayTime) {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        Configuration config = plugin.getArenasConfig();

        Vector maxLocation = config.getVector("arenas." + name + ".region.max");
        Vector minLocation = config.getVector("arenas." + name + ".region.min");
        org.bukkit.World bukkitWorld = Bukkit.getWorld(config.getString("arenas." + name + ".world"));
        World world = BukkitAdapter.adapt(bukkitWorld);
        BlockVector3 max = BlockVector3.at(maxLocation.getX(), maxLocation.getY(), maxLocation.getZ());
        BlockVector3 min = BlockVector3.at(minLocation.getX(), minLocation.getY(), minLocation.getZ());
        CuboidRegion region = new CuboidRegion(world, min, max);
        RandomPattern pattern = new RandomPattern();
        BlockState air = BukkitAdapter.adapt(Material.AIR.createBlockData());
        pattern.add(air, 1.0);
        Random random = new Random();

        int id = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                for (Iterator<BlockVector3> it = region.iterator(); it.hasNext(); ) {
                    BlockVector3 block = it.next();

                    if(random.nextInt(50) % 10 == 0) {
                        try(EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                            editSession.setBlock(block, pattern);
                        } catch (MaxChangedBlocksException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        },0, decayTime);

        FalloutGames.getInstance().getGames().put(name, id);
    }

    public Boolean stopArena(String name) {
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        Configuration config = plugin.getArenasConfig();

        if(config.getConfigurationSection("arenas") == null || !config.getConfigurationSection("arenas").getKeys(false).contains(name)) {
            return false;
        }

        if(!FalloutGames.getInstance().getGames().containsKey(name)) {
            return false;
        }

        scheduler.cancelTask(FalloutGames.getInstance().getGames().get(name));
        FalloutGames.getInstance().getGames().remove(name);
        return true;
    }

    public Boolean deleteArena(String name) {
        Configuration config = plugin.getArenasConfig();

        if(config.getConfigurationSection("arenas") == null ||
        !config.getConfigurationSection("arenas").getKeys(false).contains(name)) {
            return false;
        }

        if(FalloutGames.getInstance().getGames().containsKey(name)) {
            return false;
        }

        config.set("arenas." + name, null);
        plugin.saveCfg();
        return true;
    }
}
