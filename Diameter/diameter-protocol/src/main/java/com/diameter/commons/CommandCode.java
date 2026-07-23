package com.diameter.commons;

import java.util.HashMap;
import java.util.Map;

public enum CommandCode {
	CAPABILITIES_EXCHANGE(257, "CE"), DEVICE_WATCHDOG(280, "DW"), DISCONNECT_PEER(282, "DP"),
	AUTHENTICATION_AUTHORIZATION(265, "AA"), CREDIT_CONTROL(272, "CC"), SESSION_TERMINATION(275, "ST"),
	ACCOUNTING(271, "AC"), DIAMETER_EAP(268, "DE"), ABORT_SESSION(274, "AS", true), RE_AUTHORIZATION(258, "RA", true),
	SPENDING_LIMIT(8388635, "SL"), SPENDING_STATUS_NOTIFICATION(8388636, "SN", true), USER_AUTHORIZATION(300, "UA"),
	SERVER_ASSIGNMENT(301, "SA"), LOCATION_INFO(302, "LI"), MULTIMEDIA_AUTHENTICATION(303, "MA"),
	REGISTRATION_TERMINATION(304, "RT", true), PUSH_PROFILE(305, "PP", true), USER_DATA(306, "UD"),
	PROFILE_UPDATE(307, "PU"), SUBSCRIBE_NOTIFICATIONS(308, "SBN"), PUSH_NOTIFICATION(309, "PN", true),
	BOOTSTRAPPING_INFO(310, "BI"), MESSAGE_PROCESS(311, "MP"), UNIMPLEMENTED(0, "UN");

	private static final Map<Integer, CommandCode> map;

	private static final CommandCode[] COMMAND_CODES;

	public final int code;

	public final String displayName;

	public final boolean isServerInitiated;

	static {
		COMMAND_CODES = values();
		map = new HashMap<>();
		for (CommandCode type : COMMAND_CODES)
			map.put(Integer.valueOf(type.code), type);
	}

	CommandCode(int code, String displayName) {
		this.code = code;
		this.displayName = displayName;
		this.isServerInitiated = false;
	}

	CommandCode(int code, String displayName, boolean isServerInitiated) {
		this.code = code;
		this.displayName = displayName;
		this.isServerInitiated = isServerInitiated;
	}

	public int getCode() {
		return this.code;
	}

	public static boolean isValid(int value) {
		return map.containsKey(Integer.valueOf(value));
	}

	public static String fromCode(int value) {
		CommandCode commandCode = map.get(Integer.valueOf(value));
		if (commandCode != null)
			return commandCode.name();
		return String.valueOf(value);
	}

	public static String getDisplayName(int value) {
		CommandCode commandCode = map.get(Integer.valueOf(value));
		if (commandCode != null)
			return commandCode.displayName;
		return String.valueOf(value);
	}

	public static CommandCode getCommandCode(int intCommandCode) {
		CommandCode commandCode = map.get(Integer.valueOf(intCommandCode));
		if (commandCode != null)
			return commandCode;
		return UNIMPLEMENTED;
	}

	public static CommandCode fromDisplayName(String displayName) {
		CommandCode[] commandCodes = values();
		if (commandCodes != null)
			for (int i = 0; i < commandCodes.length; i++) {
				if ((commandCodes[i]).displayName.equalsIgnoreCase(displayName))
					return commandCodes[i];
			}
		return null;
	}
}
