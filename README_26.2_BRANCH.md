# Ветка SkinTotem — только Minecraft 26.2 (Fabric)

Эта ветка собирает ТОЛЬКО fabric-26.2 (versions/fabric-26.1*, 26.1.1, 26.1.2
удалены из versions/, чтобы CI здесь не зависел от остальных версий и не
блокировал релизы в основной ветке).

## Известная проблема (открыта)
Сборка падает на компиляции — класс `MultiBufferSource` не найден.
Проверено и НЕ сработало:
- `com.mojang.blaze3d.vertex.MultiBufferSource` (старый пакет, работал в 26.1.x)
- `net.minecraft.client.renderer.MultiBufferSource` (предполагаемый новый пакет
  по аналогии с 1.21.6–1.21.9 — НЕ существует в 26.2)

Похоже, в 26.2 класс полностью убран/переименован, а не просто переехал —
возможно в рамках перехода рендер-пайплайна на новый backend (см. анонсы
Fabric про 26.2 и OpenGL/Vulkan).

## Файлы, использующие MultiBufferSource / OutlineBufferSource
(сейчас на старом рабочем для 26.1.x импорте `com.mojang.blaze3d.vertex.*`,
для 26.2 потребуется правка):

- src/main/java/com/darkz/skintotem/doll/renderer/SkinTotemRenderer.java
- src/main/java/com/darkz/skintotem/doll/renderer/special/ItemGuiElementRenderer.java
- src/main/java/com/darkz/skintotem/doll/renderer/special/SkinTotemGuiElementRenderer.java
- src/main/java/com/darkz/skintotem/doll/data/SkinTotemData.java
- src/main/java/com/darkz/skintotem/doll/model/SkinTotemModel.java (Drawer.draw)
- src/main/java/com/darkz/skintotem/model/base/MModel.java — КЛЮЧЕВАЯ точка:
  `provider.getBuffer(atlasRenderLayer)` → VertexConsumer для кастомной
  геометрии модели тотема. Именно это нужно понять, как делать в 26.2.
- src/main/java/com/darkz/skintotem/optimization/SkinTotemRenderRequest.java
- src/main/java/com/darkz/skintotem/optimization/SkinTotemRenderRequestsCollector.java
- src/main/java/com/darkz/skintotem/mixin/ItemStackRenderStateMixin.java
- src/main/java/com/darkz/skintotem/mixin/GuiRendererMixin.java

## Что нужно для продолжения
Точные сигнатуры новых классов рендеринга в 26.2. Варианты получить:
1. `./gradlew genSources` локально (нужна сеть/Loom может скачать mappings)
2. mappings.dev / Linkie для версии 26.2
3. Найти любой опубликованный мод, уже портированный на 26.2, посмотреть
   как там заменили MultiBufferSource

После получения сигнатур — правки точечно по списку файлов выше.

## Как влить готовый фикс обратно в основную ветку
1. Скопировать исправленные файлы из src/ этой ветки в основной репозиторий
2. Скопировать versions/fabric-26.2/ обратно в основной repo/versions/
3. Обновить versions/active.txt по необходимости
4. Прогнать полный CI build на всех версиях
