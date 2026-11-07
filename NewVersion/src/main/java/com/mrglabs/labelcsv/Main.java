package com.mrglabs.labelcsv;

import javax.swing.SwingUtilities;

/**
 * Program entry point — the first method Java runs.
 *
 * <p>Schneider Prize 2026 · MRG Laboratories · Label → 7-column CSV
 *
 * <p>This class stays tiny on purpose: it only starts the Swing window.
 * All the real work lives in other classes with beginner-friendly comments.
 *
 * <p><b>Run:</b>
 * <pre>
 *   mvn -q compile exec:java
 * </pre>
 *
 * <p><b>Local AI:</b> Ollama must be running with a small vision model
 * (default {@code moondream}) at {@code http://127.0.0.1:11434}.
 * If Ollama is offline, you can still load demo fields and save a CSV.
 */
public class Main {

    /**
     * Standard Java starting place. {@code args} are unused for now.
     *
     * @param args command-line arguments from the shell (we ignore them)
     */
    public static void main(String[] args) {
        // Swing rule: build windows on the Event Dispatch Thread (EDT),
        // not on the plain main thread. invokeLater schedules that safely.
        SwingUtilities.invokeLater(() -> {
            LabelCsvFrame frame = new LabelCsvFrame();
            frame.setVisible(true);
        });
    }
}
