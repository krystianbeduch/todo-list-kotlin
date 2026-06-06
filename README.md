# To-Do List
The application is a task planner designed to help users organize everyday activities. It allows creating tasks with priorities and deadlines, makring tasks as completed, editing existing entries, and attaching files such as images, videos, or PDF documents. The application also includes an in-app notification mechanism for upcoming and overdue tasks, as well as import and export of task lists in CSV, JSON and XML formats. The project was implemented in Kotlin using the Model-View-ViewModel (MVVM) architectural pattern with Room as the local persistence layer.

### Contents
1. [Application functionality](#application-functionality)
2. [Technology](#technology)
3. [Project structure](#project-structure)
4. [Database schema](#database-schema)
5. [Setup](#setup)
6. [Functional description](#functional-description)
   - [List of tasks](#list-of-tasks)
   - [Add new task](#add-new-task)
   - [Managing tasks](#managing-tasks)
   - [Edit task](#edit-task)
   - [Managing attachments](#managing-attachments)
   - [Notification mechanism](#notification-mechanism)
   - [Import and export of task list to files](#import-and-export-of-task-list-to-files)
   - [Changing the application language](#changing-the-application-language)
7. [Technical documentation](#technical-documentation)
    - [Architectural overview](#architectural-overview)
    - [Data modeling in Kotlin](#data-modeling-in-kotlin)
    - [Dependency creation and object management](#dependency-creation-and-object-management)
    - [Asynchronous data flow](#asynchronous-data-flow)
    - [Persistence layer](#persistence-layer)
    - [File handling and serialization](#file-handling-and-serialization)
    - [Localization support](#localization-support)
    - [Testing approach](#testing-approach)

## Application functionality
- Displays all saved tasks in a clear list, allowing easy browsing and quick access to task details
- Allows users to create new task by providing a title, deadline, and priority
- Supports task management, including editing, deleting, marking tasks as completed, and sorting tasks by selected criteria
- Enables adding, viewing, and deleting attachments associated with tasks
- Provides an in-app notification mechanism for tasks with upcoming deadlines and overdue tasks
- Supports importing and exporting the task list using CSV, JSON, and XML file formats
- Allows changing the application language between multiple available localizations

## Technology
### Core
- Android SDK 37 (min. SDK 28)
- Kotlin 2.4.0
- Android Gradle Plugin (AGP) 9.2.1

### Android Jetpack
- Lifecycle ViewModel 2.10.0
- Navigation Component 2.9.8
- Room 2.8.4
- WorkManager 2.11.2

### Asynchronous programming
- Kotlin Coroutines 1.11.0
- Kotlin Flow

### Serialization anf file formats
- Kotlinx Serialization JSON 1.11.0
- Simple XML 2.7.1

### UI
- Material Components 1.14.0
- AppCompat 1.7.1
- ConstraintLayout 2.2.1
- Activity KTX 1.13.0
- Fragment KTX 1.8.9

### Build tools
- KSP 2.3.9

### Testing
- JUnit 4.13.2
- AndroidX JUnit 1.3.0
- Mockito Kotlin 6.3.0
- Kotlin Coroutines Test 1.11.0

## Project structure

```bash
├───data
│   ├───dao
│   │       AttachmentDao.kt
│   │       TaskDao.kt
│   │
│   ├───db
│   │       AppDatabase.kt
│   │
│   ├───mapper
│   │       AttachmentMapper.kt
│   │       TaskJsonMapper.kt
│   │       TaskMapper.kt
│   │
│   ├───model
│   │   │   AttachmentEntity.kt
│   │   │   TaskEntity.kt
│   │   │   TaskWithAttachments.kt
│   │   │
│   │   └───enums
│   │           FileType.kt
│   │           NotificationType.kt
│   │           Priority.kt
│   │           SortType.kt
│   │
│   └───repository
│           AttachmentRepositoryImpl.kt
│           TaskRepositoryImpl.kt
│
├───domain
│   ├───model
│   │       Attachment.kt
│   │       Task.kt
│   │
│   └───repository
│           AttachmentRepository.kt
│           TaskRepository.kt
│
├───presentation
│   │   AppContainer.kt
│   │
│   ├───activity
│   │       MainActivity.kt
│   │       TaskActivity.kt
│   │
│   ├───addtask
│   │       AddTaskFragment.kt
│   │
│   ├───home
│   │       HomeFragment.kt
│   │       TaskAdapter.kt
│   │
│   ├───notifications
│   │       NotificationAdapter.kt
│   │       NotificationsFragment.kt
│   │
│   └───viewmodel
│           TaskViewModel.kt
│           TaskViewModelFactory.kt
│
└───util
    ├───converter
    │       Converters.kt
    │
    ├───file
    │   │   FileService.kt
    │   │
    │   ├───json
    │   │       LocalDateTimeAsStringSerializer.kt
    │   │       TaskJsonDto.kt
    │   │
    │   └───xml
    │           TaskXml.kt
    │           TaskXmlWrapper.kt
    │
    ├───lang
    │       LocalHelper.kt
    │
    ├───notification
    │       NotificationUtils.kt
    │
    └───task
            BaseTaskAdapter.kt
            TaskFormHelper.kt
```
- `data` - data layer responsible for local persistence, database operations, and transforming data between storage-specific and domain representations:
   - `dao` - Data Access Object interfaces used to communicate with the Room database
   - `db` - Room database configuration and entry point
   - `mapper` - mapping classes responsible for converting database entities and file-related models into domain models and back
   - `model` - Room entities, relations, and enums used by the persistence layer
   - `repository` - concrete implementations of repository interfaces defined in the domain layer
- `domain` - domain layer containing the core business models and repository contracts used by the rest of the application:
   - `model` - platform-independent application models used in business logic
   - `repository` - abstractions defining how tasks and attachments are accessed and managed
- `presentation` - presentation layer responsible for the user interface and screen-related logic:
   - `AppContainer` - manual dependency container used to provide repositories and shared dependencies to the presentation layer
   - `activity` - activities acting as application entry points and task-related screens
   - `addtask` - fragment responsible for creating new tasks
   - `home` - main screen displaying the task list and its adapter
   - `notifications` - fragment and adapter used to present tasks with active notifications
   - `viewmodel` - ViewModel classes responsible for exposing UI state and handling user actions
- `util` - helper classes supporting the application’s additional features:
   - `converter` - converters used by Room, especially for date-time transformations
   - `file` - classes responsible for importing, exporting, and serializing task data in JSON and XML formats
   - `lang` - utilities related to changing and applying the application language
   - `notification` - helper classes for creating and displaying notifications
   - `task` - reusable task-related helpers, including form handling and base adapter logic

### Resource directory (`res`)
The `res` folder contains all app resources organized into subdirectories by type:
- `drawable` - image assets and icons in PNG and XML formats, including app icons, navigation icons, and language flags
- `layout` - XML layouts for activities, fragments, and list items, defining the UI structure
- `menu` - XML files defining menus such as bottom navigation, sorting, and task context menus
- `mipmap-*` - launcher icons in various resolutions (mdpi, hdpi, xhdpi, etc.) optimized for different screen densities
- `navigation` - navigation graph XML describing app navigation flow
- `values` — default resourcies such as colors, strings, and themes
- `values-*-r*` - localized string resources for supported languages and regions
- `xml` - miscellaneous XML configurations, including backup rules, file paths, and data extraction settings

## Database schema
<img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/database-schema.png" alt="Database schema" title="Database schema" height="350">

## Setup  
### 1. Configure the Android Studio environment
> [!NOTE]
> This project was developed using the _Panda_ version of Android Studio.<br>
> You do not need to install a separate JDK, as Android Studio includes its own buit-in JDK.

### 2. Download or clone the project
- Option 1: Download the ZIP file from github and extract it
- Option 2: Clone the repository:
```bash
git clone https://github.com/krystianbeduch/todo-list-kotlin.git
```

### 3. Open the project in Android Studio
- Select `Open` and choose the project folder
- Wait for Gradle to finish syncing all dependencies

### 4. Run the application
- Ensure you have a connected __emulator__ or __physical Android device__ with __debugging enabled__
- Press __Run__ (`Shift + F10` or the green button <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/green-play-button.png" alt="Green button" title="Green button" height="20">) to launch the app

### 5. Optional sample data
> [!CAUTION]
> The `enableDummyTasks` flag in [`MainActivity`](./app/src/main/java/com/krybed/todolist/presentation/activity/MainActivity.kt) controls whether sample tasks are loaded on startup.
> 
> ```kotlin
> private val enableDummyTasks = false
> ```
> 
> If you change its value to `true`, the application will load sample tasks during startup. This is useful for quickly testing the UI and basic task management features without manually creating data. By default, the flag is disabled.

## Functional description
### List of tasks
Presents all tasks in a scrollable list using a _RecyclerView_. Each task is visually styled according to the `item_task.xml` layout, ensuring a consistent and user-friendly appearance. 
Each task item displays the following information:
- Title — the main name or description of the task
- Deadline — the due date and time by which the task should be completed
- Addition date — the date and time when the task was originally added
- Priority — indicates the importance level of the task, which can be _High_, _Medium_, or _Low_
- Completion status — represented by a ✔ symbol when the task is marked as completed
- Attachment icon — displayed if one or more attachments are associated with the task, indicating additional files or resources linked to it
<p align="center">
   <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/list-of-tasks.jpg" alt="List of tasks" title="List of tasks" height="800" align="center">
</p>
This setup provides a clear and informative overview of all tasks, helping users to easily track, prioritize, and manage their work

### Add new task
To add a new task, the user navigates to a dedicated fragment accessible from the bottom navigation menu. This fragment presents a form where the user can enter all necessary details for the task creation. 
The form includes the following fields:
- Task title — a text input where the user specifies the title of the task.
- Deadline — a date and time picker allowing the user to set the task’s deadline.
- Priority — a dropdown menu from which the user selects the task’s priority level (_High_, _Medium_, _Low_).
<p align="center">
   <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/add-new-task.jpg" alt="Add new task" title="Add new task" height="800">
</p>

The form is designed with validation rules to ensure data integrity:
- Empty fields are not accepted, preventing the creation of tasks without essential information.
- Due date cannot be set to a past date, ensuring that deadlines are always in the present or future.
<p align="center">
   <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/add-new-task-empty-fields.jpg" alt="Add new task - empty fields" title="Add new task - empty fields" height="800">
   <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/add-new-task-past-date.jpg" alt="Add new task - past date" title="Add new task - past date" height="800">
</p>

### Managing tasks
The application provides a flexible and intuitive interface for managing tasks. 
- Interacting with a task - tapping on a task in the list opens a context menu with several options:
   - [Edit Task](#edit-task) - allows the user to update the task details
   - Delete Task – permanently removes the task
   - Mark as Done / Undone – toggles the task completion status
   - [Managing attachments](#managing-attachments)
- Quick Status Change - long-pressing a task directly toggles its completion status (✔)
- Task Sorting - tasks can be sorted dynamically by the user using a _spinner_ menu located in the toolbar. The available sorting options include:
   - Addition date (default)
   - Title
   - Deadline
   - Priority
   - Status <br>
<p align="center">
   <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/managing-tasks-menu.jpg" alt="Managing tasks - menu" title="Managing tasks - menu" height="800">
   <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/managing-tasks-sort-by-priority.jpg" alt="Managing tasks - sort by priority" title="Managing tasks - sort by priority" height="800"> 
</p>

### Edit Task
Selecting the `Edit` option from a task’s context menu navigates the user to a dedicated activity for editing. 
The interface presents a form identical to the one used for adding a new task. However, the form fields are pre-filled with the existing task data.
The user can update any of these values and save the changes. The form retains the same validation mechanisms.
<p align="center">
   <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/edit-task.jpg" alt="Edit task" title="Edit task" height="800">
</p>

### Managing attachments
Attachments can be managed through a task’s context menu. The following operations are available:
- Add attachment:
   - User can attach files (image, video, PDF document) to a task directly from their device. When a file is selected, it is copied to the application's internal storage, ensuring the attachment remains accessible even if the original file is deleted from the device.
- View attachments:
   - User can view all attachments linked to a specific task. Each file is opened using an appropriate external application, if available on the device.
- Delete attachment:
   - User can delete individual attachments from a task. This action removes the file from both the task and the application's internal storage.
<p align="center">
   <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/managing-attachments.jpg" alt="Managing attachments" title="Managing attachments" height="800">
</p>

### Notification mechanism
The app implements a notification system to help users stay on top of their tasks’ deadlines:
- When the application starts, it automatically shows notifications for all incomplete tasks whose deadlines are within the next 24 hours or have already passed. This ensures users are immediately notified of upcoming or overdue tasks
- Accessible via the bottom navigation menu, the `Notifications` fragment displays the total count of tasks with active notifications. Inside this fragment, users can see detailed information about each task along with the notification type:
   - __"Task deadline has passed"__ for overdue tasks
   - __"Upcoming task deadline"__ for tasks whose deadline is approaching within 24 hours
<p align="center">
   <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/notification-fragment.jpg" alt="Notification fragment" title="Notification fragment" height="800">
   <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/notification-mechanism.jpg" alt="Notification mechanism" title="Notification mechanism" height="400">
</p>

### Import and export of task list to files
The application provides the ability to import and export the task list to external files, allowing users to back up their tasks or load them from other sources.
This functionality is accessible through the `More` section in the bottom navigation menu, where two options are available: `Import tasks` and `Export tasks`:
- Exporting tasks
   - After selecting the `Export` option, a dialog window appears where the user can choose the file format. Once the export process is completed successfully, a confirmation message is shown to the user. The exported file is saved to the device's `Downloads` directory.
- Importing tasks
   - After selecting the `Import` option, the app opens a file picker, allowing the user to select a file from the device's storage. A built-in mechanism checks the file extension to ensure it matches the expected formats. Files with unsupported extensions are automatically rejected, and an error message is displayed. It is important that the structure of the imported file adheres to the required format. If the format is incorrect or inconsistent, the import will fail, and an appropriate error message will be shown to the user. The `id` field at the import stage is ommited although it is required in the file.
 
#### CSV
```csv
ID;Title;Deadline;Priority;Status;Created at
1;Example task 1;08.06.2026 12:00;HIGH;true;04.06.2026 23:56
2;Example task 2;30.06.2026 14:30;MEDIUM;false;02.06.2026 16:56
```

#### JSON
```json
[
   {
      "id": 1,
      "title": "Example task 1",
      "deadline": "08.06.2026 12:00",
      "priority": "HIGH",
      "isDone": true,
      "createdAt": "04.06.2026 23:56"
   },
   {
      "id": 2,
      "title": "Example task 2",
      "deadline": "30.06.2026 14:30",
      "priority": "MEDIUM",
      "isDone": false,
      "createdAt": "02.06.2026 16:56"
    }
]
```

#### XML
```XML
<Tasks>
   <Task>
      <id>1</id>
      <title>Example task 1</title>
      <deadline>08.06.2026 12:00</deadline>
      <priority>HIGH</priority>
      <isDone>true</isDone>
      <createdAt>04.06.2026 23:56</createdAt>      
   </Task>
   <Task>
      <id>2</id>
      <title>Example task 2</title>
      <deadline>30.06.2026 14:30</deadline>
      <priority>MEDIUM</priority>
      <isDone>false</isDone>
      <createdAt>02.06.2026 16:56</createdAt>
   </Task>
</Tasks>
```
<p align="center">
   <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/import-tasks.jpg" alt="Import tasks" title="Import tasks" height="800">
</p>

### Changing the application language
The application allow users to switch between available languages: Polish and English. There are two ways to change the language:
- Through the `More` Section - in the bottom navigation menu, under the `More` tab, users can select the preferred language from the available options. Selecting a language immediately updates the app's interface to reflect the new setting.
- Language icon on the toolbar - the `Home` screen _Toolbar_ displays a flag icon representing the currently selected language. Tapping on this icon also allows the user to toggle between languages quickly and intuitively.
<p align="center">
   <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/changing-app-language-to-polish.jpg" alt="Changing app language to Polish" title="Changing app language to Polish" height="800">
   <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/changing-app-language-to-spanish.jpg" alt="Changing app language to Spanish" title="Changing app language to Spanish" height="800">
   <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/changing-app-language-to-turkish.jpg" alt="Changing app language to Turkish" title="Changing app language to Turkish" height="800">
   <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/changing-app-language-to-arabic.jpg" alt="Changing app language to Arabic" title="Changing app language to Arabic" height="800">
</p>

## Technical documentation
This section describes the key Kotlin and Android solutions used in the project. The goal is to present not only the final functionality of the application, but also the programming techniques, architectural decisions, and language features applied during implementation.

### Architectural overview
The application follows the MVVM architectural pattern, which separates the user interface from the business logic and data handling layers. UI components such as activities and fragments are responsible only for displaying data and reacting to user actions, while the `ViewModel` classes expose screen state and coordinate data operations.

The project is organized into clearly separated packages: `presentation`, `domain`, `data`, and `util`. This structure improves code readability, makes the project easier to maintain, and reduces coupling between the UI, database, and utility logic.

### Data modeling in Kotlin
The main application models, such as [`Task`](./app/src/main/java/com/krybed/todolist/domain/model/Task.kt), [`Attachment`](./app/src/main/java/com/krybed/todolist/domain/model/Attachment.kt), [`TaskEntity`](./app/src/main/java/com/krybed/todolist/data/model/TaskEntity.kt), [`AttachmentEntity`](./app/src/main/java/com/krybed/todolist/data/model/AttachmentEntity.kt), and [`TaskWithAttachments`](./app/src/main/java/com/krybed/todolist/data/model/TaskWithAttachments.kt), are implemented as Kotlin data classes. This approach is convenient because Kotlin automatically generates useful methods such as `equals()`, `hashCode()`, `toString()`, and `copy()`, which simplifies working with immutable data and comparing objects.

Separate domain models and database entities are used intentionally. Domain classes represent the application logic, while entity classes are tailored to Room persistence. This makes the code cleaner and allows the database layer to evolve independently from the rest of the application.

### Dependency creation and object management
Dependency creation in the project is handled in a simple and explicit way, without introducing a full dependency injection framework. Shared objects are provided through classes such as [`AppContainer`](./app/src/main/java/com/krybed/todolist/presentation/AppContainer.kt), which acts as a manual dependency container and supplies repositories to the presentation layer. This keeps dependency wiring centralized and makes the structure of the application easier to understand.

Several stateless helpers are implemented using Kotlin `object`, which is the idiomatic way to declare a singleton. Examples include [`LocalHelper`](./app/src/main/java/com/krybed/todolist/util/lang/LocalHelper.kt), [`NotificationUtils `](./app/src/main/java/com/krybed/todolist/util/notification/NotificationUtils.kt), and [`FileService`](./app/src/main/java/com/krybed/todolist/util/file/FileService.kt). Using object avoids the need for utility classes with static methods and provides a single shared instance with a compact syntax.

The Factory pattern is used for creating `ViewModel` instances that require constructor parameters. [`TaskViewModelFactory`](./app/src/main/java/com/krybed/todolist/presentation/viewmodel/TaskViewModelFactory.kt) receives [`TaskRepository`](./app/src/main/java/com/krybed/todolist/domain/repository/TaskRepository.kt) and [`AttachmentRepository`](./app/src/main/java/com/krybed/todolist/domain/repository/AttachmentRepository.kt), and passes them to [`TaskViewModel`](./app/src/main/java/com/krybed/todolist/presentation/viewmodel/TaskViewModel.kt). This is necessary because a custom `ViewModel` with dependencies cannot be created using the default no-argument factory

### Asynchronous data flow
Asynchronous operations in the project are implemented using Kotlin coroutines, `Flow`, and `StateFlow`. This approach allows database queries, file operations, and background logic to run without blocking the main thread, which keeps the user interface responsive even during longer tasks such as importing data or updating records.

A central example of this approach is [`TaskViewModel`](./app/src/main/java/com/krybed/todolist/presentation/viewmodel/TaskViewModel.kt) , where methods such as `insert()`, `update()`, `delete()`, `importTasksFromFile()`, and `exportTasksToFile()` launch asynchronous work in `viewModelScope`. The current list of tasks is exposed as a StateFlow, and additional state such as the selected sort type or notification-related data is also stored in flows. This makes the UI reactive, because every state change can be observed automatically by the screen layer.

The task list is derived by combining the task stream from the repository with the currently selected sort type using `combine()`, and then converted into a lifecycle-aware `StateFlow` with `stateIn()`. On the UI side, flows are collected inside `repeatOnLifecycle()`, which ensures that updates are received only while the screen is in an active state. This reduces unnecessary work and prevents updates from being delivered to a stopped or destroyed activity or fragment.

### Persistence layer
The persistence layer is based on Room and provides local storage for tasks and attachments. Database access is defined through [`TaskDao`](./app/src/main/java/com/krybed/todolist/data/dao/TaskDao.kt) and [`AttachmentDao`](./app/src/main/java/com/krybed/todolist/data/dao/AttachmentDao.kt), while [`AppDatabase`](./app/src/main/java/com/krybed/todolist/data/db/AppDatabase.kt) serves as the main database entry point. This setup makes the data layer structured and consistent with standard Android persistence practices.

The repository layer acts as an abstraction between Room and the rest of the application. [`TaskRepositoryImpl`](./app/src/main/java/com/krybed/todolist/data/repository/TaskRepositoryImpl.kt) and [`AttachmentRepositoryImpl`](./app/src/main/java/com/krybed/todolist/data/repository/AttachmentRepositoryImpl.kt) implement the contracts defined in the domain layer and encapsulate direct database access. Thanks to this, the ViewModel interacts with repositories rather than DAOs, which keeps UI logic independent from persistence details.

Room relations are used to represent connected data, for example through [`TaskWithAttachments`](./app/src/main/java/com/krybed/todolist/data/model/TaskWithAttachments.kt). The conversion between persistence models and domain models is handled by dedicated mapper functions, which keeps Room-specific details out of the business logic and UI layers. In this project, mapping is implemented in [`TaskMapper`](./app/src/main/java/com/krybed/todolist/data/mapper/TaskMapper.kt) using Kotlin extension functions such as `TaskEntity.toDomain()`, `Task.toEntity()`, and `TaskWithAttachments.toDomain()`. This keeps the transformation logic centralized and makes repository code easier to read.

### File handling and serialization
The application supports export and import of task lists in CSV, JSON, and XML formats. This functionality is implemented in the [`FileService`](./app/src/main/java/com/krybed/todolist/util/file/FileService.kt) singleton, which provides dedicated methods for each file type and handles reading, writing, and validation of file contents. Keeping this logic in one place makes the file-related code easier to reuse and prevents serialization concerns from being mixed into the UI or repository layers.

The project also uses separate helper classes for format-specific data processing, such as JSON and XML DTOs located in the `util/file/json` and `util/file/xml` packages. These classes make it possible to adapt the internal task model to external file structures without changing the core domain models. As a result, serialization remains isolated, explicit, and easier to maintain when supporting multiple formats.

### Localization support
Application localization is handled by the [`LocalHelper`](./app/src/main/java/com/krybed/todolist/util/lang/LocalHelper.kt) singleton, which centralizes all language-related operations in one place. This object is responsible for storing the selected language, applying it to the application context, and refreshing the UI after a language change. The helper uses `SharedPreferences` to persist the selected language code under a dedicated key, which allows the app to restore the user’s language preference after restart. The `setLocale()` method creates a new `Configuration` based on the selected locale, updates the layout direction, and returns a localized context using `createConfigurationContext()`.

Previously selected language settings are restored through `applySavedLocale()`, which reads the saved value from preferences and reapplies it when a new activity context is attached. This makes it possible to keep the selected language consistent across screens and app launches. The language selection UI is provided by `showChangeLanguageDialog()`, which displays an `AlertDialog` with the list of supported languages and their language tags. After the user chooses a language, `changeLanguage()` applies the new locale and recreates the current activity so that all visible resources, such as strings and layout direction, are reloaded immediately.

### Testing approach
The project includes both local unit tests and Android instrumented tests. Unit tests run directly on the JVM and are used to verify business logic in isolation, without depending on the Android runtime. Instrumented tests run on an Android device or emulator and are used to validate Android-specific components, such as Room database operations.

A good example of a local unit test is [`AttachmentMapperTest`](./app/src/test/java/com/krybed/todolist/data/mapper/AttachmentMapperTest.kt), which verifies the mapping logic between [`AttachmentEntity`](./app/src/main/java/com/krybed/todolist/data/model/AttachmentEntity.kt) and [`Attachment`](./app/src/main/java/com/krybed/todolist/domain/model/Attachment.kt). This type of test is useful for checking that transformation functions return correct values and preserve the expected fields in both directions.

An instrumented test is [`TaskDaoInstrumentedTest`](./app/src/androidTest/java/com/krybed/todolist/TaskDaoInstrumentedTest.kt), which tests Room DAO operations against an in-memory database. This test class verifies common database scenarios such as inserting, updating, deleting, filtering, and loading tasks with attachments. It also checks whether the data returned by DAO queries is ordered and mapped correctly.

JUnit is used as the main testing framework, Mockito is used for mocking dependencies where needed, and `runTest` from `kotlinx.coroutines.test` is used to test coroutine-based code in a controlled and deterministic way. This combination makes it possible to verify asynchronous logic without relying on real delays or background threads.