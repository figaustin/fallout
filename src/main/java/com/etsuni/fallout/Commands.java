package com.etsuni.fallout;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.Set;

import static com.etsuni.fallout.Fallout.plugin;

public class Commands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            if(command.getName().equalsIgnoreCase("fallout")) {
                if(args.length > 0) {
                    if(args[0].equalsIgnoreCase("create")) {

                        if(args.length > 1) {
                            String name = args[1];

                            if(args.length > 2) {
                                String block = args[2];

                                if(Material.getMaterial(block.toUpperCase()) == null || !Material.getMaterial(block.toUpperCase()).isBlock()) {
                                    sender.sendMessage(ChatColor.RED + "Please provide a proper block.");
                                    return false;
                                }

                                Arena arena = new Arena();
                                if(!arena.createArena(((Player) sender).getPlayer(), name, block)) {
                                    sender.sendMessage(ChatColor.RED + "There is already an arena with that name!");
                                    return false;
                                } else {
                                    sender.sendMessage(ChatColor.GREEN + "Successfully created " + name + "!");
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "Please provide a block type for this arena");
                                return false;
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Please provide a name and type of block for the arena");
                            return false;
                        }

                    }
                     else if(args[0].equalsIgnoreCase("reset")) {

                         if(args.length > 1) {
                             String name = args[1];

                             Arena arena = new Arena();
                             if (!arena.resetArena(name)) {
                                 sender.sendMessage(ChatColor.RED + "Can not find an arena by that name or that arena is currently in play!");
                                 return false;
                             } else {
                                 sender.sendMessage(ChatColor.GREEN + "Reset " + name + "!");
                             }
                         }else {
                             sender.sendMessage(ChatColor.RED + "Please provide an arena.");
                             return false;
                         }
                    }
                     else if(args[0].equalsIgnoreCase("start")) {

                         if(args.length > 1) {
                             String name = args[1];

                             Arena arena = new Arena();
                             if (!arena.startArena(name)) {
                                 sender.sendMessage(ChatColor.RED + "Can not find an arena by that name or that arena is already in play!");
                                 return false;
                             } else {
                                 sender.sendMessage(ChatColor.GREEN + "Started fallout game!");
                             }
                         }else {
                             sender.sendMessage(ChatColor.RED + "Please provide an arena.");
                             return false;
                         }
                    }
                     else if(args[0].equalsIgnoreCase("stop")) {

                         if(args.length > 1) {
                             String name = args[1];

                             Arena arena = new Arena();
                             if(!arena.stopArena(name)) {
                                 sender.sendMessage(ChatColor.RED + "Can not find an arena by that name or that arena is not in play!");
                                 return false;
                             } else {
                                 sender.sendMessage(ChatColor.GREEN + "Stopped fallout game!");
                             }
                         }else {
                             sender.sendMessage(ChatColor.RED + "Please provide an arena.");
                             return false;
                         }
                     }
                     else if(args[0].equalsIgnoreCase("list")) {
                        Configuration config = plugin.getArenasConfig();
                        Set<String> arenas = config.getConfigurationSection("arenas").getKeys(false);
                        sender.sendMessage(ChatColor.GOLD + "List of Fallout Arenas: ");
                        for(String arena : arenas) {
                            sender.sendMessage(
                                    ChatColor.GREEN + "- Name: " + arena + " Block: "
                                            + config.getString("arenas." +arena + ".block_type"));
                        }
                     }
                     else if (args[0].equalsIgnoreCase("delete")) {

                         if(args.length > 1) {
                             String name = args[1];

                             Arena arena = new Arena();
                             if(!arena.deleteArena(name)) {
                                 sender.sendMessage(ChatColor.RED + "Can not find an arena by that name!");
                                 return false;
                             } else {
                                 sender.sendMessage(ChatColor.GREEN + "Successfully deleted " + arena + "!");
                             }

                         } else {
                             sender.sendMessage(ChatColor.RED + "Please provide an arena.");
                         }
                    }
                }
            }
        }
        return false;
    }
}
