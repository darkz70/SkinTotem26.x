# Changelog

## 2.2 — Languages 3 & ffmpeg-free MP3

### English

**40 new languages (23 → 63)**

- First batch: ไทย (`th_th`), Ελληνικά (`el_gr`), Suomi (`fi_fi`), Dansk (`da_dk`), Norsk bokmål (`nb_no`),
  Беларуская (`be_by`), Slovenčina (`sk_sk`), Български (`bg_bg`), Hrvatski (`hr_hr`), Slovenščina (`sl_si`),
  Lietuvių (`lt_lt`), Latviešu (`lv_lv`), Српски (`sr_sp`), Eesti (`et_ee`), Қазақша (`kk_kz`),
  Català (`ca_es`), العربية (`ar_sa`), עברית (`he_il`), فارسی (`fa_ir`), हिन्दी (`hi_in`).
- Second batch: Shqip (`sq_al`), Македонски (`mk_mk`), Azərbaycan (`az_az`), Հայերեն (`hy_am`),
  ქართული (`ka_ge`), Íslenska (`is_is`), Gaeilge (`ga_ie`), Galego (`gl_es`), Euskara (`eu_es`),
  Bahasa Melayu (`ms_my`), Filipino (`fil_ph`), தமிழ் (`ta_in`), ಕನ್ನಡ (`kn_in`), Монгол (`mn_mn`),
  Afrikaans (`af_za`), Cymraeg (`cy_gb`), Norsk nynorsk (`nn_no`), Srpski — latinica (`sr_cs`),
  English — United Kingdom (`en_gb`), Español — México (`es_mx`).
- All 63 locales carry the full set of 163 strings in the canonical `en_us` order — no fallbacks to English,
  verified by `tools/check_lang.py` (keys, `%s` placeholders, `&`-colour codes, line breaks).
- Minecraft terminology is used in every language (Totem of Undying, anvil, cape, elytra), and the
  colour-code multiset is preserved even where the word order differs.
- Regional variants are real variants, not copies: `en_gb` uses British phrasing, `es_mx` uses Mexican
  wording (`mouse`, `presionar`, `ayuda emergente`), and `sr_cs` is a proper Latin transliteration of `sr_sp`.
- The checker now skips the "untranslated" rule for English variants, so `en_gb` can legitimately share
  wording with `en_us`.

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

**40 новых языков (23 → 63)**

- Первая партия: ไทย (`th_th`), Ελληνικά (`el_gr`), Suomi (`fi_fi`), Dansk (`da_dk`), Norsk bokmål (`nb_no`),
  Беларуская (`be_by`), Slovenčina (`sk_sk`), Български (`bg_bg`), Hrvatski (`hr_hr`), Slovenščina (`sl_si`),
  Lietuvių (`lt_lt`), Latviešu (`lv_lv`), Српски (`sr_sp`), Eesti (`et_ee`), Қазақша (`kk_kz`),
  Català (`ca_es`), العربية (`ar_sa`), עברית (`he_il`), فارسی (`fa_ir`), हिन्दी (`hi_in`).
- Вторая партия: Shqip (`sq_al`), Македонски (`mk_mk`), Azərbaycan (`az_az`), Հայերեն (`hy_am`),
  ქართული (`ka_ge`), Íslenska (`is_is`), Gaeilge (`ga_ie`), Galego (`gl_es`), Euskara (`eu_es`),
  Bahasa Melayu (`ms_my`), Filipino (`fil_ph`), தமிழ் (`ta_in`), ಕನ್ನಡ (`kn_in`), Монгол (`mn_mn`),
  Afrikaans (`af_za`), Cymraeg (`cy_gb`), Norsk nynorsk (`nn_no`), Srpski — латиница (`sr_cs`),
  English — Великобритания (`en_gb`), Español — México (`es_mx`).
- Во всех 63 локалях присутствуют все 163 строки в каноническом порядке `en_us` — нигде нет отката на
  английский; проверено скриптом `tools/check_lang.py` (ключи, подстановки `%s`, цветовые коды `&`, переносы строк).
- Везде использована терминология Minecraft (тотем бессмертия, наковальня, плащ, элитры), а набор цветовых
  кодов сохранён даже там, где порядок слов в языке другой.
- Региональные варианты сделаны по-настоящему, а не скопированы: `en_gb` — британские формулировки,
  `es_mx` — мексиканская лексика (`mouse`, `presionar`, `ayuda emergente`), `sr_cs` — корректная
  транслитерация `sr_sp` в латиницу.
- Проверочный скрипт больше не требует перевода для вариантов английского, поэтому `en_gb` законно
  совпадает с `en_us` в части строк.

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
