import 'dart:convert';
import 'dart:developer';
import 'package:crypto/crypto.dart';

import 'logger.dart';

class HmacUtil {
  static String generateHmac(String data, String secretKey) {
    final key = utf8.encode(secretKey);
    final bytes = utf8.encode(data);
    log("✅ $secretKey===>>>$data");
    // debugLog(data, tag: "✅ $secretKey");
    final hmacSha256 = Hmac(sha256, key); // HMAC-SHA256
    final digest = hmacSha256.convert(bytes);

    return base64Encode(digest.bytes); // Base64 encoded output
  }
}
