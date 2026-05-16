# ⏱️ Pomodoro - Focus & Task Management

A robust, native Android application designed to boost productivity using the Pomodoro Technique. Effortlessly create custom tasks, manage work-break cycles with a persistent background timer, and stay dialed-in with seamless visual and audio alerts.

<div align="center">
    <img src="https://img.shields.io/badge/Platform-Android%20%28API%2026%2B%29-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android Badge">
    <img src="https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java Badge">
    <img src="https://img.shields.io/badge/License-Open%20Source-blue?style=for-the-badge" alt="License Badge">
</div>

---

## 📱 Visual Preview

| Task Dashboard | Active Focus Timer | Custom Task Creation |
| :-: | :-: | :-: |
| *[Add Screenshot 1]* | *[Add Screenshot 2]* | *[Add Screenshot 3]* |

---

## 🚀 Key Features

### 📝 Custom Task Management
* **Flexible Creation:** Set specific task names and detailed contextual descriptions.
* **Granular Control:** Adjust work durations (**1-60 minutes**) using intuitive `NumberPickers`.
* **Cycle Settings:** Configure exact break segments tailored to each specific task.
* **Persistent Storage:** Your task list is safely saved locally via `SharedPreferences`.

### ⚡ Advanced Timer Engine
* **Persistent Foreground Service:** The timer remains active even when the app is minimized or the screen is turned off.
* **Automatic Phase Switching:** Seamlessly transitions between **Work (Çalışma)** and **Break (Mola)** phases.
* **Smart Notifications:** Pause, resume, or track your timer status directly from the Android notification shade.

### 🎨 Interactive Timer UI
* **Circular Progress Visualization:** A beautiful, real-time progress bar perfectly synced with your countdown.
* **Dynamic Controls:** Instantly Start, Pause, Skip Phase, or Finish the entire task early via a secure confirmation dialog.

### ⚙️ Personalized Settings
* **Audio Alerts:** Toggle sound triggers for phase completions.
* **Custom Sounds:** Support for custom notification audio (drop your `timer_sound.raw` into `res/raw`).
* **Haptic Feedback:** Tactile vibration support for physical alerts.

---

## 🛠️ Technical Architecture

| Component | Technology / Implementation |
| :--- | :--- |
| **Language** | Java |
| **UI Architecture** | XML Layouts with `ConstraintLayout` & `Material Components` |
| **Persistence** | `SharedPreferences` (User settings & task serialization) |
| **Background Execution**| `TimerService` (`specialUse` foreground service type optimized for background limits) |
| **IPC / Communication**| `BroadcastReceiver` & `ServiceConnection` for real-time UI/Service binding |

---

## 📂 Project Structure

```text
📂 app/src/main/java/[your/package/name]/
├── 📄 MainActivity     # Task dashboard & entry navigation
├── 📄 TimerActivity    # Core focus screen with circular progress tracking
├── 📄 AddTaskActivity   # Interface for configuring new work sessions
├── 📄 SettingsActivity  # Central hub for app preferences & alerts
└── 📄 TimerService      # Background engine managing CountDownTimer & notifications