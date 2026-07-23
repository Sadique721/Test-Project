package com.savbill.radius.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.savbill.radius.entity.AcctCdr;
import com.savbill.radius.entity.LiveUser;

@Service
public class ExcelExportService {

    private void writeHeaderLine(XSSFWorkbook workbook, XSSFSheet sheet) {
	Row row = sheet.createRow(0);

	CellStyle style = workbook.createCellStyle();
	XSSFFont font = workbook.createFont();
	font.setBold(true);
	font.setFontHeight(12);
	style.setFont(font);

	createCell(sheet, row, 0, "Username", style);
	createCell(sheet, row, 1, "Password", style);
	createCell(sheet, row, 2, "Chap Password", style);
	createCell(sheet, row, 3, "Nas IP Address", style);
	createCell(sheet, row, 4, "Nas Port", style);
	createCell(sheet, row, 5, "Service Type", style);
	createCell(sheet, row, 6, "Framed Protocol", style);
	createCell(sheet, row, 7, "Framed IP Address", style);
	createCell(sheet, row, 8, "Framed IP Netmask", style);
	createCell(sheet, row, 9, "Framed Routing", style);
	createCell(sheet, row, 10, "Filter Id", style);
	createCell(sheet, row, 11, "Framed Mtu", style);
	createCell(sheet, row, 12, "Framed Compression", style);
	createCell(sheet, row, 13, "Login IP Host", style);
	createCell(sheet, row, 14, "Login Service", style);
	createCell(sheet, row, 15, "Login Tcp Port", style);
	createCell(sheet, row, 16, "Reply Message", style);
	createCell(sheet, row, 17, "Callback Number", style);
	createCell(sheet, row, 18, "Callback Id", style);
	createCell(sheet, row, 19, "Framed Route ", style);
	createCell(sheet, row, 20, "Framed IPx Network ", style);
	createCell(sheet, row, 21, "State ", style);
	createCell(sheet, row, 22, "Acct Class no", style);
	createCell(sheet, row, 23, "Vendor Specific ", style);
	createCell(sheet, row, 24, "Session Timeout ", style);
	createCell(sheet, row, 25, "Idle Timeout ", style);
	createCell(sheet, row, 26, "Termination Action ", style);
	createCell(sheet, row, 27, "Called Station Id ", style);
	createCell(sheet, row, 28, "Calling Station Id", style);
	createCell(sheet, row, 29, "Nas Identifier ", style);
	createCell(sheet, row, 30, "Idle Timeout ", style);
	createCell(sheet, row, 31, "Proxy State ", style);
	createCell(sheet, row, 32, "LoginLat Service ", style);
	createCell(sheet, row, 33, "LoginLat Node ", style);
	createCell(sheet, row, 34, "LoginLat Group ", style);
	createCell(sheet, row, 35, "Framed Apple TalkLink ", style);
	createCell(sheet, row, 36, "Idle Timeout ", style);
	createCell(sheet, row, 37, "Framed Apple TalkNetwork ", style);
	createCell(sheet, row, 38, "Framed Apple TalkZone ", style);
	createCell(sheet, row, 39, "Acct Status Type", style);
	createCell(sheet, row, 40, "Acct Delay Time ", style);
	createCell(sheet, row, 41, "Acct InputOctets ", style);
	createCell(sheet, row, 42, "Acct OutputOctets ", style);
	createCell(sheet, row, 43, "Acct Session Id ", style);
	createCell(sheet, row, 44, "Acct Authentic ", style);
	createCell(sheet, row, 45, "Acct Session Time ", style);
	createCell(sheet, row, 46, "Acct Input Packets ", style);
	createCell(sheet, row, 47, "Acct Output Packets", style);
	createCell(sheet, row, 48, "Acct Terminate Cause", style);
	createCell(sheet, row, 49, "Acct Multi Session Id", style);
	createCell(sheet, row, 50, "Acct Link Count", style);
	createCell(sheet, row, 51, "Acct Input Gigawords", style);
	createCell(sheet, row, 52, "Acct Output Gigawords", style);
	createCell(sheet, row, 53, "Event Timestamp", style);
	createCell(sheet, row, 54, "Chap Challenge", style);
	createCell(sheet, row, 55, "Nas Port Type", style);
	createCell(sheet, row, 56, "Acct Session Time", style);
	createCell(sheet, row, 57, "Port Limit", style);
	createCell(sheet, row, 58, "Login LAT Port", style);
	createCell(sheet, row, 59, "Acct Tunnel Connection", style);
	createCell(sheet, row, 60, "Arap Password", style);
	createCell(sheet, row, 61, "Arap Features", style);
	createCell(sheet, row, 62, "Arap Zone Access", style);
	createCell(sheet, row, 63, "Arap Security", style);
	createCell(sheet, row, 64, "Arap Security Data", style);
	createCell(sheet, row, 65, "Password Retry", style);
	createCell(sheet, row, 66, "Prompt", style);
	createCell(sheet, row, 67, "Connect Info Default", style);
	createCell(sheet, row, 68, "Configuration Token", style);
	createCell(sheet, row, 69, "Eap Message", style);
	createCell(sheet, row, 70, "Message Authenticator", style);
	createCell(sheet, row, 71, "Arap Challenge Response", style);
	createCell(sheet, row, 72, "Acct Interim Interval", style);
	createCell(sheet, row, 73, "Nas Port Id", style);
	createCell(sheet, row, 74, "Framed Pool", style);
	createCell(sheet, row, 75, "Nas IPv6 Address", style);
	createCell(sheet, row, 76, "Framed Interface Id", style);
	createCell(sheet, row, 77, "Framed IPv6 Prefix", style);
	createCell(sheet, row, 78, "Login IPv6 Host", style);
	createCell(sheet, row, 79, "Framed IPv6 Route", style);
	createCell(sheet, row, 80, "Framed IPv6 Pool", style);
	createCell(sheet, row, 81, "Digest Response", style);
	createCell(sheet, row, 82, "Digest Attributes", style);
	createCell(sheet, row, 83, "Framed Ipv6 Address", style);

    }

