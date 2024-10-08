package com.github.aburaagetarou;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * メッセージ送信に関するユーティリティクラス
 * @author AburaAgeTarou
 */
public class MessageUtils {

    /**
     * メッセージのプレイヤー名を置換する
     * @param message メッセージ
     * @param player プレイヤー
     */
    public static String replacePlayerName(String message, Player player) {
        return message.replace("<player>", player.getName());
    }

    /**
     * 色付きのメッセージをブロードキャストする
     * @param message メッセージ
     */
    public static void broadcastColoredMessage(String message) {
        Bukkit.broadcast(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
    }
    
    /**
     * 色付きのメッセージを送信する
     * @param target 対象プレイヤー
     * @param message メッセージ
     */
    public static void sendColoredMessage(Audience target, String message) {
        target.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
    }
}
