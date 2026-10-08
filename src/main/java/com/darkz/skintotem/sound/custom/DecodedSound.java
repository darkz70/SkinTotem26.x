package com.darkz.skintotem.sound.custom;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Locale;
import javax.sound.sampled.AudioFormat;

/**
 * Раскодированный пользовательский звук: сырые PCM-данные ровно в том виде, в каком их ждёт OpenAL.
 * <p>
 * Данные хранятся отдельно от {@code SoundBuffer}, потому что Minecraft обнуляет буфер сразу после
 * загрузки в OpenAL, а при перезагрузке звукового движка буфер нужно создать заново.
 *
 * @param file   файл, из которого звук прочитан
 * @param data   прямой (direct) буфер с PCM-данными
 * @param format формат PCM: частота, каналы, разрядность
 */
public record DecodedSound(Path file, ByteBuffer data, AudioFormat format) {

	public int sizeBytes() {
		return this.data.limit();
	}

	/** Длительность в секундах; {@code 0}, если формат не позволяет её вычислить. */
	public float seconds() {
		int frameSize = this.format.getFrameSize();
		float frameRate = this.format.getFrameRate();
		if (frameSize <= 0 || frameRate <= 0.0F) {
			return 0.0F;
		}
		return this.sizeBytes() / (float) frameSize / frameRate;
	}

	/** Короткое описание для логов и команды {@code /skintotem sounds list}. */
	public String describe() {
		String channels = switch (this.format.getChannels()) {
			case 1 -> "mono";
			case 2 -> "stereo";
			default -> this.format.getChannels() + "ch";
		};
		return String.format(Locale.ROOT, "%.0f Hz, %s, %d-bit, %.2fs, %d KiB",
				this.format.getSampleRate(),
				channels,
				this.format.getSampleSizeInBits(),
				this.seconds(),
				this.sizeBytes() / 1024);
	}
}
