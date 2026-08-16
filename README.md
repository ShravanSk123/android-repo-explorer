# Repo Explorer

A small Android app that looks up any GitHub user's public repositories, paired with a fully automated CI/CD pipeline: containerized Gradle builds, static analysis, and automatic distribution to testers.

---

## What it does

Type a GitHub username, tap Search, and see that user's public repos — name, description, and star count — pulled live from the GitHub REST API.

**Why it exists:** less a "hello world" and more a working demo of an end-to-end mobile DevOps pipeline — from a git push to a tested build landing on a tester's phone, with no manual steps in between.

## Tech stack

| Layer | Tools |
|---|---|
| App | Kotlin, Jetpack Compose, Retrofit, Coroutines, ViewModel + StateFlow |
| Build | Gradle |
| Containerization | Docker (custom image: JDK 17 + Android SDK) |
| CI/CD | GitHub Actions |
| Static analysis | SonarQube Cloud |
| Distribution | Firebase App Distribution |
| Scripting | Bash |

## Pipeline overview

```
push to main
     │
     ▼
build Docker image (JDK + Android SDK) ──► GitHub Container Registry
     │
     ▼
run inside that container:
  ./gradlew test lintDebug   (unit tests + lint)
  ./gradlew sonar            (SonarQube Cloud static analysis)
  ./gradlew assembleDebug    (build the APK)
     │
     ▼
upload APK to Firebase App Distribution
     │
     ▼
testers get an email + install the new build
```

Every step above runs automatically on every push to `main` — see [`.github/workflows/android-ci.yml`](.github/workflows/android-ci.yml).

## Project structure

```
.
├── app/
│   ├── src/main/java/com/example/repoexplorer/
│   │   ├── MainActivity.kt        # Compose UI
│   │   ├── RepoViewModel.kt       # State management
│   │   └── GitHub.kt              # Retrofit API + repository
│   ├── src/test/java/com/example/repoexplorer/
│   │   └── RepoViewModelTest.kt   # Unit tests
│   └── build.gradle.kts
├── scripts/
│   └── build.sh                   # Test/build wrapper script
├── .github/workflows/
│   └── android-ci.yml             # CI/CD pipeline
├── Dockerfile                     # Reproducible build environment
└── build.gradle.kts               # Root Gradle config + SonarQube plugin
```

## Running it locally

**Requirements:** Android Studio (free), a device or emulator running API 24+.

```bash
git clone https://github.com/ShravanSk123/android-repo-explorer.git
cd android-repo-explorer
```

Open the project in Android Studio, let Gradle sync, then run it on an emulator or a physical device connected via USB debugging.

## Running the build pipeline locally

The same Docker image used in CI can be built and run on your own machine:

```bash
docker build -t repo-explorer-build .
docker run --rm -v "$PWD":/workspace repo-explorer-build ./gradlew assembleDebug --no-daemon
```

> **Apple Silicon (M1–M4) users:** add `--platform linux/amd64` to both commands above — Android's SDK build-tools don't have an arm64 Linux build. See the `Dockerfile` comments for details.

## Testing

```bash
./scripts/build.sh test
```

Runs the unit test suite (`RepoViewModelTest`) plus Android lint.

## CI/CD secrets required

To run the full pipeline on your own fork, add these under **Settings → Secrets and variables → Actions**:

| Secret | Purpose |
|---|---|
| `SONAR_TOKEN` | Authenticates the SonarQube Cloud scan |
| `FIREBASE_APP_ID` | Identifies the Firebase Android app to publish to |
| `FIREBASE_SERVICE_ACCOUNT` | Service account JSON with App Distribution Admin role |

## License

MIT — see [LICENSE](LICENSE).
