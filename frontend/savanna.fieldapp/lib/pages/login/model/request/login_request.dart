class LoginRequest {
  String? password;
  String? username;
  //String? deviceToken;

  //LoginRequest({this.password, this.username,this.deviceToken});
   LoginRequest({this.password, this.username});


  LoginRequest.fromJson(Map<String, dynamic> json) {
    password = json['password'];
    username = json['username'];
    // deviceToken = json['deviceToken'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['password'] = this.password;
    data['username'] = this.username;
  //  data['deviceToken'] = this.deviceToken;
    return data;
  }
}
