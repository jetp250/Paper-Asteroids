package me.jetp250.asteroids.commands;

import me.jetp250.asteroids.commands.subcommands.CreateSubCommand;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class AsteroidsCommand implements TabExecutor {
    private final Map<String, SubCommand> subCommandMap;

    public AsteroidsCommand() {
        this.subCommandMap = new HashMap<>();
        subCommandMap.put("create", new CreateSubCommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("asteroids.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to execute this command!");
            return true;
        }

        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        SubCommand subCommand = subCommandMap.get(args[0].toLowerCase());
        if (subCommand == null) {
            sender.sendMessage(ChatColor.RED + "Unknown subcommand: \"" + args[0] + "\"");
            showHelp(sender);
        } else {
            subCommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("asteroids.admin")) {
            return Collections.emptyList();
        }

        if (args.length == 0) {
            return new ArrayList<>(this.subCommandMap.keySet());
        }

        SubCommand subCommand = subCommandMap.get(args[0].toLowerCase());
        if (subCommand != null) {
            return subCommand.getTabCompletions(sender, Arrays.copyOfRange(args, 1, args.length));
        }
        return Collections.emptyList();
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "-- [Asteroids help] --");
        for (SubCommand subCommand : subCommandMap.values()) {
            BaseComponent[] message = new ComponentBuilder()
                    .append("> ")
                    .color(net.md_5.bungee.api.ChatColor.DARK_GRAY)
                    .append("/asteroids ")
                    .color(net.md_5.bungee.api.ChatColor.GRAY)
                    .append(subCommand.getSyntaxHelpMessage())
                    .create();

            sender.sendMessage(message);
        }
    }

}
