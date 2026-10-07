# Changelog / История изменений

All notable changes to SkinTotem are documented here. The newest release is on top.
Каждая версия описана на английском и на русском. Новые версии добавляются сверху.

Format: [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) · Versioning: `MAJOR.MINOR.PATCH`

---

## [2.0.4] — 2026-10-07

### 🇬🇧 English

**Added**
- **Custom sounds folder** — `config/skintotem/sounds`. Drop a file named after a sound
  (`totem_activate`, `doll_summon`, `skin_change`, `skin_error`) and it replaces the built-in one.
  The folder is created on first launch together with a bilingual `README.txt`.
- **Automatic conversion** — `.ogg`, `.wav`, `.aiff` and `.au` are used as they are, with no
  resampling, no volume change and no quality loss. Any other format (`.mp3`, `.m4a`, `.flac`,
  `.opus`, ...) is converted automatically with ffmpeg when it is installed; the result is cached in
  `.converted`, so each file is converted only once.
- **Random variants** — several files for one sound (`skin_change_1.ogg`, `skin_change_2.ogg`, or a
  `skin_change/` subfolder) are picked at random on every play.
- **Live reload** — new files are picked up on resource reload (F3+T) or with `/skintotem sounds reload`.
- **New command** `/skintotem sounds [reload|list]` — shows the folder, loaded files, their format
  and length, and the reason when a file could not be read.
- **Two new config options**: *Sounds From Folder* and *Convert Any Format*.
- **New languages**: Polish (`pl_pl`) and German (`de_de`), fully translated.

**Changed**
- Ukrainian (`uk_ua`) and Japanese (`ja_jp`) are complete again — all sound, emote and skin-provider
  strings were missing since 2.0.3.
- Tatar (`tt_ru`) received the sound and emote strings; the rest of the file is still being filled in.

### 🇷🇺 Русский

**Добавлено**
- **Папка со своими звуками** — `config/skintotem/sounds`. Файл с именем звука
  (`totem_activate`, `doll_summon`, `skin_change`, `skin_error`) заменяет встроенный.
  Папка создаётся при первом запуске вместе с двуязычным `README.txt`.
- **Автоматическая конвертация** — `.ogg`, `.wav`, `.aiff` и `.au` берутся как есть: без
  передискретизации, без изменения громкости и без потери качества. Любой другой формат (`.mp3`,
  `.m4a`, `.flac`, `.opus`, ...) конвертируется автоматически через ffmpeg, если он установлен;
  результат кэшируется в `.converted`, поэтому каждый файл конвертируется один раз.
- **Случайные варианты** — несколько файлов на один звук (`skin_change_1.ogg`, `skin_change_2.ogg`
  или вложенная папка `skin_change/`) выбираются случайно при каждом воспроизведении.
- **Перезагрузка на лету** — новые файлы подхватываются при перезагрузке ресурсов (F3+T) или
  командой `/skintotem sounds reload`.
- **Новая команда** `/skintotem sounds [reload|list]` — показывает папку, загруженные файлы, их
  формат и длительность, а также причину, если файл прочитать не удалось.
- **Две новые настройки**: «Звуки из папки» и «Конвертировать любой формат».
- **Новые языки**: польский (`pl_pl`) и немецкий (`de_de`), переведены полностью.

**Изменено**
- Украинский (`uk_ua`) и японский (`ja_jp`) снова переведены полностью — с версии 2.0.3 в них не
  хватало строк про звуки, эмоции и источники скинов.
- Татарский (`tt_ru`) получил строки про звуки и эмоции; остальные строки добавляются постепенно.

---

## [2.0.3] — 2026-10-07

### 🇬🇧 English

**Added**
- **Own sounds** for four events: totem activation, a doll appearing in your hand, a skin loaded
  successfully and a skin failing to load. The sounds are client-side only and are never registered
  in the sound registry, so vanilla servers stay happy.
- **Emotecraft support** — the doll in your hand mirrors the emote you are playing. The pose is read
  from the live Player Animation Library state, so smoothing and transitions come for free.
- **Activation emote** — an emote of your choice can be played automatically when the totem saves you.
- **New config category “Sounds & Emotes”** with five options: enable sounds, volume, replace the
  vanilla totem sound, mirror emotes, activation emote.

**Fixed**
- The doll pose is always restored after rendering, even if the Emotecraft integration fails.

### 🇷🇺 Русский

**Добавлено**
- **Собственные звуки** для четырёх событий: срабатывание тотема, появление куклы в руке, успешная
  загрузка скина и ошибка загрузки. Звуки клиентские и не регистрируются в реестре звуков, поэтому
  ванильные серверы остаются довольны.
- **Поддержка Emotecraft** — кукла в руке повторяет вашу эмоцию. Поза берётся из живого состояния
  Player Animation Library, поэтому сглаживание и переходы работают сами собой.
- **Эмоция при срабатывании** — выбранная эмоция проигрывается автоматически, когда тотем спасает вас.
- **Новая категория настроек «Звуки и эмоции»** с пятью опциями: включение звуков, громкость, замена
  ванильного звука тотема, повтор эмоций, эмоция при срабатывании.

**Исправлено**
- Поза куклы теперь всегда снимается после отрисовки, даже если интеграция с Emotecraft сломалась.

---

## [2.0.2] — 2026-10-06

### 🇬🇧 English

**Added**
- **Minecraft 26.3 support**: the submit-based render architecture, the new access-widener format
  (`.classTweaker`), updated Fabric API, YACL, ModMenu and Sodium dependencies.
- **Totem glow is back** — the outline pass was lost in 26.2 and now works again through
  `submitCustomGeometry`.

**Changed**
- Mod metadata is generated from `gradle.properties`; the version is no longer hard-coded.
- `/skintotem info` and `/skintotem credits` read the version from the mod metadata.
- Documentation refreshed: badges, dependency list, jar name.

### 🇷🇺 Русский

**Добавлено**
- **Поддержка Minecraft 26.3**: новая submit-архитектура рендера, новый формат access-widener
  (`.classTweaker`), обновлённые зависимости Fabric API, YACL, ModMenu и Sodium.
- **Свечение тотема вернулось** — фаза обводки пропала в 26.2 и снова работает через
  `submitCustomGeometry`.

**Изменено**
- Метаданные мода собираются из `gradle.properties`, версия больше не зашита в код.
- `/skintotem info` и `/skintotem credits` берут версию из метаданных мода.
- Обновлена документация: бейджи, список зависимостей, имя jar-файла.

---

Earlier releases are not documented here.
Более ранние версии в этом файле не описаны.
