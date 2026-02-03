package dev.cattails.util;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class PlayerStore implements Component<EntityStore> {

    private Boolean GhostMode;
    private double DeathX;
    private double DeathY;
    private double DeathZ;

    public static final BuilderCodec<PlayerStore> CODEC = BuilderCodec.builder(PlayerStore.class, PlayerStore::new)
            .append(
                    new KeyedCodec<>("GhostMode", Codec.BOOLEAN),
                    (data, value) -> data.GhostMode = value,
                    data -> data.GhostMode
            ).add()
            .append(
                    new KeyedCodec<>("DeathX", Codec.DOUBLE),
                    (data, value) -> data.DeathX = value,
                    data -> data.DeathX
            ).add()
            .append(
                    new KeyedCodec<>("DeathY", Codec.DOUBLE),
                    (data, value) -> data.DeathY = value,
                    data -> data.DeathY
            ).add()
            .append(
                    new KeyedCodec<>("DeathZ", Codec.DOUBLE),
                    (data, value) -> data.DeathZ = value,
                    data -> data.DeathZ
            ).add()
            .build();

    public PlayerStore() {
        this.GhostMode = false;
    }

    public PlayerStore(PlayerStore clone) {
        this.GhostMode = clone.GhostMode;
    }

    public Boolean getGhostMode() {
        return GhostMode;
    }

    public double getDeathX() { return DeathX; }
    public double getDeathY() { return DeathY; }
    public double getDeathZ() { return DeathZ; }

    public void setGhostMode(Boolean value) {
        this.GhostMode = value;
    }

    public void setDeathX(double value) { this.DeathX = value; }
    public void setDeathY(double value) { this.DeathY = value; }
    public void setDeathZ(double value) { this.DeathZ = value; }

    @Nonnull
    @Override
    public Component<EntityStore> clone() {
        return new PlayerStore(this);
    }


}
