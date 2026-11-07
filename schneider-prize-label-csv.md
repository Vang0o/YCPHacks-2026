# Schneider Prize — GitHub-ready trial package

## Zip (grab this)

- `/agent/mrg-label-csv-github.zip`
- `/cursor/stores/self/docs/mrg-label-csv-github.zip` (Context copy)

## Inside

```
mrg-label-csv/
  .gitignore
  README.md
  pom.xml
  src/main/java/com/mrglabs/labelcsv/Main.java
  .git/   (one commit on main — ready to push)
```

No `target/`, no secrets. Local AI OCR is TODO.

## Run after unzip

```bash
cd mrg-label-csv
mvn -q compile exec:java
```

## Push to a new GitHub repo

```bash
# Option A — GitHub CLI (logged in)
cd mrg-label-csv
gh repo create mrg-label-csv --public --source=. --remote=origin --push

# Option B — create empty repo on github.com, then:
cd mrg-label-csv
git remote add origin https://github.com/YOUR_USER/mrg-label-csv.git
git push -u origin main
```
