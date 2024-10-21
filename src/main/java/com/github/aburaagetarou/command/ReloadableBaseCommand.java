package com.github.aburaagetarou.command;

import co.aikar.commands.BaseCommand;
import com.github.aburaagetarou.SyncCommandExec;

/**
 * Plugmanによるリロードに対応したコマンドの元クラス
 * @author AburaAgeTarou
 */
public class ReloadableBaseCommand extends BaseCommand {

	/**
	 * コンストラクタ
	 */
	public ReloadableBaseCommand() {
		SyncCommandExec.addCommand(this);
	}
}
