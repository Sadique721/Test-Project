import socket
import struct
import time
import sctp   # <<< ADDED FOR SCTP SUPPORT >>>

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
    avps += build_avp(264, 0x40, ORIGIN_HOST)
    avps += build_avp(296, 0x40, ORIGIN_REALM)
    avps += build_avp(266, 0x40, struct.pack("!I", 10415))
    avps += build_avp(269, 0x00, "PythonClient")
    avps += build_avp(267, 0x40, struct.pack("!I", 1))
    avps += build_avp(278, 0x40, struct.pack("!I", 4))
    avps += build_avp(257, 0x40, struct.pack("!I", 0x0A03FC5E))
    return avps

# === Build DWA ===
def build_dwa(hbh_id, ete_id):
    avps = b""
    avps += build_avp(264, 0x40, ORIGIN_HOST)
    avps += build_avp(296, 0x40, ORIGIN_REALM)
    header = build_diameter_header(
        DIAMETER_COMMAND_CODE_DWA,
        DIAMETER_FLAG_RESPONSE,
        0,
        hbh_id,
        ete_id,
        len(avps)
    )
    return header + avps

# === Build CCR ===
def build_ccr(hbh_id, ete_id):
    avps = b""
    avps += build_avp(263, 0x40, "GX-1300001")
    avps += build_avp(264, 0x40, ORIGIN_HOST)
    avps += build_avp(296, 0x40, ORIGIN_REALM)
    avps += build_avp(293, 0x40, "127.0.0.1")
    avps += build_avp(283, 0x40, "local")

    vsai = b""
    vsai += build_avp(266, 0x40, struct.pack("!I", 10415))
    vsai += build_avp(258, 0x40, struct.pack("!I", 4))
    avps += build_avp(260, 0x40, vsai)

    avps += build_avp(416, 0x40, struct.pack("!I", 1))
    avps += build_avp(416, 0x40, struct.pack("!I", 2))

    sub1 = build_avp(450, 0x40, struct.pack("!I", 0))
    sub1 += build_avp(444, 0x40, "AN0001")
    avps += build_avp(443, 0x40, sub1)

    header = build_diameter_header(
        DIAMETER_COMMAND_CODE_CCR,
        DIAMETER_FLAG_REQUEST,
        DIAMETER_APPLICATION_ID,
        hbh_id,
        ete_id,
        len(avps)
    )
    return header + avps

# === Main SCTP Client ===
def run_diameter_client(host='0.0.0.0', port=3969):

 
    

    try:
        print(f"[+] Connecting to Diameter server via SCTP at {host}:{port}...")
        
        # Create SCTP socket (NO 'with' - pysctp does not support __enter__)
        sock = sctp.sctpsocket_tcp(socket.AF_INET)
    
        # --- Correct SCTP init parameters ---
        params = sock.get_initparams()
        params.num_ostreams = 5
        params.max_instreams = 5
        sock.set_initparams(params)
        
        sock.connect((host, port))

        # CER
        hbh = 0x01020304
        ete = 0x05060708
        cer_avps = build_cer_avps()

        cer_header = build_diameter_header(
            DIAMETER_COMMAND_CODE_CER,
            DIAMETER_FLAG_REQUEST,
            0,
            hbh,
            ete,
            len(cer_avps)
        )

        print("[>] Sending CER...")
        sock.sctp_send(cer_header + cer_avps)

        cea, _ = sock.sctp_recv(4096)
        print("[<] Received CEA:", cea.hex())

        # CCR
        hbh += 1
        ete += 1
        ccr = build_ccr(hbh, ete)

        print("[>] Sending CCR...")
        sock.sctp_send(ccr)

        # Main receive loop
        while True:
            data, _ = sock.sctp_recv(4096)
            if not data:
                print("[-] Connection closed.")
                break

            header = parse_diameter_header(data)
            if not header:
                continue

            print(f"[<] Diameter msg: Code={header['command_code']} Flags={header['flags']:02x}")

            if header["command_code"] == DIAMETER_COMMAND_CODE_DWR and (header["flags"] & 0x80):
                print("[✓] Received DWR → Sending DWA")
                dwa = build_dwa(header["hop_by_hop"], header["end_to_end"])
                sock.sctp_send(dwa)

            elif header["command_code"] == DIAMETER_COMMAND_CODE_CCA:
                print("[<] Received CCA")
                print(data.hex())

    finally:
        print("[x] Closing SCTP socket...")
        sock.close()


if __name__ == "__main__":
    run_diameter_client()