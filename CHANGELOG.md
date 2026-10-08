# Changelog

## 2.2.1 — NeoForge port

### English

**SkinTotem now runs on NeoForge**

- One source tree, two loaders: the mod is built for **Fabric** and **NeoForge** from the same code
  (Stonecutter multi-loader setup, `mod_loaders = fabric neoforge`), and CI builds both in parallel.
- NeoForge entry point `SkinTotemNeoForgeClient` (`@Mod(dist = CLIENT)`): picture-in-picture renderers,
  tooltip components, the resource reload listener, client commands and the client-stopping hook are all
  registered through the matching NeoForge events.
- The settings screen opens **straight from the NeoForge mod list** (`Mods → SkinTotem → Config`) — ModMenu
  is not needed there. The "YACL is missing / YACL is outdated" screens work on NeoForge too.
- Fabric API is no longer referenced by shared code. Version comparison, "is mod X loaded", the config
  directory and dev-environment detection now go through a thin loader facade (`SkinTotemLoader`) and a new
  `VersionUtils` helper, so the same classes compile for both loaders.
- Commands were rewritten on plain Brigadier with a generic command source: the identical command tree is
  registered by `ClientCommandRegistrationCallback` on Fabric and by `RegisterClientCommandsEvent` on
  NeoForge; chat replies go through one shared `CommandFeedback`.
- **MP3 still needs no ffmpeg on either loader** — the JLayer decoder is bundled with `jarJar` on NeoForge
  and with Fabric's `include` on Fabric.
- Loader metadata and transformers: `neoforge.mods.toml`, the access widener translated into an access
  transformer (`aws/neoforge-26.3.cfg`), and a NeoForge-specific mixin set.

**Known limits of the NeoForge build**

- The ModMenu integration and the Sodium-specific rendering patches stay **Fabric-only**; everything else
  (dolls, skins, tags, sounds, Emotecraft, YACL config) is identical on both loaders.

**2 new languages (63 → 65)**

- Bosanski (`bs_ba`) and Esperanto (`eo_uy`) — both complete: all 163 strings in the canonical `en_us`
  order, verified by `tools/check_lang.py` (keys, `%s` placeholders, `&`-colour codes, line breaks).

### Русский

**SkinTotem теперь работает на NeoForge**

- Один исходник, два загрузчика: мод собирается под **Fabric** и **NeoForge** из одного и того же кода
  (мультилоадерная схема Stonecutter, `mod_loaders = fabric neoforge`), CI собирает обе сборки параллельно.
- Точка входа NeoForge `SkinTotemNeoForgeClient` (`@Mod(dist = CLIENT)`): рендереры «картинка в картинке»,
  компоненты подсказок, слушатель перезагрузки ресурсов, клиентские команды и хук остановки клиента
  регистрируются через соответствующие события NeoForge.
- Экран настроек открывается **прямо из списка модов NeoForge** (`Моды → SkinTotem → Config`) — ModMenu там
  не нужен. Экраны «YACL не установлен» и «YACL устарел» на NeoForge тоже работают.
- Общий код больше не обращается к Fabric API. Сравнение версий, проверка «загружен ли мод X», папка
  конфигов и определение среды разработки идут через тонкий фасад загрузчика (`SkinTotemLoader`) и новый
  помощник `VersionUtils`, поэтому одни и те же классы компилируются под оба загрузчика.
- Команды переписаны на чистом Brigadier с обобщённым источником команды: одно и то же дерево команд
  регистрируется через `ClientCommandRegistrationCallback` на Fabric и через `RegisterClientCommandsEvent`
  на NeoForge; ответы в чат идут через общий `CommandFeedback`.
- **MP3 по-прежнему не требует ffmpeg ни на одном загрузчике** — декодер JLayer вшивается через `jarJar`
  на NeoForge и через `include` на Fabric.
- Метаданные и трансформеры загрузчика: `neoforge.mods.toml`, access widener переведён в access transformer
  (`aws/neoforge-26.3.cfg`), отдельный набор миксинов для NeoForge.

**Ограничения сборки под NeoForge**

- Интеграция с ModMenu и патчи совместимости с Sodium остаются **только на Fabric**; всё остальное (куклы,
  скины, теги, звуки, Emotecraft, настройки YACL) одинаково работает на обоих загрузчиках.

**2 новых языка (63 → 65)**

- Bosanski (`bs_ba`) и Esperanto (`eo_uy`) — оба полные: все 163 строки в каноническом порядке `en_us`,
  проверено скриптом `tools/check_lang.py` (ключи, подстановки `%s`, цветовые коды `&`, переносы строк).
