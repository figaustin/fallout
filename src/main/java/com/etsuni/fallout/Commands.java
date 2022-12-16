package com.etsuni.fallout;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

                                Arena arena = new Arena();
                                arena.createArena(((Player) sender).getPlayer(), name, block);
                            }
                        }

                    }
                     else if(args[0].equalsIgnoreCase("reset")) {

                         if(args.length > 1) {
                             String name = args[1];

                             Arena arena = new Arena();
                             arena.resetArena(name);
                         }
                    }
                }
            }
        }
        return false;
    }
}
