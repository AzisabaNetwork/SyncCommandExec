package com.github.aburaagetarou.config;

import java.io.File;

import com.github.aburaagetarou.SyncCommandExec;

/**
 * config.ymlの設定内容を管理するクラス
 * @auther AburaAgeTarou
 */
public class SyncCommandExecConfig {

    // 設定バージョン
    private static String version = "";

    // 読み込み対象のファイルパス
    private static String syncDataDir = null;

    /**
     * 設定読み込み
     */
    public static void loadConfig() {
        SyncCommandExec.getInstance().saveDefaultConfig();
        version = SyncCommandExec.getInstance().getConfig().getString("version");
        syncDataDir = SyncCommandExec.getInstance().getConfig().getString("sync-data-dir");

        // 同期コマンド設定ファイルの読み込み
        File file = new File(syncDataDir, "sync-commands.yml");
        if(file.exists()) {
            SyncCommandTriggers.loadConfig(file);
        }
        else {
            SyncCommandExec.getInstance().getLogger().info("同期コマンドの設定ファイルが存在しません。");
        }
    }
    
    /**
     * 設定バージョンを取得する
     * @return 設定バージョン
     */
    public static String getVersion() {
        return version;
    }

    /**
     * 読み込み対象のファイルパスを取得する
     * @return ファイルパス文字列
     */
    public static String getSyncDataDir() {
        return syncDataDir;
    }
}
