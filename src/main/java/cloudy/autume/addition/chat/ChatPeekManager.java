package cloudy.autume.addition.chat;

import cloudy.autume.addition.QCloudyAdditionClient;
import cloudy.autume.addition.config.ConfigManager;
import net.minecraft.client.Minecraft;

public final class ChatPeekManager {
    private ChatPeekManager() {
    }

    public static boolean active() {
        Minecraft client = Minecraft.getInstance();
        return ConfigManager.get().chat.chatPeek && client.screen == null
                && QCloudyAdditionClient.isChordDown(
                QCloudyAdditionClient.ChordAction.PEEK_CHAT);
    }

    public static boolean scrollsChat() {
        return active() && "CHAT".equals(ConfigManager.get().chat.peekScrollTarget);
    }
}
