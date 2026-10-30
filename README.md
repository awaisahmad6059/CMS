# CMS Portal - Complaint Management System

A Kotlin-based Android application for managing complaints, maintenance requests, equipment requests, and staff assignments. This app follows a role-based system with **Admin**, **Staff**, and **User** panels.

## 📱 About Project

CMS Portal is designed to streamline the process of raising, assigning, and resolving complaints/maintenance tasks. Users can submit requests, Staff can update their task status, and Admin can monitor, assign, approve/reject, and manage the entire system from one place.

## ✨ Features

### 👤 User Side
- **Authentication** - User registration & login
- **Submit Complaints/Requests** - Raise maintenance or service requests
- **Track Requests** - View status of submitted requests
- **Feedback** - Give feedback to admin/staff
- **Profile Management** - View & update profile

### 🛠️ Staff Side
- **Staff Dashboard** - View assigned tasks at a glance
- **Task Management** - Update task status (Pending, In-Progress, Completed, Paused)
- **Task Details** - View complete details of assigned complaints/maintenance requests
- **Profile & Settings** - Manage staff account

### 🧑‍💼 Admin Side
- **Admin Dashboard** - Overview of users, staff, requests & tasks
- **User Management** - View, manage and monitor registered users
- **Staff Management** - Add/view staff details and assign tasks
- **Maintenance Requests** - View all maintenance requests with full details
- **Equipment Requests** - Approve/Reject equipment requests
- **Task Assignment** - Assign workers/staff to pending requests
- **Complete/Pause Tasks** - Monitor completed and paused tasks
- **Feedback Management** - View user feedback
- **Notifications** - Send notifications to users/staff
- **Settings** - Profile, Change Password, General & Security Settings

## 🛠️ Tech Stack

| Technology | Usage |
|-------------|-------|
| **Kotlin** | Primary programming language |
| **Android SDK** | Native Android Development |
| **XML** | UI Design & Layouts |
| **ViewBinding** | Type-safe view access |
| **Firebase Authentication** | User/Staff/Admin Login & Signup |
| **Firebase Firestore** | Real-time database for requests, users, tasks |
| **Firebase Storage** | Image/Profile picture storage |
| **Material Design Components** | Modern UI elements |
| **Picasso** | Image loading & caching |
| **CircleImageView** | Circular profile images |
| **AndroidX** | Core Android libraries |

## 🗂️ Project Structure

```text
app/src/main/java/com/faa/cmsportalcui/
├── AdminAdapter/      # Adapters for Admin lists
├── AdminFragment/     # Admin dashboard fragments
├── AdminModel/        # Data models for Admin
├── AdminSide/         # All Admin activities
├── Authentication/    # Login, SignUp, Welcome screens
├── StaffAdapter/      # Adapters for Staff
├── StaffFragment/     # Staff fragments
├── StaffModel/        # Staff models
├── StaffSide/         # Staff activities
├── UserAdapter/       # Adapters for User
├── UserFragment/      # User fragments
├── UserModel/         # User models
└── UserSide/          # User activities
```

## ⚙️ Requirements

- Android Studio (Latest Stable Version Recommended)
- JDK 8+
- Android SDK 23 (Min) to 36 (Compile)
- Firebase Project with `google-services.json`
- Internet Connection

## 🚀 How to Run Locally

1. **Clone the Repository**
   ```bash
   git clone https://github.com/awaisahmad6059/CMS.git
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select `File > Open` and choose the cloned `CMS` folder

3. **Setup Firebase**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project or use existing one
   - Add Android app with package name: `com.faa.cmsportalcui`
   - Download `google-services.json` and place it inside `app/` directory

4. **Sync & Run**
   - Sync Gradle files
   - Connect an emulator or physical device
   - Click `Run > Run 'app'`

## 📄 App Info

- **Package Name:** `com.faa.cmsportalcui`
- **Version Code:** `1`
- **Version Name:** `1.0`
- **Min SDK:** `23 (Android 6.0+)`
- **Target SDK:** `34`
- **Compile SDK:** `36`

## 📝 Note

This project uses Firebase for Authentication, Firestore Database & Storage. Make sure to configure proper Firebase rules before using in production.
