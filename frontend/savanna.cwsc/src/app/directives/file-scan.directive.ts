import { Directive, EventEmitter, HostListener, Input, Output } from "@angular/core";
import { MessageService } from "primeng/api";

@Directive({
  selector: "[appFileScan]"
})
export class FileScanDirective {
  @Input() allowMultiple = false; // set true for multi upload
  @Output() safeFile = new EventEmitter<File>();
  @Output() safeFiles = new EventEmitter<File[]>();

  constructor(private messageService: MessageService) {}

  @HostListener("change", ["$event"])
  async onFileChange(event: any): Promise<void> {
    const inputFiles: FileList = event.target.files;
    if (!inputFiles || inputFiles.length === 0) return;

    const cleanFiles: File[] = [];

    for (let i = 0; i < inputFiles.length; i++) {
      const file = inputFiles[i];

      //   if (file.type !== "application/pdf") {
      //     this.messageService.add({
      //       severity: "warn",
      //       summary: "Invalid File Type",
      //       detail: `${file.name} is not a PDF.`,
      //       icon: "pi pi-exclamation-triangle"
      //     });
      //     continue;
      //   }

      const content = await this.readFile(file);
      const text = this.bufferToString(new Uint8Array(content));

      // check for suspicious file content (JavaScript, JS, or AA) in the file content
      if (text.includes("/JavaScript") || text.includes("/JS") || text.includes("/AA")) {
        this.messageService.add({
          severity: "error",
          summary: "Suspicious File Detected",
          detail: `${file.name} contains embedded JavaScript.`,
          icon: "pi pi-ban"
        });
        continue;
      }

      cleanFiles.push(file);
    }

    // Emit result
    if (this.allowMultiple) {
      this.safeFiles.emit(cleanFiles);
    } else if (cleanFiles.length > 0) {
      this.safeFile.emit(cleanFiles[0]); // only first file for single upload
    }

    // Optional: clear file input if needed
    if (cleanFiles.length !== inputFiles.length) {
      event.target.value = "";
    }
  }

  private readFile(file: File): Promise<ArrayBuffer> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result as ArrayBuffer);
      reader.onerror = reject;
      reader.readAsArrayBuffer(file);
    });
  }

  private bufferToString(buffer: Uint8Array): string {
    const chunkSize = 8192; // Safe chunk size
    let result = "";

    for (let i = 0; i < buffer.length; i += chunkSize) {
      const chunk = buffer.subarray(i, i + chunkSize);
      result += String.fromCharCode(...chunk);
    }

    return result;
  }
}
