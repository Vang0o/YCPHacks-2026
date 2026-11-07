package com.mrglabs.labelcsv;

/**
 * Which physical sample-tube label we are reading.
 *
 * <p>An {@code enum} is Java's way of listing a fixed set of named choices.
 * Here the only legal values are {@link #SMALL} and {@link #LARGE}.
 *
 * <p>Mapping rules differ by size (see {@link FieldMapper}).
 */
public enum LabelSize {

    /**
     * Small tube:
     * Sample ID = Circled Num + GS/NGS;
     * Machine = Equip ID;
     * Component = Sample Point;
     * Reference Lube = Ref Lube;
     * Serial Number stays blank.
     */
    SMALL,

    /**
     * Large tube:
     * Sample ID = Barcode or Num;
     * Machine = SN;
     * Component blank;
     * Reference Lube = RN;
     * Serial Number stays blank.
     */
    LARGE
}
