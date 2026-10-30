# CMS Portal - Detailed Project Documentation

## Project Name
**CMS Portal (Complaint Management System)**

## Purpose
To digitalize and simplify the complaint/maintenance request process by providing role-based access to Users, Staff, and Admin for better tracking, assignment, and resolution.

## User Roles & Responsibilities

| Role | Responsibilities |
|---|---|
| **User** | Raise complaints/maintenance/equipment requests, track status, give feedback, manage profile. |
| **Staff** | View assigned tasks, update task progress/status, mark tasks as completed or paused, view task details. |
| **Admin** | Manage all users & staff, assign tasks, approve/reject equipment requests, monitor maintenance, send notifications, handle feedback & system settings. |

## Module Breakdown

### Authentication Module
Handles app entry with `WelcomeActivity`, `LoginActivity`, `SignUpActivity` and `AuthenticationActivity`. Role-based redirection is used to navigate to User/Staff/Admin dashboards after login.

### Admin Module (`AdminSide/` & `AdminFragment/`)
Covers complete control of the system including dashboard overview, staff/user management, maintenance tracking, equipment approvals, notifications, feedback, and configuration settings.

### Staff Module (`StaffSide/` & `StaffFragment/`)
Focused on task execution. Staff can see only their assigned work and update its status accordingly.

### User Module (`UserSide/` & `UserFragment/`)
Allows users to raise requests and stay updated with real-time status of their complaints/requests.

## Key Activities Overview

| Activity | Description |
|---|---|
| `AdminDashboardActivity` | Main admin panel with navigation to all admin features |
| `StaffActivity` / `StaffProfileActivity` | Staff management & profile views |
| `UserManagementActivity` | Admin view for all registered users |
| `MaintananceActivity` / `MaintananceDetailActivity` | Maintenance request listing & detailed view |
| `AdminEquipmentsApprovalActivity` | Equipment request approval/rejection panel |
| `AssignWorkerActivity` | Assign staff to pending requests |
| `CompleteTaskActivity` / `AdminPauseTaskListActivity` | Track completed & paused tasks |
| `FeedbackActivity` / `FeedbackRequestListActivity` | User feedback review |
| `NotificationActivity` / `AddNotificationActivity` | Push notifications system |
| `SettingActivity` | Role-based settings panel |

## Data Models

Models like `Admin.kt`, `Staff.kt`, `User.kt`, `MaintenanceRequest.kt`, `AdminEquipmentRequest.kt`, `PauseTask.kt`, `Availability.kt`, `AdminFeedbackList.kt`, `AdminCompleteTask.kt` are used to structure Firestore data.

## UI Components

- **RecyclerView Adapters** – For listing users, staff, requests, tasks, feedback & notifications
- **Fragments** – For tab-based/admin dashboard views
- **Custom Drawables & XML Layouts** – Clean role-based UI
- **CircleImageView & Picasso** – For profile display

## Dependencies Used

```kotlin
// Core
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.appcompat)
implementation(libs.material)
implementation(libs.androidx.activity)
implementation(libs.androidx.constraintlayout)

// Firebase
implementation(libs.firebase.auth)
implementation(libs.firebase.firestore)
implementation(libs.firebase.storage)
implementation(libs.firebase.crashlytics.buildtools)

// Media
implementation(libs.androidx.media3.exoplayer)

// Third-party
implementation("de.hdodenhof:circleimageview:3.1.0")
implementation("com.squareup.picasso:picasso:2.71828")

// Testing
testImplementation(libs.junit)
androidTestImplementation(libs.androidx.junit)
androidTestImplementation(libs.androidx.espresso.core)
```

## Build Configuration Highlights

- **Namespace:** `com.faa.cmsportalcui`
- **ViewBinding Enabled:** Yes
- **Google Services Plugin:** Applied for Firebase
- **Kotlin JVM Target:** `1.8`
- **Java Compatibility:** `1.8`

## Future Improvements (Suggestions)
- Push notifications via FCM
- In-app chat between user & staff
- Complaint rating system
- PDF report generation for completed tasks
- Dark mode support

## Author Info
Project developed for academic/demo purposes under the name **CMS Portal**.
