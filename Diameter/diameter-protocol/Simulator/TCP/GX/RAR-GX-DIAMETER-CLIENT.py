import socket
import struct

# === Diameter Constants ===
DIAMETER_VERSION = 1
DIAMETER_COMMAND_CODE_CER = 257
DIAMETER_COMMAND_CODE_DWR = 280
DIAMETER_COMMAND_CODE_DWA = 280
DIAMETER_COMMAND_CODE_RAR = 258  # Re-Auth-Request
DIAMETER_COMMAND_CODE_RAA = 258  # Re-Auth-Answer
DIAMETER_APPLICATION_ID = 16777238  # Gx
DIAMETER_FLAG_REQUEST = 0x80
DIAMETER_FLAG_RESPONSE = 0x00

# === Identity ===
ORIGIN_HOST = "pravin.diameter.com"
ORIGIN_REALM = "local"

# === AVP Builder ===
def build_avp(code, flags, data, vendor_id=None):
    avp_header = struct.pack("!I", code)
    if vendor_id is not None:
        flags |= 0x80  # Vendor-Specific flag

    data_bytes = data if isinstance(data, bytes) else data.encode()
    length = 8 + len(data_bytes)
    if vendor_id is not None:
        length += 4

    padded_length = (length + 3) & ~0x03
    avp_header += struct.pack("!B", flags)
    avp_header += struct.pack("!I", length)[1:]

    if vendor_id is not None:
        avp_header += struct.pack("!I", vendor_id)

    avp = avp_header + data_bytes
    avp += b'\x00' * (padded_length - len(avp))
    return avp

# === Header Builder ===
def build_diameter_header(command_code, flags, app_id, hbh_id, ete_id, payload_len):
    version = DIAMETER_VERSION
    length = 20 + payload_len
    header = struct.pack("!B", version)
    header += struct.pack("!I", length)[1:]
    header += struct.pack("!B", flags)
    header += struct.pack("!I", command_code)[1:]
    header += struct.pack("!I", app_id)
    header += struct.pack("!I", hbh_id)
    header += struct.pack("!I", ete_id)
    return header

# === Parse Header ===
def parse_diameter_header(data):
    if len(data) < 20:
        return None
    return {
        "version": data[0],
        "length": int.from_bytes(data[1:4], "big"),
        "flags": data[4],
        "command_code": int.from_bytes(data[5:8], "big"),
        "application_id": int.from_bytes(data[8:12], "big"),
        "hop_by_hop": int.from_bytes(data[12:16], "big"),
        "end_to_end": int.from_bytes(data[16:20], "big"),
    }

# === Build CER AVPs ===
def build_cer_avps():
    avps = b""
    avps += build_avp(264, 0x40, ORIGIN_HOST)  # Origin-Host
    avps += build_avp(296, 0x40, ORIGIN_REALM)  # Origin-Realm
    avps += build_avp(266, 0x40, struct.pack("!I", 10415))  # Vendor-ID
    avps += build_avp(269, 0x00, "PythonClient")  # Product-Name
    avps += build_avp(267, 0x40, struct.pack("!I", 1))  # Firmware-Revision
    avps += build_avp(278, 0x40, struct.pack("!I", 4))  # Auth-Application-Id
    return avps

# === Build DWA ===
def build_dwa(hbh_id, ete_id):
    avps = b""
    avps += build_avp(264, 0x40, ORIGIN_HOST)  # Origin-Host
    avps += build_avp(296, 0x40, ORIGIN_REALM)  # Origin-Realm
    header = build_diameter_header(
        command_code=DIAMETER_COMMAND_CODE_DWA,
        flags=DIAMETER_FLAG_RESPONSE,
        app_id=0,
        hbh_id=hbh_id,
        ete_id=ete_id,
        payload_len=len(avps)
    )
    return header + avps

# === Build RAR ===
def build_rar(hbh_id, ete_id, session_id="GX-1300001", subscriber_id="AN0001"):
    avps = b""
    avps += build_avp(263, 0x40, session_id)  # Session-Id
    avps += build_avp(264, 0x40, ORIGIN_HOST)
    avps += build_avp(296, 0x40, ORIGIN_REALM)
    avps += build_avp(293, 0x40, "127.0.0.1")  # Destination-Host
    avps += build_avp(283, 0x40, "local")      # Destination-Realm

    # Re-Auth-Request-Type AVP (285)
    avps += build_avp(285, 0x40, struct.pack("!I", 0))  # AUTHORIZE_ONLY

    # Subscription-Id (Grouped)
    sub1 = build_avp(450, 0x40, struct.pack("!I", 0)) + build_avp(444, 0x40, subscriber_id)
    avps += build_avp(443, 0x40, sub1)

    event_trigger_value = 2  # e.g., QOS_CHANGE
    avps += build_avp(
        1006,              # AVP Code
        0x40,              # Flags (will be OR'ed with 0x80 in build_avp for vendor)
        struct.pack("!I", event_trigger_value),
        vendor_id=10415    # Vendor-Specific ID
    )

    header = build_diameter_header(
        command_code=DIAMETER_COMMAND_CODE_RAR,
        flags=DIAMETER_FLAG_REQUEST,
        app_id=DIAMETER_APPLICATION_ID,
        hbh_id=hbh_id,
        ete_id=ete_id,
        payload_len=len(avps)
    )
    return header + avps

# === Main Client Function ===
def run_diameter_client(host='127.0.0.1', port=3869):
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        print(f"[+] Connecting to Diameter server at {host}:{port}...")
        sock.connect((host, port))

        # CER
        hbh = 0x01020304
        ete = 0x05060708
        cer_avps = build_cer_avps()
        cer_header = build_diameter_header(DIAMETER_COMMAND_CODE_CER, DIAMETER_FLAG_REQUEST, 0, hbh, ete, len(cer_avps))
        print("[>] Sending CER...")
        sock.sendall(cer_header + cer_avps)

        # Wait for CEA
        cea = sock.recv(4096)
        print("[<] Received CEA:", cea.hex())

        # Send RAR
        hbh += 1
        ete += 1
        rar = build_rar(hbh, ete)
        print("[>] Sending RAR...")
        sock.sendall(rar)

        # Main loop: Receive RAA or DWR
        while True:
            data = sock.recv(4096)
            if not data:
                print("[-] Connection closed by server.")
                break

            header = parse_diameter_header(data)
            if not header:
                continue

            print(f"[<] Diameter message: Code={header['command_code']} Flags={header['flags']:02x}")

            if header["command_code"] == DIAMETER_COMMAND_CODE_DWR and (header["flags"] & 0x80):
                print("[✓] Received DWR, replying with DWA...")
                dwa = build_dwa(header["hop_by_hop"], header["end_to_end"])
                sock.sendall(dwa)
                print("[>] DWA sent.")

            elif header["command_code"] == DIAMETER_COMMAND_CODE_RAA and not (header["flags"] & 0x80):
                print("[<] Received RAA (Re-Auth-Answer)")
                print(data.hex())

if __name__ == "__main__":
    run_diameter_client()
