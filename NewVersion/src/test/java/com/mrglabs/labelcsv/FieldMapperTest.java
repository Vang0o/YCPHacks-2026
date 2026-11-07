package com.mrglabs.labelcsv;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests check FieldMapper without opening a window.
 * Run: {@code mvn test}
 */
class FieldMapperTest {

    private final FieldMapper mapper = new FieldMapper();

    @Test
    void mapsSmallTube() {
        String text = """
                CIRCLED_NUM: 1842
                GS_OR_NGS: GS
                DATE: 10/28/2026
                EQUIP_ID: EQ-7741
                SAMPLE_POINT: GEARBOX-IN
                REF_LUBE: ISO-VG-220
                """;
        SampleRow row = mapper.map(text, LabelSize.SMALL);
        assertEquals("1842 GS", row.getSampleId());
        assertEquals("2026-10-28", row.getDateSampled());
        assertEquals("EQ-7741", row.getMachine());
        assertEquals("GEARBOX-IN", row.getComponent());
        assertEquals("", row.getSerialNumber());
        assertEquals("ISO-VG-220", row.getReferenceLube());
    }

    @Test
    void mapsLargeTube() {
        String text = """
                BARCODE_OR_NUM: 9823410567
                DATE: 11/02/2026
                SN: SN-A91K2
                RN: RN-4481
                """;
        SampleRow row = mapper.map(text, LabelSize.LARGE);
        assertEquals("9823410567", row.getSampleId());
        assertEquals("SN-A91K2", row.getMachine());
        assertEquals("", row.getComponent());
        assertEquals("", row.getSerialNumber());
        assertEquals("RN-4481", row.getReferenceLube());
    }
}
