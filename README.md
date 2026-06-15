# Football Player Management System

Java console application for the PRO192-style football player management project.

## Features

- Manage players: add, update, deactivate, view, search, view details.
- Manage training and matches: create sessions, record attendance with snapshot overwrite rules, create matches, add or replace player performance.
- Salary management: player type display, monthly performance points, bonus, salary summary.
- Reports: salary summary by month/year and all-time top goal scorers.
- File I/O only. No database, GUI, web app, or external framework.

## Build

Run from this project folder:

```bash
javac -d out $(find src -name "*.java")
```

## Run

```bash
java -cp out football.Main
```

By default, data is loaded from and saved to the `data` folder. You can pass another data folder if needed:

```bash
java -cp out football.Main /path/to/data
```

## Data Files

The saved files are readable text files:

- `data/players.txt`
- `data/training_sessions.txt`
- `data/attendance_records.txt`
- `data/match_records.txt`
- `data/performance_records.txt`

Data is written to files when choosing `Save and Exit` from the Exit menu.
