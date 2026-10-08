#!/usr/bin/env bash
# Чистая сборка SkinTotem / Clean build of SkinTotem
#
#   ./tools/clean_build.sh            -> собирает Fabric
#   ./tools/clean_build.sh neoforge   -> собирает NeoForge
#
# Скрипт останавливает демон Gradle и удаляет кэши Fabric Loom. Именно их loom иногда
# не может пересоздать сам и падает с сообщением:
#   [Fabric Loom Message] Incompatible Gradle cache. Can't delete it, requires manual deleting.
#
# The script stops the Gradle daemon and deletes the Fabric Loom caches: loom sometimes fails
# to recreate them on its own and aborts the configuration phase with the message above.

set -u

LOADER="${1:-fabric}"
cd "$(dirname "$0")/.."

if [ "$LOADER" != "fabric" ] && [ "$LOADER" != "neoforge" ]; then
	echo "Usage: $0 [fabric|neoforge]" >&2
	exit 2
fi

echo "==> Останавливаю демон Gradle / Stopping the Gradle daemon"
./gradlew --stop >/dev/null 2>&1 || true

echo "==> Чищу кэши Fabric Loom / Removing Fabric Loom caches"
rm -rf "$HOME/.gradle/caches/fabric-loom" \
       .gradle/loom-cache \
       build/loom-cache \
       versions/*/build/loom-cache 2>/dev/null || true

echo "==> Собираю $LOADER / Building $LOADER"
./gradlew "buildAndCollect+${LOADER}+All" -Pci_loader="${LOADER}" "${@:2}"
STATUS=$?

if [ $STATUS -ne 0 ]; then
	cat >&2 <<'EOF'

Сборка упала. Если в логе есть "Incompatible Gradle cache" — удалите весь кэш Gradle и повторите:
    ./gradlew --stop && rm -rf ~/.gradle/caches .gradle && ./tools/clean_build.sh LOADER

The build failed. If the log contains "Incompatible Gradle cache", wipe the whole Gradle cache and retry:
    ./gradlew --stop && rm -rf ~/.gradle/caches .gradle && ./tools/clean_build.sh LOADER
EOF
	exit $STATUS
fi

echo
echo "==> Готово / Done. Jars:"
find libs versions/*/build/libs -name "SkinTotem-*.jar" ! -name "*-sources.jar" 2>/dev/null | sort -u
