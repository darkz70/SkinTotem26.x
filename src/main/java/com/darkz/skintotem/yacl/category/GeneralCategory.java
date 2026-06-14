package com.darkz.skintotem.yacl.category;

import dev.isxander.yacl3.api.*;
import lombok.experimental.ExtensionMethod;
import com.darkz.skintotem.config.SkinTotemConfig;
import com.darkz.skintotem.extension.SimpleOptionExtension;
import com.darkz.skintotem.thread.SkinTotemTaskExecutor;
import com.darkz.skintotem.yacl.custom.simple.main.*;
import com.darkz.skintotem.yacl.custom.simple.utils.SimpleContent;

@ExtensionMethod(SimpleOptionExtension.class)
public class GeneralCategory {

	public static ConfigCategory get(SkinTotemConfig defConfig, SkinTotemConfig config) {
		return SimpleCategory.startBuilder("general")
				.groups(getMainGroup(defConfig, config))
				.groups(getThreadGroup(defConfig, config))
				.groups(getAutoRefreshGroup(defConfig, config))
				.build();
	}

	private static OptionGroup getMainGroup(SkinTotemConfig defConfig, SkinTotemConfig config) {
		return SimpleGroup.startBuilder("main")
				.options(
						SimpleOption.<Boolean>startBuilder("mod_enabled")
								.withBinding(defConfig.isModEnabled(), config::isModEnabled, config::setModEnabled, true)
								.withDescription(SimpleContent.NONE)
								.withController()
								.build(),
						SimpleOption.<Boolean>startBuilder("support_other_mods_totems")
								.withBinding(defConfig.isSupportOtherModsTotems(), config::isSupportOtherModsTotems, config::setSupportOtherModsTotems, true)
								.withDescription(SimpleContent.NONE)
								.withController()
								.build(),
						SimpleOption.<Boolean>startBuilder("debug_log_enabled")
								.withBinding(defConfig.isDebugLogEnabled(), config::isDebugLogEnabled, config::setDebugLogEnabled, true)
								.withDescription(SimpleContent.NONE)
								.withController()
								.build()
				)
				.build();
	}


	private static OptionGroup getAutoRefreshGroup(SkinTotemConfig defConfig, SkinTotemConfig config) {
		return SimpleGroup.startBuilder("auto_refresh")
				.options(
						SimpleOption.<Boolean>startBuilder("auto_refresh_enabled")
								.withBinding(defConfig.isAutoRefreshEnabled(), config::isAutoRefreshEnabled, (v) -> {
									config.setAutoRefreshEnabled(v);
									com.darkz.skintotem.refresh.SkinAutoRefresher.restart();
								}, true)
								.withDescription(SimpleContent.NONE)
								.withController()
								.build(),
						SimpleOption.<Integer>startBuilder("auto_refresh_interval_minutes")
								.withBinding(defConfig.getAutoRefreshIntervalMinutes(), config::getAutoRefreshIntervalMinutes, (v) -> {
									config.setAutoRefreshIntervalMinutes(v);
									com.darkz.skintotem.refresh.SkinAutoRefresher.restart();
								}, false)
								.withDescription(SimpleContent.NONE)
								.withController(1, 60, 1)
								.build()
				)
				.build();
	}

	public static OptionGroup getThreadGroup(SkinTotemConfig defConfig, SkinTotemConfig config) {
		return SimpleGroup.startBuilder("parallel_tasks")
				.options(
						SimpleOption.<Integer>startBuilder("parallel_tasks_count")
								.withBinding(defConfig.getParallelTasksCount(), config::getParallelTasksCount, (i) -> {
									config.setParallelTasksCount(i);
									SkinTotemTaskExecutor.reload();
								}, false)
								.withDescription(SimpleContent.NONE)
								.withController(1, 50, 1)
								.build()
				)
				.build();
	}

}
