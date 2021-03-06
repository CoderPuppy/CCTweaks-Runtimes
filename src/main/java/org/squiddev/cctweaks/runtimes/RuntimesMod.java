package org.squiddev.cctweaks.runtimes;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.squiddev.cctweaks.api.CCTweaksAPI;

import java.io.File;
import java.util.Map;

@Mod(
	modid = RuntimesMod.ID,
	name = RuntimesMod.NAME,
	version = "${mod_version}",
	dependencies = "required-after:ComputerCraft;",
	acceptedMinecraftVersions = "[1.8.9,]",
	guiFactory = "org.squiddev.cctweaks.runtimes.GuiConfigFactory"
)
public class RuntimesMod {
	public static final String ID = "cctweaks-runtimes";
	public static final String NAME = "CCTweaks Runtimes";

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		org.squiddev.cctweaks.runtimes.ConfigForgeLoader.init(new File(event.getModConfigurationDirectory(), RuntimesMod.ID + ".cfg"));
		Config.configuration = org.squiddev.cctweaks.runtimes.ConfigForgeLoader.getConfiguration();

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
		if (eventArgs.modID.equals(RuntimesMod.ID)) {
			org.squiddev.cctweaks.runtimes.ConfigForgeLoader.sync();
		}
	}
}
