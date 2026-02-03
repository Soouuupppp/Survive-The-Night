package dev.cattails.util;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class PlayerBuffStore implements Component<EntityStore> {

    private int Currency;

    private int HealthBoost;
    private int StaminaBoost;
    private int UltimateBoost;
    private int Revives;

    public static final Map<String, Map<Integer, Integer>> Buffs =
            new HashMap<>(Map.of(
                    "Health", new HashMap<>(Map.of(
                            1, 5,
                            2, 10,
                            3, 20,
                            4, 40,
                            5, 80
                    )),
                    "Stamina", new HashMap<>(Map.of(
                            1, 1,
                            2, 2,
                            3, 5,
                            4, 8,
                            5, 10
                    )),
                    "Ultimate", new HashMap<>(Map.of(
                            1, 1,
                            2, 3,
                            3, 10,
                            4, 30,
                            5, 50
                    )),
                    "Revives", new HashMap<>(Map.of(
                            1, 1,
                            2, 2,
                            3, 3

                    ))
            ));

    public static final Map<String, Map<Integer, Integer>> BuffPrices =
            new HashMap<>(Map.of(
                    "Health", new HashMap<>(Map.of(
                            1, 250,
                            2, 500,
                            3, 1000,
                            4, 2000,
                            5, 4000
                    )),
                    "Stamina", new HashMap<>(Map.of(
                            1, 200,
                            2, 400,
                            3, 800,
                            4, 1600,
                            5, 3200
                    )),
                    "Ultimate", new HashMap<>(Map.of(
                            1, 500,
                            2, 1000,
                            3, 2500,
                            4, 10000,
                            5, 25000
                    )),
                    "Revives", new HashMap<>(Map.of(
                            1, 100,
                            2, 1000,
                            3, 10000

                    ))
            ));


    public static final BuilderCodec<PlayerBuffStore> CODEC = BuilderCodec.<PlayerBuffStore>builder(PlayerBuffStore.class, PlayerBuffStore::new)
            .append(
                    new KeyedCodec<>("Currency", Codec.INTEGER),
                    (data, value) -> data.Currency = value,
                    data -> data.Currency
            ).add()
            .append(
                    new KeyedCodec<>("HealthBoost", Codec.INTEGER),
                    (data, value) -> data.HealthBoost = value,
                    data -> data.HealthBoost
            ).add()
            .append(
                    new KeyedCodec<>("StaminaBoost", Codec.INTEGER),
                    (data, value) -> data.StaminaBoost = value,
                    data -> data.StaminaBoost
            ).add()
            .append(
                    new KeyedCodec<>("Revives", Codec.INTEGER),
                    (data, value) -> data.Revives = value,
                    data -> data.Revives
            ).add()
//            .append(
//                    new KeyedCodec<>("HarvestBoost", Codec.INTEGER),
//                    (data, value) -> data.HarvestBoost = value,
//                    data -> data.HarvestBoost
//            ).add()
            .build();

    public PlayerBuffStore() {
        this.Currency = 0;
        this.HealthBoost = 0;
        this.UltimateBoost = 0;
        this.StaminaBoost = 0;
        this.Revives = 0;
//        this.HarvestBoost = 0;
    }

    public PlayerBuffStore(PlayerBuffStore clone) {
        this.Currency = clone.Currency;
        this.HealthBoost = clone.HealthBoost;
        this.UltimateBoost = clone.UltimateBoost;
        this.StaminaBoost = clone.StaminaBoost;
        this.Revives = clone.Revives;
//        this.HarvestBoost = clone.HarvestBoost;
    }

    public int getCurrency() { return Currency; }
    public int getHealthBoost() { return HealthBoost; }
    public int getUltimateBoost() { return UltimateBoost; }
    public int getStaminaBoost() { return StaminaBoost; }
    public int getRevives() { return Revives; }
//    public int getHarvestBoost() { return HarvestBoost; }

    public void addCurrency(int Currency) { this.Currency += Currency; }
    public void addHealthBoost(int HealthBoost) { this.HealthBoost += HealthBoost; }
    public void addUltimateBoost(int UltimateBoost) { this.UltimateBoost += UltimateBoost; }
    public void addStaminaBoost(int StaminaBoost) { this.StaminaBoost += StaminaBoost; }
    public void addRevives(int Revives) { this.Revives += Revives; }
//    public void addHarvestBoost(int HarvestBoost) { this.HarvestBoost += HarvestBoost; }

    public boolean canAfford(int cost){
        return this.Currency >= cost;
    }
    public void spendCurrency(int cost) {
        this.Currency -= cost;
    }

    @Nonnull
    @Override
    public Component<EntityStore> clone() { return new  PlayerBuffStore(this); }

}