    private void createCell(XSSFSheet sheet, Row row, int columnCount, Object value, CellStyle style) {
	sheet.autoSizeColumn(columnCount);
	Cell cell = row.createCell(columnCount);
	if (value instanceof Integer) {
	    cell.setCellValue((Integer) value);
	} else if (value instanceof Boolean) {
	    cell.setCellValue((Boolean) value);
	} else {
	    cell.setCellValue((String) value);
	}
	cell.setCellStyle(style);
    }

    private void writeDataLines(XSSFWorkbook workbook, XSSFSheet sheet, List<AcctCdr> cdrUsersList) {
	int rowCount = 1;

	CellStyle style = workbook.createCellStyle();
	XSSFFont font = workbook.createFont();
	font.setFontHeight(10);
	style.setFont(font);

	for (AcctCdr user : cdrUsersList) {
	    Row row = sheet.createRow(rowCount++);
	    int columnCount = 0;
	    createCell(sheet, row, columnCount++, user.getUserName(), style);
	    createCell(sheet, row, columnCount++, user.getUserPassword(), style);
	    createCell(sheet, row, columnCount++, user.getChapPassword(), style);
	    createCell(sheet, row, columnCount++, user.getNasIpAddress(), style);
	    createCell(sheet, row, columnCount++, user.getNasPort(), style);
	    createCell(sheet, row, columnCount++, user.getServiceType(), style);
	    createCell(sheet, row, columnCount++, user.getFramedProtocol(), style);
	    createCell(sheet, row, columnCount++, user.getFramedIpAddress(), style);
	    createCell(sheet, row, columnCount++, user.getFramedIpNetmask(), style);
	    createCell(sheet, row, columnCount++, user.getFramedRouting(), style);
	    createCell(sheet, row, columnCount++, user.getFilterId(), style);
	    createCell(sheet, row, columnCount++, user.getFramedMtu(), style);
	    createCell(sheet, row, columnCount++, user.getFramedCompression(), style);
	    createCell(sheet, row, columnCount++, user.getLoginIpHost(), style);
	    createCell(sheet, row, columnCount++, user.getLoginService(), style);
	    createCell(sheet, row, columnCount++, user.getLoginTcpPort(), style);
	    createCell(sheet, row, columnCount++, user.getReplyMessage(), style);
	    createCell(sheet, row, columnCount++, user.getCallbackNumber(), style);
	    createCell(sheet, row, columnCount++, user.getCallbackId(), style);
	    createCell(sheet, row, columnCount++, user.getFramedRoute(), style);
	    createCell(sheet, row, columnCount++, user.getFramedIpxNetwork(), style);
	    createCell(sheet, row, columnCount++, user.getState(), style);
	    createCell(sheet, row, columnCount++, user.getAcctClass(), style);
	    createCell(sheet, row, columnCount++, user.getVendorSpecific(), style);
	    createCell(sheet, row, columnCount++, user.getSessionTimeout(), style);
	    createCell(sheet, row, columnCount++, user.getIdleTimeout(), style);
	    createCell(sheet, row, columnCount++, user.getTerminationAction(), style);
	    createCell(sheet, row, columnCount++, user.getCalledStationId(), style);
	    createCell(sheet, row, columnCount++, user.getCallingStationId(), style);
	    createCell(sheet, row, columnCount++, user.getNasIdentifier(), style);
	    createCell(sheet, row, columnCount++, user.getIdleTimeout(), style);
	    createCell(sheet, row, columnCount++, user.getProxyState(), style);
	    createCell(sheet, row, columnCount++, user.getLoginLatService(), style);
	    createCell(sheet, row, columnCount++, user.getLoginLatNode(), style);
	    createCell(sheet, row, columnCount++, user.getLoginLatGroup(), style);
	    createCell(sheet, row, columnCount++, user.getFramedAppleTalkLink(), style);
	    createCell(sheet, row, columnCount++, user.getIdleTimeout(), style);
	    createCell(sheet, row, columnCount++, user.getFramedAppleTalkNetwork(), style);
	    createCell(sheet, row, columnCount++, user.getFramedAppleTalkZone(), style);
	    createCell(sheet, row, columnCount++, user.getAcctStatusType(), style);
	    createCell(sheet, row, columnCount++, user.getAcctDelayTime(), style);
	    createCell(sheet, row, columnCount++, user.getAcctInputOctets(), style);
	    createCell(sheet, row, columnCount++, user.getAcctOutputOctets(), style);
	    createCell(sheet, row, columnCount++, user.getAcctSessionId(), style);
	    createCell(sheet, row, columnCount++, user.getAcctAuthentic(), style);
	    createCell(sheet, row, columnCount++, user.getAcctSessionTime(), style);
	    createCell(sheet, row, columnCount++, user.getAcctInputPackets(), style);
	    createCell(sheet, row, columnCount++, user.getAcctOutputOctets(), style);
	    createCell(sheet, row, columnCount++, user.getAcctTerminateCause(), style);
	    createCell(sheet, row, columnCount++, user.getAcctMultiSessionId(), style);
	    createCell(sheet, row, columnCount++, user.getAcctLinkCount(), style);
	    createCell(sheet, row, columnCount++, user.getAcctInputGigawords(), style);
	    createCell(sheet, row, columnCount++, user.getAcctOutputGigawords(), style);
	    createCell(sheet, row, columnCount++, user.getEventTimestamp(), style);
	    createCell(sheet, row, columnCount++, user.getChapChallenge(), style);
	    createCell(sheet, row, columnCount++, user.getNasPortType(), style);
	    createCell(sheet, row, columnCount++, user.getAcctSessionTime(), style);
	    createCell(sheet, row, columnCount++, user.getPortLimit(), style);
	    createCell(sheet, row, columnCount++, user.getLoginLATPort(), style);
	    createCell(sheet, row, columnCount++, user.getAcctTunnelConnection(), style);
	    createCell(sheet, row, columnCount++, user.getArapPassword(), style);
	    createCell(sheet, row, columnCount++, user.getArapFeatures(), style);
	    createCell(sheet, row, columnCount++, user.getArapZoneAccess(), style);
	    createCell(sheet, row, columnCount++, user.getArapSecurity(), style);
	    createCell(sheet, row, columnCount++, user.getArapSecurityData(), style);
	    createCell(sheet, row, columnCount++, user.getPasswordRetry(), style);
	    createCell(sheet, row, columnCount++, user.getPrompt(), style);
	    createCell(sheet, row, columnCount++, user.getConnectInfo(), style);
	    createCell(sheet, row, columnCount++, user.getConfigurationToken(), style);
	    createCell(sheet, row, columnCount++, user.getEapMessage(), style);
	    createCell(sheet, row, columnCount++, user.getMessageAuthenticator(), style);
	    createCell(sheet, row, columnCount++, user.getArapChallengeResponse(), style);
	    createCell(sheet, row, columnCount++, user.getAcctInterimInterval(), style);
	    createCell(sheet, row, columnCount++, user.getNasPortId(), style);
	    createCell(sheet, row, columnCount++, user.getFramedPool(), style);
	    createCell(sheet, row, columnCount++, user.getNasIPv6Address(), style);
	    createCell(sheet, row, columnCount++, user.getFramedInterfaceId(), style);
	    createCell(sheet, row, columnCount++, user.getFramedIPv6Prefix(), style);
	    createCell(sheet, row, columnCount++, user.getLoginIPv6Host(), style);
	    createCell(sheet, row, columnCount++, user.getFramedIPv6Route(), style);
	    createCell(sheet, row, columnCount++, user.getFramedIPv6Pool(), style);
	    createCell(sheet, row, columnCount++, user.getDigestResponse(), style);
	    createCell(sheet, row, columnCount++, user.getDigestAttributes(), style);
	    createCell(sheet, row, columnCount++, user.getFramedipv6address(), style);
	}
    }

