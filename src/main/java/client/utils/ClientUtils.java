/*
 * Decompiled with CFR 0.151.
 */
package client.utils;

import client.utils.MinecraftInstance;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.IChatComponent;

public class ClientUtils
extends MinecraftInstance {
    public static void displayChatMessage(String message) {
        if (ClientUtils.mc.thePlayer == null) {
            return;
        }
        String s = "[\u00a73MCP LITE\u00a7f] " + message;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", s);
        ClientUtils.mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent(jsonObject.toString()));
    }

    public static void displayClearChatMessage(String message) {
        if (ClientUtils.mc.thePlayer == null) {
            return;
        }
        String s = message;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", s);
        ClientUtils.mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent(jsonObject.toString()));
    }

    public static boolean nullCheck() {
        return ClientUtils.mc.thePlayer == null || ClientUtils.mc.theWorld == null;
    }

    public static void fixedLanguageLoad() {
        ArrayList<IResourcePack> list = Lists.newArrayList(ClientUtils.mc.defaultResourcePacks);
        for (ResourcePackRepository.Entry resourcepackrepository$entry : ClientUtils.mc.mcResourcePackRepository.getRepositoryEntries()) {
            list.add(resourcepackrepository$entry.getResourcePack());
        }
        if (ClientUtils.mc.mcResourcePackRepository.getResourcePackInstance() != null) {
            list.add(ClientUtils.mc.mcResourcePackRepository.getResourcePackInstance());
        }
        try {
            list.add(null);
            ClientUtils.mc.mcResourceManager.reloadResources(list);
        }
        catch (RuntimeException runtimeexception) {
            Minecraft.logger.info("Caught error stitching, removing all assigned resourcepacks", (Throwable)runtimeexception);
            list.clear();
            list.addAll(ClientUtils.mc.defaultResourcePacks);
            ClientUtils.mc.mcResourcePackRepository.setRepositories(Collections.emptyList());
            list.add(null);
            ClientUtils.mc.mcResourceManager.reloadResources(list);
            ClientUtils.mc.gameSettings.resourcePacks.clear();
            ClientUtils.mc.gameSettings.incompatibleResourcePacks.clear();
            ClientUtils.mc.gameSettings.saveOptions();
        }
        list.remove(null);
        ClientUtils.mc.mcLanguageManager.parseLanguageMetadata(list);
        if (ClientUtils.mc.renderGlobal != null) {
            ClientUtils.mc.renderGlobal.loadRenderers();
        }
    }

    public static void reloadLanguage(IResourceManager reloadListener, List<IResourceManagerReloadListener> reloadListeners) {
        for (IResourceManagerReloadListener iresourcemanagerreloadlistener : reloadListeners) {
            if (!(iresourcemanagerreloadlistener instanceof LanguageManager)) continue;
            iresourcemanagerreloadlistener.onResourceManagerReload(reloadListener);
        }
    }
}

