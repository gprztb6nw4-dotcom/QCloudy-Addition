package cloudy.autume.addition.config;

/** Client-tick bridge for the optional, read-only cross-mod capability scan. */
public final class IntegrationScanService {
    private IntegrationScanService() { }

    public static void tick() {
        // Scans are started only by an explicit confirmation screen. Restoring
        // an enabled master switch after launch must never start one silently.
        UnifiedModIntegration.tickScan();
    }
}
