package gc.mc.tropical_fish.client;

import net.fabricmc.api.ClientModInitializer;

public class TropicalFishClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FishListener.registerListener();
    }

}
