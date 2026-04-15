## 🚀 Overview

**Staff Detector** is a high-performance, intelligent utility for Minecraft players who value their privacy. It acts as a **Global Vanish Tracker**, detecting in real-time when staff members enter vanish mode or observe you in spectator mode—works regardless of distance!

The mod monitors the player list (TAB) and alerts you instantly if someone hides without actually disconnecting from the server.

---

## ✨ Key Features

### 🕵️ Global Vanish Tracker

Detects instantly when a staff member disappears from the TAB list. This is the strongest signal and triggers an immediate high-priority alert.

### 🔍 Advanced Rank Detection

Don't be fooled by custom fonts or fancy formatting. StaffDetector uses **Unicode Normalization** and **Scoreboard Team scanning** to detect keywords like `Admin`, `Mod`, `Staff`, even when written with:

- Special characters (e.g., `ᴍᴏᴅ`)
- Complex gradients
- Decorative symbols (e.g., `«Staff»`)

### 🧠 Proximity Heuristics

Logs anomalies around you that give away invisible players:

- **Suspicious Sounds:** Detects footstep sounds or block interactions where no visible player exists.
- **Ghost Particles:** Tracks sprint or hit particles coming from invisible entities.
- **Block Updates:** Alerts you if someone opens a chest or interacts with a block near you without being present.

### 👁️ Spectator Alert (Anti-Watch)

An intelligent system that analyzes network packet priority. If your entity starts receiving unusually frequent updates from the server, the mod warns you that a **Spectator** is likely focused on you, tracking your every move.

### 👻 Invisible Entity Detection

Scans nearby entity metadata to find players who have the `Invisibility` flag active but are still being sent to your client by the server.

### 🎨 Native UI & HUD

Keep track of detected staff at all times with a sleek HUD overlay. Configure the mod through an intuitive screen using Minecraft's native textures to toggle each detection method.

---

## 🛡️ Staff Watch Alert (Sensors & Triggers)

When the mod detects multiple suspicious signals, the cumulative scoring system triggers the **"STAFF IS WATCHING YOU"** critical alert.

| Sensor                     | Description                                                          |
| :------------------------- | :------------------------------------------------------------------- |
| **Vanish Event**           | Immediate signal when a staff member disappears from TAB.            |
| **Entity Packet Spike**    | Detects prioritization of network data for your own entity.          |
| **Invisible Entity Flags** | Tracks players with the "Invisible" metadata bit active.             |
| **Ghost Particles**        | Detects crit particles or smoke effects near you.                    |
| **Suspicious Sounds**      | Logs footstep sounds or chest openings at your location.             |
| **Chunk Re-sends**         | Detects block data re-sends while stationary (spectator cam signal). |

---

## 🔧 Requirements

- **Fabric Loader** ≥ 0.16.10
- **Fabric API**
- **Java 21**

---

## 🎮 How to Use

1.  **Install** the mod and required dependencies.
2.  **Open Config:** Use the default keybind (configurable in the Controls menu).
3.  **Keywords:** Add specific ranks used by your server (e.g., `Admin`, `Support`, `Vip+`).
4.  **Modules:** Enable your preferred detection modules. _Recommendation: Keep Vanish Tracker and Spectator Watch always on._
5.  **Stay Alert:** Watch the HUD and chat for red-text alerts.

---

## ⚠️ Disclaimer (BETA)

> [!WARNING]
> This mod is currently in **BETA**. It may contain technical bugs or unexpected crashes.

- **False Positives:** Since the mod uses heuristics (data-based assumptions), you might receive false alerts in areas with heavy redstone, complex ambient sound effects, or laggy server conditions.
- **Compliance:** This mod is strictly client-side. The use of staff detection tools may be prohibited on certain servers.
- **Responsibility:** Use at your own risk. I am not responsible for bans or sanctions applied by server administrators.

---

## 🐛 Bug Reports & Suggestions

If you find a bug or have a suggestion to improve detection, send a DM on Discord:
**Discord:** `marcellesa`
