![Banner](https://github.com/user-attachments/assets/67608836-b085-4d6b-9f6f-faa562e20912)
[<img width="380px;" src="https://github.com/user-attachments/assets/e49643e6-3e2b-4a33-97fb-d2f94a8ea91e"/>](https://play.google.com/store/apps/details?id=com.doyoonkim.knutice)

<br>



# 🔔 KNUTICE [![kor](https://img.shields.io/badge/lang-kr-blue.svg)](https://github.com/KNUTICE/KNUTICE-Android/blob/release/README.md)

**Korea National University of Transportation Announcement Aggregator**

> **Role:** Android Engineer (Native)
> 
> **Status:** Live in Production (Google Play Store)
> 
> **Users:** 250+ Total Users / 130+ MAU

# 💁 Service Introduction

**"Never miss a campus update again."**

KNUTICE is a utility service that delivers real-time push notifications the moment a new announcement is posted on the university website.

I developed this service to help students avoid the hassle of manually checking the homepage and to ensure they never miss critical information regarding scholarships or academic schedules.

# ⚒️ Tech Stack

Built with a focus on stability, scalability, and modern Android development standards.

- **Language:** `Kotlin`
    
- **UI:** `Jetpack Compose` (Material3), `Navigation for Compose`
    
- **Architecture:** `Multi-Module Clean Architecture`, `MVI (Unidirectional Data Flow)`
    
- **Async & Stream:** `Coroutines`, `Flow (StateFlow, SharedFlow)`
    
- **DI:** `Dagger 2`
    
- **Local DB:** `Room` (FTS4, Custom Tokenizer applied), `DataStore`
    
- **Background Task:** `WorkManager` (PeriodicWork, Chained Task)
    
- **Network:** `Retrofit2`, `OkHttp`
    
- **CI:** `GitHub Actions`
    

# ⚙️ Architecture

To ensure long-term maintainability and scalability beyond simple feature implementation, I adopted **Multi-Module Clean Architecture**.

- **Multi-Module Strategy:** Decomposed the codebase into `app`, `core`, `feature`, `domain`, and `data` modules. This separation of concerns reduced code coupling and significantly improved build efficiency.
    
- **MVI Pattern:** Explicitly separated `State`, `Event`, and `SideEffect` to manage data flow unidirectionally. This made the UI state predictable and greatly simplified the debugging process.
    

# 🚀 Technical Challenges & Solutions

A summary of key engineering challenges encountered during production and how I solved them.

### 1. Enhancing Search Precision (Room FTS4 + Tokenization)

When searching through saved notices (Bookmarks), standard SQL `LIKE` queries resulted in poor accuracy. For example, searching for "Notice" (`공지`) would incorrectly include unrelated results like "Artificial Intelligence" (`인공지능`) due to substring matching.

- **Room FTS4 (Full-Text Search):** Migrated the local database to use FTS4 for optimized text search capabilities.
    
- **Custom Tokenization:** Integrated a Korean morphological analysis library (`OpenKoreanTextProcessor`) to tokenize search terms. By applying these tokens to FTS queries, I successfully **filtered out semantic false positives (e.g., 'Artificial Intelligence'), ensuring only relevant results matching 'Notice' are returned.**
    

### 2. Custom Markdown Renderer (Parser & Native UI)

Rendering AI-generated Markdown from the server using third-party libraries resulted in UI inconsistencies that clashed with the app's design language.

- **Native Component Mapping:** Beyond simple text styling, I implemented a rendering engine that maps structural Markdown elements (Headers, Tables, Dividers, Lists) to distinct **Native Composables**. This ensured 100% consistency with the app's Material Theme.
    
- **Recursive Text Parsing:** For complex inline formatting (e.g., nested syntax like `_**Bold & Italic**_`), I applied a **DFS-based recursive algorithm** to accurately parse the structure into `AnnotatedString`, ensuring precise rendering of compound text styles.
    

### 3. Optimizing UI Response & UX (Staging Table & WorkManager)

Writing to an FTS-enabled table is slower than standard tables due to the indexing process. Even when offloaded to `Dispatchers.IO`, saving a bookmark took long enough to keep the `CircularProgressIndicator` (loading state) active, degrading the user experience.

- **Staging Table Strategy:** Implemented a lightweight "Staging Table" for immediate writes. When a user bookmarks an item, it is instantly saved here, allowing the UI to return to a success state immediately without blocking.
    
- **Deferred Indexing:** Configured a chained **WorkManager** pipeline to migrate data from the Staging Table to the FTS Table in the background. This approach secured both high performance and a seamless user experience.
    

### 4. Reliable Push Notification Management (PeriodicWork)

If the app remains unused for extended periods or the device enters Doze mode, FCM tokens can become stale or unsynchronized, leading to missed notifications.

- **PeriodicWork:** Deployed **WorkManager** to periodically synchronize the FCM token with the server and validate its status. This ensures that users reliably receive critical announcements even if they do not open the app frequently.
    

# 🧐 What I Learned

Through this project, I gained deep insights into the Android framework and the importance of robust architecture.

- **Clean Architecture & Multi-Module:** I experienced firsthand the benefits of enforcing dependency rules through physical module separation, compared to simply using packages in a single module, particularly regarding maintainability and build speed.
    
- **Compose & Lifecycle:** Transitioning from imperative XML to `Jetpack Compose`, I mastered the lifecycle management of declarative UI and efficient component reuse (Composables).
    
- **Efficient Data Pipeline (Flow):** I implemented robust MVI state management using `StateFlow` for UI state and `SharedFlow`/`Channel` for handling side effects. I optimized search logic using `snapshotFlow` with `debounce` and `distinctUntilChanged` to detect real-time input without excessive network requests. Furthermore, I established a seamless Unidirectional Data Flow (UDF) by connecting the data layer—from `Remote/DB` through `Domain` to `ViewModel`—using `Cold Flow`.
    
- **Deep Dive into Dagger 2 (Migration from Hilt):** Initially utilizing Dagger Hilt, I migrated to **Pure Dagger 2**during the multi-module transition to deeply understand the framework's internals. Although challenging, manually implementing `Component`/`SubComponent` hierarchies, `Scope` management, and `Builder/Factory` patterns allowed me to master the fundamental mechanisms of Dependency Injection hidden behind Hilt's convenience.
    
- **Modern Navigation:** I learned to seamlessly handle screen transitions and argument passing within a Single Activity architecture using `Navigation for Compose`.

# 📱 Preview
<div style="display: flex; overflow-x: auto; justify-content: center; margin-bottom: 10px;">
  <img width="130" alt="스크린샷 2024-08-15 23 51 59" src="https://github.com/user-attachments/assets/0ff78e0a-1f5c-49fb-a055-ab0eadae7170">
  <img width="130" alt="스크린샷 2024-08-15 23 51 59" src="https://github.com/user-attachments/assets/ef4b72f6-87d0-4b4e-829f-4aff8dce438f">
  <img width="130" alt="스크린샷 2024-08-15 23 51 59" src="https://github.com/user-attachments/assets/bb921b96-6438-4896-b0aa-0c937997b8ca">
  <img width="130" alt="스크린샷 2024-08-15 23 51 59" src="https://github.com/user-attachments/assets/f6cdfe51-50d2-4b47-a977-8d5d6b488287">
  <img width="130" alt="스크린샷 2024-08-15 23 51 59" src="https://github.com/user-attachments/assets/29e3c455-ca07-4aae-bb44-30bab2379418">
  <img width="130" alt="스크린샷 2024-08-15 23 51 59" src="https://github.com/user-attachments/assets/5c02cb75-0658-4a7b-85ed-4177f0231e12">  
</div>

<br>
