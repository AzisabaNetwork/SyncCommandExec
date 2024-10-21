package com.github.aburaagetarou.command;

import com.github.aburaagetarou.SyncCommandExec;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import com.github.aburaagetarou.CommandSyncManager;
import com.github.aburaagetarou.MessageUtils;
import com.github.aburaagetarou.config.SyncCommandExecConfig;
import com.github.aburaagetarou.config.SyncCommandTriggers;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;

/**
 * コマンド実行同期プラグイン 管理コマンド
 * @auther AburaAgeTarou
 */
@CommandAlias("synccommandexec|syncomma")
@Description("コマンド実行同期プラグイン 管理コマンド")
public class SyncCommandExecCommand extends ReloadableBaseCommand {

    /**
     * コンストラクタ
     */
    public SyncCommandExecCommand() {
        SyncCommandExec.addCommand(this);
    }

    @Dependency
    private Plugin plugin;

    @HelpCommand
    @CommandPermission("syncomma.help")
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("--reload")
    @CommandPermission("syncomma.reload")
    @Description("設定情報を再読み込みします")
    public void onReload(CommandSender sender) {

        // 再読み込み
        SyncCommandExecConfig.loadConfig();
        MessageUtils.sendColoredMessage(sender, "&aSyncCommandExecの設定情報を再読み込みしました。");
    }

    @Default
    @CommandPermission("syncomma.exec")
    @CommandCompletion("@syncommakey")
    @Description("設定されたコマンドを同期実行します")
    public void onExec(CommandSender sender, @Flags("syncommakey") String key) {
        if(SyncCommandTriggers.getSyncCmds(key) == null) {
            MessageUtils.sendColoredMessage(sender, "&c指定されたキーは設定されていません。");
            return;
        }
        CommandSyncManager.executeCommand(key);
        CommandSyncManager.addCommand(key);
    }
}
