package me.alenalex.notaprisoncore.paper.commands;

import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Optional;
import me.alenalex.notaprisoncore.api.exceptions.mine.FailedMineClaimException;
import me.alenalex.notaprisoncore.paper.abstracts.AbstractCommand;
import me.alenalex.notaprisoncore.paper.commands.help.CommandHelpProvider;
import me.alenalex.notaprisoncore.paper.commands.help.SubcommandHelpProvider;
import me.alenalex.notaprisoncore.paper.constants.DefaultAdminMessages;
import me.alenalex.notaprisoncore.paper.constants.LocaleConstants;
import me.alenalex.notaprisoncore.paper.constants.Permission;
import me.alenalex.notaprisoncore.paper.entity.ClaimQueueEntity;
import me.alenalex.notaprisoncore.paper.entity.profile.PrisonUserProfile;
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

        boolean other = false;
        if(!isPlayer(sender)){
            target = asPlayer(sender);
            other = true;
        }

        PrisonUserProfile profile = getProfile(target);
        if(profile == null){
            target.sendMessage(DefaultAdminMessages.MineCommand.PROFILE_NOT_LOADED);
            if(other)
                sender.sendMessage(DefaultAdminMessages.MineCommand.PROFILE_NOT_LOADED_SENDER);
            return;
        }

        /*
       getCommandManager().getPrisonManagers().getMineManager().claimMineForUser(profile)
                .whenComplete((res, err) -> {
                    if (err != null) {
                        if(err instanceof FailedMineClaimException){
                            System.out.println(err.getMessage());
                        }
                        profile.sendLocalizedMessage(LocaleConstants.MINE_CLAIM_FAILED);
                        return;
                    }
                    profile.sendLocalizedMessage(LocaleConstants.MINE_CLAIM_SUCCESS);
                    return;
                });*/


        if (getCommandManager().getPrisonManagers().getConfigurationManager().getPluginConfiguration().getClaimQueueConfiguration().isEnabled()) {
            if (!getCommandManager().getPrisonManagers().getPluginInstance().getPrisonQueueProvider().getClaimQueue().enqueue(new ClaimQueueEntity(target))) {
                profile.sendLocalizedMessage(LocaleConstants.MINE_CLAIM_QUEUE_EXISTS);
                return;
            }
            profile.sendLocalizedMessage(LocaleConstants.MINE_CLAIM_QUEUE);
        }else{
            getCommandManager().getPrisonManagers().getMineManager().claimMineForUser(profile)
                    .whenComplete((res, err) -> {
                        if (err != null) {
                            if(err instanceof FailedMineClaimException){
                                System.out.println(err.getMessage());
                            }
                            profile.sendLocalizedMessage(LocaleConstants.MINE_CLAIM_FAILED);
                            return;
                        }
                        profile.sendLocalizedMessage(LocaleConstants.MINE_CLAIM_SUCCESS);
                        return;
                    });
        }
    }
}
