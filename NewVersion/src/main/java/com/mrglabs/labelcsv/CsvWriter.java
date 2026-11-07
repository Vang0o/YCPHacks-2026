package com.mrglabs.labelcsv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Writes the downloadable CSV file with <b>exactly</b> {@link CsvColumns#NAMES}.
 *
 * <p>You can save one row or many rows (a "batch" of samples).
 */
public class CsvWriter {

    private final List<SampleRow> rows = new ArrayList<>();

    /** How many rows are waiting to be saved. */
    public int size() {
        return rows.size();
    }

    /** Add one reviewed sample to the batch. */
    public void add(SampleRow row) {
        rows.add(row);
    }

    /** Remove every row (Clear batch button). */
    public void clear() {
        rows.clear();
    }

    /** Snapshot of current rows (for the on-screen table). */
    public List<SampleRow> getRows() {
        return List.copyOf(rows);
    }

    /**
     * Write header + all rows to {@code file}.
     *
     * @param file destination, usually ending in {@code .csv}
     */
    public void write(File file) throws IOException {
        // try-with-resources = automatically close the writer when done
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(String.join(",", CsvColumns.NAMES));
            writer.write("\n");
            for (SampleRow row : rows) {
                String[] cells = row.toArray();
                for (int i = 0; i < cells.length; i++) {
                    if (i > 0) {
                        writer.write(",");
                    }
                    writer.write(escape(cells[i]));
                }
                writer.write("\n");
            }
        }
    }

    /**
     * CSV rule: if a cell has a comma, quote, or newline, wrap it in quotes
     * and double any quotes inside.
     */
    static String escape(String value) {
        String v = value == null ? "" : value;
        if (v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r")) {
            return "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }
}
