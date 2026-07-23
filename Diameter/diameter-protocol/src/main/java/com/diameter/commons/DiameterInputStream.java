package com.diameter.commons;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

public class DiameterInputStream extends FilterInputStream {
  private static final String MODULE = "DIA-IN-STRM";
  
  public static final int MAX_CHUNK_SIZE = 4000;
  
  public static final int COMMAND_FLAG_REQUEST_BIT = 128;
  
  private int chunkSize;
  
  private byte[] readBuffer;
  
  private int currentIndex = 0;
  
  private int size = 0;
  
  public DiameterInputStream(InputStream in, int chunkSize) {
    super(in);
    this.chunkSize = chunkSize;
    this.readBuffer = new byte[chunkSize];
  }
  
  public DiameterInputStream(InputStream in) {
    this(in, 4000);
  }
  
  public synchronized DiameterPacket readDiameterPacket() throws IOException, MalformedPacketException {
    DiameterPacket diameterPacket = null;
    byte[] headerBytes = getBytes(20);
    try {
      diameterPacket = DiameterPacket.createPacket(headerBytes);
    } catch (MalformedPacketException e) {
      flush();
      throw e;
    } 
    try {
      diameterPacket.parsePacketAVPBytes(
          getBytes(diameterPacket.getRcvdLength() - 20));
    } catch (IOException e) {
      throw new MalformedPacketException("Exception occured while parsing packet with HbH-ID=" + diameterPacket.getHop_by_hopIdentifier() + " and EtE-ID=" + diameterPacket
          .getEnd_to_endIdentifier(), e);
    } 
    return diameterPacket;
  }
  
  private byte[] getBytes(int length) throws IOException {
    byte[] requiredBytes = new byte[length];
    int noOfBytesFilled = 0;
    do {
      if (this.size == this.currentIndex)
        try {
          this.size = this.in.read(this.readBuffer, 0, this.chunkSize);
          this.currentIndex = 0;
          if (this.size < 0)
            throw new EOFException("End of Stream reached"); 
        } catch (SocketTimeoutException e) {} 
      int noOfAvailableBytes = this.size - this.currentIndex;
      int noOfBytesRequired = length - noOfBytesFilled;
      int noOfBytesToConsume = (noOfAvailableBytes < noOfBytesRequired) ? noOfAvailableBytes : noOfBytesRequired;
      System.arraycopy(this.readBuffer, this.currentIndex, requiredBytes, noOfBytesFilled, noOfBytesToConsume);
      this.currentIndex += noOfBytesToConsume;
      noOfBytesFilled += noOfBytesToConsume;
    } while (noOfBytesFilled < length);
    return requiredBytes;
  }
  
  private void flush() {
    this.currentIndex = this.size;
  }
}
