package com.etsuni.fallout;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.function.pattern.RandomStatePattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.Regions;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.block.FuzzyBlockState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static com.etsuni.fallout.Fallout.plugin;

public class Arena {

    public void createArena(Player player, String arenaName, String block) {
        com.sk89q.worldedit.entity.Player actor = BukkitAdapter.adapt(player);
        SessionManager manager = WorldEdit.getInstance().getSessionManager();
        LocalSession localSession = manager.get(actor);
        Region region;
        World selectionWorld = localSession.getSelectionWorld();

        try{
            if(selectionWorld == null) throw new IncompleteRegionException();

            region = localSession.getSelection(selectionWorld);
        } catch (IncompleteRegionException ex) {
            actor.printError(TextComponent.of("Please make a selection first."));
            return;
        }

        BlockType type = BlockTypes.get("minecraft:" + block);
        ConfigurationSection section = plugin.getArenasConfig().createSection("arenas." + arenaName);
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        Location maxLocation = new Location(player.getLocation().getWorld(), max.getX(), max.getY(), max.getZ());
        Location minLocation = new Location(player.getLocation().getWorld(), min.getX(), min.getY(), min.getZ());
        section.set("block_type", "minecraft:" + block );
        section.set("world", player.getLocation().getWorld().getName());
        section.createSection("region").set("max", maxLocation.getBlock().getLocation().toVector());
        plugin.getArenasConfig().set("arenas." + arenaName + ".region." + "min", minLocation.getBlock().getLocation().toVector());
        plugin.saveCfg();
    }

    public void resetArena(String name) {
        Configuration config = plugin.getArenasConfig();
        Vector maxLocation = config.getVector("arenas." + name + ".region.max");
        Vector minLocation = config.getVector("arenas." + name + ".region.min");
        org.bukkit.World bukkitWorld = Bukkit.getWorld(config.getString("arenas." + name + ".world"));
        World world = BukkitAdapter.adapt(bukkitWorld);
        BlockType type = BlockTypes.SANDSTONE;
        BlockVector3 max = BlockVector3.at(maxLocation.getX(), maxLocation.getY(), maxLocation.getZ());
        BlockVector3 min = BlockVector3.at(minLocation.getX(), minLocation.getY(), minLocation.getZ());
        CuboidRegion region = new CuboidRegion(world, min, max);

        try(EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            editSession.setBlocks(region, type.getDefaultState());
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException(e);
        }

    }
}
