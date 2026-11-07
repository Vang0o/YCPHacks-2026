# MRG Label → CSV

**Schneider Prize for Technology Innovation 2026 · MRG Laboratories**

Java + Maven + Swing desktop app. Pick a Small/Large sample-tube label photo,
read it with a **small local** vision model (Ollama · **moondream**), edit the
fields, and save a user-named CSV with **exactly seven columns**.

## Stack

| Piece | Choice | Where it runs |
| --- | --- | --- |
| Language | Java 21 + Maven | Your computer |
| UI | Swing | Desktop window |
| Label reading | Ollama + `moondream` (~1.7 GB) | **Local** `127.0.0.1:11434` only |
| Cloud OCR? | **No** | — |

### Model size (laptop-friendly)

- **moondream** ≈ 1.8B-class / **~1.7 GB** download / typically **2–4 GB RAM**
- Install once, then works offline

## Setup

```bash
# 1) Java 21+ and Maven
java -version
mvn -version

# 2) Local AI runtime + small vision model
#    Install from https://ollama.com then:
ollama serve
ollama pull moondream
```

## Run

```bash
cd mrg-label-csv
mvn -q compile exec:java
```

Needs a display (Swing GUI).

## How to use

1. Choose **Small tube** or **Large tube**
2. **Pick / upload label image…**
3. **Read with local AI** (or **Load demo fields** if Ollama is offline)
4. Edit any of the 7 fields
5. **Add row to batch** (repeat for more labels)
6. **Save CSV…** and choose the file name

## CSV columns (only these 7)

| Column | Small | Large |
| --- | --- | --- |
| Sample ID | Circled Num + GS/NGS | Barcode or Num |
| Date Sampled | Date | Date |
| Date Received | Today | Today |
| Machine | Equip ID | SN |
| Component | Sample Point | *(blank)* |
| Serial Number | *(blank)* | *(blank)* |
| Reference Lube | Ref Lube | RN |

## Project map (beginners)

```
pom.xml
README.md
src/main/java/com/mrglabs/labelcsv/
  Main.java              starts the app
  LabelCsvFrame.java     Swing window
  LabelSize.java         SMALL / LARGE
  SampleRow.java         seven cells
  CsvColumns.java        header names
  FieldMapper.java       AI text → CSV mapping
  LocalVisionClient.java HTTP to local Ollama
  CsvWriter.java         writes the .csv file
src/test/java/.../FieldMapperTest.java
```

Every class has plain-English comments.

## Tests

```bash
mvn test
```

## Author

Hammond Broiles · MRG-Labs2026
