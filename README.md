# NewGen Client

Fabric mod: HUD modullari (FPS, CPS, ping, koordinata, yo'nalish), ko'k "liquid glass" tema va offline Alt Manager.

## Tuzilma

| Papka | Minecraft | Java | Holat |
|---|---|---|---|
| `core/` | versiyadan mustaqil | 8+ | kompilyatsiya qilingan va sinalgan |
| `fabric-1.21/` | 1.21.4 | 21 | yozilgan, hali build qilinmagan |
| `fabric-26.3/` | 26.3 | 25 | yozilgan, hali build qilinmagan |

Har bir modul alohida Gradle loyiha (26.x boshqa Loom plaginini va Gradle 9.4 ni talab qiladi).

## 26.3 uchun avval qiling

`fabric-26.3/gradle.properties` ichida `fabric_version=FILL_ME` ni 26.3 uchun Fabric API versiyasi bilan almashtiring
(https://modrinth.com/mod/fabric-api/versions, filtr: Minecraft 26.3, Fabric). Aks holda build ataylab to'xtaydi.

## Build

GitHub Actions (tavsiya): loyihani yuklang → Actions → "Build mod" → har modul uchun artifact'dan jar'ni oling.

Lokal: Java 25 va Gradle 9.4+ bilan `cd fabric-26.3 && gradle build`.
Jar: `fabric-26.3/build/libs/newgen-client-fabric-26.3-0.1.0.jar`

## Mod ichidagi fayllar (26.3)

- `fabric.mod.json`, `newgen.mixins.json`
- `META-INF/MANIFEST.MF` (manba: `fabric-26.3/manifest/MANIFEST.MF`, build vaqtida jar'ga qo'shiladi)
- `assets/newgen/icon.png`, `assets/newgen/lang/en_us.json`, `uz_uz.json`

## Ishlatish

O'yin ichida **O'ng Shift**: Alt Manager. Sozlamalar: `.minecraft/config/newgen/`.

## Ma'lum xavf nuqtalari (26.3 qatlami)

API nomlari Fabric hujjatlaridan olingan (26.1.2 va 26.2), 26.3 da tekshirilmagan. Birinchi buildda xato chiqsa, eng ehtimolli joylar:
`User` konstruktori, `Minecraft.getFps()`, `getWindow().handle()`, `KeyMappingHelper` paketi, `gui.setScreen(...)`.
