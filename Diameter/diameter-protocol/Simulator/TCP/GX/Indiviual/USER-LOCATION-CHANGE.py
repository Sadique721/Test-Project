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
DIAMETER_APPLICATION_ID = 16777238  # 16777238 for Gx
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
    avps += build_avp(263, 0x40, "GX-1300001")  # Session-Id
    avps += build_avp(264, 0x40, ORIGIN_HOST)
    avps += build_avp(296, 0x40, ORIGIN_REALM)
    avps += build_avp(293, 0x40, "127.0.0.1")  # Destination-Host
    avps += build_avp(283, 0x40, "local")      # Destination-Realm

    # Vendor-Specific-Application-Id
    vsai = b""
    vsai += build_avp(266, 0x40, struct.pack("!I", 10415))  # Vendor-ID
    vsai += build_avp(258, 0x40, struct.pack("!I", 4))      # Auth-Application-Id
    avps += build_avp(260, 0x40, vsai)  # VSAI (Grouped)
    
    avps += build_avp(416, 0x40, struct.pack("!I", 1))  # CC-Request-Number
    avps += build_avp(415, 0x40, struct.pack("!I", 1))  # CC-Request-Type
    
    # 3GPP-User-Location-Info
    uli = build_user_location_info(
        mcc="404",
        mnc="45",
        tac=12345,
        eci=987654321
    )

    # AVP 22
    avps += build_avp(
        1006,
        0x40,           # Mandatory + Vendor Specific
        struct.pack("!I", 13),
        vendor_id=10415 # 3GPP
    )
    
    # AVP 22
    avps += build_avp(
        22,
        0x40,           # Mandatory + Vendor Specific
        uli,
        vendor_id=10415 # 3GPP
    )
    
    header = build_diameter_header(
        command_code=DIAMETER_COMMAND_CODE_CCR,
        flags=DIAMETER_FLAG_REQUEST,
        app_id=DIAMETER_APPLICATION_ID,
        hbh_id=hbh_id,
        ete_id=ete_id,
        payload_len=len(avps)
    )
    return header + avps
    
def encode_plmn(mcc, mnc):
    """
    Encode MCC/MNC into 3-byte TBCD format.

    Example:
        MCC = 404
        MNC = 45
    """

    if len(mnc) == 2:
        mnc = mnc + "F"

    b1 = int(mcc[1] + mcc[0], 16)
    b2 = int(mnc[2] + mcc[2], 16)
    b3 = int(mnc[1] + mnc[0], 16)

    return bytes([b1, b2, b3])
    
def build_user_location_info(mcc, mnc, tac, eci):

    plmn = encode_plmn(mcc, mnc)

    data = bytearray()

    # TAI + ECGI present
    data.append(0x82)

    # TAI
    data.extend(plmn)
    data.extend(struct.pack("!H", tac))

    # ECGI
    data.extend(plmn)

    # ECGI is 28 bits
    eci = eci & 0x0FFFFFFF
    data.extend(struct.pack("!I", eci))

    return bytes(data)

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
        print("[>] Sending CCR Intial Request...")
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