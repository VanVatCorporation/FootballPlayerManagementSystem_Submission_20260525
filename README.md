# ⚽ Football Player Management System

A vibrant Java console app for managing football players, training sessions, matches, and salary reports — built for PRO192-style projects.

## ✨ Highlights

- ✅ Add, update, deactivate, view, and search players
- 🏋️ Manage training sessions and attendance with smart overwrite logic
- ⚽ Create match records and track player performance
- 💰 Generate salary summaries: monthly points, bonuses, and pay totals
- 📊 Run reports for salary history and all-time top scorers
- 🗄️ File-based storage only — no database, GUI, or external frameworks

## 🚀 Build

From the project root:

```bash
javac -d out $(find src -name "*.java")
```

## ▶️ Run

```bash
java -cp out football.Main
```

## 📁 Optional data path

Default data folder:

```bash
java -cp out football.Main /path/to/data
```

## 📝 Data files

Saved files are plain text and easy to inspect:

- `data/players.txt`
- `data/training_sessions.txt`
- `data/attendance_records.txt`
- `data/match_records.txt`
- `data/performance_records.txt`

Data is saved when you choose `Save and Exit` from the menu.
