# Mizan

Mizan is a personal expense tracker for one user, built with Kotlin Multiplatform and
Compose Multiplatform. It records income and expenses, reports where the money went, and
shares the result without an account, a server, or a network connection.

Data lives in a local Room 3 database. The interface is offline-first, targets seven
languages, and keeps every amount in EGP minor units.

## Overview

The app is organized around four top-level destinations: Dashboard, Transactions, Reports,
and Settings. Category management is a child screen of Settings. Amounts are entered
manually, stored as integer piasters, and formatted with Latin digits so figures read the
same in every language.

The visual language is a printed ledger rather than a banking dashboard: warm paper
surfaces, deep teal and vibrant accents, generous spacing, strong numerals, and no
decorative illustration. Light and dark themes are both supported. The interface follows
the system theme unless the user overrides it in Settings.

## Features

- Transactions: create, edit, delete, search, and filter by date range, kind, category,
  payment method, and note. List entries are grouped by day with a daily expense total.
- Dashboard: month switcher, expense, income, and net summary cards, daily spending chart,
  top categories, and recent transactions.
- Reports: monthly totals with a comparison against the previous month, average daily
  spend, category donut and ranked breakdown, share text, and CSV export.
- Categories: stable seeded categories plus user-created categories, archive instead of
  delete, and a color per category.
- Settings: theme, language, PIN lock, category management, and export of all
  transactions.
- Storage: Room 3 with schema version 1. Reads are exposed as flows; writes are suspend
  functions with typed results.
- Sharing: OS share sheet where available, with copy, WhatsApp, Telegram, and email
  fallbacks. CSV is UTF-8 with a BOM so spreadsheet software reads Arabic correctly.
- Accessibility and layout: RTL or LTR layout per language, minimum 48dp touch targets,
  content descriptions on icon-only controls, and empty, loading, and error states on
  data screens.

## Requirements

- JDK 17 or newer. The Gradle toolchain targets JVM 17; the project is built and tested
  with JDK 24.
- The Gradle wrapper. Use `./gradlew`; a global Gradle installation is not required.
- A Chromium-based browser for the Wasm target.
- Android builds require an Android SDK and `ANDROID_HOME` once the Android target is
  added.
- iOS builds require macOS with Xcode once the iOS target is added.
- No network access is required at runtime.

## Running EEC

Run all commands from the repository root.

### Desktop

```
./gradlew :composeApp:run
```

The first launch creates `eec.db` in the process working directory and seeds the default
categories. Data survives restarts.

### Web (Kotlin/Wasm)

Development server:

```
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

Open the URL printed by Gradle. The development server is intended for iteration; verify
persistence under a server that sends the cross-origin isolation headers described below.

Production distribution:

```
./gradlew :composeApp:wasmJsBrowserDistribution
```

Output directory:

```
composeApp/build/dist/wasmJs/productionExecutable/
```

Serve that directory as a static site.

#### Cross-origin isolation

Wasm persistence uses SQLite over OPFS in a Web Worker, which requires
`SharedArrayBuffer`. The server must send both headers on the page and on every asset:

```
Cross-Origin-Opener-Policy: same-origin
Cross-Origin-Embedder-Policy: require-corp
```

After loading the page, confirm that `crossOriginIsolated` is `true` in the browser
console. Without both headers, the database worker cannot use OPFS and changes do not
survive a reload.

### Android

Android is not part of the build produced by this repository state. The Gradle modules
declare JVM and Wasm targets only, and the build host has no Android SDK. The Android gate
below is therefore skipped, not passed. Adding the Android target and `androidMain`
source set is required before the app can be assembled or installed:

```
./gradlew :composeApp:assembleDebug
```

### iOS

iOS is likewise not part of the current build. It requires macOS and Xcode, neither of
which is available on the build host used here, so the iOS gate is skipped, not passed.
The command that will apply once the target and framework configuration exist is:

```
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

### Platform status

Desktop and Wasm are exercised by the gates in this repository. Android and iOS are
specified as product targets but are not compiled on this host; they must be reported as
skipped with the reason above.

## Localization

Localization uses seven resource sets:

| Language | Resource directory | Direction |
| --- | --- | --- |
| Arabic (default) | `values/` | RTL |
| English | `values-en/` | LTR |
| French | `values-fr/` | LTR |
| Italian | `values-it/` | LTR |
| German | `values-de/` | LTR |
| Dari | `values-fa/` | RTL |
| Pashto | `values-ps/` | RTL |

Arabic is the default and the fallback. `values/strings.xml` contains the Arabic strings,
so any key missing from another locale falls back to Arabic rather than to English.

