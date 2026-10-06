# Git и семантическое версионирование (SemVer)

Задание на отработку процесса разработки в git/GitHub. Само приложение вторично: это небольшой скрипт `hello_world.py` на Python (`argparse`), а цель — показать **как версия растёт вместе с изменениями**.

## Что отрабатывается
- отдельные ветки под каждое изменение (`feature/…` и `fix/…`);
- pull request'ы в `main`;
- правила семантического версионирования `MAJOR.MINOR.PATCH`: новая функциональность — MINOR, исправление ошибки — PATCH, несовместимое изменение — MAJOR;
- теги `vX.Y.Z` и GitHub Releases с записями об изменениях (`Added`, `Fixed`, `Docs`).

## Репозиторий задания
Работа лежит в отдельном репозитории и подключена сюда как подмодуль: [`SemVer/`](SemVer) → <https://github.com/Diminikhui/SemVer>.
Вся история, pull request'ы и релизы находятся там: <https://github.com/Diminikhui/SemVer/releases>.

## Релизы

| Версия | Дата | Что изменилось | Как получена |
|---|---|---|---|
| `v1.0.0` | 16.12.2025 | Hello World + README с инструкцией запуска | первый релиз |
| `v1.1.0` | 17.12.2025 | аргумент `--name` | PR #1, ветка `feature/name-arg` (MINOR) |
| `v1.1.1` | 17.12.2025 | исправлена обработка пустого и пробельного `--name` | PR #2, ветка `fix/empty-name` (PATCH) |
| `v1.2.0` | 17.12.2025 | параметр `--times` для повторения приветствия | PR #3, ветка `feature/times` (MINOR) |

## Как получить код
Подмодуль подтягивается отдельно:

```bash
git clone --recurse-submodules https://github.com/Diminikhui/StudyHub.git
# или, если репозиторий уже склонирован:
git submodule update --init assignments/Git/SemVer
```

Запуск: `python assignments/Git/SemVer/hello_world.py --name Dima --times 3`.

> Подмодуль закреплён на коммите `9262880` (состояние после релиза `v1.2.0`). Если `SemVer` изменится, ссылку нужно обновить: `git submodule update --remote assignments/Git/SemVer` и закоммитить.
