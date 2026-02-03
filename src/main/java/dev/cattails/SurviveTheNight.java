package dev.cattails;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PacketWatcher;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.cattails.commands.BuffCommand;
import dev.cattails.commands.CurrencyCommand;
import dev.cattails.commands.SpawnCommand;
import dev.cattails.commands.StoreCommand;
import dev.cattails.events.*;
import dev.cattails.util.MobSpawn;
import dev.cattails.util.PlayerBuff;
import dev.cattails.util.PlayerBuffStore;
import dev.cattails.util.PlayerStore;

import javax.annotation.Nonnull;

public class SurviveTheNight extends JavaPlugin {

    public static SurviveTheNight INSTANCE;

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private ComponentType<EntityStore, PlayerStore> PlayerStoreComponent;
    private ComponentType<EntityStore, PlayerBuffStore> PlayerBuffStoreComponent;

    public SurviveTheNight(@Nonnull JavaPluginInit init) {

        super(init);

        INSTANCE = this;

        String name = this.getManifest().getName();
        String version = this.getManifest().getVersion().toString();
        LOGGER.atInfo().log("Loading " + name + " | " + version);

    }

    @Override
    protected void setup() {

        LOGGER.atInfo().log("Setting up " + this.getManifest().getName());

         // Load Stores
        this.PlayerStoreComponent = this.getEntityStoreRegistry().registerComponent(
                PlayerStore.class, "PlayerStoreComponent", PlayerStore.CODEC);
        this.PlayerBuffStoreComponent = this.getEntityStoreRegistry().registerComponent(
                PlayerBuffStore.class, "PlayerBuffStoreComponent", PlayerBuffStore.CODEC);


        // Register Events
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, OnJoin::onPlayerReady);

        // Register System
        this.getEntityStoreRegistry().registerSystem(new PlayerDeathSystem());
        this.getEntityStoreRegistry().registerSystem(new MobDeathSystem());
        this.getEntityStoreRegistry().registerSystem(new PlayerBuff());
        this.getEntityStoreRegistry().registerSystem(new MobSpawn());

        // Register Ghostmode Events,
        this.getEntityStoreRegistry().registerSystem(new GhostDamage());
        this.getEntityStoreRegistry().registerSystem(new GhostMode.CancelBreakBlock());
        this.getEntityStoreRegistry().registerSystem(new GhostMode.CancelPlaceBlock());
        this.getEntityStoreRegistry().registerSystem(new GhostMode.CancelUseBlockPre());
        this.getEntityStoreRegistry().registerSystem(new GhostMode.CancelPickupItem());

        // Interactions
        this.getCodecRegistry(Interaction.CODEC).register("open_storebench_ui", StoreBenchInteraction.class, StoreBenchInteraction.CODEC);

        // Commands
        this.getCommandRegistry().registerCommand(new BuffCommand());
        this.getCommandRegistry().registerCommand(new StoreCommand());
        this.getCommandRegistry().registerCommand(new CurrencyCommand());
        this.getCommandRegistry().registerCommand(new SpawnCommand());

    }

    public ComponentType<EntityStore, PlayerStore> getPlayerStoreComponent() {
        return this.PlayerStoreComponent;
    }
    public ComponentType<EntityStore, PlayerBuffStore> getPlayerBuffsStoreComponent() {
        return this.PlayerBuffStoreComponent;
    }

}
