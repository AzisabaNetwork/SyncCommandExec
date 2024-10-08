package com.github.aburaagetarou.config;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.aburaagetarou.SyncCommandExec;

/**
 * 同期トリガーの設定を管理するクラス
 * @auther AburaAgeTarou
 */
public class SyncCommandTriggers {
    
    // 同期トリガー
    private static Set<String> keys = new HashSet<>();
    private static Map<String, String> triggerCmds = new HashMap<>();

    // 実行コマンド
    private static Map<String, List<String>> syncCmds = new HashMap<>();
    private static Map<String, List<String>> beginExecMsgs = new HashMap<>();
    private static Map<String, List<String>> execEndMsgs = new HashMap<>();

    /**
     * 設定読み込み
     * @param config 設定ファイル
     */
    public static void loadConfig(File file) {
        if(!file.exists()) {
            SyncCommandExec.getInstance().getLogger().warning("同期コマンドの設定ファイルが存在しません。");
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection triggers = config.getConfigurationSection("triggers");
        keys = triggers.getKeys(false);
        triggerCmds = new HashMap<>();
        syncCmds = new HashMap<>();
        beginExecMsgs = new HashMap<>();
        execEndMsgs = new HashMap<>();
        for(String key : keys) {
            List<String> cmds = triggers.getStringList(key + ".commands");
            List<String> beginMsgs = triggers.getStringList(key + ".messages.begin");
            List<String> endMsgs = triggers.getStringList(key + ".messages.end");
            for(String cmd : cmds) triggerCmds.put(cmd, key);
            syncCmds.put(key, cmds);
            beginExecMsgs.put(key, beginMsgs);
            execEndMsgs.put(key, endMsgs);
        }
    }

    /**
     * 同期処理発火対象のコマンドを取得する
     * @return コマンドセット
     */
    public static Map<String, String> getTriggerCommands() {
        return triggerCmds;
    }

    /**
     * 設定キーリストを取得する
     * @return コマンドセット
     */
    public static Set<String> getKeys() {
        return keys;
    }

    /**
     * 同期コマンドを取得する
     * @return コマンドリスト
     */
    public static List<String> getSyncCmds(String key) {
        return syncCmds.get(key);
    }

    /**
     * 同期コマンド実行時メッセージを取得する
     * @return コマンドリスト
     */
    public static List<String> getBeginExecMsgs(String key) {
        return beginExecMsgs.get(key);
    }

    /**
     * 同期コマンド実行終了時メッセージを取得する
     * @return コマンドリスト
     */
    public static List<String> getExecEndMsgs(String key) {
        return execEndMsgs.get(key);
    }
}
