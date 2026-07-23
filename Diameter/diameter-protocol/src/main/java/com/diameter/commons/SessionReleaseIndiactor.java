package com.diameter.commons;

public interface SessionReleaseIndiactor {
  boolean isEligible(DiameterPacket paramDiameterPacket);
}