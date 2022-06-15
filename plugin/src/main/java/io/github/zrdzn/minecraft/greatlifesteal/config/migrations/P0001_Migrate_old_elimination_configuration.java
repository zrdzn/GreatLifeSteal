package io.github.zrdzn.minecraft.greatlifesteal.config.migrations;

import static eu.okaeri.configs.migrate.ConfigMigrationDsl.move;
import static eu.okaeri.configs.migrate.ConfigMigrationDsl.when;
import static eu.okaeri.configs.migrate.ConfigMigrationDsl.match;
import static eu.okaeri.configs.migrate.ConfigMigrationDsl.update;
import static eu.okaeri.configs.migrate.ConfigMigrationDsl.multi;
import static eu.okaeri.configs.migrate.ConfigMigrationDsl.delete;

import eu.okaeri.configs.migrate.builtin.NamedMigration;

import java.util.ArrayList;
import java.util.List;

public class P0001_Migrate_old_elimination_configuration extends NamedMigration {

    public P0001_Migrate_old_elimination_configuration() {
        super(
            "Migrate old top-level keys from eliminationMode to customActions subconfig",
            when(
                match("eliminationMode.action", v -> v instanceof String && ((String) v).equalsIgnoreCase("SPECTATOR_MODE")),
                multi(
                    move("eliminationMode.enabled", "customActions.spectate.enabled"),
                    move("eliminationMode.action", "customActions.spectate.type"),
                    move("eliminationMode.requiredHealth", "customActions.spectate.activateAtHealth"),
                    move("eliminationMode.commands", "customActions.spectate.parameters"),
                    when(
                        match("customActions.spectate.parameters", v -> v instanceof List),
                        update("customActions.spectate.parameters", oldList -> {
                            List<String> newList = new ArrayList<>();
                            newList.add("gamemode spectator {victim}");
                            return newList;
                        })
                    ),
                    delete("eliminationMode.broadcastMessages")
                )
            ),
            when(
                match("eliminationMode.action", v -> v instanceof String && ((String) v).equalsIgnoreCase("BROADCAST")),
                multi(
                    move("eliminationMode.enabled", "customActions.announce.enabled"),
                    move("eliminationMode.action", "customActions.announce.type"),
                    move("eliminationMode.requiredHealth", "customActions.announce.activateAtHealth"),
                    move("eliminationMode.broadcastMessages", "customActions.announce.parameters"),
                    delete("eliminationMode.commands")
                )
            ),
            when(
                match("eliminationMode.action", v -> v instanceof String && ((String) v).equalsIgnoreCase("DISPATCH_COMMANDS")),
                multi(
                    move("eliminationMode.enabled", "customActions.commands.enabled"),
                    move("eliminationMode.action", "customActions.commands.type"),
                    move("eliminationMode.requiredHealth", "customActions.commands.activateAtHealth"),
                    move("eliminationMode.commands", "customActions.commands.parameters"),
                    delete("eliminationMode.broadcastMessages")
                )
            )
        );
    }

}
