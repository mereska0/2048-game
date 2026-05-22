# 2048 Glass

Нативная Android-игра `2048` на `Kotlin + Jetpack Compose`.

## Что внутри

- поле `4x4`
- локальный `best score`
- свайпы и управление с клавиатуры в эмуляторе
- анимации движения и merge
- современный светлый UI

## Запуск в Android Studio

1. Открой проект: `/Users/alexandrbyrgazov/extra/game`
2. Дождись `Gradle Sync`
3. Запусти эмулятор или подключи Android-устройство
4. Нажми `Run app`

## Сборка из терминала

```bash
cd /Users/alexandrbyrgazov/extra/game
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
GRADLE_USER_HOME=/Users/alexandrbyrgazov/androidstudio \
./gradlew assembleDebug
```

## Unit-тесты

```bash
cd /Users/alexandrbyrgazov/extra/game
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
GRADLE_USER_HOME=/Users/alexandrbyrgazov/androidstudio \
./gradlew testDebugUnitTest
```

## Готовый APK

После сборки debug APK лежит здесь:

`/Users/alexandrbyrgazov/extra/game/app/build/outputs/apk/debug/app-debug.apk`
# 2048-game
