# Changelog / Что нового

This file always describes **the latest update only**. Older entries are replaced, not stacked —
the full history stays in the Git log and in the GitHub releases.

В этом файле всегда описано **только последнее обновление**. Старые записи заменяются, а не
накапливаются — полная история остаётся в истории Git и в релизах на GitHub.

---

## 2.0.5 — 2026-10-07

### 🇬🇧 English

**MP3 now works out of the box**
- A built-in MP3 decoder (JLayer) ships inside the mod jar, so `.mp3` files in
  `config/skintotem/sounds` just play — no ffmpeg, no codecs, nothing to install.
- Formats that need nothing at all: `.ogg`, `.mp3`, `.wav`, `.aiff`, `.au`.
- Rare formats (`.m4a`, `.flac`, `.opus`, ...) are still converted automatically with ffmpeg when
  it happens to be installed; the result is cached so each file is converted only once.
- The audio is never touched: no resampling, no volume changes, no muffling — only bit depth and
  channel count are adjusted when OpenAL cannot handle them.
- If an unusual `.mp3` cannot be decoded, the mod falls back to ffmpeg and, failing that, keeps the
  built-in sound and explains why in `/skintotem sounds list`.

**Languages**
- 🇨🇳 Chinese Simplified (`zh_cn`) added — fully translated.
- 🇷🇺 Tatar (`tt_ru`) finished: the remaining 89 strings were translated, so **all 8 languages are
  now complete** (English, Русский, Українська, Deutsch, Polski, 日本語, Татарча, 简体中文).

**Docs**
- This changelog now keeps only the newest release, in English and Russian.
- README: updated sound formats, language table and the JLayer credit (LGPL 2.1).

### 🇷🇺 Русский

**MP3 работает сразу**
- Встроенный MP3-декодер (JLayer) лежит внутри jar мода, поэтому `.mp3` из
  `config/skintotem/sounds` просто играет — ни ffmpeg, ни кодеков, ставить ничего не нужно.
- Форматы, которым вообще ничего не требуется: `.ogg`, `.mp3`, `.wav`, `.aiff`, `.au`.
- Редкие форматы (`.m4a`, `.flac`, `.opus`, ...) по-прежнему конвертируются автоматически через
  ffmpeg, если он случайно установлен; результат кэшируется, так что файл конвертируется один раз.
- Звук не трогается: без передискретизации, без изменения громкости, без приглушения — приводится
  только разрядность и число каналов, когда OpenAL с ними не работает.
- Если нестандартный `.mp3` раскодировать не удалось, мод пробует ffmpeg, а если и его нет —
  оставляет встроенный звук и объясняет причину в `/skintotem sounds list`.

**Языки**
- 🇨🇳 Добавлен китайский упрощённый (`zh_cn`), переведён полностью.
- 🇷🇺 Татарский (`tt_ru`) дополнен: переведены оставшиеся 89 строк, так что **все 8 языков теперь
  полные** (English, Русский, Українська, Deutsch, Polski, 日本語, Татарча, 简体中文).

**Документация**
- В этом файле теперь остаётся только последний релиз — на английском и русском.
- README: обновлены форматы звука, таблица языков и упоминание JLayer (LGPL 2.1).
