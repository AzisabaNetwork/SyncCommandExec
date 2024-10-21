package com.github.aburaagetarou.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.github.aburaagetarou.MessageUtils;
import com.github.aburaagetarou.config.SyncCommandExecConfig;
import com.github.aburaagetarou.config.SyncCommandTriggers;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * 実行サーバーでのみコマンドを一括実行する
 * @auther AburaAgeTarou
 */
@CommandAlias("batchcommandexec|batchcomma")
public class BatchCommandExec extends ReloadableBaseCommand {

	@Dependency
	private Plugin plugin;

	@Default
	@CommandPermission("batchcomma.exec")
	@CommandCompletion("@syncommakey")
	@Description("設定されたコマンドを同期実行します")
	public void onExec(CommandSender sender, @Flags("syncommakey") String key) {
		if(SyncCommandTriggers.getSyncCmds(key) == null) {
			MessageUtils.sendColoredMessage(sender, "&c指定されたキーは設定されていません。");
			return;
		}
		for(String msg : SyncCommandTriggers.getBeginExecMsgs(key)) {
			MessageUtils.broadcastColoredMessage(msg);
		}
		for(String cmd : SyncCommandTriggers.getSyncCmds(key)) {
			plugin.getServer().dispatchCommand(sender, cmd);
		}
		for(String msg : SyncCommandTriggers.getExecEndMsgs(key)) {
			MessageUtils.broadcastColoredMessage(msg);
		}
	}

}
