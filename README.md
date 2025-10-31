# Personal Finance Manager Using Kotlin and Jetpack Compose

A comprehensive Android application for managing personal finances. Track income, expenses, budgets, and financial goals with a modern, intuitive interface built using Kotlin and Jetpack Compose.

## Features

### Core Functionality
- **Multi-Account Management** - Track multiple bank accounts, wallets, and etc.
- **Smart Categorization** - Organize income and expenses with custom categories
- **Transaction Tracking** - Record income, expenses, and transfers between accounts
- **Budget Management** - Set budgets with custom periods and track spending limits
- **Financial Reports** - Visualize financial data with comprehensive reports
- **Notifications** - Stay informed about financial activities and budget alerts
- **Flexible Settings** - Customize calendar type, currency, and financial month start date

### Advanced Features
- **Recurring Budgets** - Set up repeating budgets for regular expenses (e.g., loan payments)
- **Transfer Management** - Move money between your own accounts seamlessly
- **Custom Financial Month** - Define your own financial month start date
- **Multi-Currency Support** - Manage finances in different currencies
- **Calendar Flexibility** - Choose between different calendar types

## Technology Stack

- **Platform**: Android (API 24+)
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with Clean Architecture principles
- **Dependency Injection**: Hilt (Dagger)
- **Database**: Room (SQLite)
- **Async Operations**: Kotlin Coroutines & Flow
- **Data Storage**: DataStore (Preferences)
- **Networking**: Retrofit, OkHttp, Ktor Client
- **Build System**: Gradle with Kotlin DSL

## Project Structure

```
app/
├── data/
│   ├── api/              # API services (currency exchange)
│   ├── dao/              # Room DAOs
│   ├── datastore/        # DataStore implementations
│   └── repository/       # Repository implementations
├── di/                   # Dependency injection modules
├── model/                # Data models and entities
├── navigation/           # Navigation graph and screen definitions
├── service/              # Background services
├── ui/
│   ├── auth/             # Authentication screens
│   ├── components/       # Reusable UI components
│   └── theme/            # Theme configuration
├── util/                 # Utility classes
└── view/                 # Feature screens
    ├── accounts/
    ├── budgeting/
    ├── categories/
    ├── dashboard/
    ├── notifications/
    ├── reports/
    ├── settings/
    └── transactions/
```

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11 or higher
- Android SDK with API 24+

### Installation
1. Clone this repository
   ```bash
   git clone https://github.com/Erfan-Farahmandnejad/Personal-Finance-Manager-Using-Kotlin-and-Jetpack-Compose.git
   ```
2. Open the project in Android Studio
3. Sync Gradle dependencies
4. Build and run the application on your device or emulator

### First-Time Setup

#### 1. Authentication
- Launch the app and create your account via the Sign Up screen
- Sign in with your credentials

#### 2. Dashboard Overview
- After signing in, you will land on the Dashboard
- The navigation drawer provides access to all features:
  - Dashboard
  - Accounts
  - Categories
  - Transactions
  - Reports
  - Notifications
  - Settings
  - Budgets

#### 3. Initial Configuration

##### a. Add Accounts
Navigate to **Accounts** and add your financial accounts:
- Bank accounts
- Wallets
- Other

##### b. Create Categories
Set up **Categories** for better organization:
- **Expense Categories**: Food, Transportation, Entertainment, Utilities, Healthcare, etc.
- **Income Categories**: Salary, Freelance, Investments, Gifts, etc.

##### c. Configure Settings
Go to **Settings** to customize:
- **First Day of Financial Month**: Choose when your financial month starts
- **Calendar Type**: Select your preferred calendar system
- **Currency**: Set your primary currency

#### 4. Set Up Budgets
Navigate to **Budgets** to create spending limits:
- Choose an expense category
- Set the budget amount
- Define the time period with start and end dates
- Set number of repeats for recurring budgets (useful for loan payments, subscriptions)

## Usage

### Adding Transactions

1. Navigate to **Transactions**
2. Click on "Add Transaction"
3. Fill in the required details:
   - **Type**: Choose between Income, Expense, or Transfer
   - **Category**: Select the appropriate category (not required for transfers)
   - **Account**: Choose the source account
   - **Destination Account**: For transfers, select the destination account
   - **Amount**: Enter the transaction amount
   - **Date**: Select the transaction date
   - **Note**: Add optional notes for reference

### Transaction Types

- **Income**: Money coming into your account
- **Expense**: Money spent from your account
- **Transfer**: Moving money between your own accounts only (not to external accounts). This feature is designed for internal financial management, allowing you to track money movement between your personal accounts (e.g., from checking to savings, from cash to bank account)

### Monitoring Your Finances

#### Reports
- View financial reports
- Monitor budget performance

#### Notifications
- Stay updated on budget limits

## User Flow

