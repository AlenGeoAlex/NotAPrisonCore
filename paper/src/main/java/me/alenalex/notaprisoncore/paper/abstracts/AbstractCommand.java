package me.alenalex.notaprisoncore.paper.abstracts;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.locale.LocaleKey;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import me.alenalex.notaprisoncore.paper.commands.help.CommandHelpProvider;
import me.alenalex.notaprisoncore.paper.manager.CommandManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@Getter
@EqualsAndHashCode
@ToString
public abstract class AbstractCommand {

    private final CommandManager commandManager;

    private final CommandHelpProvider commandHelpProvider;

    public AbstractCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
        this.commandHelpProvider = helpPrompt();
    }

    protected NotAPrisonCore pluginInstance(){
        return this.commandManager.getPrisonManagers().getPluginInstance();
    }
    protected abstract CommandHelpProvider helpPrompt();

    protected void sendLocaleMessage(LocaleKey key){

    }

    protected void sendFormattedMessage(CommandSender sender, String message){
        sender.sendMessage(ChatColor.GRAY+"["+ChatColor.YELLOW+"N"+ChatColor.RED+"P"+ChatColor.WHITE+"Core"+ChatColor.GRAY+"] "+ChatColor.WHITE+"➔  "+ChatColor.GRAY+message);
    }
}
