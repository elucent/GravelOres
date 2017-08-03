package elucent.gravelores.proxy;

import elucent.gravelores.GravelOres;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {
		GravelOres.registerAll();
	}

	public void init(FMLInitializationEvent event) {
		GravelOres.registerOreDict();
	}

	public void postInit(FMLPostInitializationEvent event) {
		GravelOres.registerRecipes();
	}
}
