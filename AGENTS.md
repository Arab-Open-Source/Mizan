# AGENTS.md — Mizan

Guidance for AI coding agents working in this repository.

## Project

Mizan is a personal expense tracker built with Kotlin Multiplatform (Desktop JVM, Android, iOS, Wasm). Single user, local-only storage (Room 3), Arabic-first with 7 supported languages, RTL/LTR per language.

## Commands

```
./gradlew :composeApp:compileKotlinJvm
./gradlew :composeApp:compileKotlinWasmJs
./gradlew :composeApp:wasmJsBrowserDistribution
./gradlew :core:jvmTest
./gradlew :composeApp:jvmTest
./gradlew :androidApp:assembleDebug
bash scripts/check-no-emoji.sh
git diff --check
```

Android builds need ANDROID_HOME pointing at a local SDK with platform 37 and build tools installed.

Desktop run: `./gradlew :composeApp:run`
Wasm dev: `./gradlew :composeApp:wasmJsBrowserDevelopmentRun`

## Architecture

- Modules: `androidApp -> composeApp, core`; `composeApp -> core, designsystem`. `core` has no Compose. `designsystem` has no core.
- Android: `androidApp` is the only `com.android.application` module; the shared modules use `com.android.kotlin.multiplatform.library`. Android namespaces are `com.eec.core`, `com.eec.design`, `com.eec.app.shared`, and `com.eec.app.android`. compileSdk 37, minSdk 26.
- Android entry point: `androidApp` MainActivity creates the Room database, initializes `AndroidShareContext`, and calls the shared `App(database)`.
- Package: `com.eec.*`. Application id: `com.eec.app`.
- Money: Long minor units (piasters). No Double near money.
- Dates: kotlinx-datetime + Clock abstraction. No System.currentTimeMillis() outside container.
- Screen models: one immutable State + single onEvent. No business logic in composables.
- Repositories: Flow for reads, suspend for writes. Typed errors.
- Navigation: JetBrains Compose Navigation (NavHost). Screen sealed interface as routes.
- No navigation library was originally planned; migration recorded in DECISIONS.md.

## Conventions

- No emoji anywhere (code, strings, resources, comments, docs, commits).
- No explanatory comments. KDoc only where contract not obvious.
- Files under 400 lines. Composables under 80 lines.
- Trailing commas, 4-space indent, 120 column limit, sorted imports.
- No hardcoded colors/sizes/fonts in screens. Use designsystem tokens.
- Full RTL support: directional icons mirror; chart axes stay LTR.
- Money never color-only (sign + label).
- Every screen has loading/empty/error states.

## Testing

- Deterministic tests only. No sleeps, network, or real clock.
- Use FixedClock for date tests.
- In-memory BundledSQLiteDriver for DAO tests.

## Do not

- Edit BRIEF.md.
- Commit, amend, push, or rewrite history.
- Add dependencies (no chart lib, icon pack, DI, or nav lib beyond what exists).
- Use Dispatchers.IO in common code.
- Weaken tests to make gates pass.
