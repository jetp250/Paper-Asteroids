package me.jetp250.asteroids.commands;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {

    BaseComponent[] getSyntaxHelpMessage();

    List<String> getTabCompletions(CommandSender sender, String[] args);

    void execute(CommandSender sender, String[] args);

}
