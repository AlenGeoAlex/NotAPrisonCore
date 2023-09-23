package me.alenalex.notaprisoncore.paper.entity;

import me.alenalex.notaprisoncore.api.config.options.PriorityConfiguration;
import me.alenalex.notaprisoncore.api.entity.IClaimQueueEntity;
import me.alenalex.notaprisoncore.api.queue.QueueEntity;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;
public class ClaimQueueEntity extends QueueEntity<Player> implements IClaimQueueEntity {

    private static int findPriority(Player player){
        NotAPrisonCore pluginInstance = ((Bootstrap) Bootstrap.getJavaPlugin()).getPluginInstance();
        PriorityConfiguration priorityConfiguration = pluginInstance.getPrisonManagers().getConfigurationManager().getPluginConfiguration().getClaimQueueConfiguration().getPriorityConfiguration();
        LinkedHashMap<String, Integer> weightMap = priorityConfiguration.getWeightMap();

        int weight = priorityConfiguration.getDefaultWeight();

        for (Map.Entry<String, Integer> entry : weightMap.entrySet()) {
            if(player.hasPermission(entry.getKey())){
                weight = entry.getValue();
                break;
            }
        }

        return weight;
    }

    public ClaimQueueEntity(Player player){
        super(player, findPriority(player));
    }




}
