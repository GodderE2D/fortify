package gg.bsr.fortify.lib;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public interface ChatMsg {
    static TextComponent error(String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize("&2Fortify&r - &c" + message);
    }

    static TextComponent info(String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize("&2Fortify&r - " + message);
    }
}
