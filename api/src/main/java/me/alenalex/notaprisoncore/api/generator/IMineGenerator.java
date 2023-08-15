package me.alenalex.notaprisoncore.api.generator;

import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public interface IMineGenerator {

    IMineMeta generateMine(CommandSender requester, String schematicName);

    Collection<IMineMeta> generateMines(CommandSender requester, String schematicName, int generationCount, long coolDownInterval);


}
