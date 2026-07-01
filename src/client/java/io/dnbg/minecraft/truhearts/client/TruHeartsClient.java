package io.dnbg.minecraft.truhearts.client;

import io.dnbg.minecraft.truhearts.client.config.TruHeartsConfig;
import io.dnbg.minecraft.truhearts.client.hud.HpReadout;
import io.dnbg.minecraft.truhearts.client.input.KeyBindings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/**
 * Client entry point. Register HUD callbacks, attack callbacks, and any
 * other client-only wiring here.
 *
 * <p>Feature classes (HUD overlay, damage tracker, …) live as siblings
 * under {@code client.hud} / sibling sub-packages — this file stays as a
 * small wiring hub so new features land as one-line additions here.
 */
public class TruHeartsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HpReadout.register();
		KeyBindings.register();
		ClientTickEvents.END_CLIENT_TICK.register(TruHeartsClient::pollToggle);
	}

	/**
	 * Drain any pending toggle-key clicks (there can be more than one per
	 * tick under lag) and flip {@link TruHeartsConfig#enabled} for each,
	 * persisting after every flip and echoing to the action bar so the
	 * player sees the state change.
	 */
	private static void pollToggle(Minecraft client) {
		while (KeyBindings.TOGGLE.consumeClick()) {
			TruHeartsConfig cfg = TruHeartsConfig.get();
			cfg.enabled = !cfg.enabled;
			cfg.save();
			String msgKey = "truhearts.toggle." + (cfg.enabled ? "on" : "off");
			// setOverlayMessage lives on Gui.hud (public field) in 26.2;
			// the method was hoisted off of Gui itself into the Hud subobject.
			client.gui.hud.setOverlayMessage(Component.translatable(msgKey), false);
		}
	}
}
