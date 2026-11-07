package com.mrglabs.labelcsv;

import java.time.LocalDate;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Turns free-form text from the local AI into a {@link SampleRow}
 * using the prize Small vs Large mapping rules.
 *
 * <p>Think of this class as a translator:
 * <pre>
 *   AI text  →  FieldMapper  →  seven CSV cells
 * </pre>
 */
public class FieldMapper {

    // Pattern = reusable text search. (?i) means "ignore UPPER/lower case".
    private static final Pattern KEY_LINE = Pattern.compile(
            "(?i)^(CIRCLED_NUM|GS_OR_NGS|DATE|EQUIP_ID|SAMPLE_POINT|REF_LUBE|"
                    + "BARCODE_OR_NUM|SN|RN)\\s*:\\s*(.*)$");

    /**
     * Map model output (or any label transcript) into the seven columns.
     *
     * @param modelText words returned by the local vision model
     * @param size      SMALL or LARGE mapping table
     * @return a filled {@link SampleRow} (Date Received = today)
     */
    public SampleRow map(String modelText, LabelSize size) {
        SampleRow row = new SampleRow();
        // Date Received is always "today" for the lab intake workflow
        row.setDateReceived(LocalDate.now().toString());

        if (modelText == null || modelText.isBlank()) {
            return row;
        }

        // Temporary buckets — we assemble Sample ID later
        String circledNum = "";
        String gsNgs = "";
        String barcode = "";
        String date = "";
        String equipId = "";
        String samplePoint = "";
        String refLube = "";
        String sn = "";
        String rn = "";

        // \\R matches any newline style (Windows \r\n or Unix \n)
        for (String raw : modelText.split("\\R")) {
            Matcher m = KEY_LINE.matcher(raw.trim());
            if (!m.matches()) {
                continue; // skip lines that are not KEY: value
            }
            String key = m.group(1).toUpperCase(Locale.ROOT);
            String val = m.group(2).trim();
            switch (key) {
                case "CIRCLED_NUM" -> circledNum = val;
                case "GS_OR_NGS" -> gsNgs = val;
                case "DATE" -> date = val;
                case "EQUIP_ID" -> equipId = val;
                case "SAMPLE_POINT" -> samplePoint = val;
                case "REF_LUBE" -> refLube = val;
                case "BARCODE_OR_NUM" -> barcode = val;
                case "SN" -> sn = val;
                case "RN" -> rn = val;
                default -> {
                    // ignore unknown keys
                }
            }
        }

        // Fallback scrapes if the model wrote plain sentences instead of KEY: value
        if (date.isBlank()) {
            date = first(modelText, "(?i)DATE\\s*[:.]?\\s*([0-9]{1,4}[/\\-.][0-9]{1,2}[/\\-.][0-9]{2,4})");
        }
        if (date.isBlank()) {
            date = first(modelText, "\\b(\\d{1,2}[/\\-]\\d{1,2}[/\\-]\\d{2,4})\\b");
        }
        row.setDateSampled(normalizeDate(date));

        if (size == LabelSize.LARGE) {
            if (barcode.isBlank()) {
                barcode = first(modelText, "(?i)BARCODE\\s*[:.]?\\s*([A-Z0-9\\-]+)");
            }
            if (barcode.isBlank()) {
                barcode = first(modelText, "\\b([0-9]{6,14})\\b");
            }
            if (sn.isBlank()) {
                sn = first(modelText, "(?i)\\bS\\.?N\\.?\\s*[:.]?\\s*([A-Z0-9\\-_/]+)");
            }
            if (rn.isBlank()) {
                rn = first(modelText, "(?i)\\bRN\\s*[:.]?\\s*([A-Z0-9\\-_/]+)");
            }
            row.setSampleId(barcode);
            row.setMachine(sn);
            row.setComponent("");      // Large: leave blank
            row.setSerialNumber("");   // schema: blank for both sizes
            row.setReferenceLube(rn);
        } else {
            // SMALL — prefer digits for Circled Num (tiny models often garble "NUM")
            if (circledNum.isBlank() || !circledNum.matches(".*\\d.*")) {
                String fromGs = first(gsNgs, "(\\d{2,6})");
                String fromText = first(modelText, "(?i)(?:NUM|NO\\.?|#)\\s*[:.]?\\s*(\\d{2,6})");
                String anyDigits = first(modelText, "\\b(\\d{2,6})\\b");
                if (!fromGs.isBlank()) {
                    circledNum = fromGs;
                } else if (!fromText.isBlank()) {
                    circledNum = fromText;
                } else if (!anyDigits.isBlank()) {
                    circledNum = anyDigits;
                }
            }
            // Keep only GS or NGS as the second Sample ID token
            String gsOnly = first(gsNgs + "\n" + modelText, "(?i)\\b(GS|NGS)\\b");
            if (!gsOnly.isBlank()) {
                gsNgs = gsOnly;
            }
            if (equipId.isBlank()) {
                equipId = first(modelText, "(?i)EQUIP(?:MENT)?\\s*ID\\s*[:.]?\\s*([A-Z0-9\\-_/]+)");
            }
            if (samplePoint.isBlank()) {
                samplePoint = first(modelText, "(?i)SAMPLE\\s*POINT\\s*[:.]?\\s*([A-Z0-9 \\-_/]+)");
            }
            if (refLube.isBlank()) {
                refLube = first(modelText, "(?i)REF\\.?\\s*LUBE\\s*[:.]?\\s*([A-Z0-9 \\-_/]+)");
            }
            // Sample ID = circled number + GS/NGS
            StringBuilder id = new StringBuilder();
            if (!circledNum.isBlank()) {
                id.append(circledNum);
            }
            if (!gsNgs.isBlank()) {
                if (!id.isEmpty()) {
                    id.append(' ');
                }
                id.append(gsNgs.toUpperCase(Locale.ROOT));
            }
            row.setSampleId(id.toString());
            row.setMachine(equipId);
            row.setComponent(samplePoint);
            row.setSerialNumber("");   // Small: leave blank
            row.setReferenceLube(refLube);
        }

        return row;
    }

