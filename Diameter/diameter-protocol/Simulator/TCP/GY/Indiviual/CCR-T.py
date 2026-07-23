import socket
import struct
import time

# === Diameter Constants ===
DIAMETER_VERSION = 1
DIAMETER_COMMAND_CODE_CER = 257
DIAMETER_COMMAND_CODE_DWR = 280
DIAMETER_COMMAND_CODE_DWA = 280
DIAMETER_COMMAND_CODE_CCR = 272
DIAMETER_COMMAND_CODE_CCA = 272
DIAMETER_APPLICATION_ID = 4  # 4 for Gy
DIAMETER_FLAG_REQUEST = 0x80
DIAMETER_FLAG_RESPONSE = 0x00

# === Identity ===
ORIGIN_HOST = "demo.diameter.com"
ORIGIN_REALM = "demo"

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

# === Build CCR ===
def build_ccr(hbh_id, ete_id):
    avps = b""
    avps += build_avp(263, 0x40, "GY-1300001")  # Session-Id
    avps += build_avp(264, 0x40, ORIGIN_HOST)
    avps += build_avp(296, 0x40, ORIGIN_REALM)
    avps += build_avp(293, 0x40, "127.0.0.1")  # Destination-Host
    avps += build_avp(283, 0x40, "local")      # Destination-Realm

    # Vendor-Specific-Application-Id
    vsai = b""
    vsai += build_avp(266, 0x40, struct.pack("!I", 10415))  # Vendor-ID
    vsai += build_avp(258, 0x40, struct.pack("!I", 4))      # Auth-Application-Id
    avps += build_avp(260, 0x40, vsai)  # VSAI (Grouped)
    
    avps += build_avp(416, 0x40, struct.pack("!I", 3))  # CC-Request-Number
    avps += build_avp(415, 0x40, struct.pack("!I", 3))  # CC-Request-Type

    # Subscription-Id (Grouped)
    sub1 = build_avp(450, 0x40, struct.pack("!I", 0)) + build_avp(444, 0x40, "AN0001")
    avps += build_avp(443, 0x40, sub1)
    
    # MSCC
    used_unit = b""
    used_unit += build_avp(421, 0x40, struct.pack("!Q", 10000000))     # CC-Total-Octets
    used_unit += build_avp(414, 0x40, struct.pack("!Q", 0))     # CC-Output-Octets
    used_unit += build_avp(420, 0x40, struct.pack("!I", 100))   # CC-Time
    used_unit += build_avp(872, 0x40, struct.pack("!I", 0))     # Reporting-Reason
    used_service_unit = build_avp(446, 0x40, used_unit)

    mscc = b""
    mscc += build_avp(432, 0x40, struct.pack("!I", 1004))       # Rating-Group
    mscc += build_avp(439, 0x40, struct.pack("!I", 1004))       # Service-Identifier
    mscc += used_service_unit
    #avps += build_avp(456, 0x40, mscc)
    
    # --- PS-Information (874) ---
    ps_info = b''

    # 3GPP-Charging-Id (AVP 2, Vendor 10415)
    ps_info += build_avp(2, 0xC0, struct.pack("!I", 12345678), vendor_id=10415)

    # PDP-Address (AVP 1227, Vendor 10415)
    ps_info += build_avp(1227, 0xC0, b'\x00\x01\xc0\xa8\x01\x0a', vendor_id=10415)  
    # Example IP 192.168.1.10

    # Wrap PS-Information
    ps_information_avp = build_avp(874, 0xC0, ps_info, vendor_id=10415)

    # --- Service-Information (873) ---
    service_info = ps_information_avp

    avps += build_avp(873, 0xC0, service_info, vendor_id=10415)
    
    header = build_diameter_header(
        command_code=DIAMETER_COMMAND_CODE_CCR,
        flags=DIAMETER_FLAG_REQUEST,
        app_id=DIAMETER_APPLICATION_ID,
        hbh_id=hbh_id,
        ete_id=ete_id,
        payload_len=len(avps)
    )
    return header + avps

# === Build CER AVPs ===
def build_cer_avps():
    avps = b""
    avps += build_avp(264, 0x40, ORIGIN_HOST)  # Origin-Host
    avps += build_avp(296, 0x40, ORIGIN_REALM)  # Origin-Realm
    avps += build_avp(266, 0x40, struct.pack("!I", 10415))  # Vendor-ID
    avps += build_avp(269, 0x00, "PythonClient")  # Product-Name
    avps += build_avp(267, 0x40, struct.pack("!I", 1))  # Firmware-Revision
    avps += build_avp(278, 0x40, struct.pack("!I", 4))  # Origin-State-Id
    avps += build_avp(257, 0x40, struct.pack("!I", 0x0A03FC5E))  # Host-IP
    return avps

# === Main Client Function ===
def run_diameter_client(host='127.0.0.1', port=3868):
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

        # Send CCR
        hbh += 1
        ete += 1
        ccr = build_ccr(hbh, ete)
        print("[>] Sending CCR Terminate Request...")
        sock.sendall(ccr)

        # Main loop: Receive CCA or DWR
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
                print("[✓] Received DWR (Watchdog Request), replying with DWA...")
                dwa = build_dwa(header["hop_by_hop"], header["end_to_end"])
                sock.sendall(dwa)
                print("[>] DWA sent.")

            elif header["command_code"] == DIAMETER_COMMAND_CODE_CCA and not (header["flags"] & 0x80):
                print("[<] Received CCA (Credit-Control-Answer)")
                print(data.hex())

if __name__ == "__main__":
    run_diameter_client()