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
            move("eliminationMode", "customActions"),
            when(
                match("customActions.action", v -> v instanceof String && ((String) v).equalsIgnoreCase("SPECTATOR_MODE")),
                multi(
                    move("customActions.enabled", "customActions.spectate.enabled"),
                    move("customActions.action", "customActions.spectate.type"),
                    move("customActions.requiredHealth", "customActions.spectate.activateAtHealth"),
                    move("customActions.commands", "customActions.spectate.parameters"),
                    when(
                        match("customActions.spectate.parameters", v -> v instanceof List),
                        update("customActions.spectate.parameters", oldList -> {
                            List<String> newList = new ArrayList<>();
                            newList.add("gamemode spectator {victim}");
                            return newList;
                        })
                    ),
                    delete("customActions.broadcastMessages")
                )
            ),
            when(
                match("customActions.action", v -> v instanceof String && ((String) v).equalsIgnoreCase("BROADCAST")),
                multi(
                    move("customActions.enabled", "customActions.announce.enabled"),
                    move("customActions.action", "customActions.announce.type"),
                    move("customActions.requiredHealth", "customActions.announce.activateAtHealth"),
                    move("customActions.broadcastMessages", "customActions.announce.parameters"),
                    delete("customActions.commands")
                )
            ),
            when(
                match("customActions.action", v -> v instanceof String && ((String) v).equalsIgnoreCase("DISPATCH_COMMANDS")),
                multi(
                    move("customActions.enabled", "customActions.commands.enabled"),
                    move("customActions.action", "customActions.commands.type"),
                    move("customActions.requiredHealth", "customActions.commands.activateAtHealth"),
                    move("customActions.commands", "customActions.commands.parameters"),
                    delete("customActions.broadcastMessages")
                )
            )
        );
    }

}
