package org.squiddev.cctweaks.runtimes;

import com.google.common.collect.ImmutableSet;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.MissingModsException;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;
import org.squiddev.cctweaks.api.CCTweaksAPI;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mod(
	modid = RuntimesMod.ID,
	name = RuntimesMod.NAME,
	version = "${mod_version}",
	dependencies = "after:ComputerCraft;after:computercraft",
	acceptedMinecraftVersions = "[1.8.9,]",
	guiFactory = "org.squiddev.cctweaks.runtimes.GuiConfigFactory"
)
public class RuntimesMod {
	public static final String ID = "cctweaks-runtimes";
	public static final String NAME = "CCTweaks Runtimes";

	public static Logger logger;

	public RuntimesMod() {
		if (!Loader.isModLoaded("computercraft") && !Loader.isModLoaded("ComputerCraft")) {
			throw new MissingModsException(ImmutableSet.of(
				(ArtifactVersion) new DefaultArtifactVersion("ComputerCraft", true)
			), ID, NAME);
		}
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		org.squiddev.cctweaks.runtimes.ConfigForgeLoader.init(new File(event.getModConfigurationDirectory(), RuntimesMod.ID + ".cfg"));
		Config.configuration = org.squiddev.cctweaks.runtimes.ConfigForgeLoader.getConfiguration();

		logger = event.getModLog();

		MinecraftForge.EVENT_BUS.register(this);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		Runtimes.register(CCTweaksAPI.instance().luaEnvironment());
	}

	@NetworkCheckHandler
	public boolean onNetworkConnect(Map<String, String> mods, Side side) {
		// This can work on the server or on the client
		return true;
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		String modID = null;

		try {
			modID = (String) ConfigChangedEvent.OnConfigChangedEvent.class.getField("modID").get(eventArgs);
		} catch (NoSuchFieldException ignored) {
		} catch (IllegalAccessException ignored) {
		} catch (ClassCastException ignored) {
		}

		try {
			modID = (String) ConfigChangedEvent.OnConfigChangedEvent.class.getMethod("getModID").invoke(eventArgs);
		} catch (ClassCastException ignored) {
		} catch (IllegalAccessException ignored) {
		} catch (InvocationTargetException ignored) {
		} catch (NoSuchMethodException ignored) {
		}

		if (modID == null) {
			logger.warn("Could not get the mod id from OnConfigChangedEvent");
		} else if (modID.equals(RuntimesMod.ID)) {
			org.squiddev.cctweaks.runtimes.ConfigForgeLoader.sync();
		}
	}
}
