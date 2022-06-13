# ezLib Changelog

This document will explain how ezLib evolves version to version, starting at 0.1.0 (from QuartzLib 0.0.4), and how to migrate your code if there are breaking changes.

This project will adhere to semver, but its version `0.0.1` is `0.1.0` to convey a forked status.

Changes marked with :warning: are **breaking changes**.

## ezLib 0.1.0

_Published on June 12th, 2022_

### Changed

#### Renamed packages and classes

:warning: QuartzLib is now ezLib, so some stuff got changed...

- The base package `fr.zcraft.zlib` is now `dev.dejay.ezlib`.
- The `QuartzLib` class is now `EZLib`.
- The `QuartzComponent` class is now `EZLibComponent`.
- The `QuartzPlugin` class is now `EZPlugin`.

Just rename these references, the interfaces have remained the same.

### Removed

References to the original project, except for their license <3