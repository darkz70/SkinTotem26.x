# Changelog / Что нового

This file always describes **the latest update only**. Older entries are replaced, not stacked —
the full history stays in the Git log and in the GitHub releases.

В этом файле всегда описано **только последнее обновление**. Старые записи заменяются, а не
накапливаются — полная история остаётся в истории Git и в релизах на GitHub.

---

## 2.1.0 — 2026-10-07

### 🇬🇧 English

**Fifteen new languages — 23 in total**
- 🇪🇸 Español (`es_es`), 🇫🇷 Français (`fr_fr`), 🇮🇹 Italiano (`it_it`), 🇳🇱 Nederlands (`nl_nl`),
  🇧🇷 Português do Brasil (`pt_br`), 🇵🇹 Português de Portugal (`pt_pt`), 🇨🇿 Čeština (`cs_cz`),
  🇭🇺 Magyar (`hu_hu`), 🇷🇴 Română (`ro_ro`), 🇸🇪 Svenska (`sv_se`), 🇹🇷 Türkçe (`tr_tr`),
  🇮🇩 Bahasa Indonesia (`id_id`), 🇻🇳 Tiếng Việt (`vi_vn`), 🇰🇷 한국어 (`ko_kr`), 🇹🇼 繁體中文 (`zh_tw`).
- Every one of the 23 languages now contains all 163 strings, in the same order as `en_us`.
  Nothing falls back to English any more.

**Translation fixes in the existing languages**
- 🇯🇵 Japanese: the broken colour code `& l` showed up as literal text in "Unsupported Format!";
  the welcome screen and the custom-models tooltip had lost all of their line breaks and were
  rendered as one long line.
- 🇺🇦 Ukrainian: a broken `&з` code and a stray line break in the middle of the YACL message.
- 🇹🇹 Tatar: the whole file used raw `§` instead of `&`, so the mod could not recolour it.
- Three leftover keys that no longer exist in the game were removed.

**A lone `&` is no longer eaten**
- Mod text turns `&a`, `&l`, ... into Minecraft colour codes. Until now *every* `&` was converted,
  so an ordinary ampersand — "Sounds & Emotes" — became `§ ` and the font swallowed it together
  with the next character. Only real colour codes are converted now, and translators can write
  `&` as plain text.

**Checker for translators**
- `python3 tools/check_lang.py` compares every locale with `en_us` and reports missing or extra
  keys, broken colour codes, lost `%s` placeholders and lost line breaks. `--fix-order` rewrites
  the files in the canonical `en_us` order.

### 🇷🇺 Русский

**Пятнадцать новых языков — всего 23**
- 🇪🇸 испанский (`es_es`), 🇫🇷 французский (`fr_fr`), 🇮🇹 итальянский (`it_it`),
  🇳🇱 нидерландский (`nl_nl`), 🇧🇷 португальский Бразилии (`pt_br`), 🇵🇹 португальский
  Португалии (`pt_pt`), 🇨🇿 чешский (`cs_cz`), 🇭🇺 венгерский (`hu_hu`), 🇷🇴 румынский (`ro_ro`),
  🇸🇪 шведский (`sv_se`), 🇹🇷 турецкий (`tr_tr`), 🇮🇩 индонезийский (`id_id`),
  🇻🇳 вьетнамский (`vi_vn`), 🇰🇷 корейский (`ko_kr`), 🇹🇼 китайский традиционный (`zh_tw`).
- Во всех 23 языках есть все 163 строки в том же порядке, что и в `en_us`, — ничего больше
  не откатывается на английский.

**Исправления в уже существующих переводах**
- 🇯🇵 Японский: сломанный код `& l` выводился текстом в «Unsupported Format!», а у экрана
  приветствия и подсказки с моделями пропали переносы строк — всё шло одной длинной строкой.
- 🇺🇦 Украинский: сломанный код `&з` и лишний перенос строки посреди сообщения про YACL.
- 🇹🇹 Татарский: весь файл был написан через `§` вместо `&`, поэтому мод не мог его раскрасить.
- Удалены три старых ключа, которых уже нет в игре.

**Одиночный «&» больше не пропадает**
- Мод превращает `&a`, `&l`, ... в цветовые коды Minecraft. Раньше заменялся *любой* `&`, поэтому
  обычный амперсанд — «Sounds & Emotes» — превращался в `§ ` и шрифт съедал его вместе со
  следующим символом. Теперь заменяются только настоящие коды, а `&` можно писать как обычный текст.

**Проверка переводов**
- `python3 tools/check_lang.py` сравнивает каждую локаль с `en_us` и показывает пропущенные и
  лишние ключи, сломанные цветовые коды, потерянные `%s` и переносы строк. Ключ `--fix-order`
  пересохраняет файлы в каноническом порядке `en_us`.
