package me.alenalex.notaprisoncore.paper.commands.help;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class CommandHelpProvider {

    public static final String HEADER =
                    ChatColor.BOLD +String.valueOf(ChatColor.GOLD) + "--"+
                    ChatColor.BOLD +ChatColor.RED + "--" +
                    ChatColor.BOLD +ChatColor.GOLD + "--"+
                    ChatColor.BOLD +ChatColor.RED + "--" +
                    ChatColor.BOLD +ChatColor.GOLD + "--"+
                    ChatColor.BOLD +ChatColor.RED + "-- " +
                    ChatColor.YELLOW + "["+ChatColor.BOLD +ChatColor.WHITE+" NPCore "+ChatColor.YELLOW +"]"+
                    ChatColor.BOLD +ChatColor.GOLD + " --"+
                    ChatColor.BOLD +ChatColor.RED + "--" +
                    ChatColor.BOLD +ChatColor.GOLD + "--"+
                    ChatColor.BOLD +ChatColor.RED + "--" +
                    ChatColor.BOLD +ChatColor.GOLD + "--"+
                    ChatColor.BOLD +ChatColor.RED + "--";

    public static final String SUBCOMMAND_HEADER =
                    ChatColor.BOLD +String.valueOf(ChatColor.GOLD) + "--"+
                    ChatColor.BOLD +ChatColor.AQUA + "--" +
                    ChatColor.BOLD +ChatColor.GOLD + "--"+
                    ChatColor.BOLD +ChatColor.AQUA + "-- " +
                    ChatColor.GOLD + "["+ChatColor.BOLD +ChatColor.WHITE+" Subcommands "+ChatColor.GOLD +"]"+
                    ChatColor.BOLD +ChatColor.GOLD + " --"+
                    ChatColor.BOLD +ChatColor.AQUA + "--" +
                    ChatColor.BOLD +ChatColor.GOLD + "--"+
                    ChatColor.BOLD +ChatColor.AQUA + "--";

    public final String commandName;
    public final String permission;
    public final String description;
    public final SubcommandHelpProvider[] subcommands;
    public final String[] aliases;

    private CommandHelpProvider(Builder builder) {
        commandName = builder.commandName;
        permission = builder.permission;
        description = builder.description;
        subcommands = builder.subcommands;
        aliases = builder.aliases;
    }

    public static Builder newBuilder(CommandHelpProvider copy) {
        Builder builder = new Builder();
        builder.commandName = copy.getCommandName();
        builder.permission = copy.getPermission();
        builder.description = copy.getDescription();
        builder.subcommands = copy.getSubcommands();
        builder.aliases = copy.getAliases();
        return builder;
    }


    public String getCommandName() {
        return commandName;
    }

    public String getPermission() {
        return permission;
    }

    public String getDescription() {
        return description;
    }

    public SubcommandHelpProvider[] getSubcommands() {
        return subcommands;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void send(CommandSender sender){
        sender.sendMessage(HEADER);
        String command = ChatColor.GREEN +"Command"+": "+ChatColor.WHITE+commandName;
        String commandNameDesc = ChatColor.GREEN +"Description"+": "+ChatColor.WHITE+description;
        boolean hasPermission = permission != null;
        String commandPerms = ChatColor.GREEN +"Permission"+": "+ (hasPermission ? ChatColor.WHITE+permission : "");
        String aliasesCommand =  ChatColor.GREEN +"Aliases"+": "+ChatColor.WHITE+Arrays.toString(aliases);
        sender.sendMessage(command);
        sender.sendMessage(commandNameDesc);
        sender.sendMessage(commandPerms);
        sender.sendMessage(aliasesCommand);
        if(subcommands.length > 0)
            sender.sendMessage(SUBCOMMAND_HEADER);

        for (SubcommandHelpProvider subcommand : subcommands) {
            if(subcommand == null)
                continue;

            if(!hasPermission(sender, subcommand.permission))
                continue;

            String fullCommand = ChatColor.GOLD+commandName + " "+ChatColor.AQUA +subcommand.subcommand + " "+ChatColor.YELLOW +subcommand.args;

            TextComponent component = new TextComponent();
            component.setText(ChatColor.WHITE+fullCommand +": "+ChatColor.GRAY+subcommand.description);
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(ChatColor.BLUE+"Permission: "+(subcommand.permission != null ? ChatColor.WHITE+subcommand.permission : ChatColor.GRAY+"This command doesn't have any permission requirements"))
                            .append("\n")
                            .append(ChatColor.BLUE+"Aliases: "+ ((subcommand.aliases != null) ? ChatColor.WHITE+ Arrays.toString(subcommand.aliases) : ChatColor.GRAY+"This command doesn't have any aliases"))
                            .append("\n")
                            .append("\n")
                            .append(ChatColor.YELLOW+"Click to auto complete the command")
                            .create()
            ));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/"+commandName + " " +subcommand.subcommand+ " "));
            if(sender instanceof Player)
                sender.spigot().sendMessage(component);
            else sender.sendMessage(component.toLegacyText());
        }
    }

    public boolean hasPermission(CommandSender sender, String permission){
        if(permission == null)
            return true;

        if(permission.isEmpty())
            return true;

        return sender.hasPermission(permission);
    }

    public void sendAsync(CommandSender sender){
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                send(sender);
            }
        });
    }
    public static final class Builder {
        private String commandName;
        private String permission;
        private String description;
        private SubcommandHelpProvider[] subcommands;
        private String[] aliases;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder withCommandName(String commandName) {
            this.commandName = commandName;
            return this;
        }

        public Builder withPermission(String permission) {
            this.permission = permission;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withSubcommands(SubcommandHelpProvider... subcommands) {
            this.subcommands = subcommands;
            return this;
        }

        public Builder withAliases(String... aliases) {
            this.aliases = aliases;
            return this;
        }

        public CommandHelpProvider build() {
            return new CommandHelpProvider(this);
        }
    }
}

