import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.BitSet;

public class UniqueIPCounter {
    // Constants for IP address conversion
    private static final long MAX_IP_COUNT = (1L << 32); // 2^32 possible IP addresses

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java UniqueIPCounter <file path>");
            System.exit(1);
        }

        String filePath = args[0];

        try {
            long uniqueCount = countUniqueIPs(filePath);
            System.out.println("Number of unique IP addresses: " + uniqueCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long countUniqueIPs(String filePath) throws IOException {
        BitSet bitSet = new BitSet((int) MAX_IP_COUNT);
        long uniqueCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                long ipAsLong = ipToLong(line.trim());
                if (!bitSet.get((int) ipAsLong)) {
                    bitSet.set((int) ipAsLong);
                    uniqueCount++;
                }
            }
        }

        return uniqueCount;
    }

    private static long ipToLong(String ipAddress) {
        String[] octets = ipAddress.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result |= (Long.parseLong(octets[i]) << (8 * (3 - i)));
        }
        return result;
    }
}
