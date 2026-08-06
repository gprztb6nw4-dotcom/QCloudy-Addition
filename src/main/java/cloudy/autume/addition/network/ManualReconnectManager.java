package cloudy.autume.addition.network;

import cloudy.autume.addition.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;

/** Stores only the current client's last explicit connection target. */
public final class ManualReconnectManager {
    private static Target lastTarget;

    private ManualReconnectManager() {
    }

    public static void remember(ServerAddress address, ServerData data) {
        if (address == null) return;
        String fallback = formatAddress(address.getHost(), address.getPort());
        String ip = data == null || data.ip == null || data.ip.isBlank() ? fallback : data.ip.trim();
        if (ip.isBlank()) return;
        String name = data == null || data.name == null || data.name.isBlank() ? ip : data.name;
        ServerData.Type type = data == null ? ServerData.Type.OTHER : data.type();
        ServerData.ServerPackStatus packStatus = data == null ? ServerData.ServerPackStatus.PROMPT
                : data.getResourcePackStatus();
        lastTarget = new Target(name, ip, type, packStatus);
    }

    public static boolean available() {
        Minecraft client = Minecraft.getInstance();
        return ConfigManager.get().manualReconnectButton && client.allowsMultiplayer()
                && lastTarget != null && !lastTarget.ip().isBlank();
    }

    /** Called only by the player's click on the disconnect-screen button. */
    public static void reconnect(Screen parent) {
        if (!available()) return;
        Target target = lastTarget;
        ServerData data = new ServerData(target.name(), target.ip(), target.type());
        data.setResourcePackStatus(target.packStatus());
        ConnectScreen.startConnecting(parent, Minecraft.getInstance(), ServerAddress.parseString(target.ip()),
                data, false, null);
    }

    static String formatAddress(String host, int port) {
        if (host == null || host.isBlank()) return "";
        String normalized = host.trim();
        if (port == 25565) return normalized;
        if (normalized.indexOf(':') >= 0 && !normalized.startsWith("[")) normalized = "[" + normalized + "]";
        return normalized + ":" + port;
    }

    private record Target(String name, String ip, ServerData.Type type,
                          ServerData.ServerPackStatus packStatus) {
    }
}
