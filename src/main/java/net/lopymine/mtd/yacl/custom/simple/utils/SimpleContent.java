package net.lopymine.mtd.yacl.custom.simple.utils;

import lombok.Getter;

@Getter
public enum SimpleContent {
	NONE("none"),
	IMAGE("png"),
	WEBP("webp"),
	GIF("gif");

	private final String fileExtension;

	SimpleContent(String fileExtension) {
		this.fileExtension = fileExtension;
	}
}
