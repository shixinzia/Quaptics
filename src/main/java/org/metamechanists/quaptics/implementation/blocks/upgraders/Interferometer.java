package org.metamechanists.quaptics.implementation.blocks.upgraders;

import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.quaptics.connections.ConnectionGroup;
import org.metamechanists.quaptics.connections.ConnectionPoint;
import org.metamechanists.quaptics.connections.ConnectionPointType;
import org.metamechanists.quaptics.connections.Link;
import org.metamechanists.quaptics.implementation.attachments.PowerAnimatedBlock;
import org.metamechanists.quaptics.implementation.attachments.PowerLossBlock;
import org.metamechanists.quaptics.implementation.base.ConnectedBlock;
import org.metamechanists.quaptics.implementation.blocks.Settings;
import org.metamechanists.quaptics.items.Lore;
import org.metamechanists.quaptics.items.Tier;
import org.metamechanists.quaptics.utils.id.complex.ConnectionGroupId;
import org.metamechanists.quaptics.utils.id.complex.ConnectionPointId;
import org.metamechanists.quaptics.utils.models.ModelBuilder;
import org.metamechanists.quaptics.utils.models.components.ModelCuboid;

import java.util.List;
import java.util.Optional;


public class Interferometer extends ConnectedBlock implements PowerAnimatedBlock, PowerLossBlock {
    public static final Settings INTERFEROMETER_1_SETTINGS = Settings.builder()
            .tier(Tier.BASIC)
            .powerLoss(0.07)
            .build();
    public static final SlimefunItemStack INTERFEROMETER_1 = new SlimefunItemStack(
            "QP_INTERFEROMETER_1",
            Material.YELLOW_TERRACOTTA,
            "&cInterferometer &4I",
            Lore.create(INTERFEROMETER_1_SETTINGS,
                    "&7● Sets the Phase of the main ray to the phase of the auxiliary ray"));

    private static final Vector MAIN_INPUT_LOCATION = new Vector(0.0F, 0.0F, -0.45F);
    private static final Vector AUXILIARY_INPUT_LOCATION = new Vector(0.45F, 0.0F, 0.0F);
    private static final Vector OUTPUT_LOCATION = new Vector(0.0F, 0.0F, 0.45);

    public Interferometer(final ItemGroup itemGroup, final SlimefunItemStack item, final RecipeType recipeType, final ItemStack[] recipe, final Settings settings) {
        super(itemGroup, item, recipeType, recipe, settings);
    }

    @Override
    protected float getConnectionRadius() {
        return 0.0F;
    }
    @Override
    protected Optional<Location> calculatePointLocationSphere(@NotNull final ConnectionPointId from, @NotNull final ConnectionPointId to) {
        final Optional<ConnectionPoint> point = from.get();
        return point.isPresent() ? point.get().getLocation() : Optional.empty();
    }
    @Override
    protected DisplayGroup initModel(final @NotNull Location location, final @NotNull Player player) {
        return new ModelBuilder()
                .add("main", new ModelCuboid()
                        .material(Material.YELLOW_TERRACOTTA)
                        .facing(player.getFacing())
                        .size(0.3F, 0.3F, 0.9F))
                .add("auxiliary", new ModelCuboid()
                        .material(Material.GRAY_CONCRETE)
                        .facing(player.getFacing())
                        .size(0.4F, 0.15F, 0.15F)
                        .location(0.2F, 0, 0))
                .add("prism", new ModelCuboid()
                        .material(settings.getTier().concreteMaterial)
                        .facing(player.getFacing())
                        .rotation(Math.PI/4)
                        .size(0.4F))
                .buildAtBlockCenter(location);
    }
    @Override
    protected List<ConnectionPoint> initConnectionPoints(final ConnectionGroupId groupId, final Player player, final Location location) {
        return List.of(
                new ConnectionPoint(ConnectionPointType.INPUT, groupId, "main", formatPointLocation(player, location, MAIN_INPUT_LOCATION)),
                new ConnectionPoint(ConnectionPointType.INPUT, groupId, "auxiliary", formatPointLocation(player, location, AUXILIARY_INPUT_LOCATION)),
                new ConnectionPoint(ConnectionPointType.OUTPUT, groupId, "output", formatPointLocation(player, location, OUTPUT_LOCATION)));
    }

    @Override
    public void onInputLinkUpdated(@NotNull final ConnectionGroup group, @NotNull final Location location) {
        if (doBurnoutCheck(group, "main") || doBurnoutCheck(group, "auxiliary")) {
            return;
        }

        final Optional<Link> mainLink = getLink(location, "main");
        final Optional<Link> auxiliaryLink = getLink(location, "auxiliary");
        final Optional<Link> outputLink = getLink(location, "output");
        onPoweredAnimation(location, settings.isOperational(auxiliaryLink));
        if (outputLink.isEmpty()) {
            return;
        }

        if (auxiliaryLink.isEmpty() || mainLink.isEmpty() || !settings.isOperational(mainLink.get())) {
            outputLink.get().disable();
            return;
        }

        outputLink.get().setPowerFrequencyPhase(
                PowerLossBlock.calculatePowerLoss(settings, mainLink.get()),
                mainLink.get().getFrequency(),
                auxiliaryLink.get().getPhase());
    }

    @Override
    public void onPoweredAnimation(final Location location, final boolean powered) {
        brightnessAnimation(location, "prism", powered);
    }
}
