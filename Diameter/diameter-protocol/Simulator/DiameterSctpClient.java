import com.sun.nio.sctp.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class DiameterSctpClient {

    public static void main(String[] args) throws Exception {
        String serverIP = "127.0.0.1";  // SCTP server IP
        int serverPort = 3869;          // Diameter SCTP port

        try (SctpChannel sctpChannel = SctpChannel.open()) {

            // Connect to SCTP server
            InetSocketAddress serverAddr = new InetSocketAddress(serverIP, serverPort);
            sctpChannel.connect(serverAddr);
            System.out.println("Connected to SCTP Diameter server: " + serverIP + ":" + serverPort);

            // Build CEA Diameter message
            byte[] ceaMessage = buildCeaMessage();

            // Send message
            ByteBuffer sendBuffer = ByteBuffer.wrap(ceaMessage);
            MessageInfo messageInfo = MessageInfo.createOutgoing(null, 0);
            sctpChannel.send(sendBuffer, messageInfo);
            System.out.println("CEA Diameter message sent.");

            // --- Wait for server answer ---
            ByteBuffer recvBuffer = ByteBuffer.allocate(4096); // Adjust size if needed
            System.out.println("Waiting for Diameter answer from server...");

            MessageInfo receivedInfo = sctpChannel.receive(recvBuffer, null, null); // Blocking receive
            recvBuffer.flip();
            byte[] receivedBytes = new byte[recvBuffer.remaining()];
            recvBuffer.get(receivedBytes);
            System.out.println("Received Diameter answer (hex): " + bytesToHex(receivedBytes));

            // Optionally: parse first byte (version) and command code
            if (receivedBytes.length >= 4) {
                int version = receivedBytes[0] & 0xFF;
                int length = ((receivedBytes[1] & 0xFF) << 16) |
                             ((receivedBytes[2] & 0xFF) << 8) |
                             (receivedBytes[3] & 0xFF);
                System.out.println("Diameter version: " + version + ", Message length: " + length);
            }
			try {
				Thread.sleep(90000); // milliseconds, e.g., 90 seconds
			} catch (InterruptedException e) {
				e.printStackTrace();
			}


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Build CEA Diameter message ---
    private static byte[] buildCeaMessage() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // Diameter Header
        buffer.put((byte) 0x01); // Version = 1
        buffer.put((byte)0x00);  // Placeholder length
        buffer.put((byte)0x00);
        buffer.put((byte)0x00);
        buffer.put((byte)0x00);  // Flags = 0x00 for Answer
        int commandCode = 257;   // CEA
        buffer.put((byte)((commandCode >> 16) & 0xFF));
        buffer.put((byte)((commandCode >> 8) & 0xFF));
        buffer.put((byte)(commandCode & 0xFF));
        buffer.putInt(0x00000000); // Application-ID
        buffer.putInt(0x00000001); // Hop-by-Hop
        buffer.putInt(0x00000001); // End-to-End

        // AVPs
        String sessionId = "client.example.com;" + System.currentTimeMillis();
        putAvp(buffer, 263, sessionId);             // Session-Id
        putAvp(buffer, 264, "pravin.diameter.com"); // Origin-Host
        putAvp(buffer, 296, "local");               // Origin-Realm
        putAvp(buffer, 266, intToBytes(10415));    // Vendor-Id
        putAvp(buffer, 269, "MyDiameterClient");   // Product-Name

        // Update message length
        int messageLength = buffer.position();
        buffer.put(1, (byte)((messageLength >> 16) & 0xFF));
        buffer.put(2, (byte)((messageLength >> 8) & 0xFF));
        buffer.put(3, (byte)(messageLength & 0xFF));

        // Return message bytes
        byte[] msg = new byte[messageLength];
        buffer.flip();
        buffer.get(msg);
        return msg;
    }

    private static void putAvp(ByteBuffer buffer, int code, String value) {
        byte[] valBytes = value.getBytes(StandardCharsets.UTF_8);
        int length = 8 + valBytes.length;
        buffer.putInt(code);
        buffer.putInt(length);
        buffer.put(valBytes);
        int padding = (4 - (valBytes.length % 4)) % 4;
        for (int i = 0; i < padding; i++) buffer.put((byte)0x00);
    }

    private static void putAvp(ByteBuffer buffer, int code, byte[] value) {
        int length = 8 + value.length;
        buffer.putInt(code);
        buffer.putInt(length);
        buffer.put(value);
        int padding = (4 - (value.length % 4)) % 4;
        for (int i = 0; i < padding; i++) buffer.put((byte)0x00);
    }

    private static byte[] intToBytes(int value) {
        return new byte[] {
            (byte)((value >> 24) & 0xFF),
            (byte)((value >> 16) & 0xFF),
            (byte)((value >> 8) & 0xFF),
            (byte)(value & 0xFF)
        };
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02X ", b));
        return sb.toString();
    }
}
