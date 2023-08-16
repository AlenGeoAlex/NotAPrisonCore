package me.alenalex.notaprisoncore.api.config.entry;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.entity.mine.MinePositionalKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

@EqualsAndHashCode(callSuper = true)
@ToString
public class MinePositionalKeys extends HashSet<MinePositionalKey> {

    @Getter
    private final String mineName;

    public MinePositionalKeys(String mineName) {
        this.mineName = mineName;
    }

    public MinePositionalKeys(@NotNull Collection<? extends MinePositionalKey> c, String mineName) {
        super(c);
        this.mineName = mineName;
    }
}