The language switcher is in Settings. The selection is persisted in the `settings` table
under the `language` key and restored at startup. Changing the language immediately
changes the strings and the layout direction of the running session.

Direction is a property of the selected language, not a global constant: Arabic, Dari, and
Pashto render RTL; the other four render LTR. The app root provides
`LocalLayoutDirection` from the selected language, the Wasm target updates the document
`lang` and `dir` attributes, and the desktop target updates the JVM default locale.
Month names, weekdays, payment method labels, transaction kind labels, and the report
share text are resolved through Compose Multiplatform resources. The core share formatter
receives its strings from composeApp, so core stays free of UI dependencies while the
share text follows the selected language.

Localization is still being completed. A string that has not yet moved into a resource
file remains hardcoded in Arabic and does not change with the switcher. Treat
untranslated copy as a known gap, not as a language fallback.

Money formatting is deliberately locale-invariant: amounts use Latin digits, comma
thousands separators, and a dot before two decimal places in every language. Currency
display remains EGP.

## Project layout

```
EEC/
  BRIEF.md
  DECISIONS.md
  README.md
  build.gradle.kts
  settings.gradle.kts
  gradle/libs.versions.toml
  composeApp/
    src/commonMain/kotlin/com/eec/app/     screens, screen models, navigation, container
    src/commonMain/composeResources/       strings for the default locale and six translations
    src/jvmMain/kotlin/com/eec/app/        desktop entry point and locale actual
    src/jvmTest/kotlin/                    app-level JVM tests
    src/wasmJsMain/kotlin/com/eec/app/     Wasm entry point and locale actual
    src/wasmJsMain/resources/              SQLite worker and WebAssembly assets
  core/
    src/commonMain/kotlin/com/eec/core/    database, models, repositories, reports, format,
                                           sharing, security, time
    src/jvmMain/kotlin/com/eec/core/       JVM database and share implementations
    src/jvmTest/kotlin/                    core tests
    src/wasmJsMain/kotlin/com/eec/core/    Wasm database and share implementations
    schemas/                               exported Room schema
  designsystem/
    src/commonMain/kotlin/com/eec/design/  theme tokens, components, and icons
    src/commonMain/composeResources/       bundled IBM Plex Sans Arabic fonts and license
  scripts/check-no-emoji.sh
```

Dependency direction is one way: `composeApp` depends on `core` and `designsystem`;
`core` and `designsystem` do not depend on the app. `core` does not import Compose, and
`designsystem` does not import `core`.

## Gates

Run the full set before reporting a change. The JVM test gate is split between the two
modules because the repository has no root test module.

```
./gradlew :composeApp:compileKotlinJvm
./gradlew :core:jvmTest
./gradlew :composeApp:jvmTest
./gradlew :composeApp:compileKotlinWasmJs
./gradlew :composeApp:wasmJsBrowserDistribution
bash scripts/check-no-emoji.sh
git diff --check
```

Android gate, only when `ANDROID_HOME` is set:

```
./gradlew :composeApp:assembleDebug
```

iOS gate, only on macOS with Xcode:

```
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

Android and iOS are reported as skipped with the reason, never as passing.

`check-no-emoji.sh` scans source, resource, documentation, and script files. Do not add
emoji to code, strings, comments, commit messages, or these documents.

## PIN lock

The PIN lock is optional and configured in Settings. A PIN has four to six digits.

- Enable requires a new PIN and confirmation.
- Change requires the current PIN, a new PIN, and confirmation.
- Disable requires the current PIN.

Only an iterated, salted SHA-256 hash is stored, under the `app_lock` settings key. The
presence of that key means the lock is enabled; no plaintext PIN is stored and no database
schema changes are involved. The lock is checked at launch. Enabling or changing a PIN
does not lock the running session, because that session already verified the PIN; the new
state takes effect on the next launch.

While locked, the app shows only the PIN screen. An incorrect PIN produces an inline
error and clears the field for the next attempt. The lock gates the user interface; it
does not encrypt the database file.

## Category colors and the color picker

Seeded categories store stable palette keys such as `teal`, `gold`, or `slate`. Theme
changes therefore recolor them consistently in both light and dark themes.

The category editor also accepts a custom hex color in `#RRGGBB` form. The value is stored
in `color_key` alongside token keys. Resolution order is explicit: a valid hex value wins;
otherwise the value is treated as a palette key. Text and icons drawn on a category color
choose black or white from the surface's relative luminance, so custom colors remain
legible instead of inheriting a theme token that may not match. Custom colors are fixed
values and do not adapt when the theme changes; that is the intended trade-off.