    public void exportExcel(List<AcctCdr> cdrUsers, HttpServletResponse response) throws IOException {
	XSSFWorkbook workbook = new XSSFWorkbook();
	XSSFSheet sheet = workbook.createSheet("CDR Users");
	writeHeaderLine(workbook, sheet);
	writeDataLines(workbook, sheet, cdrUsers);

	ServletOutputStream outputStream = response.getOutputStream();
	workbook.write(outputStream);
	workbook.close();
	outputStream.close();

    }

    public void exportExcelLiveUsers(List<LiveUser> liveUser, HttpServletResponse httpResponse) throws IOException {
	
	XSSFWorkbook workbook = new XSSFWorkbook();
	XSSFSheet sheet = workbook.createSheet("Live Users");
	writeHeaderLineForLiveUsers(workbook, sheet);
	writeDataLinesForLiveUsers(workbook, sheet, liveUser);

	ServletOutputStream outputStream = httpResponse.getOutputStream();
	workbook.write(outputStream);
	workbook.close();
	outputStream.close();

    }

    private void writeDataLinesForLiveUsers(XSSFWorkbook workbook, XSSFSheet sheet, List<LiveUser> liveUser) {

	int rowCount = 1;

	CellStyle style = workbook.createCellStyle();
	XSSFFont font = workbook.createFont();
	font.setFontHeight(10);
	style.setFont(font);

	for (LiveUser user : liveUser) {
	    Row row = sheet.createRow(rowCount++);
	    int columnCount = 0;
	    createCell(sheet, row, columnCount++, user.getUserName(), style);
	    createCell(sheet, row, columnCount++, user.getUserPassword(), style);
	    createCell(sheet, row, columnCount++, user.getChapPassword(), style);
	    createCell(sheet, row, columnCount++, user.getNasIpAddress(), style);
	    createCell(sheet, row, columnCount++, user.getNasPort(), style);
	    createCell(sheet, row, columnCount++, user.getServiceType(), style);
	    createCell(sheet, row, columnCount++, user.getFramedProtocol(), style);
	    createCell(sheet, row, columnCount++, user.getFramedIpAddress(), style);
	    createCell(sheet, row, columnCount++, "", style);
	    createCell(sheet, row, columnCount++, user.getFramedRouting(), style);
	    createCell(sheet, row, columnCount++, user.getFilterId(), style);
	    createCell(sheet, row, columnCount++, user.getFrmaedMTU(), style);
	    createCell(sheet, row, columnCount++, user.getFramedCompression(), style);
	    createCell(sheet, row, columnCount++, user.getLoginIPHost(), style);
	    createCell(sheet, row, columnCount++, user.getLoginService(), style);
	    createCell(sheet, row, columnCount++, user.getLoginTCPPort(), style);
	    createCell(sheet, row, columnCount++, user.getReplyMessage(), style);
	    createCell(sheet, row, columnCount++, user.getCallbackNumber(), style);
	    createCell(sheet, row, columnCount++, user.getCallbackId(), style);
	    createCell(sheet, row, columnCount++, user.getFramedRoute(), style);
	    createCell(sheet, row, columnCount++, user.getFramedIPXNetwork(), style);
	    createCell(sheet, row, columnCount++, user.getState(), style);
	    createCell(sheet, row, columnCount++, user.getlClass(), style);
	    createCell(sheet, row, columnCount++, user.getVendorSpecific(), style);
	    createCell(sheet, row, columnCount++, user.getSessionTimeout(), style);
	    createCell(sheet, row, columnCount++, user.getIdleTimeout(), style);
	    createCell(sheet, row, columnCount++, user.getTerminationAction(), style);
	    createCell(sheet, row, columnCount++, user.getCalledStationId(), style);
	    createCell(sheet, row, columnCount++, user.getCallingStationId(), style);
	    createCell(sheet, row, columnCount++, user.getNasIpAddress(), style);
	    createCell(sheet, row, columnCount++, user.getIdleTimeout(), style);
	    createCell(sheet, row, columnCount++, user.getProxyState(), style);
	    createCell(sheet, row, columnCount++, user.getLoginLATService(), style);
	    createCell(sheet, row, columnCount++, user.getLoginLATNode(), style);
	    createCell(sheet, row, columnCount++, user.getLoginLATGroup(), style);
	    createCell(sheet, row, columnCount++, user.getFramedAppleTalkLink(), style);
	    createCell(sheet, row, columnCount++, user.getIdleTimeout(), style);
	    createCell(sheet, row, columnCount++, user.getFramedAppleTalkNetwork(), style);
	    createCell(sheet, row, columnCount++, user.getFramedAppleTalkZone(), style);
	    createCell(sheet, row, columnCount++, user.getAcctStatusType(), style);
	    createCell(sheet, row, columnCount++, user.getAcctDelayTime(), style);
	    createCell(sheet, row, columnCount++, user.getAcctInputOctets(), style);
	    createCell(sheet, row, columnCount++, user.getAcctOutputOctets(), style);
	    createCell(sheet, row, columnCount++, user.getAcctSessionId(), style);
	    createCell(sheet, row, columnCount++, user.getAcctAuthentic(), style);
	    createCell(sheet, row, columnCount++, user.getAcctSessionTime(), style);
	    createCell(sheet, row, columnCount++, user.getAcctInputPackets(), style);
	    createCell(sheet, row, columnCount++, user.getAcctOutputOctets(), style);
	    createCell(sheet, row, columnCount++, user.getAcctTerminateCause(), style);
	    createCell(sheet, row, columnCount++, user.getAcctMultiSessionId(), style);
	    createCell(sheet, row, columnCount++, user.getAcctLinkCount(), style);
	    createCell(sheet, row, columnCount++, user.getAcctInputGigawords(), style);
	    createCell(sheet, row, columnCount++, user.getAcctOutputGigawords(), style);
	    createCell(sheet, row, columnCount++, user.getEventTimestamp(), style);
	    createCell(sheet, row, columnCount++, user.getChapChallenge(), style);
	    createCell(sheet, row, columnCount++, user.getNasPortType(), style);
	    createCell(sheet, row, columnCount++, user.getAcctSessionTime(), style);
	    createCell(sheet, row, columnCount++, user.getPortLimit(), style);
	    createCell(sheet, row, columnCount++, user.getLoginLATPort(), style);
	    createCell(sheet, row, columnCount++, user.getAcctTunnelConnection(), style);
	    createCell(sheet, row, columnCount++, user.getArapPassword(), style);
	    createCell(sheet, row, columnCount++, user.getArapFeatures(), style);
	    createCell(sheet, row, columnCount++, user.getArapZoneAccess(), style);
	    createCell(sheet, row, columnCount++, user.getArapSecurity(), style);
	    createCell(sheet, row, columnCount++, user.getArapSecurityData(), style);
	    createCell(sheet, row, columnCount++, user.getPasswordRetry(), style);
	    createCell(sheet, row, columnCount++, user.getPrompt(), style);
	    createCell(sheet, row, columnCount++, user.getConnectInfo(), style);
	    createCell(sheet, row, columnCount++, user.getConfigurationToken(), style);
	    createCell(sheet, row, columnCount++, user.getEapMessage(), style);
	    createCell(sheet, row, columnCount++, user.getMessageAuthenticator(), style);
	    createCell(sheet, row, columnCount++, user.getArapChallengeResponse(), style);
	    createCell(sheet, row, columnCount++, user.getAcctInterimInterval(), style);
	    createCell(sheet, row, columnCount++, user.getNasPortId(), style);
	    createCell(sheet, row, columnCount++, user.getFramedPool(), style);
	    createCell(sheet, row, columnCount++, user.getNasIPv6Address(), style);
	    createCell(sheet, row, columnCount++, user.getFramedInterfaceId(), style);
	    createCell(sheet, row, columnCount++, user.getFramedIPv6Prefix(), style);
	    createCell(sheet, row, columnCount++, user.getLoginIPv6Host(), style);
	    createCell(sheet, row, columnCount++, user.getFramedIPv6Route(), style);
	    createCell(sheet, row, columnCount++, user.getFramedIPv6Pool(), style);
	    createCell(sheet, row, columnCount++, user.getDigestResponse(), style);
	    createCell(sheet, row, columnCount++, user.getDigestAttributes(), style);
	    createCell(sheet, row, columnCount++, user.getFramedipv6address(), style);
	}

    }

