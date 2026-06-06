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
git clone https://github.com/krystianbeduch/todo-list.git
```

### 3. Open the project in Android Studio
- Select `Open` and choose the project folder
- Wait for Gradle to finish syncing all dependencies

### 4. Run the application
- Ensure you have a connected __emulator__ or __physical Android device__ with __debugging enabled__
- Press __Run__ (`Shift + F10` or the green button <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/green-play-button.png" alt="Green button" title="Green button" height="20">) to launch the app

## Functional description
### List of tasks
Presents all tasks in a scrollable list using a _RecyclerView_. Each task is visually styled according to the `item_task.xml` layout, ensuring a consistent and user-friendly appearance. 
Each task item displays the following information:
- Title — the main name or description of the task
- Deadline — the due date and time by which the task should be completed
- Creation date — the date and time when the task was originally added
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
   - Creation Date (default)
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
   <img src="https://github.com/krystianbeduch/todo-list/blob/main/readme-images/import-tasks.jpg" alt="Import tasks" title="Import tasks" height="800">
</p>

### Changing the application language
The application allow users to switch between available languages: Polish and English. There are two ways to change the language:
- Through the `More` Section - in the bottom navigation menu, under the `More` tab, users can select the preferred language from the available options. Selecting a language immediately updates the app's interface to reflect the new setting.
- Language icon on the toolbar - the `Home` screen _Toolbar_ displays a flag icon representing the currently selected language. Tapping on this icon also allows the user to toggle between languages quickly and intuitively.
<p align="center">
   <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/changing-app-language-to-polish.jpg" alt="Changing app language to Polish" title="Changing app language to Polish" height="800">
   <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/changing-app-language-to-spanish.jpg" alt="Changing app language to Spanish" title="Changing app language to Spanish" height="800">
   <img src="https://github.com/krystianbeduch/todo-list-kotlin/blob/main/readme-images/changing-app-language-to-arabic.jpg" alt="Changing app language to Arabic" title="Changing app language to Arabic" height="800">
</p>