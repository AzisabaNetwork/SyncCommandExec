package com.github.aburaagetarou;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

import org.bukkit.Bukkit;

import com.github.aburaagetarou.config.SyncCommandExecConfig;
import com.github.aburaagetarou.config.SyncCommandTriggers;

/**
 * 連携元/先サーバーで行われた同期コマンド実行を監視するクラス
 * @author AburaAgeTarou
 */
public class CommandSyncManager {

    public final static String CHECK_FILENAME = ".reload-automation";

    // 前回使用した再読み込み通知ファイルのタイムスタンプ
    public static long lastFileCreate = 0L;
    public static long lastFileUpdate = 0L;
    private static long lineCount = 0L;

    // 監視スレッド
    private static Thread thread;

    // 監視タスク
    private static ObserveTask observeTask = new ObserveTask();

    /**
     * 監視の開始
     */
    public static void start() {

        // タスクが実行中の場合は停止
        if (thread != null && thread.isAlive()) {
            observeTask.running = false;
            thread.interrupt();
        }

        // 他サーバーからの再読み込み通知を受け取る
        observeTask = new ObserveTask();
        thread = new Thread(observeTask);
        thread.start();
    }

    /**
     * 監視の停止
     */
    public static void stop() {

        // タスクが実行中の場合は停止
        if (thread != null) {
            observeTask.running = false;
            thread.interrupt();
        }
    }

    /**
     * 同期コマンドを追加
     */
    public static boolean addCommand(String fullCommand) {

        // 連携用ファイルの保存ディレクトリにファイルを作成
        File file = new File(SyncCommandExecConfig.getSyncDataDir(), CommandSyncManager.CHECK_FILENAME);

        try {
            // 実行したコマンドを追記
            if(!file.exists()) file.createNewFile();
            FileWriter filewriter = new FileWriter(file, true);
            String command = fullCommand.substring(fullCommand.indexOf("/") + 1);
            filewriter.write(command + System.lineSeparator());
            filewriter.close();

            // 自動更新ファイルのタイムスタンプを更新
            BasicFileAttributes atr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            CommandSyncManager.lastFileCreate = atr.creationTime().toMillis();
            CommandSyncManager.lastFileUpdate = file.lastModified();

            // 60秒後にファイルを削除
            Bukkit.getScheduler().runTaskLaterAsynchronously(SyncCommandExec.getInstance(), 
            () -> { if(CommandSyncManager.lastFileUpdate == file.lastModified()) file.delete(); }, 60L*20L);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 監視タスク
     */
    private static class ObserveTask implements Runnable {
    
        // 監視タスクの実行フラグ
        protected volatile boolean running = true;

        /**
         * タスク開始
         */
        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(1000L);
                    checkCommand();
                } catch (InterruptedException e) {
                    SyncCommandExec.getInstance().getLogger().info("監視タスクを停止します。");
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public synchronized void checkCommand() {
            try {
                // 連携用ファイルの保存ディレクトリにファイルが存在する場合
                File file = new File(SyncCommandExecConfig.getSyncDataDir(), CHECK_FILENAME);
                if (file.exists()) {

                    // 作成日時を取得
                    BasicFileAttributes atr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    long fileCreate = atr.creationTime().toMillis();

                    // 作成日時が異なる場合、1行目から
                    if(fileCreate != lastFileCreate) {
                        CommandSyncManager.lineCount = 0L;
                    }

                    // 前回使用した再読み込み通知ファイルのタイムスタンプと異なる場合
                    if (lastFileUpdate != file.lastModified()) {

                        // 未実行のコマンドを取得
                        FileReader fr = new FileReader(file);
                        BufferedReader br = new BufferedReader(fr);
                        long count = 0L;
                        while (true) {
                            final String cmd = br.readLine();
                            if(cmd == null) break;
                            if(count++ < CommandSyncManager.lineCount) continue;
                            CommandSyncManager.lineCount++;
                            Bukkit.getScheduler().runTask(SyncCommandExec.getInstance(), () -> {
                                if(SyncCommandTriggers.getTriggerCommands().keySet().contains(cmd)) {
                                    String key = SyncCommandTriggers.getTriggerCommands().get(cmd);
                                    for(String msg : SyncCommandTriggers.getBeginExecMsgs(key)) {
                                        MessageUtils.broadcastColoredMessage(msg);
                                    }
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                                    for(String msg : SyncCommandTriggers.getExecEndMsgs(key)) {
                                        MessageUtils.broadcastColoredMessage(msg);
                                    }
                                }
                            });
                        }

                        // リソース解放
                        br.close();

                        // 再読み込み通知ファイルのタイムスタンプを更新
                        lastFileCreate = fileCreate;
                        lastFileUpdate = file.lastModified();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
