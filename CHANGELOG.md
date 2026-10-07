# Changelog

## 2.2 — Languages 3 & ffmpeg-free MP3

### English

**20 new languages (23 → 43)**

- Added: ไทย (`th_th`), Ελληνικά (`el_gr`), Suomi (`fi_fi`), Dansk (`da_dk`), Norsk bokmål (`nb_no`),
  Беларуская (`be_by`), Slovenčina (`sk_sk`), Български (`bg_bg`), Hrvatski (`hr_hr`), Slovenščina (`sl_si`),
  Lietuvių (`lt_lt`), Latviešu (`lv_lv`), Српски (`sr_sp`), Eesti (`et_ee`), Қазақша (`kk_kz`),
  Català (`ca_es`), العربية (`ar_sa`), עברית (`he_il`), فارسی (`fa_ir`), हिन्दी (`hi_in`).
- All 43 locales carry the full set of 163 strings in the canonical `en_us` order — no fallbacks to English,
  verified by `tools/check_lang.py` (keys, `%s` placeholders, `&`-colour codes, line breaks).
- Minecraft terminology is used in every language (Totem of Undying, anvil, cape, elytra), and the
  colour-code multiset is preserved even where the word order differs.

**MP3 now plays on its own — ffmpeg is not needed**

- Audio files are recognised **by their content**, not by their extension: OGG, WAV, AIFF/AIFC, AU, MP3
  (including files that start with an ID3 tag), FLAC, MP4/M4A and Matroska are detected by signature.
  A `song.mpv`, `sound.bin` or an extension-less file is now loaded correctly.
- The MP3 decoder (bundled JLayer, nothing to install) became fault tolerant: damaged frames are skipped
  (up to 128), frame buffers are always released, and an error is reported only if not a single frame decodes.
- Files with an unknown extension are still picked up if their content looks like audio.
- ffmpeg remains an optional last resort for exotic containers only; the error hint now says explicitly that
  OGG, MP3 and WAV need nothing installed.

### Русский

**20 новых языков (23 → 43)**

- Добавлены: ไทย (`th_th`), Ελληνικά (`el_gr`), Suomi (`fi_fi`), Dansk (`da_dk`), Norsk bokmål (`nb_no`),
  Беларуская (`be_by`), Slovenčina (`sk_sk`), Български (`bg_bg`), Hrvatski (`hr_hr`), Slovenščina (`sl_si`),
  Lietuvių (`lt_lt`), Latviešu (`lv_lv`), Српски (`sr_sp`), Eesti (`et_ee`), Қазақша (`kk_kz`),
  Català (`ca_es`), العربية (`ar_sa`), עברית (`he_il`), فارسی (`fa_ir`), हिन्दी (`hi_in`).
- Во всех 43 локалях присутствуют все 163 строки в каноническом порядке `en_us` — нигде нет отката на
  английский; проверено скриптом `tools/check_lang.py` (ключи, подстановки `%s`, цветовые коды `&`, переносы строк).
- Везде использована терминология Minecraft (тотем бессмертия, наковальня, плащ, элитры), а набор цветовых
  кодов сохранён даже там, где порядок слов в языке другой.

**MP3 теперь играет сам — ffmpeg не нужен**

- Звуковые файлы распознаются **по содержимому**, а не по расширению: OGG, WAV, AIFF/AIFC, AU, MP3
  (в том числе с ID3-тегом в начале), FLAC, MP4/M4A и Matroska определяются по сигнатуре.
  Файл `song.mpv`, `sound.bin` или вовсе без расширения теперь загружается правильно.
- Декодер MP3 (встроенный JLayer, ставить ничего не надо) стал устойчивым к повреждениям: битые кадры
  пропускаются (до 128), буферы кадров всегда освобождаются, а ошибка выдаётся только если не декодировался
  ни один кадр.
- Файлы с незнакомым расширением всё равно подхватываются, если их содержимое похоже на звук.
- ffmpeg остаётся необязательным запасным вариантом только для экзотических контейнеров; в подсказке об
  ошибке теперь прямо сказано, что OGG, MP3 и WAV работают без установки чего-либо.