```
Sign Up → Sign In → Dashboard
                        ↓
        ┌───────────────┴───────────────┐
        ↓                               ↓
    Setup Phase                    Daily Usage
        ↓                               ↓
1. Add Accounts              → Add Transactions
2. Create Categories         → View Reports
3. Configure Settings        → Check Notifications
4. Set Up Budgets            → Monitor Budgets
```

## Best Practices

1. **Start with Setup**: Complete the initial configuration (accounts, categories, settings) before adding transactions
2. **Regular Updates**: Record transactions promptly for accurate tracking
3. **Review Reports**: Check your financial reports regularly to understand spending patterns
4. **Budget Monitoring**: Keep an eye on budget notifications to stay within limits
5. **Categorize Properly**: Use appropriate categories for better financial insights

## Screenshots

The application includes the following screens:

### Authentication
- **Sign Up Screen**: User registration with account creation
- **Sign In Screen**: User authentication and login

### Main Application
- **Dashboard**: Overview of financial status, recent transactions, and quick statistics
- **Accounts Management**: View, add, edit, and manage multiple financial accounts
- **Categories**: Organize and manage income and expense categories
- **Transactions**: Record and view all financial transactions (income, expense, transfer)
- **Reports**: Visual analytics and insights into spending patterns and financial trends
- **Notifications**: Alerts and reminders for budget limits
- **Settings**: Customize app preferences including calendar type, currency, and financial month start date
- **Budgets**: Create and monitor spending limits with custom periods and recurring options

<img src="https://github.com/user-attachments/assets/7fc79436-39b3-4729-b22b-7ce4c2217d76" width="300">
<img src="https://github.com/user-attachments/assets/96814c5b-672d-4abb-958e-f5dc0a0aea56" width="300">
<img src="https://github.com/user-attachments/assets/3ac9152a-9d58-4fcd-b709-e51b46354083" width="300">
<img src="https://github.com/user-attachments/assets/530c5c11-8a12-4522-93b9-15d1e35a8b9c" width="300">
<img src="https://github.com/user-attachments/assets/65ba700d-78e5-491f-9b70-e60ba4b7602b" width="300">
<img src="https://github.com/user-attachments/assets/e1d8b827-f42c-47ce-b758-20be8e7acf0b" width="300">
<img src="https://github.com/user-attachments/assets/358e597e-5347-4d00-8562-65c0551fc8a1" width="300">
<img src="https://github.com/user-attachments/assets/23043fcc-d57b-4c75-8e47-d8619067d253" width="300">
<img src="https://github.com/user-attachments/assets/1c068561-7707-409b-8626-94b8f20ab6b3" width="300">
<img src="https://github.com/user-attachments/assets/94682744-2eaf-47e4-9829-4b47c1ca81d1" width="300">
<img src="https://github.com/user-attachments/assets/ed1fbccb-4902-4f1d-bf0a-a692e1275fe9" width="300">
<img src="https://github.com/user-attachments/assets/8a41153a-1b74-44a2-b2e7-41c350660ec2" width="300">
<img src="https://github.com/user-attachments/assets/43720f2c-0490-40cf-89ef-d192253812ee" width="300">
<img src="https://github.com/user-attachments/assets/20373154-b663-439b-af6c-e8bb9fb53c27" width="300">
<img src="https://github.com/user-attachments/assets/39eae99e-5e52-48e5-aa54-b4ff03745090" width="300">
<img src="https://github.com/user-attachments/assets/5968b5bd-f4a5-4dfe-8535-091264eeeb56" width="300">
<img src="https://github.com/user-attachments/assets/9087e42e-6c80-499f-9917-91985eebb84d" width="300">
<img src="https://github.com/user-attachments/assets/25dc91f3-49de-4582-b28a-47ee84e19927" width="300">
<img src="https://github.com/user-attachments/assets/03c55518-a627-47bc-a240-aee104160a7e" width="300">
<img src="https://github.com/user-attachments/assets/95586df7-c247-442f-802e-7d6549d7acd2" width="300">
<img src="https://github.com/user-attachments/assets/218151a1-b6e4-4968-a812-1e544476bf9d" width="300">
<img src="https://github.com/user-attachments/assets/a480d0d9-8b0a-40ae-883c-6b5b806b228c" width="300">




## Key Features Implementation

### Database Schema
- **Users**: User authentication and profile data
- **Accounts**: Financial account information
- **Categories**: Income and expense categories
- **Transactions**: Financial transactions with type, amount, date, and notes
- **Budgets**: Budget limits with start/end dates and repeat functionality
- **Notifications**: System notifications and alerts
- **Settings**: User preferences and app configuration

### Services
- **BudgetNotificationService**: Background service for monitoring budgets and sending alerts

## Contributors

* [Erfan-Farahmandnejad](https://github.com/Erfan-Farahmandnejad)
* [Shabnam-Khaqanpoor](https://github.com/Shabnam-Khaqanpoor)

