# 🏦 Bank Management System

A robust Java application designed to manage bank accounts and financial transactions with persistent JSON storage.

## 📀 Core Features

* **Account Management**: Create, delete, and query account balances effortlessly.
* **Transactions**: Full support for transfers (`Transfer`) and interest-based payments (`Payment`).
* **Persistence**: Automatic saving and loading of account data in JSON format via **GSON**.
* **Business Logic**: Integrated validation, sorting, and filtering of all transaction histories.

## 🏗️ Architecture

* **`PrivateBank`**: The heart of the system, handling central logic and file I/O.
* **`Transaction`**: Abstract base class defining the blueprint for all bookings.
* **`Payment` / `Transfer**`**: Specialized implementations for versatile financial scenarios.
* **`TransactionSerial`**: Custom serialization logic for clean JSON data structures.

## ⚙️ Setup & Quality

* ☕ **Requirements**: Java 17+ and the GSON library.
* ✅ **Testing**: Comprehensive JUnit 5 test suites are included to ensure system reliability.