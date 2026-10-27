# EEC — Personal Expense Tracker

Kotlin Multiplatform (JVM, Wasm, Android, iOS). M1 foundation with reports, category
management, CSV export, and an optional PIN lock.

## Run

Desktop:
```
./gradlew :composeApp:run
```

Wasm (dev server):
```
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```
Then open the printed URL in a browser.

Build distribution:
```
./gradlew :composeApp:wasmJsBrowserDistribution
```
Output: `composeApp/build/dist/wasmJs/productionExecutable/`. Serve with COOP/COEP headers for OPFS persistence.

Android (requires ANDROID_HOME):
```
./gradlew :composeApp:assembleDebug
```

iOS (requires macOS + Xcode):
```
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

## Navigation

Four top-level destinations (Dashboard, Transactions, Reports, Settings) sit in a typed
back stack in `composeApp/navigation`. Below 600dp they render in the bottom bar; at 600dp
and above they render in a navigation rail. Category management is a child screen pushed
from Settings, with a back arrow in the top bar. The app is fully RTL.

Navigation uses `androidx.navigation:navigation-compose` behind the small `EecNavigator`
wrapper; screens depend on `Screen`, never on `NavController`.

## PIN lock

Settings has a Lock section:

- Enable sets a PIN of 4 to 6 digits.
- Change replaces the PIN after verifying the current one.
- Disable removes the lock after verifying the current one.

Only `PinLock.hash(pin)` is stored, under the `app_lock` settings key. The lock is checked
on the next launch; the running session stays unlocked after enabling or changing the PIN.
When locked, the app shows a numeric PIN screen with an inline Arabic error and does not
render any other destination.

## Category colors

Categories store a color key, never a hex value, in the seeds. The editor also accepts raw
`#RRGGBB` values through the color picker; the palette resolves valid hex before token keys.
Text on a colored surface picks black or white from relative luminance so both themes stay
legible.

## Gates

Run the full set before reporting a change:

```
./gradlew :composeApp:compileKotlinJvm
./gradlew :core:jvmTest
./gradlew :composeApp:jvmTest
./gradlew :composeApp:compileKotlinWasmJs
./gradlew :composeApp:wasmJsBrowserDistribution
bash scripts/check-no-emoji.sh
git diff --check
```

Android and iOS gates run only on a host with the Android SDK or macOS plus Xcode; they are
reported as skipped otherwise.
