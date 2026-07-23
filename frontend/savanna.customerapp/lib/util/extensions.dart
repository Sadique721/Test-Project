import 'package:intl/intl.dart';

extension Extension on Object {
  bool isNullOrEmpty() => this == null || this == '';
}

extension IsNullOrEmpty on List {
  bool get isListNullOrEmpty {
    return this == null || isEmpty;
  }
}

var noSimbolInUSFormat = NumberFormat.currency(locale: "en_US", symbol: "");

extension StringExtensions on String {
  bool containsIgnoreCase(String secondString) =>
      toLowerCase().contains(secondString.toLowerCase());

  bool equalsIgnoreCase(String string2) {
    return toLowerCase() == string2.toLowerCase();
  }

  bool isValidEmail() {
    return RegExp(
            r'^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$')
        .hasMatch(this);
  }

  bool isValidPanNo() {
    return RegExp("[A-Z]{5}[0-9]{4}[A-Z]{1}").hasMatch(this);
  }

  bool isValidPassword() {
    return RegExp(r'^(?=.*?[A-Z])(?=.*?[!@#\$&*~]).{6,}$')
        .hasMatch(this); //(?=.*?[a-z])
  }

  bool isNumberWithDecimalPoint() {
    return RegExp("[0-9]+(\.[0-9][0-9]?)?")
        .hasMatch(this); //(?=.*?[a-z])
  }


}
