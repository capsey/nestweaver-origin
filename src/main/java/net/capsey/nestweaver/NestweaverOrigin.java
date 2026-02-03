package net.capsey.nestweaver;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeReference;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.resources.ResourceLocation;

public class NestweaverOrigin implements ModInitializer {
	public static final String MOD_ID = "nestweaver-origin";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final PowerType<?> SPIDER_KINSHIP = new PowerTypeReference<>(NestweaverOrigin.identifier("spider_kinship"));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
	}

	public static ResourceLocation identifier(String path) {
		return new ResourceLocation(NestweaverOrigin.MOD_ID, path);
	}
}