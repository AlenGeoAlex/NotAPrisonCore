package me.alenalex.notaprisoncore.paper.entity.mine;

import me.alenalex.notaprisoncore.paper.abstracts.AbstractBlockChoices;
import org.jetbrains.annotations.NotNull;

public class TemporaryBlockChoices extends AbstractBlockChoices {

    public TemporaryBlockChoices() {
    }

    @Override
    public @NotNull String toJson() {
        return "[]";
    }

    @Override
    public void clearAndSetDefault() {

    }




}
