package com.mrglabs.labelcsv;

/**
 * One row of lab data — the seven CSV cells as plain strings.
 *
 * <p>Empty label fields stay empty ({@code ""}), never {@code null}.
 * That way the CSV writer never prints the word "null".
 */
public class SampleRow {

    private String sampleId = "";
    private String dateSampled = "";
    private String dateReceived = "";
    private String machine = "";
    private String component = "";
    private String serialNumber = "";
    private String referenceLube = "";

    /** Create a blank row (all seven cells empty). */
    public SampleRow() {
    }

    /**
     * Return the seven values in CSV column order.
     * Index 0 = Sample ID … index 6 = Reference Lube.
     */
    public String[] toArray() {
        return new String[] {
                sampleId,
                dateSampled,
                dateReceived,
                machine,
                component,
                serialNumber,
                referenceLube
        };
    }

    /**
     * Fill this row from seven text boxes (same order as {@link CsvColumns#NAMES}).
     *
     * @param values length must be {@link CsvColumns#COUNT}
     */
    public void fromArray(String[] values) {
        // Guard clause: refuse bad input early with a clear message
        if (values == null || values.length != CsvColumns.COUNT) {
            throw new IllegalArgumentException("Expected " + CsvColumns.COUNT + " values");
        }
        setSampleId(values[0]);
        setDateSampled(values[1]);
        setDateReceived(values[2]);
        setMachine(values[3]);
        setComponent(values[4]);
        setSerialNumber(values[5]);
        setReferenceLube(values[6]);
    }

    // ---- getters / setters ----
    // Beginners: getters read a field; setters write a field (with cleanup).

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = clean(sampleId);
    }

    public String getDateSampled() {
        return dateSampled;
    }

    public void setDateSampled(String dateSampled) {
        this.dateSampled = clean(dateSampled);
    }

    public String getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(String dateReceived) {
        this.dateReceived = clean(dateReceived);
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = clean(machine);
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = clean(component);
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = clean(serialNumber);
    }

    public String getReferenceLube() {
        return referenceLube;
    }

    public void setReferenceLube(String referenceLube) {
        this.referenceLube = clean(referenceLube);
    }

    /** Turn null into "" and trim spaces so CSV cells stay tidy. */
    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
