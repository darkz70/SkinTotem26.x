#!/usr/bin/env python3
"""
Проверка файлов локализации SkinTotem / SkinTotem translation checker.

Сравнивает каждый <locale>.json с en_us.json и сообщает о:
  * пропущенных и лишних ключах;
  * разном количестве подстановок %s;
  * разном количестве переносов строк \\n;
  * различающемся наборе цветовых кодов (&a, &l, ...);
  * символах § (в переводах нужен &: мод сам заменяет его на §);
  * непереведённых строках (значение совпадает с английским).

Compares every <locale>.json with en_us.json and reports missing/extra keys,
mismatched %s placeholders, mismatched line breaks, a different set of colour
codes, raw § characters and untranslated values.

Запуск / usage:
    python3 tools/check_lang.py            # отчёт / report only
    python3 tools/check_lang.py --fix-order  # пересохранить в порядке en_us
"""

from __future__ import annotations

import collections
import json
import pathlib
import re
import sys

LANG_DIR = pathlib.Path(__file__).resolve().parent.parent / "src/main/resources/assets/skintotem/lang"
SOURCE = "en_us"
COLOR_CODE = re.compile(r"&[0-9A-Fa-fK-Ok-oRr]")
# строки, которые нормально оставить как в английском (имена собственные и т.п.)
ALLOWED_SAME = re.compile(r"^[&§%\w\s.:,!?\"'/\[\]()+-]{0,16}$")


def load(path: pathlib.Path) -> collections.OrderedDict:
    with path.open(encoding="utf-8") as handle:
        return json.load(handle, object_pairs_hook=collections.OrderedDict)


def dump(path: pathlib.Path, data: collections.OrderedDict) -> None:
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def main() -> int:
    fix_order = "--fix-order" in sys.argv
    source = load(LANG_DIR / f"{SOURCE}.json")
    print(f"{SOURCE}: {len(source)} keys")

    problems = 0
    for path in sorted(LANG_DIR.glob("*.json")):
        locale = path.stem
        if locale == SOURCE:
            continue

        data = load(path)
        missing = [key for key in source if key not in data]
        extra = [key for key in data if key not in source]
        shared = [key for key in source if key in data]

        placeholders = [k for k in shared if data[k].count("%s") != source[k].count("%s")]
        newlines = [k for k in shared if data[k].count("\n") != source[k].count("\n")]
        colors = [k for k in shared if sorted(COLOR_CODE.findall(data[k])) != sorted(COLOR_CODE.findall(source[k]))]
        sections = [k for k, v in data.items() if "§" in v]
        untranslated = [k for k in shared if data[k] == source[k] and source[k] and not ALLOWED_SAME.match(source[k])]
        order_ok = list(data.keys()) == [k for k in source if k in data]

        issues = {
            "missing": missing, "extra": extra, "%s": placeholders,
            "\\n": newlines, "colors": colors, "§": sections, "untranslated": untranslated,
        }
        broken = {name: keys for name, keys in issues.items() if keys}
        problems += sum(len(keys) for keys in broken.values())

        status = "OK" if not broken else "FAIL"
        print(f"{locale:8} {len(data):4} keys  order={'en_us' if order_ok else 'custom'}  {status}")
        for name, keys in broken.items():
            print(f"    {name}: {len(keys)} -> {', '.join(keys[:4])}{' ...' if len(keys) > 4 else ''}")

        if fix_order and not missing and not extra:
            dump(path, collections.OrderedDict((key, data[key]) for key in source))

    print("\nall locales are consistent" if not problems else f"\n{problems} problem(s) found")
    return 0 if not problems else 1


if __name__ == "__main__":
    raise SystemExit(main())
