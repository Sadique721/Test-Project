// lib/utils/logger.dart
import 'package:flutter/foundation.dart';

/// Global debug logger: only prints when app is running in Debug mode.
void debugLog(Object? message, {String? tag}) {
  if (kDebugMode) {
    // ignore: avoid_print
    if (tag != null && tag.isNotEmpty) {
      print("🐞 [$tag] $message");
    } else {
      print("🐞 $message");
    }
  }
}
