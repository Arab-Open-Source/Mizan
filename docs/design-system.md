# Mizan Design System

## Principles

Quiet Precision: restrained surfaces, strong typographic hierarchy, purposeful spacing,
selective brand accent. Material 3 is used for semantics and controls, not as the
visual identity.

## Tokens

Primitive and semantic tokens live in `designsystem/src/commonMain/kotlin/com/eec/design/theme`.

- `EecColors`: semantic color roles for light and dark themes, including text, surfaces,
  borders, states, financial semantics, and chart series.
- `EecTypography`: semantic text styles with tabular numerals.
- `EecSpacing`: 4dp grid tokens (`space1`..`space16`) plus legacy aliases.
- `EecShapes`: corner radius tokens (`radiusSmall`..`radiusExtraLarge`).
- `EecMotion`: durations.
- `EecSizes`: shared dimensions and breakpoints.

## Themes

`EecTheme(darkTheme)` provides `LocalEecColors`, `LocalEecTypography`, and `LocalEecShapes`.
Use `EecTheme.colors`, `EecTheme.typography`, `EecTheme.shapes`, `EecTheme.spacing`.

## Adaptive layout

`com.eec.design.layout.windowSizeClass` maps widths to Compact/Medium/Expanded.
`EecSizes.railBreakpoint` drives rail vs bottom navigation in `App`.

## Components

Reusable components live in `com.eec.design.component`. Showcase: `DesignSystemShowcase`.

## Localization

All user-facing strings use Compose Multiplatform resources. Direction is per language.
Money formatting stays locale-invariant (Latin digits, EGP).

## Accessibility

48dp touch targets, semantic roles, focus indicators, contrast-checked pairs, no
color-only financial meaning.

## Testing

`./gradlew :composeApp:compileKotlinJvm :composeApp:compileKotlinWasmJs :core:jvmTest :composeApp:jvmTest`
`./gradlew :composeApp:wasmJsBrowserDistribution`
`bash scripts/check-no-emoji.sh`
`git diff --check`
EOF