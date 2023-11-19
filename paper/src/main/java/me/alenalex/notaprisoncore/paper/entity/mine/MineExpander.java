package me.alenalex.notaprisoncore.paper.entity.mine;

import com.sk89q.worldedit.regions.Region;
import me.alenalex.notaprisoncore.api.entity.PrisonCoreVector;
import me.alenalex.notaprisoncore.api.entity.mine.IMineExpander;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;

public class MineExpander implements IMineExpander {

    private final Mine mine;

    public MineExpander(Mine mine) {
        this.mine = mine;
    }

    @Override
    public boolean expand(PrisonCoreVector increaseMax, PrisonCoreVector increaseMin) {
        IMineMeta mineMeta = this.mine.getMeta();
        Region copyObject = mineMeta.getMineRegion().clone();

        try {
            copyObject.expand(increaseMax.toWorldEditVector(), increaseMin.toWorldEditVector());
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return mineMeta.updateIfChanged(copyObject);
    }
}
