# Pomodoro - Focus & Task Management

A robust, native Android application designed to boost productivity using the Pomodoro Technique. This app allows users to create custom tasks, manage work-break cycles with a persistent background timer, and stay focused with visual and audio alerts.

## 🚀 Features

- **Custom Task Management**:
    - Create tasks with specific names and detailed descriptions.
    - Set work duration (1-60 minutes) using intuitive `NumberPickers`.
    - Configure the number of break segments for each task.
    - Persistent task list saved via `SharedPreferences`.
- **Advanced Timer Engine**:
    - **Persistent Foreground Service**: The timer remains active even when the app is in the background or the screen is off, ensuring your focus session isn't interrupted.
    - **Automatic Phase Switching**: Seamlessly transitions between **Work (Çalışma)** and **Break (Mola)** phases.
    - **Smart Notifications**: Control the timer (Pause/Resume) directly from the notification shade.
- **Interactive Timer UI**:
    - **Circular Progress Visualization**: A beautiful, real-time progress bar synced with the countdown.
    - **Dynamic Controls**: Options to Start/Pause, Skip Phase, or Finish the entire task early with a confirmation dialog.
- **Personalized Settings**:
    - Toggle sound alerts for phase completions.
    - Support for **Custom Notification Sounds** (place `timer_sound.raw` in `res/raw`).
    - Haptic feedback (Vibration) support.
    - Adjustable default break durations.
- **Modern Android Standards**:
    - **Edge-to-Edge** support for a modern look.
    - Android 13+ **Runtime Notification Permissions**.
    - Optimized for background execution using `specialUse` foreground service type.

## 🛠️ Technical Details

- **Language**: Java
- **UI Architecture**: XML Layouts with `ConstraintLayout` and `Material Components`.
- **Persistence**: `SharedPreferences` for user settings and task list management.
- **Components**:
    - `TimerService`: The core engine managing the `CountDownTimer` and notifications.
    - `BroadcastReceiver`: Facilitates real-time communication between the background service and the UI.
    - `ServiceConnection`: Binds the UI to the service for precise control.

## 📂 Project Structure

- `MainActivity`: Task dashboard and navigation to settings/addition.
- `TimerActivity`: The main focus screen with progress tracking and controls.
- `AddTaskActivity`: Interface for defining new work sessions.
- `SettingsActivity`: Centralized hub for app preferences and alerts.
- `TimerService`: Background service handling the timing logic and alerts.

## ⚙️ Getting Started

1. Clone the repository.
2. Open in **Android Studio**.
3. Sync Gradle dependencies.
4. Run on a physical device or emulator (API 26+).

## 📄 License

This project is open-source. Feel free to fork and customize!