    private void writeHeaderLineForLiveUsers(XSSFWorkbook workbook, XSSFSheet sheet) {

	Row row = sheet.createRow(0);

	CellStyle style = workbook.createCellStyle();
	XSSFFont font = workbook.createFont();
	font.setBold(true);
	font.setFontHeight(12);
	style.setFont(font);

	createCell(sheet, row, 0, "Username", style);
	createCell(sheet, row, 1, "Password", style);
	createCell(sheet, row, 2, "Chap Password", style);
	createCell(sheet, row, 3, "Nas IP Address", style);
	createCell(sheet, row, 4, "Nas Port", style);
	createCell(sheet, row, 5, "Service Type", style);
	createCell(sheet, row, 6, "Framed Protocol", style);
	createCell(sheet, row, 7, "Framed IP Address", style);
	createCell(sheet, row, 8, "Framed IP Netmask", style);
	createCell(sheet, row, 9, "Framed Routing", style);
	createCell(sheet, row, 10, "Filter Id", style);
	createCell(sheet, row, 11, "Framed Mtu", style);
	createCell(sheet, row, 12, "Framed Compression", style);
	createCell(sheet, row, 13, "Login IP Host", style);
	createCell(sheet, row, 14, "Login Service", style);
	createCell(sheet, row, 15, "Login Tcp Port", style);
	createCell(sheet, row, 16, "Reply Message", style);
	createCell(sheet, row, 17, "Callback Number", style);
	createCell(sheet, row, 18, "Callback Id", style);
	createCell(sheet, row, 19, "Framed Route ", style);
	createCell(sheet, row, 20, "Framed IPx Network ", style);
	createCell(sheet, row, 21, "State ", style);
	createCell(sheet, row, 22, "Acct Class no", style);
	createCell(sheet, row, 23, "Vendor Specific ", style);
	createCell(sheet, row, 24, "Session Timeout ", style);
	createCell(sheet, row, 25, "Idle Timeout ", style);
	createCell(sheet, row, 26, "Termination Action ", style);
	createCell(sheet, row, 27, "Called Station Id ", style);
	createCell(sheet, row, 28, "Calling Station Id", style);
	createCell(sheet, row, 29, "Nas Identifier ", style);
	createCell(sheet, row, 30, "Idle Timeout ", style);
	createCell(sheet, row, 31, "Proxy State ", style);
	createCell(sheet, row, 32, "LoginLat Service ", style);
	createCell(sheet, row, 33, "LoginLat Node ", style);
	createCell(sheet, row, 34, "LoginLat Group ", style);
	createCell(sheet, row, 35, "Framed Apple TalkLink ", style);
	createCell(sheet, row, 36, "Idle Timeout ", style);
	createCell(sheet, row, 37, "Framed Apple TalkNetwork ", style);
	createCell(sheet, row, 38, "Framed Apple TalkZone ", style);
	createCell(sheet, row, 39, "Acct Status Type", style);
	createCell(sheet, row, 40, "Acct Delay Time ", style);
	createCell(sheet, row, 41, "Acct InputOctets ", style);
	createCell(sheet, row, 42, "Acct OutputOctets ", style);
	createCell(sheet, row, 43, "Acct Session Id ", style);
	createCell(sheet, row, 44, "Acct Authentic ", style);
	createCell(sheet, row, 45, "Acct Session Time ", style);
	createCell(sheet, row, 46, "Acct Input Packets ", style);
	createCell(sheet, row, 47, "Acct Output Packets", style);
	createCell(sheet, row, 48, "Acct Terminate Cause", style);
	createCell(sheet, row, 49, "Acct Multi Session Id", style);
	createCell(sheet, row, 50, "Acct Link Count", style);
	createCell(sheet, row, 51, "Acct Input Gigawords", style);
	createCell(sheet, row, 52, "Acct Output Gigawords", style);
	createCell(sheet, row, 53, "Event Timestamp", style);
	createCell(sheet, row, 54, "Chap Challenge", style);
	createCell(sheet, row, 55, "Nas Port Type", style);
	createCell(sheet, row, 56, "Acct Session Time", style);
	createCell(sheet, row, 57, "Port Limit", style);
	createCell(sheet, row, 58, "Login LAT Port", style);
	createCell(sheet, row, 59, "Acct Tunnel Connection", style);
	createCell(sheet, row, 60, "Arap Password", style);
	createCell(sheet, row, 61, "Arap Features", style);
	createCell(sheet, row, 62, "Arap Zone Access", style);
	createCell(sheet, row, 63, "Arap Security", style);
	createCell(sheet, row, 64, "Arap Security Data", style);
	createCell(sheet, row, 65, "Password Retry", style);
	createCell(sheet, row, 66, "Prompt", style);
	createCell(sheet, row, 67, "Connect Info Default", style);
	createCell(sheet, row, 68, "Configuration Token", style);
	createCell(sheet, row, 69, "Eap Message", style);
	createCell(sheet, row, 70, "Message Authenticator", style);
	createCell(sheet, row, 71, "Arap Challenge Response", style);
	createCell(sheet, row, 72, "Acct Interim Interval", style);
	createCell(sheet, row, 73, "Nas Port Id", style);
	createCell(sheet, row, 74, "Framed Pool", style);
	createCell(sheet, row, 75, "Nas IPv6 Address", style);
	createCell(sheet, row, 76, "Framed Interface Id", style);
	createCell(sheet, row, 77, "Framed IPv6 Prefix", style);
	createCell(sheet, row, 78, "Login IPv6 Host", style);
	createCell(sheet, row, 79, "Framed IPv6 Route", style);
	createCell(sheet, row, 80, "Framed IPv6 Pool", style);
	createCell(sheet, row, 81, "Digest Response", style);
	createCell(sheet, row, 82, "Digest Attributes", style);
	createCell(sheet, row, 83, "Framed Ipv6 Address", style);

    }
}
