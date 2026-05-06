import java.util.Arrays;

public class RobotAnimation {

    private static final String RESET   = "\u001B[0m";
    private static final String CYAN    = "\u001B[36m";
    private static final String GREEN   = "\u001B[32m";
    private static final String YELLOW  = "\u001B[33m";
    private static final String RED     = "\u001B[31m";
    private static final String BOLD    = "\u001B[1m";
    private static final String DIM     = "\u001B[2m";

    private static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private static String rep(char c, int n) {
        if (n <= 0) return "";
        char[] arr = new char[n];
        Arrays.fill(arr, c);
        return new String(arr);
    }

    private static final String CLEAR = "\r" + rep(' ', 85) + "\r";

    // ─── Startup: boot sequence → robot paints welcome text → waving exit ────────
    public static void playStartup() {
        System.out.println();

        // Phase 1: Boot spinner with checkmarks
        String[] spin   = {"|", "/", "-", "\\"};
        String[] labels = {
            "Initializing patient database",
            "Loading data structures      ",
            "All systems operational      "
        };
        int[] counts = {14, 12, 8};

        for (int b = 0; b < labels.length; b++) {
            for (int s = 0; s < counts[b]; s++) {
                System.out.print("\r  " + CYAN + "[" + spin[s % 4] + "]" + RESET
                    + "  " + DIM + labels[b] + "..." + RESET + "   ");
                System.out.flush();
                sleep(60);
            }
            System.out.println("\r  " + GREEN + BOLD + "[+]" + RESET
                + "  " + labels[b] + "   ");
        }
        System.out.println();

        // Phase 2: Robot walks right, text builds in its wake
        String   msg   = "WELCOME TO HOSPITAL MANAGEMENT SYSTEM!";
        int      width = msg.length();
        String[] legs  = {"-->", "==>", "-->"};

        for (int i = 0; i <= width; i++) {
            String behind = YELLOW + BOLD + msg.substring(0, i) + RESET;
            String ahead  = rep(' ', width - i);
            System.out.print(CLEAR + "  " + behind + CYAN + BOLD + "[^_^]" + RESET
                + legs[i % legs.length] + ahead);
            System.out.flush();
            sleep(36);
        }

        // Phase 3: Robot stays at the end and waves in place
        sleep(450);
        String[] waveFaces = {
            CYAN   + BOLD + "[^o^]" + RESET,
            YELLOW + BOLD + "[^O^]" + RESET,
            CYAN   + BOLD + "[^o^]" + RESET,
            GREEN  + BOLD + "[ ^ ]" + RESET,
            CYAN   + BOLD + "[^o^]" + RESET,
        };
        for (String face : waveFaces) {
            System.out.print(CLEAR + "  " + YELLOW + BOLD + msg + RESET + "  " + face);
            System.out.flush();
            sleep(220);
        }
        System.out.println();
        System.out.println();
    }

    // ─── Fetch: robot dispatched to hospital, carries patient back ───────────────
    public static void playFetchPatient(String patientName, boolean isEmergency) {
        int    width = 32;
        String robot = CYAN + BOLD + "[^_^]" + RESET;
        String alert = RED  + BOLD + "[!_!]" + RESET;
        String face  = isEmergency ? alert : robot;
        String fwd   = isEmergency ? RED + BOLD + "==>" + RESET : "-->";
        String bck   = isEmergency ? RED + BOLD + "<==" + RESET : "<--";
        String hosp  = CYAN + "[H]" + RESET;
        int    spd   = isEmergency ? 11 : 20;

        System.out.println();

        // Emergency: flashing header; normal: calm dispatch line
        if (isEmergency) {
            for (int f = 0; f < 6; f++) {
                String col = (f % 2 == 0) ? RED + BOLD : YELLOW + BOLD;
                System.out.print("\r  " + col + "!!! EMERGENCY DISPATCH !!!" + RESET + "          ");
                System.out.flush();
                sleep(160);
            }
            System.out.println("\r  " + RED + BOLD + "!!! EMERGENCY DISPATCH !!!" + RESET + "          ");
        } else {
            System.out.println("  " + CYAN + "Dispatching robot to fetch patient..." + RESET);
        }
        sleep(120);

        // Walk right toward hospital
        for (int i = 0; i <= width; i++) {
            String pad  = rep(' ', i);
            String tail = rep(' ', width - i + 3);
            System.out.print(CLEAR + "  " + pad + face + fwd + tail + hosp);
            System.out.flush();
            sleep(spd);
        }

        // Flash at hospital
        sleep(200);
        for (int f = 0; f < 4; f++) {
            String hs = (f % 2 == 0)
                ? (isEmergency ? RED + BOLD + "[H]!" + RESET : GREEN + BOLD + "[H]*" + RESET)
                : hosp + "   ";
            System.out.print(CLEAR + "  " + rep(' ', width) + face + fwd + "  " + hs + "   ");
            System.out.flush();
            sleep(180);
        }

        // Walk back left, carrying patient symbol
        String carried = isEmergency ? RED + BOLD + "[P]" + RESET : GREEN + "[P]" + RESET;
        for (int i = width; i >= 0; i--) {
            String pad = rep(' ', i);
            System.out.print(CLEAR + "  " + pad + bck + face + carried + rep(' ', 10));
            System.out.flush();
            sleep(spd);
        }

        // Completion message
        System.out.println();
        if (isEmergency) {
            System.out.println("  " + RED + BOLD + "[!_!] URGENT — Fetched: " + patientName + RESET);
        } else {
            System.out.println("  " + GREEN + BOLD + "[^_^]" + RESET
                + GREEN + " Done! Fetched: " + patientName + RESET);
        }
        System.out.println();
        sleep(150);
    }
}
