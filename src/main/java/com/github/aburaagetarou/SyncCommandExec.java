package com.github.aburaagetarou;

import java.util.HashSet;
import java.util.Set;

import com.github.aburaagetarou.command.BatchCommandExec;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.aburaagetarou.command.SyncCommandExecCommand;
import com.github.aburaagetarou.config.SyncCommandExecConfig;
import com.github.aburaagetarou.config.SyncCommandTriggers;

import co.aikar.commands.MessageKeys;
import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;

/**
 * SyncCommandExec メインクラス
 * @auther AburaAgeTarou
 */
public class SyncCommandExec extends JavaPlugin {

    // インスタンス
    private static SyncCommandExec instance;

    // コマンドAPI
    private static PaperCommandManager manager;

    /**
     * プラグイン有効化
     */
    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("SyncCommandExecを有効化します...");

        // 設定ファイル読み込み
        SyncCommandExecConfig.loadConfig();

        // 監視の開始
        CommandSyncManager.start();

        // コマンドAPIの初期化
        manager = new PaperCommandManager(this);

        // brigadierを有効化しろと言われたので有効化
        manager.enableUnstableAPI("brigadier");

        // helpを有効化
        manager.enableUnstableAPI("help");

        // コマンド登録
        manager.registerCommand(new SyncCommandExecCommand().setExceptionHandler((command, registeredCommand, sender, args, t) -> {
            sender.sendMessage(MessageType.ERROR, MessageKeys.ERROR_GENERIC_LOGGED);
            return true;
        }));

        // コマンド登録
        manager.registerCommand(new BatchCommandExec().setExceptionHandler((command, registeredCommand, sender, args, t) -> {
            sender.sendMessage(MessageType.ERROR, MessageKeys.ERROR_GENERIC_LOGGED);
            return true;
        }));

        // タブ補完登録
        manager.getCommandCompletions().registerCompletion("syncommakey", cmd -> {
            Set<String> ret = new HashSet<>();
            if(SyncCommandTriggers.getKeys() != null) ret.addAll(SyncCommandTriggers.getKeys());
            ret.add("--reload");
            return ret;
        });

        getLogger().info("SyncCommandExecが有効化されました。");
    }

    /**
     * プラグイン無効化
     */
    @Override
    public void onDisable() {
        getLogger().info("SyncCommandExecを無効化します...");

        // 監視の停止
        CommandSyncManager.stop();

        getLogger().info("SyncCommandExecが無効化されました。");
    }

    /**
     * このプラグインのインスタンスを取得
     * @return JavaPluginインスタンス
     */
    public static SyncCommandExec getInstance() {
        return instance;
    }

    /**
     * コマンドAPIを取得する
     */
    public static PaperCommandManager getCommandManager() {
        return manager;
    }
}
