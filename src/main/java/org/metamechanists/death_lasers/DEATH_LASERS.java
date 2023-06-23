package org.metamechanists.death_lasers;

import co.aikar.commands.PaperCommandManager;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.metamechanists.death_lasers.beams.beam.DirectBeam;
import org.metamechanists.death_lasers.beams.ticker.ticker.DirectTicker;
import org.metamechanists.death_lasers.connections.ConnectionGroup;
import org.metamechanists.death_lasers.connections.info.ConnectionInfoDisplay;
import org.metamechanists.death_lasers.connections.info.PointInformationListener;
import org.metamechanists.death_lasers.connections.links.Link;
import org.metamechanists.death_lasers.connections.points.ConnectionPointInput;
import org.metamechanists.death_lasers.connections.points.ConnectionPointOutput;
import org.metamechanists.death_lasers.implementation.tools.TargetingWandListener;
import org.metamechanists.death_lasers.items.Groups;
import org.metamechanists.death_lasers.items.Items;
import org.metamechanists.death_lasers.storage.SaveRunnable;
import org.metamechanists.death_lasers.storage.Storage;
import org.metamechanists.death_lasers.utils.Language;
import org.metamechanists.death_lasers.utils.id.BlockDisplayID;
import org.metamechanists.death_lasers.utils.id.ConnectionGroupID;
import org.metamechanists.death_lasers.utils.id.ConnectionPointID;
import org.metamechanists.death_lasers.utils.id.DisplayGroupID;
import org.metamechanists.death_lasers.utils.id.InteractionID;

public final class DEATH_LASERS extends JavaPlugin implements SlimefunAddon {
    @Getter
    private static DEATH_LASERS instance;

    private void initializeSerializables() {
        ConfigurationSerialization.registerClass(BlockDisplayID.class, "BlockDisplayID");
        ConfigurationSerialization.registerClass(ConnectionGroupID.class, "ConnectionGroupID");
        ConfigurationSerialization.registerClass(ConnectionPointID.class, "ConnectionPointID");
        ConfigurationSerialization.registerClass(DisplayGroupID.class, "DisplayGroupID");
        ConfigurationSerialization.registerClass(InteractionID.class, "InteractionID");


        ConfigurationSerialization.registerClass(ConnectionGroup.class, "ConnectionGroup");
        ConfigurationSerialization.registerClass(ConnectionPointOutput.class, "ConnectionPointOutput");
        ConfigurationSerialization.registerClass(ConnectionPointInput.class, "ConnectionPointInput");
        ConfigurationSerialization.registerClass(ConnectionInfoDisplay.class, "ConnectionInfoDisplay");

        ConfigurationSerialization.registerClass(Link.class, "Link");

        ConfigurationSerialization.registerClass(DirectBeam.class, "DirectBlockDisplayBeam");
        ConfigurationSerialization.registerClass(DirectTicker.class, "DirectSinglePulseTicker");
    }

    private void initializeListeners() {
        final PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new TargetingWandListener(), this);
        pluginManager.registerEvents(new PointInformationListener(), this);
    }

    public void initializeRunnables() {
        new LaserTicker().runTaskTimer(instance, 0, LaserTicker.INTERVAl_TICKS);
        new SaveRunnable().runTaskTimer(instance, SaveRunnable.INTERVAL_TICKS, SaveRunnable.INTERVAL_TICKS);
    }

    public void initializeCommands() {
        final PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");
    }

    @Override
    public void onEnable() {
        instance = this;
        Language.initialize();
        Groups.initialize();
        Items.initialize();
        initializeSerializables();
        Storage.load();
        initializeListeners();
        initializeRunnables();
        initializeCommands();
    }

    @Override
    public void onDisable() {
        Storage.save();
    }

    @NotNull
    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Nullable
    @Override
    public String getBugTrackerURL() {
        return null;
    }
}
