package me.alenalex.notaprisoncore.api.config;

import me.alenalex.notaprisoncore.api.config.entry.MinePositionalKeys;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public interface IMineIdentifierConfiguration {
    @NotNull Optional<MinePositionalKeys> ofMine(String mine);

    @NotNull Collection<MinePositionalKeys> getKeys();

}