    /**
     * Built-in demo row when Ollama is offline or the user clicks "Load demo".
     * Values match the prize mapping for the chosen size.
     */
    public SampleRow demo(LabelSize size) {
        SampleRow row = new SampleRow();
        row.setDateReceived(LocalDate.now().toString());
        if (size == LabelSize.LARGE) {
            row.setSampleId("9823410567");
            row.setDateSampled("2026-11-02");
            row.setMachine("SN-A91K2");
            row.setComponent("");
            row.setSerialNumber("");
            row.setReferenceLube("RN-4481");
        } else {
            row.setSampleId("1842 GS");
            row.setDateSampled("2026-10-28");
            row.setMachine("EQ-7741");
            row.setComponent("GEARBOX-IN");
            row.setSerialNumber("");
            row.setReferenceLube("ISO-VG-220");
        }
        return row;
    }

    /** First regex capturing-group match, or "" if nothing matched. */
    private static String first(String text, String regex) {
        Matcher m = Pattern.compile(regex).matcher(text);
        return m.find() ? m.group(1).trim() : "";
    }

    /**
     * Prefer YYYY-MM-DD in the CSV. If we cannot parse, keep the original text
     * so the user can still edit it by hand.
     */
    private static String normalizeDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String s = raw.trim();
        Matcher slash = Pattern.compile("^(\\d{1,2})[/\\-.](\\d{1,2})[/\\-.](\\d{2,4})$").matcher(s);
        if (!slash.matches()) {
            return s;
        }
        String mm = String.format("%02d", Integer.parseInt(slash.group(1)));
        String dd = String.format("%02d", Integer.parseInt(slash.group(2)));
        String yyyy = slash.group(3);
        if (yyyy.length() == 2) {
            int y = Integer.parseInt(yyyy);
            yyyy = (y > 70 ? "19" : "20") + String.format("%02d", y);
        }
        return yyyy + "-" + mm + "-" + dd;
    }
}
