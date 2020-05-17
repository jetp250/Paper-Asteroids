package me.jetp250.asteroids.commands.subcommands;

import me.jetp250.asteroids.commands.SubCommand;
import me.jetp250.asteroids.track.builder.TrackBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public final class CreateSubCommand implements SubCommand {

    @Override
    public BaseComponent[] getSyntaxHelpMessage() {
        return new ComponentBuilder()
                .append("create")
                    .color(ChatColor.WHITE)
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/asteroids create"))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Click to select!")))
                .create();
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return Collections.singletonList("create");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TrackBuilder.startCreatingTrack((Player) sender);
    }
}
