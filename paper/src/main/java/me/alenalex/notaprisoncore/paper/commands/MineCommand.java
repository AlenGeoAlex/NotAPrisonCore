package me.alenalex.notaprisoncore.paper.commands;

import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Optional;
import me.alenalex.notaprisoncore.paper.abstracts.AbstractCommand;
import me.alenalex.notaprisoncore.paper.commands.help.CommandHelpProvider;
import me.alenalex.notaprisoncore.paper.commands.help.SubcommandHelpProvider;
import me.alenalex.notaprisoncore.paper.constants.DefaultAdminMessages;
import me.alenalex.notaprisoncore.paper.constants.Permission;
import me.alenalex.notaprisoncore.paper.manager.CommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(value = "mine", alias = {"claim", "cmine"})
public class MineCommand extends AbstractCommand {
    public MineCommand(CommandManager commandManager) {
        super(commandManager);
    }

    @Override
    protected CommandHelpProvider helpPrompt() {
        return CommandHelpProvider.Builder
                .newBuilder()
                .withCommandName("mine")
                .withDescription("Claim a mine if not exists")
                .withAliases("claim", "cmine")
                .withSubcommands(
                        SubcommandHelpProvider.Builder
                                .newBuilder()
                                .withSubcommand("[Player]")
                                .withDescription("Optional player name, to which the mine should be claimed. The player should be connected to this server")
                                .withPermission(Permission.CLAIM_COMMAND_OTHERS.getPermissionKey())
                                .build()
                )
                .build();
    }

    @Command
    public void onClaimCommand(CommandSender sender, @Optional Player target){
        if(isConsole(sender) && target == null){
            sendFormattedMessage(sender, DefaultAdminMessages.MineCommand.TARGET_REQUIRED_FOR_CONSOLE_COMMAND);
            return;
        }

        if(isPlayer(sender))
            target = asPlayer(sender);


    }
}
