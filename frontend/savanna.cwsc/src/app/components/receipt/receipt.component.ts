import { Component, OnInit } from "@angular/core";

@Component({
  selector: "app-receipt",
  templateUrl: "./receipt.component.html",
  styleUrls: ["./receipt.component.css"],
})
export class ReceiptComponent implements OnInit {
  razopayredirectTimeInSecondsNum: any;
  orderId: any;
  pgTransactionId: any;
  constructor() {}

  ngOnInit(): void {
    this.recieptRedirect();
  }
  recieptRedirect() {
    var razopayredirect = localStorage.getItem("RedirectTimeInSecond");
    this.orderId = localStorage.getItem("OrderId");
    this.pgTransactionId = localStorage.getItem("TransactionId");
    console.log("razorpayRedirect :::", razopayredirect);
    console.log("enter in redirect seconds");
    let self = this;
    self.razopayredirectTimeInSecondsNum = parseInt(razopayredirect);
    var downloadTimer = setInterval(function () {
      if (self.razopayredirectTimeInSecondsNum <= 0) {
        clearInterval(downloadTimer);
        window.close();
      } else {
        document.getElementById("countdown").innerHTML =
          "Redirecting to Home in " +
          self.razopayredirectTimeInSecondsNum +
          " seconds";
      }
      self.razopayredirectTimeInSecondsNum -= 1;
    }, 1000);
  }
}
