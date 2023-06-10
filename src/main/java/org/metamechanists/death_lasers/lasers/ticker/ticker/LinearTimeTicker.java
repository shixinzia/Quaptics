package org.metamechanists.death_lasers.lasers.ticker.ticker;

import dev.sefiraat.sefilib.entity.display.builders.BlockDisplayBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.util.Vector;
import org.metamechanists.death_lasers.DEATH_LASERS;

public class LinearTimeTicker implements LaserBlockDisplayTicker {
    private final int lifespanTicks;
    private final Vector velocity;
    private final BlockDisplay display;
    private int ageTicks = 0;

    public LinearTimeTicker(BlockDisplayBuilder displayBuilder, Location source, Location target, int lifespanTicks) {
        this.lifespanTicks = lifespanTicks;
        velocity = target.toVector()
                .subtract(source.toVector())
                .multiply(1.0/lifespanTicks);
        this.display = new BlockDisplayBuilder()
                .setBrightness(new Display.Brightness(4, 0))
                .setBlockData(Material.RED_CONCRETE.createBlockData())
                .setGroupParentOffset(new Vector())
                .setLocation(source)
                .setDisplayHeight(0.1F)
                .setDisplayWidth(0.1F)
                .build();
    }


    @Override
    public void tick() {
        display.teleport(display.getLocation().add(velocity));
        ageTicks++;
    }

    @Override
    public void remove() {
        display.remove();
    }

    @Override
    public boolean expired() {
        return ageTicks >= lifespanTicks;
    }
}

