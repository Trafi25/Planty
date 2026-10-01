# Polanty 🌿

**Polanty** is a modern Android application for smart plant care, garden management, and AI-powered plant identification. Built with **Jetpack Compose**, **Clean Architecture**, **Hilt**, **Room**, and **WorkManager**, Polanty helps plant enthusiasts track their plants, organize spaces, and never miss a watering or soil check routine.

---

## ✨ Features

* **🪴 Plant & Garden Management**: Catalog all plants in your collection with custom nicknames, species taxonomy, and photo thumbnails.
* **🏠 Room & Space Organization**: Group plants into physical rooms or locations (*Living Room, Bedroom, Kitchen, Balcony, Bathroom, Office, or Custom spaces*).
* **💧 Smart Care Engine**: Automatically calculates customized care routines (watering and soil moisture checks) tailored to plant species guidelines. Auto-adjusts watering intervals dynamically when soil check results (*dry vs. moist*) are recorded.
* **🔔 Scheduled Notifications & Deep Linking**: Background care reminders scheduled via **WorkManager** and delivered through **NotificationManagerCompat**. Tapping a reminder notification opens the app directly to that specific plant's detail screen.
* **🔍 Plant Identification & Knowledge**: Identify plant species from camera shots or gallery photos powered by **PlantNet** & **Perenual** APIs, **Gemini AI** knowledge generation, and **GBIF** taxonomy resolution.
* **🛡️ Firebase App Check Security**: Enforces Firebase App Check with **Play Integrity** (and Debug Provider for development) to secure Gemini AI API calls.

---

## 🏗️ Architecture & Tech Stack

Polanty strictly follows **Android Modern App Architecture** and **Clean Architecture** principles (*Presentation, Domain, Data* layers) with **Unidirectional Data Flow (UDF)**.

* **Language**: Kotlin 100% (Coroutines, Flow, StateFlow)
* **UI**: Jetpack Compose, Material 3, Navigation Compose, Coil 3 (image loading), Edge-to-Edge display
* **Dependency Injection**: Hilt (`@HiltAndroidApp`, `@HiltViewModel`, `@HiltWorker`, `HiltWorkerFactory`)
* **Local Database**: Room (`PlantDatabase`) with DAOs (`CareTaskDao`, `PlantDao`, `PlantSpaceDao`, `PlantKnowledgeDao`), foreign key cascading, and optimized index lookups
* **Background Tasks**: Android WorkManager with custom Hilt `WorkerFactory`
* **Networking**: Retrofit 2, Moshi, OkHttp 4 with custom authentication interceptors
* **AI & Cloud Services**: Firebase App Check (Play Integrity & Debug Provider), Gemini AI Logic
* **Code Formatting**: Spotless (`app:spotlessApply`)
* **Testing**: JUnit 5, MockK, Google Truth, Turbine, Coroutines Test framework

---

## 📁 Project Structure

```
app/src/main/java/com/traffipart/polanty/
├── PlantyApp.kt               # Application entry point, HiltWorkerFactory & Firebase App Check setup
├── MainActivity.kt             # Single activity host, notification intent deep-link handler
├── core/                      # Common utilities, DI modules (Database, Network, Repository), Network interceptors
├── data/                      # Room Entities, DAOs, Mappers, Repositories, Remote DTOs & APIs, Reminder Workers
├── domain/                    # Care Engine logic, Domain models, Repository interfaces, Use cases
├── presentation/              # Jetpack Compose UI
│   ├── root/                  # Root scaffold, bottom bar, navigation host graph
│   ├── home/                  # Dashboard summary, today's care tasks, soil check actions
│   ├── garden/                # All plants & spaces tabs, space creation/deletion
│   ├── details/               # Plant details & care knowledge content
│   ├── scan/                  # Plant identification flow
│   ├── setup/                 # Plant candidate setup & space picker
│   └── spaceDetails/          # Room view showing filtered plants
└── ui/theme/                  # Material 3 theme palette, typography, shapes, and spacing
```

---

## 🚀 Getting Started

### Prerequisites

* **Android Studio**: Ladybug (2024.2.1) or newer
* **JDK**: 17
* **Android SDK**: `compileSdk = 37`, `minSdk = 29`, `targetSdk = 37`

### Setup Instructions

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/traffipart/polanty.git
   cd polanty
   ```

2. **Configure Local Properties**:
   Create or open `local.properties` in the project root directory and add your API keys:
   ```properties
   PLANT_NET_API_KEY=your_plant_net_api_key
   PERENUAL_API_KEY=your_perenual_api_key
   ```

3. **Build & Run**:
   Open the project in Android Studio and run the `app` module on an emulator or physical device running Android 10 (API level 29) or higher.

---

## 🧪 Testing & Code Formatting

### Unit Tests
Execute unit tests across repository layers, care engine logic, and use cases using JUnit 5:
```bash
./gradlew test
```

### Code Formatting
Apply automatic code formatting using Spotless:
```bash
./gradlew app:spotlessApply
```

---

## 📄 License

```text
Copyright 2026 Polanty Contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
