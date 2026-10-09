<div align="center">

# Panorama ScreenMake

**Press one key. Get a full cube panorama of your world.**

[![Build](https://github.com/Egor201-alt/PanoramaScreenMake/actions/workflows/build.yml/badge.svg)](https://github.com/Egor201-alt/PanoramaScreenMake/actions/workflows/build.yml)
![Fabric](https://img.shields.io/badge/loader-Fabric-dbd0b4?logo=data:image/svg%2bxml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIxNiIgaGVpZ2h0PSIxNiIgdmlld0JveD0iMCAwIDE2IDE2Ij48cGF0aCBmaWxsPSIjMzgzNDJhIiBkPSJNOSAxaDF2MWgxdjFoMXYxaDF2MWgxdjFoMXYyaC0xdjFoLTJ2MWgtMXYxaC0xdjFIOXYySDh2MUg2di0xSDV2LTFINHYtMUgzdi0xSDJWOWgxVjhoMVY3aDFWNmgxVjVoMVY0aDFWMmgxeiIvPjxwYXRoIGZpbGw9IiNkYmQwYjQiIGQ9Ik00IDlWOGgxVjdoMVY2aDFsMS0xVjRoMVYyaDF2MWgxdjFoMXYxaDF2MWwtMSAxLTIgMy0zIDMtMy0zeiIvPjxwYXRoIGZpbGw9IiNiY2IyOWMiIGQ9Ik05IDNoMXYxaDF2MWgxdjFoMXYxaC0xTDkgNHpNMTAgMTBoMVY5aDFWN2gtMXYxaC0xekg4djJoMXYtMWgxek04IDEySDd2MWgxeiIvPjxwYXRoIGZpbGw9IiNjNmJjYTUiIGQ9Ik03IDVoMXYyaDN2MUg5VjZIN3pNNiA4aDF2MmgyVjlINnoiLz48cGF0aCBmaWxsPSIjYWVhNjk0IiBkPSJNMyA5djFsMyAzaDF2LTFINnYtMUg1di0xSDRWOXoiLz48cGF0aCBmaWxsPSIjOWE5MjdlIiBkPSJNMyAxMHYxaDJ2MmgydjFINnYtMkg0di0yeiIvPjxwYXRoIGZpbGw9IiM4MDdhNmQiIGQ9Ik0xMyA3aDF2MWgtMXoiLz48cGF0aCBmaWxsPSIjYWVhNjk0IiBkPSJNOSA0djFoMnYyaDFWNmgtMlY0eiIvPjwvc3ZnPg==)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11%20%7C%2026.1%20%7C%2026.2%20%7C%2026.3-62b47a)
![Side](https://img.shields.io/badge/side-client-blue)
[![License](https://img.shields.io/badge/license-LGPL--3.0--or--later-orange)](LICENSE)
[![Stars](https://img.shields.io/github/stars/Egor201-alt/PanoramaScreenMake?style=flat&color=yellow)](https://github.com/Egor201-alt/PanoramaScreenMake/stargazers)

</div>

---

## What it does

Vanilla Minecraft ships with a panorama screenshot routine that Mojang never wired to a key. This mod hooks it up. Stand anywhere, press **F4**, and the client renders the six cube faces used by the title screen background.

Good for:

- custom main menu backgrounds
- 360 previews of builds and servers
- consistent screenshots for modpack pages

<!--
<p align="center">
  <img src="docs/images/menu.png" width="720" alt="Custom title screen made with Panorama ScreenMake">
</p>
-->

## Install

1. Install [Fabric Loader](https://fabricmc.net/use/) for your Minecraft version.
2. Drop the matching [Fabric API](https://modrinth.com/mod/fabric-api) into your `mods` folder.
3. Drop the Panorama ScreenMake jar for your version into the same folder.

Jars for every version are on the [Releases](https://github.com/Egor201-alt/PanoramaScreenMake/releases) page, and each CI run uploads them as artifacts.

## Usage

| Step | Action |
| --- | --- |
| 1 | Load a world and stand where you want the camera |
| 2 | Press **F4** (rebindable in Controls) |
| 3 | Wait until the chat message appears |
| 4 | Open the `panoramas` folder inside your game directory |

> [!TIP]
> To use the result as a title screen background, put the six images in a resource pack at `assets/minecraft/textures/gui/title/background/` and name them `panorama_0.png` to `panorama_5.png`.

> [!NOTE]
> The game freezes for a moment while the faces render. This is normal.

## Supported versions

| Minecraft | Java | Config screen |
| :---: | :---: | :---: |
| 1.21.11 | 21 | Yes (Mod Menu + YACL) |
| 26.1 | 25 | No |
| 26.2 | 25 | No |
| 26.3 | 25 | No |

## Compatibility

- **Distant Horizons**: the capture waits a few extra render passes after resizing the framebuffer so distant terrain is loaded before each face is taken.
- **Sodium** (26.2 and newer): a small workaround avoids a submit fence error that can happen during capture.

<details>
<summary><b>Building from source</b></summary>

<br>

Every version is a standalone Gradle project in `versions/`, so pick a folder and build it with the JDK it needs.

```bash
cd versions/26.3
./gradlew build
```

The jar lands in `build/libs/`.

```text
.
├── common/resources/     shared icon and lang files
├── versions/
│   ├── 1.21.11/
│   ├── 26.1/
│   ├── 26.2/
│   └── 26.3/
└── .github/workflows/    matrix build for all versions
```

</details>

<details>
<summary><b>Adding a new Minecraft version</b></summary>

<br>

1. Copy the closest folder in `versions/` to `versions/<new version>`.
2. Update `gradle.properties` and the `minecraft` range in `fabric.mod.json`.
3. Fix whatever the compiler complains about.
4. Add the version to the matrix in `.github/workflows/build.yml`.

</details>

## Contributing

Bug reports and pull requests are welcome. If a capture looks wrong, open an [issue](https://github.com/Egor201-alt/PanoramaScreenMake/issues) with your Minecraft version, other rendering mods, and `latest.log`.

## License

Released under the [LGPL-3.0-or-later](LICENSE).