package com.mrglabs.labelcsv;

/**
 * The prize CSV schema — <b>exactly these seven columns</b>, in this order.
 *
 * <p>We keep the names in one place so the UI, the mapper, and the file writer
 * cannot drift apart. Beginners: if the schema ever changes, edit HERE only.
 */
public final class CsvColumns {

    /** Fixed header names written to every CSV file. */
    public static final String[] NAMES = {
            "Sample ID",
            "Date Sampled",
            "Date Received",
            "Machine",
            "Component",
            "Serial Number",
            "Reference Lube"
    };

    /** How many columns we always export (handy for loops). */
    public static final int COUNT = NAMES.length;

    /**
     * Private constructor — this class only holds constants.
     * Making the constructor private stops anyone from writing {@code new CsvColumns()}.
     */
    private CsvColumns() {
    }
}
