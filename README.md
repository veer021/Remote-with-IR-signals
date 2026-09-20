# 📺 Remote with IR Signals

An Android IR Remote Control application built with **Jetpack Compose** and **Material 3**. This app utilizes hardware-captured **38kHz Infrared (IR) Frequency Signals** to control Samsung TVs and compatible devices directly via built-in smartphone IR Blasters using Android's native `ConsumerIrManager` API.

---

## ✨ Features

- 📱 **Jetpack Compose & Material 3**: Fully declarative, dark-themed UI optimized for single-hand remote usage.
- 🎯 **34 Hardware-Captured IR Signal Frequency Arrays**:
  - **Power & System**: Power, Mute, Menu, Home, Source / Input, Settings, Aspect Ratio.
  - **Navigation**: Circular 5-key D-Pad (Up, Down, Left, Right, OK).
  - **Controls**: Pill-shaped Channel Up/Down and Volume Up/Down rockers.
  - **Modal Numpad Sheet**: Interactive number pad (`0`–`9`), `APP`, `PIC`, and quick shortcuts (`YouTube`, `Netflix`, `Hotstar`, `Sound Mode`).
- ⚡ **Exact Frequency Transmission**: Single-frame 38kHz NEC IR transmission for instant TV response.
- 📳 **Haptic Feedback**: Subtle vibration response on button press.
- 🔍 **Hardware Auto-Detection**: Built-in hardware check for IR Blaster support and dynamic carrier frequency matching.

---

## 🛠️ Hardware & System Requirements

- **Android Device**: Smartphone with a built-in Infrared (IR) Blaster (e.g., Xiaomi/Redmi/POCO, Huawei, LG G series, Samsung Galaxy S4/S5/Note 3/4).
- **Android OS**: Android 5.0 (API Level 21) or higher.
- **Permissions**: `TRANSMIT_IR` and `VIBRATE`.

---

## 📡 IR Signal Protocol Specifications

All IR frequency arrays are exact hardware-captured timings in microseconds for the **Samsung NEC 38kHz Protocol**:

| Button / Key | Array Constant | Sample Timing Sequence (us) |
| :--- | :--- | :--- |
| **Power** | `IRPatterns.TV_POWER` | `4550, 4400, 650, 1600, ...` |
| **Source / Input** | `IRPatterns.TV_SOURCE` | `4550, 4400, 650, 1600, ...` |
| **Home** | `IRPatterns.TV_HOME` | `4550, 4400, 650, 1600, ...` |
| **Exit / Return** | `IRPatterns.TV_EXIT` | `4550, 4350, 650, 1600, ...` |
| **Menu** | `IRPatterns.TV_MENU` | `4550, 4400, 650, 1600, ...` |
| **Ratio / Aspect** | `IRPatterns.TV_RATIO` | `4550, 4400, 650, 1600, ...` |
| **D-Pad Up/Down/Left/Right/OK** | `TV_UP`, `TV_DOWN`, `TV_LEFT`, `TV_RIGHT`, `TV_OK` | `4550, 4400, ...` |
| **Volume Up/Down/Mute** | `TV_VOL_PLUS`, `TV_VOL_MINUS`, `TV_MUTE` | `4600, 4350, ...` |
| **Channel Up/Down** | `TV_CH_PLUS`, `TV_CH_MINUS` | `4600, 4350, ...` |
| **Numpad Keys 0–9** | `TV_0` – `TV_9` | `4550, 4400, ...` |
| **Streaming Shortcuts** | `TV_YOUTUBE`, `TV_NETFLIX`, `TV_HOTSTAR`, `TV_APP` | `4550, 4400, ...` |

---

## 💻 Tech Stack

- **Language**: Kotlin 1.9+
- **UI Framework**: Jetpack Compose (Compose Compiler 1.5.8)
- **Design System**: Material 3
- **Android APIs**: `android.hardware.ConsumerIrManager`, `android.os.Vibrator`
- **Build System**: Gradle 8.2 (Kotlin DSL `build.gradle.kts`)

---

## 📂 Project Structure

```
Remote-with-IR-signals/
├── app/
│   ├── src/main/
│   │   ├── java/com/bit/v21remote/
│   │   │   ├── TVRemoteActivity.kt    # Main Compose Remote Activity & IRPatterns
│   │   │   ├── MainActivity.java      # Application Launch Activity
│   │   │   └── ui/theme/              # Compose Dark Theme Colors & Typography
│   │   ├── res/
│   │   │   ├── drawable/              # Custom Vector Icons & Remote Shapes
│   │   │   └── values/                # Strings, Styles & Color Resources
│   │   └── AndroidManifest.xml        # Declares TRANSMIT_IR & VIBRATE Permissions
├── build.gradle.kts                   # Root Build Configuration
├── settings.gradle.kts                # Project Settings
└── README.md                          # Project Documentation
```

---

## 🚀 Building & Running

1. **Clone Repository**:
   ```bash
   git clone https://github.com/veer021/Remote-with-IR-signals.git
   cd Remote-with-IR-signals
   ```

2. **Open in Android Studio**:
   - Open Android Studio.
   - Sync project with Gradle files.

3. **Build Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Deploy to Device**:
   - Deploy to an Android device with an IR Blaster hardware transmitter.

---

## 👨‍💻 Author

Developed with ❤️ by **Veer** ([@veer021](https://github.com/veer021)).
