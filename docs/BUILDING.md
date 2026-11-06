# Building SkinTotem / Сборка SkinTotem

## English

### Requirements

| | |
|---|---|
| JDK | **25** (Minecraft 26.3 requires it; JDK 21 will not work) |
| OS | any — Linux/macOS/Windows |
| Internet | the first build downloads Minecraft, NeoForge and the Gradle plugins |
| RAM | `gradle.properties` asks for `-Xmx24G`; on a smaller machine lower it, e.g. `org.gradle.jvmargs=-Xmx4G` |

### One command per loader

```bash
./tools/clean_build.sh fabric      # Windows: tools\clean_build.bat fabric
./tools/clean_build.sh neoforge    # Windows: tools\clean_build.bat neoforge
```

The scripts stop the Gradle daemon, remove the Fabric Loom caches and then run the same command CI
uses. Without the scripts:

```bash
./gradlew "buildAndCollect+fabric+All"   -Pci_loader=fabric
./gradlew "buildAndCollect+neoforge+All" -Pci_loader=neoforge
```

> Always pass `-Pci_loader=<loader>`. Without it Gradle configures **both** loaders in one build —
> that is slower and multiplies the chance of hitting the Loom cache bug described below.

### Where the jars end up

```
libs/SkinTotem-2.2.1+26.3+fabric.jar
libs/SkinTotem-2.2.1+26.3+neoforge.jar
versions/<loader>-26.3/build/libs/SkinTotem-2.2.1+26.3+<loader>.jar   (same file)
```

Only these files go into `mods/`. Everything else produced by the build — `minecraft-patched-*.jar`,
`jlayer-*.jar`, `*-sources.jar`, the Gradle wrapper jar — is **not** a mod and must not be installed.
That matters when you download the CI artifact: it is a zip of *every* jar in the project tree.

### Troubleshooting

**`[Fabric Loom Message] Incompatible Gradle cache. Can't delete it, requires manual deleting.`**
followed by `Configuration 'minecraft' has no dependencies` or
`Task with name 'createMinecraftArtifacts' not found`.

Loom is pulled in as a `1.17-SNAPSHOT`, so different builds can see different snapshots and the cache
written by one of them is rejected by another. Clean it:

```bash
./gradlew --stop
rm -rf ~/.gradle/caches/fabric-loom .gradle/loom-cache build/loom-cache versions/*/build/loom-cache
```

If it still fails, wipe the whole Gradle cache (the next build re-downloads everything):

```bash
./gradlew --stop && rm -rf ~/.gradle/caches .gradle
```

**`Plugin [id: 'net.lopymine.mossy-plugin-settings', version: '3.30.0'] was not found`** — you are
building an old branch. The multi-loader build lives on the 2.2.1 branch and uses Mossy `4.10.0`.

**`error: invalid source release: 25` / `Unsupported class file major version`** — wrong JDK. Check
with `./gradlew -version` and point `JAVA_HOME` at a JDK 25.

**Could not GET `https://maven.kikugie.dev/...`** — that third-party mirror hosts the Stonecutter and
Fletching Table plugins and goes down from time to time. Just rerun the build; there is no mirror.

---

## Русский

### Что нужно

| | |
|---|---|
| JDK | **25** (этого требует Minecraft 26.3; на JDK 21 сборка не пойдёт) |
| ОС | любая — Linux/macOS/Windows |
| Интернет | первая сборка качает Minecraft, NeoForge и плагины Gradle |
| ОЗУ | в `gradle.properties` стоит `-Xmx24G`; на слабой машине уменьшите, например `org.gradle.jvmargs=-Xmx4G` |

### Одна команда на загрузчик

```bash
./tools/clean_build.sh fabric      # Windows: tools\clean_build.bat fabric
./tools/clean_build.sh neoforge    # Windows: tools\clean_build.bat neoforge
```

Скрипты останавливают демон Gradle, чистят кэши Fabric Loom и запускают ту же команду, что и CI.
Без скриптов:

```bash
./gradlew "buildAndCollect+fabric+All"   -Pci_loader=fabric
./gradlew "buildAndCollect+neoforge+All" -Pci_loader=neoforge
```

> `-Pci_loader=<loader>` указывать обязательно. Без него Gradle настраивает **оба** загрузчика в одной
> сборке: это дольше и чаще натыкается на баг кэша Loom, описанный ниже.

### Куда кладутся jar-файлы

```
libs/SkinTotem-2.2.1+26.3+fabric.jar
libs/SkinTotem-2.2.1+26.3+neoforge.jar
versions/<loader>-26.3/build/libs/SkinTotem-2.2.1+26.3+<loader>.jar   (тот же файл)
```

В папку `mods/` кладут **только** эти файлы. Всё остальное, что появляется при сборке —
`minecraft-patched-*.jar`, `jlayer-*.jar`, `*-sources.jar`, jar враппера Gradle — не моды,
устанавливать их нельзя. Это важно при скачивании артефакта CI: там zip со **всеми** jar'ами проекта.

### Если сборка не удалась

**`[Fabric Loom Message] Incompatible Gradle cache. Can't delete it, requires manual deleting.`**,
а дальше `Configuration 'minecraft' has no dependencies` или
`Task with name 'createMinecraftArtifacts' not found`.

Loom подключается как `1.17-SNAPSHOT`, поэтому разные сборки могут видеть разные снапшоты, и кэш,
записанный одним из них, другой считает несовместимым. Лечится очисткой:

```bash
./gradlew --stop
rm -rf ~/.gradle/caches/fabric-loom .gradle/loom-cache build/loom-cache versions/*/build/loom-cache
```

Если не помогло — снести весь кэш Gradle (следующая сборка скачает всё заново):

```bash
./gradlew --stop && rm -rf ~/.gradle/caches .gradle
```

**`Plugin [id: 'net.lopymine.mossy-plugin-settings', version: '3.30.0'] was not found`** — вы собираете
старую ветку. Мультилоадерная сборка живёт в ветке 2.2.1 и использует Mossy `4.10.0`.

**`error: invalid source release: 25` / `Unsupported class file major version`** — не та JDK. Проверьте
`./gradlew -version` и укажите в `JAVA_HOME` путь к JDK 25.

**Could not GET `https://maven.kikugie.dev/...`** — этот сторонний репозиторий раздаёт плагины
Stonecutter и Fletching Table и периодически лежит. Просто повторите сборку, зеркал у него нет.
