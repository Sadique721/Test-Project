package com.diameter.commons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.context.annotation.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.diameter.model.Attribute;
import com.diameter.model.Vendor;

@Configuration
public class DiameterDictionary {
	
	private Map<String, VendorInformation> vendorMap = new HashMap<>();

	private Map<AttributeData, BaseAVPBuilder> attributeMap = new HashMap<>();

	private Map<String, AttributeData> attributeIdMap = new HashMap<>();

	private List<String> vendorIdList = new ArrayList<>();

	private List<String> dictionariesRead = new ArrayList<>();

	private void setDefaultAttributes() {
		BaseAVPBuilder usrEqpmntInfoTypeBuilder = new AvpEnumeratedBuilder();
		usrEqpmntInfoTypeBuilder.setAVPCode(459);
		usrEqpmntInfoTypeBuilder.setVendorId(0);
		usrEqpmntInfoTypeBuilder.setAVPId(0, 459);
		usrEqpmntInfoTypeBuilder.setAVPEncryption("yes");
		usrEqpmntInfoTypeBuilder.setMandatoryBit();
		Map<Integer, String> supportedValueMap = new HashMap<>();
		supportedValueMap.put(Integer.valueOf(0), "IMEISV");
		supportedValueMap.put(Integer.valueOf(1), "MAC");
		supportedValueMap.put(Integer.valueOf(2), "EUI64");
		supportedValueMap.put(Integer.valueOf(3), "MODIFIED_EUI64");
		((AvpEnumeratedBuilder) usrEqpmntInfoTypeBuilder).setSupportedValuesMap(supportedValueMap);
		VendorInformation vendorInformation = new VendorInformation(Integer.toString(0), "STANDARD", "ACTIVE");
		AttributeData usrEqpmntInfoTypeAvpId = new AttributeData(vendorInformation.getVendorId(), Integer.toString(459),
				"User-Equipment-Info-Type", "yes", "no", "yes", AVPType.valueOf("ENUMERATED"), "ACTIVE", "Diameter",
				null, null, null, supportedValueMap);
		this.attributeMap.put(usrEqpmntInfoTypeAvpId, usrEqpmntInfoTypeBuilder);
		this.attributeIdMap.put("0:459", usrEqpmntInfoTypeAvpId);
		UserEquipmentInfoAvpBuilder userEquipmentInfoAvpBuilder = new UserEquipmentInfoAvpBuilder();
		ArrayList<AvpRule> requiredAttrList = new ArrayList<>();
		AvpRule usrEqpmntInfoTypeAvpRule = new AvpRule();
		usrEqpmntInfoTypeAvpRule.setVendorId(0);
		usrEqpmntInfoTypeAvpRule.setAttrId(459);
		usrEqpmntInfoTypeAvpRule.setMaximum("1");
		usrEqpmntInfoTypeAvpRule.setMinimum("1");
		usrEqpmntInfoTypeAvpRule.setName("User-Equipment-Info-Type");
		requiredAttrList.add(usrEqpmntInfoTypeAvpRule);
		AvpRule usrEqpmntInfoValueAvpRule = new AvpRule();
		usrEqpmntInfoTypeAvpRule.setVendorId(0);
		usrEqpmntInfoTypeAvpRule.setAttrId(460);
		usrEqpmntInfoTypeAvpRule.setMaximum("1");
		usrEqpmntInfoTypeAvpRule.setMinimum("1");
		usrEqpmntInfoTypeAvpRule.setName("User-Equipment-Info-Value");
		requiredAttrList.add(usrEqpmntInfoValueAvpRule);
		userEquipmentInfoAvpBuilder.setAVPCode(458);
		userEquipmentInfoAvpBuilder.setVendorId(0);
		userEquipmentInfoAvpBuilder.setAVPId(0, 458);
		userEquipmentInfoAvpBuilder.setAVPEncryption("yes");
		userEquipmentInfoAvpBuilder.setMandatoryBit();
		userEquipmentInfoAvpBuilder.setRequiredAttrList(requiredAttrList);
		userEquipmentInfoAvpBuilder.setFixedAttrList(new ArrayList());
		userEquipmentInfoAvpBuilder.setOptionalAttrList(new ArrayList());
		vendorInformation = new VendorInformation(Integer.toString(0), "STANDARD", "ACTIVE");
		AttributeData usrEqpmntInfoAvpId = new AttributeData(vendorInformation.getVendorId(), Integer.toString(458),
				"User-Equipment-Info", "yes", "no", "yes", AVPType.valueOf("USEREQUIPMENTINFO"), "ACTIVE", "Diameter",
				null, null, null, supportedValueMap);
		this.attributeMap.put(usrEqpmntInfoAvpId, userEquipmentInfoAvpBuilder);
		this.attributeIdMap.put("0:458", usrEqpmntInfoAvpId);
		BaseAVPBuilder tgppUsrLocationInfoBuilder = new AvpUserLocationInfoAvpBuilder();
		tgppUsrLocationInfoBuilder.setAVPCode(22);
		tgppUsrLocationInfoBuilder.setVendorId(10415);
		tgppUsrLocationInfoBuilder.setAVPId(10415, 22);
		tgppUsrLocationInfoBuilder.setAVPEncryption("yes");
		tgppUsrLocationInfoBuilder.setMandatoryBit();
		vendorInformation = new VendorInformation(String.valueOf(10415L), "3GPP", "ACTIVE");
		AttributeData tgppUsrLocationInfoAvpId = new AttributeData(vendorInformation.getVendorId(), "22",
				"3GPP-User-Location-Info", "yes", "no", "yes", AVPType.valueOf("USERLOCATIONINFO"), "ACTIVE",
				"Diameter", null, null, null, supportedValueMap);
		this.attributeMap.put(tgppUsrLocationInfoAvpId, tgppUsrLocationInfoBuilder);
		this.attributeIdMap.put(String.valueOf("10415:22"), tgppUsrLocationInfoAvpId);
	}

	private static DiameterDictionary dictionaryInstance = new DiameterDictionary();
	
	public static final String MODULE = "DIAMETER-DICTIONARY";

	private static final String OTHER_TYPE = "OCTETS";

	private static final String ANY = "*";

	public static final String VALUE = "value";

	public static final String ID = "id";

	public static final String TYPE = "type";

	public static final String VENDOR_ID = "vendorid";

	public static final String APPLICATION_ID = "applicationid";

	public static final String YES = "yes";

	public static final int STANDARD_VENDOR_ID = 0;

	public static final long VALUE_NOT_FOUND = -1L;

	public static final String ATTRIBUTE_LIST = "attribute-list";

	public static final String ATTRIBUTE = "attribute";

	public static final String SUPPORTED_VALUES = "supported-values";

	public static final String MANDATORY = "mandatory";

	public static final String PROTECTED = "protected";

	public static final String ENCRYPTION = "encryption";

	public static final String FIXED = "fixed";

	public static final String REQUIRED = "required";

	public static final String OPTIONAL = "optional";

	public static final String VENDOR_NAME = "vendor-name";

	public static final String APPLICATION_NAME = "application-name";

	public static final int STANDARD_APPLICATION_ID = 0;

	private static final String NAME = "name";

	private static final String GROUPED = "grouped";

	private static final String ATTRIBUTE_RULE = "attributerule";

	private static final String MINIMUM = "minimum";

	private static final String MAXIMUM = "maximum";

	private static final String STATUS = "Active";

	private static final String DICTIONARY_TYPE = "Diameter";

	private static final String XML_EXTENSION = ".xml";

	static {
		dictionaryInstance.setDefaultAttributes();
	}

	public static boolean contains(String type) {
		for (AVPType avpType : AVPType.values()) {
			if (avpType.name().equalsIgnoreCase(type))
				return true;
		}
		return false;
	}

	public static DiameterDictionary getInstance() {
		return dictionaryInstance;
	}

	public void readDictionary(DiameterDictionaryModel diameterDictionaryModel, ILicenseValidator licenseValidator)
			throws DictionaryParseException {
		Map<AttributeData, BaseAVPBuilder> attributeDataToBuidler = new HashMap<>();
		Map<String, AttributeData> idToAttributeData = new HashMap<>();
		for (VendorInformation vendorInformation : diameterDictionaryModel.getIdtoVendorInformation().values()) {
			List<AttributeData> attributeDataList = vendorInformation.getAttributeData();
			for (AttributeData attributeData : attributeDataList) {
				BaseAVPBuilder baseAVPBuilder = createAvpBuilder(vendorInformation, attributeData);
				attributeData.setAVPId(vendorInformation.getVendorId(), attributeData.getAttributeId());
				idToAttributeData.put(attributeData.getAVPId(), attributeData);
				attributeDataToBuidler.put(attributeData, baseAVPBuilder);
			}
		}
		this.vendorMap = diameterDictionaryModel.getIdtoVendorInformation();
		this.attributeMap = attributeDataToBuidler;
		this.attributeIdMap = idToAttributeData;
		this.vendorIdList = diameterDictionaryModel.getVendorIds();
	}

	private BaseAVPBuilder createAvpBuilder(VendorInformation vendorInformation, AttributeData attributeData)
			throws DictionaryParseException {
		BaseAVPBuilder baseAVPBuilder = attributeData.getType().baseAVPBuilder();
		String attributeId = attributeData.getAttributeId();
		String vendorId = vendorInformation.getVendorId();
		try {
			baseAVPBuilder.setAVPCode(Integer.parseInt(attributeId));
		} catch (NumberFormatException e) {
			if ("*".equalsIgnoreCase(attributeId)) {
				attributeId = "-1";
				baseAVPBuilder.setAVPCode(-1);
			} else {
				throw new DictionaryParseException("Invalid attribute id " + attributeId + " found");
			}
		}
		try {
			baseAVPBuilder.setVendorId(Integer.parseInt(vendorId));
		} catch (NumberFormatException e) {
			if ("*".equalsIgnoreCase(vendorId)) {
				vendorId = "-1";
				baseAVPBuilder.setVendorId(-1);
			} else {
				throw new DictionaryParseException("Invalid vendor id " + vendorId + " found");
			}
		}
		baseAVPBuilder.setAVPId(Integer.parseInt(vendorId), Integer.parseInt(attributeId));
		baseAVPBuilder.setAVPEncryption(attributeData.getEncryption());
		if (!vendorId.equals(String.valueOf(0)))
			baseAVPBuilder.setVendorBit();
		if (attributeData.getMandatory() != null && "yes".equalsIgnoreCase(attributeData.getMandatory()))
			baseAVPBuilder.setMandatoryBit();
		if (attributeData.getProtectedValue() != null && "yes".equalsIgnoreCase(attributeData.getProtectedValue()))
			baseAVPBuilder.setProtectedBit();
		if (AVPType.ENUMERATED.name().equalsIgnoreCase(attributeData.getType().toString())) {
			((AvpEnumeratedBuilder) baseAVPBuilder).setSupportedValuesMap(attributeData.getIdToSupportedValues());
		} else if (AVPType.UNSIGNED32.name().equalsIgnoreCase(attributeData.getType().toString())) {
			((AvpUnsigned32Builder) baseAVPBuilder).setSupportedValuesMap(attributeData.getIdToSupportedValues());
		}
		return baseAVPBuilder;
	}

	public void load(DiameterDictionaryModel diameterDictionaryModel, ILicenseValidator validator) throws Exception {
		try {
			readDictionary(diameterDictionaryModel, validator);
		} catch (DictionaryParseException e) {
			LogManager.getLogger().trace("DIAMETER-DICTIONARY", e);
			LogManager.getLogger().error("DIAMETER-DICTIONARY", e.getMessage());
		}
	}

	private boolean doLicenseValidation(DiameterDictionaryModel diameterDictionaryModel,
			ILicenseValidator licenseValidator) {
		for (String vendorId : diameterDictionaryModel.getVendorIds()) {
			if (licenseValidator != null && !licenseValidator.isVendorSupported(String.valueOf(vendorId))) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn("DIAMETER-DICTIONARY", "License not acquired for " + vendorId + "-"
							+ ((VendorInformation) diameterDictionaryModel.getIdtoVendorInformation().get(vendorId))
									.getName()
							+ ". Hence dictionary will not be loaded.");
				return false;
			}
		}
		return true;
	}

	private void parseGroupedAvp(String parentKey) {
		ArrayList<List<AvpRule>> list = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			List<AvpRule> avpRuleList = list.get(i);
			if (!Collectionz.isNullOrEmpty(avpRuleList))
				for (int j = 0; j < avpRuleList.size(); j++) {
					AvpRule avpRule = avpRuleList.get(j);
					if (avpRule.getVendorId() != -1 || avpRule.getAttrId() != -1) {
						AttributeData tempAvpId = this.attributeIdMap
								.get(avpRule.getVendorId() + ":" + avpRule.getAttrId());
						this.attributeIdMap.put(parentKey + "." + tempAvpId.getAVPId(), tempAvpId);
						if (tempAvpId.isGrouped())
							parseGroupedAvp(parentKey + "." + tempAvpId.getAVPId());
					}
				}
		}
	}

	public IDiameterAVP getAttribute(int iAttrId) {
		return getAttribute("0:" + iAttrId);
	}

	public List<String> getDictionaryNames() {
		return new ArrayList<>(this.dictionariesRead);
	}

	public IDiameterAVP getAttribute(long iVendorId, int iAttrId) {
		return getAttribute(iVendorId + ":" + iAttrId);
	}

	public IDiameterAVP getAttribute(String attrID) {
		IDiameterAVP diameterAVP = null;
		if (attrID != null) {
			AttributeData avpId = getAttributeId(attrID);
			if (avpId != null) {
				diameterAVP = this.attributeMap.get(avpId).createAVP();
			} else {
				LogManager.getLogger().warn("DIAMETER-DICTIONARY", "Unknown Attribute: " + attrID);
				diameterAVP = getUnknownAttribute(attrID);
			}
		} else {
			LogManager.getLogger().warn("DIAMETER-DICTIONARY", "Unknown Attribute: " + attrID);
			diameterAVP = new UnknownAttribute();
		}
		return diameterAVP;
	}

	public IDiameterAVP getKnownAttribute(String attrID) {
		IDiameterAVP diameterAVP = null;
		if (attrID != null) {
			AttributeData avpId = getAttributeId(attrID);
			if (avpId != null)
				diameterAVP = ((BaseAVPBuilder) this.attributeMap.get(avpId)).createAVP();
		}
		return diameterAVP;
	}

	private UnknownAttribute getUnknownAttribute(String attrID) {
		StringTokenizer tokenizer = new StringTokenizer(attrID, ":");
		String vendorId = null;
		String attrId = null;
		if (tokenizer.hasMoreTokens()) {
			vendorId = tokenizer.nextToken();
			if (tokenizer.hasMoreTokens())
				attrId = tokenizer.nextToken();
		}
		if (vendorId != null && attrId != null)
			try {
				int intVendorId = Integer.parseInt(vendorId);
				int intAttrId = Integer.parseInt(attrId);
				byte bFlag = 0;
				if (intVendorId > 0)
					bFlag = (byte) (bFlag | Byte.MIN_VALUE);
				return new UnknownAttribute(intAttrId, intVendorId, bFlag, attrID, "false");
			} catch (Exception e) {
				if (LogManager.getLogger().isInfoLogLevel())
					LogManager.getLogger().info("DIAMETER-DICTIONARY", "Unknown Attribute" + attrID + "Reason: " + e);
				return new UnknownAttribute();
			}
		return new UnknownAttribute();
	}

	public AttributeData getAttributeId(String attrID) {
		AttributeData diameterAvpId = null;
		diameterAvpId = this.attributeIdMap.get(attrID);
		if (diameterAvpId == null)
			try {
				StringTokenizer attributeTokens = new StringTokenizer(attrID, ".");
				if (attributeTokens.countTokens() >= 2) {
					String parentId = attributeTokens.nextToken();
					String childId = attributeTokens.nextToken();
					AttributeData parentAvpId = this.attributeIdMap.get(parentId);
					AttributeData childAvpId = this.attributeIdMap.get(childId);
					if (parentAvpId != null && childAvpId != null) {
						if (childAvpId.isGrouped()) {
							parseGroupedAvp(parentAvpId.getAVPId() + "." + childAvpId.getAVPId());
						} else {
							this.attributeIdMap.put(attrID, childAvpId);
						}
						diameterAvpId = this.attributeIdMap.get(attrID);
					}
				}
			} catch (Exception e) {
				LogManager.getLogger().trace("DIAMETER-DICTIONARY", e);
			}
		return diameterAvpId;
	}

	public String getAttributeName(int intAVPCode) {
		return getAttributeName("0:" + intAVPCode);
	}

	public String getAttributeName(int intVendorId, int intAVPCode) {
		return getAttributeName(intVendorId + ":" + intAVPCode);
	}

	public String getAttributeName(String strId) {
		AttributeData avpId = this.attributeIdMap.get(strId);
		if (avpId != null)
			return avpId.getName();
		return "unknown-attribute";
	}

	public String getStrAVPId(String name) {
		if (name != null && this.attributeIdMap.get(name) != null)
			return String.valueOf(((AttributeData) this.attributeIdMap.get(name)).getAVPId());
		return null;
	}

	public List<String> getVendorIDs() {
		List<String> vendorIds = new ArrayList<>();
		vendorIds.addAll(this.vendorIdList);
		return vendorIds;
	}

	public long getKeyFromValue(String attribute, String value) {
		AttributeData diameterAvpId = this.attributeIdMap.get(attribute);
		if (diameterAvpId != null) {
			Long val = Long.valueOf(diameterAvpId.getKeyForValue(value));
			if (val != null)
				return val.longValue();
		}
		return -1L;
	}

	public static String nameOf(String avpCode) {
		return getInstance().getAttributeName(avpCode);
	}

	public static class AvpOctetStringBuilder extends BaseAVPBuilder {
		public IDiameterAVP createAVP() {
			return (IDiameterAVP) new AvpOctetString(this.intAVPCode, this.intVendorId, this.bAVPFlag, this.strAvpId,
					this.strAVPEncryption);
		}
	}

	public static class AvpUnsigned32Builder extends BaseAVPBuilder {
		Map<Integer, String> supportedValues = new HashMap<>();

		public void setSupportedValuesMap(Map<Integer, String> supportedValueMap) {
			this.supportedValues = supportedValueMap;
		}

		public IDiameterAVP createAVP() {
			return (IDiameterAVP) new AvpUnsigned32(this.intAVPCode, this.intVendorId, this.bAVPFlag, this.strAvpId,
					this.strAVPEncryption, this.supportedValues);
		}
	}

	public static class AvpAddressBuilder extends BaseAVPBuilder {
		public IDiameterAVP createAVP() {
			return (IDiameterAVP) new AvpAddress(this.intAVPCode, this.intVendorId, this.bAVPFlag, this.strAvpId,
					this.strAVPEncryption);
		}
	}

	public static class AvpDiameterIdentityBuilder extends BaseAVPBuilder {
		public IDiameterAVP createAVP() {
			return (IDiameterAVP) new AvpDiameterIdentity(this.intAVPCode, this.intVendorId, this.bAVPFlag,
					this.strAvpId, this.strAVPEncryption);
		}
	}

	public static class AvpDiameterURIBuilder extends BaseAVPBuilder {
		public IDiameterAVP createAVP() {
			return (IDiameterAVP) new AvpDiameterURI(this.intAVPCode, this.intVendorId, this.bAVPFlag, this.strAvpId,
					this.strAVPEncryption);
		}
	}

	public static class AvpFloat32Builder extends BaseAVPBuilder {
		public IDiameterAVP createAVP() {
			return (IDiameterAVP) new AvpFloat32(this.intAVPCode, this.intVendorId, this.bAVPFlag, this.strAvpId,
					this.strAVPEncryption);
		}
	}

	public static class AvpFloat64Builder extends BaseAVPBuilder {
		public IDiameterAVP createAVP() {
			return (IDiameterAVP) new AvpFloat64(this.intAVPCode, this.intVendorId, this.bAVPFlag, this.strAvpId,
					this.strAVPEncryption);
		}
	}

	public static class AvpIpv4AddressBuilder extends BaseAVPBuilder {
		public IDiameterAVP createAVP() {
			return (IDiameterAVP) new AvpIPv4Address(this.intAVPCode, this.intVendorId, this.bAVPFlag, this.strAvpId,
					this.strAVPEncryption);
		}
	}

	public static class AvpGroupedBuilder extends BaseGroupAvpBuilder {
		public IDiameterAVP createAVP() {
			return (IDiameterAVP) new AvpGrouped(this.intAVPCode, this.intVendorId, this.bAVPFlag, this.strAvpId,
					this.strAVPEncryption, this.fixedAttrList, this.requiredAttrList, this.optionalAttrList);
		}
	}

	public static class AvpInteger32Builder extends BaseAVPBuilder {
		public IDiameterAVP createAVP() {
			return (IDiameterAVP) new AvpInteger32(this.intAVPCode, this.intVendorId, this.bAVPFlag, this.strAvpId,
					this.strAVPEncryption);
		}
	}

	public static class AvpInteger64Builder extends BaseAVPBuilder {
		public IDiameterAVP createAVP() {
			return (IDiameterAVP) new AvpInteger64(this.intAVPCode, this.intVendorId, this.bAVPFlag, this.strAvpId,
					this.strAVPEncryption);
		}
	}

	public static class AvpTimeBuilder extends BaseAVPBuilder {
		public IDiameterAVP createAVP() {
			return (IDiameterAVP) new AvpTime(this.intAVPCode, this.intVendorId, this.bAVPFlag, this.strAvpId,
					this.strAVPEncryption);
		}
	}

	public static class AvpUnsigned64Builder extends BaseAVPBuilder {
		public IDiameterAVP createAVP() {
			return (IDiameterAVP) new AvpUnsigned64(this.intAVPCode, this.intVendorId, this.bAVPFlag, this.strAvpId,
					this.strAVPEncryption);
		}
	}

	public static class AvpUTF8StringBuilder extends BaseAVPBuilder {
		public IDiameterAVP createAVP() {
			return (IDiameterAVP) new AvpUTF8String(this.intAVPCode, this.intVendorId, this.bAVPFlag, this.strAvpId,
					this.strAVPEncryption);
		}
	}

	public static class AvpEnumeratedBuilder extends BaseAVPBuilder {
		Map<Integer, String> supportedValues = new HashMap<>();

		public IDiameterAVP createAVP() {
			return (IDiameterAVP) new AvpEnumerated(this.intAVPCode, this.intVendorId, this.bAVPFlag, this.strAvpId,
					this.strAVPEncryption, this.supportedValues);
		}

		public void setSupportedValuesMap(Map<Integer, String> supportedValueMap) {
			this.supportedValues = supportedValueMap;
		}
	}

	public static class AvpUserLocationInfoAvpBuilder extends BaseAVPBuilder {
		public IDiameterAVP createAVP() {
			return (IDiameterAVP) new AvpUserLocationInfo(this.intAVPCode, this.intVendorId, this.bAVPFlag,
					this.strAvpId, this.strAVPEncryption);
		}
	}

	public static class UserEquipmentInfoValueAvpBuilder extends BaseAVPBuilder {
		public IDiameterAVP createAVP() {
			return (IDiameterAVP) new AvpUserEquipmentInfoValue(this.intAVPCode, this.intVendorId, this.bAVPFlag,
					this.strAvpId, this.strAVPEncryption);
		}
	}

	public static class UserEquipmentInfoAvpBuilder extends BaseGroupAvpBuilder {
		public IDiameterAVP createAVP() {
			return (IDiameterAVP) new AvpUserEquipmentInfo(this.intAVPCode, this.intVendorId, this.bAVPFlag,
					this.strAvpId, this.strAVPEncryption, this.fixedAttrList, this.requiredAttrList,
					this.optionalAttrList);
		}
	}

	public String findDictionaryName(File file, long vendorId, long applicationId) {
		if (!file.exists())
			return null;
		if (file.isDirectory()) {
			File[] fileList = file.listFiles();
			int i = 0;
			if (i < fileList.length)
				return findDictionaryName(fileList[i], vendorId, applicationId);
		} else if (file.getName().endsWith(".xml")) {
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug("DIAMETER-DICTIONARY", "Reading dictionary file [" + file.getName() + "]");
			try (Reader inStream = new FileReader(file)) {
				if (matchDictionary(inStream, vendorId, applicationId))
					return file.getName();
			} catch (IOException e) {
				LogManager.getLogger().trace("DIAMETER-DICTIONARY", e);
				LogManager.getLogger().error("DIAMETER-DICTIONARY", e.getMessage());
			}
		}
		return null;
	}

	private boolean matchDictionary(Reader inStream, long vendorId2, long applicationId) {
		DocumentBuilderFactory factory = null;
		DocumentBuilder documentBuilder = null;
		Document docDictionaryXMLParse = null;
		try {
			String strVendorID = null;
			String strApplicationId = null;
			long lVendorId = 0L;
			long lApplicationId = 0L;
			factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);
			documentBuilder = factory.newDocumentBuilder();
			InputSource inputSource = new InputSource(inStream);
			docDictionaryXMLParse = documentBuilder.parse(inputSource);
			Node vendorNode = docDictionaryXMLParse.getElementsByTagName("attribute-list").item(0);
			if ("attribute-list".equals(vendorNode.getNodeName())) {
				if (vendorNode.getAttributes().getNamedItem("vendorid").getTextContent() != null) {
					strVendorID = vendorNode.getAttributes().getNamedItem("vendorid").getTextContent().trim();
					validateVendorIdFormat(strVendorID);
				}
				if (vendorNode.getAttributes().getNamedItem("applicationid").getTextContent() != null) {
					strApplicationId = vendorNode.getAttributes().getNamedItem("applicationid").getTextContent().trim();
					validateApplicationIdFormat(strApplicationId, strApplicationId + " is not a valid Application-id");
				}
				return (lVendorId == vendorId2 && lApplicationId == applicationId);
			}
		} catch (Exception exp) {
			LogManager.ignoreTrace(exp);
		}
		return false;
	}

	public void load(File file, ILicenseValidator licenseValidator) throws Exception {
		if (!file.exists())
			throw new FileNotFoundException("File(" + file.getAbsolutePath() + ") does not exist");
		Map<String, AttributeData> tempAttributeIdMap = new HashMap<>();
		Map<String, VendorInformation> tmpVendorMap = new HashMap<>();
		Map<AttributeData, BaseAVPBuilder> tmpAttributeMap = new HashMap<>();
		Map<String, AttributeData> tmpAttributeIdMap = new HashMap<>();
		List<String> tmpVendorList = new ArrayList<>();
		List<String> dictionariesRead = new ArrayList<>();
	
		readDictionary(file, tempAttributeIdMap, licenseValidator, tmpAttributeMap, tmpAttributeIdMap, tmpVendorMap,
				tmpVendorList, dictionariesRead);
		this.vendorMap.putAll(tmpVendorMap);;
		this.attributeMap.putAll(tmpAttributeMap);;
		this.attributeIdMap.putAll(tmpAttributeIdMap);;
		this.vendorIdList .addAll(tmpVendorList);
		this.dictionariesRead.addAll(dictionariesRead);
		for (Map.Entry<String, AttributeData> entry : tempAttributeIdMap.entrySet()) {
			AttributeData diameterAvpId = entry.getValue();
			if (diameterAvpId.isGrouped())
				parseGroupedAvp(entry.getKey());
		}
	}

	private static void readDictionary(File file, Map<String, AttributeData> tempAttributeIdMap,
			ILicenseValidator licenseValidator, Map<AttributeData, BaseAVPBuilder> attributeMap,
			Map<String, AttributeData> attributeIdMap, Map<String, VendorInformation> vendorMap,
			List<String> vendorList, List<String> dictionaryRead) throws Exception {
		if (file.isDirectory()) {
			File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++)
				readDictionary(fileList[i], tempAttributeIdMap, licenseValidator, attributeMap, attributeIdMap,
						vendorMap, vendorList, dictionaryRead);
		} else {
			if (!file.getName().endsWith(".xml"))
				return;
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug("DIAMETER-DICTIONARY", "Reading dictionary file [" + file.getName() + "]");
			try (Reader inStream = new FileReader(file)) {
				readDictionary(inStream, tempAttributeIdMap, file.getName(), licenseValidator, attributeMap,
						attributeIdMap, vendorMap, vendorList, dictionaryRead);
			} catch (IOException e) {
				LogManager.getLogger().trace("DIAMETER-DICTIONARY", e);
				LogManager.getLogger().error("DIAMETER-DICTIONARY", e.getMessage());
			}
		}
	}

	private static void readDictionary(Reader inStream, Map<String, AttributeData> tempAttributeIdMap,
			String xmlFileName, ILicenseValidator licenseValidator, Map<AttributeData, BaseAVPBuilder> attributeMap,
			Map<String, AttributeData> attributeIdMap, Map<String, VendorInformation> vendorMap,
			List<String> vendorIdList, List<String> dictionariesRead) throws DictionaryParseException {
		String strVendorID = null;
		String strVendorName = null;
		String strEncryption = null;
		String strMandatory = null;
		String strProtected = null;
		String strAttrType = null;
		VendorInformation vendor = null;
		int iVendorId = 0;
		Map<String, VendorInformation> tmpVendorMap = new HashMap<>();
		Map<AttributeData, BaseAVPBuilder> tmpAttributeMap = new HashMap<>();
		Map<String, AttributeData> tmpAttributeIdMap = new HashMap<>();
		DocumentBuilderFactory factory = null;
		DocumentBuilder documentBuilder = null;
		Document document = null;
		NodeList nodeList = null;
		
		try {
			factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);
			documentBuilder = factory.newDocumentBuilder();
			InputSource inputSource = new InputSource(inStream);
			document = documentBuilder.parse(inputSource);
			Node vendorNode = document.getElementsByTagName("attribute-list").item(0);
			nodeList = vendorNode.getChildNodes();
			if (vendorNode.getNodeName().equals("attribute-list")) {
				if (vendorNode.getAttributes().getNamedItem("vendorid").getTextContent() != null) {
					strVendorID = vendorNode.getAttributes().getNamedItem("vendorid").getTextContent().trim();
					iVendorId = validateVendorIdFormat(strVendorID);
				} else {
					throw new DictionaryParseException("Vendor-Id not defined for the dictionary.");
				}
				if (vendorNode.getAttributes().getNamedItem("vendor-name").getTextContent() != null) {
					strVendorName = vendorNode.getAttributes().getNamedItem("vendor-name").getTextContent().trim();
				} else {
					throw new DictionaryParseException("Vendor-Name not defined for the dictionary.");
				}

				if (!tmpVendorMap.containsKey(strVendorID)) {
					vendor = new VendorInformation(strVendorID, strVendorName, "Active");
					tmpVendorMap.put(strVendorID, vendor);
					tmpVendorMap.put(strVendorName, vendor);
					if (!vendorIdList.contains(strVendorID))
						vendorIdList.add(strVendorID);
				}
			}
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				String attrID = null;
				int iAttrID = -1;
				String attrName = null;
				BaseAVPBuilder baseAVPBuilder = null;

				if ("attribute".equals(node.getNodeName())) {
					NamedNodeMap namedNodeMap = node.getAttributes();

					Node idNode = namedNodeMap.getNamedItem("id");
					if (idNode != null && idNode.getTextContent() != null) {
						attrID = idNode.getTextContent().trim();
						iAttrID = validateAttributeIdFormat(strVendorID, strVendorName, attrID);
					}

					Node nameNode = namedNodeMap.getNamedItem("name");
					if (nameNode != null && nameNode.getTextContent() != null) {
						attrName = nameNode.getTextContent().trim();
					} else {
						throw new DictionaryParseException(
								"Attribute name not specified for " + strVendorID + ":" + strVendorName + ":" + attrID);
					}

					Node mandatoryNode = namedNodeMap.getNamedItem("mandatory");
					if (mandatoryNode != null && mandatoryNode.getTextContent() != null) {
						strMandatory = mandatoryNode.getTextContent();
					}

					Node protectedNode = namedNodeMap.getNamedItem("protected");
					if (protectedNode != null && protectedNode.getTextContent() != null) {
						strProtected = protectedNode.getTextContent();
					}

					Node encryptionNode = namedNodeMap.getNamedItem("encryption");
					if (encryptionNode != null && encryptionNode.getTextContent() != null) {
						strEncryption = encryptionNode.getTextContent();
					}

					Node typeNode = namedNodeMap.getNamedItem("type");
					if (typeNode != null && typeNode.getTextContent() != null) {
						strAttrType = typeNode.getTextContent().trim();
					} else {
						throw new DictionaryParseException(
								"Attribute type not specified for " + strVendorID + ":" + strVendorName + ":" + attrID);
					}

					// Choose appropriate builder based on type
					baseAVPBuilder = getAVPBuilder(strEncryption, strMandatory, strProtected, strAttrType, iVendorId,
							iAttrID);

					NodeList subNodeList = node.getChildNodes();
					Map<Integer, String> supportedValueMap = new HashMap<>();
					for (int k = 0; k < subNodeList.getLength(); k++) {
						Node dictNameNode = subNodeList.item(k);
						if ("supported-values".equals(dictNameNode.getNodeName())) {
							supportedValueMap.putAll(getSupportedValueMap(strVendorID, strVendorName, strAttrType,
									attrID, baseAVPBuilder, null, dictNameNode));
						} else if ("grouped".equals(dictNameNode.getNodeName())) {
							strAttrType = processGroupedAttribute(strVendorID, strEncryption, strMandatory,
									strProtected, strAttrType, tmpAttributeMap, tmpAttributeIdMap, attrID, attrName,
									baseAVPBuilder, dictNameNode);
						}
					}
					if (!contains(strAttrType))
						strAttrType = "OCTETS";

					setAttibuteData(strVendorID, strEncryption, strMandatory, strProtected, strAttrType,
							tmpAttributeMap, tmpAttributeIdMap, attrID, attrName, baseAVPBuilder, supportedValueMap);
				}
			}
		} catch (Exception e) {
			throw new DictionaryParseException(e);
		}
		dictionariesRead.add(xmlFileName);
		vendorMap.putAll(tmpVendorMap);
		attributeMap.putAll(tmpAttributeMap);
		attributeIdMap.putAll(tmpAttributeIdMap);
		tempAttributeIdMap.putAll(tmpAttributeIdMap);
	}

	private static void setAttibuteData(String strVendorID, String strEncryption, String strMandatory,
			String strProtected, String strAttrType, Map<AttributeData, BaseAVPBuilder> tmpAttributeMap,
			Map<String, AttributeData> tmpAttributeIdMap, String attrID, String attrName, BaseAVPBuilder baseAVPBuilder,
			Map<Integer, String> supportedValueMap) {
		AttributeData attributeData = new AttributeData(strVendorID, attrID, attrName, strMandatory,
				strProtected, strEncryption, AVPType.valueOf(strAttrType.toUpperCase()), "Active",
				"Diameter", null, null, null, supportedValueMap);
		tmpAttributeMap.put(attributeData, baseAVPBuilder);
		tmpAttributeIdMap.put(strVendorID + ":" + attrID, attributeData);
		tmpAttributeIdMap.put(attrName, attributeData);
	}

	private static BaseAVPBuilder getAVPBuilder(String strEncryption, String strMandatory, String strProtected,
			String strAttrType, int iVendorId, int iAttrID) {
		BaseAVPBuilder baseAVPBuilder;
		switch (strAttrType) {
		case "Unsigned32":
			baseAVPBuilder = new AvpUnsigned32Builder();
			break;
		case "Unsigned64":
			baseAVPBuilder = new AvpUnsigned64Builder();
			break;
		case "Integer32":
			baseAVPBuilder = new AvpInteger32Builder();
			break;
		case "Integer64":
			baseAVPBuilder = new AvpInteger64Builder();
			break;
		case "Float32":
			baseAVPBuilder = new AvpFloat32Builder();
			break;
		case "Float64":
			baseAVPBuilder = new AvpFloat64Builder();
			break;
		case "DiameterIdentity":
			baseAVPBuilder = new AvpDiameterIdentityBuilder();
			break;
		case "DiameterURI":
			baseAVPBuilder = new AvpDiameterURIBuilder();
			break;
		case "Time":
			baseAVPBuilder = new AvpTimeBuilder();
			break;
		case "UTF8String":
			baseAVPBuilder = new AvpUTF8StringBuilder();
			break;
		case "IPAddress":
			baseAVPBuilder = new AvpAddressBuilder();
			break;
		case "IPv4Address":
			baseAVPBuilder = new AvpIpv4AddressBuilder();
			break;
		case "Enumerated":
			baseAVPBuilder = new AvpEnumeratedBuilder();
			break;
		case "UserLocationInfo":
			baseAVPBuilder = new AvpUserLocationInfoAvpBuilder();
			break;
		case "UserEquipmentInfoValue":
			baseAVPBuilder = new UserEquipmentInfoValueAvpBuilder();
			break;
		case "UserEquipmentInfo":
			baseAVPBuilder = new UserEquipmentInfoAvpBuilder();
			break;
		case "Grouped":
			// If special handling for Grouped, implement here
			baseAVPBuilder = new AvpGroupedBuilder();
			break;
		default:
			baseAVPBuilder = new AvpOctetStringBuilder();
		}

		baseAVPBuilder.setAVPCode(iAttrID);
		baseAVPBuilder.setVendorId(iVendorId);
		baseAVPBuilder.setAVPId(iVendorId, iAttrID);
		baseAVPBuilder.setAVPEncryption(strEncryption);

		if (iVendorId != 0)
			baseAVPBuilder.setVendorBit();
		if ("yes".equalsIgnoreCase(strMandatory))
			baseAVPBuilder.setMandatoryBit();
		if ("yes".equalsIgnoreCase(strProtected))
			baseAVPBuilder.setProtectedBit();
		return baseAVPBuilder;
	}

	private static String processGroupedAttribute(String strVendorID, String strEncryption, String strMandatory,
			String strProtected, String strAttrType, Map<AttributeData, BaseAVPBuilder> tmpAttributeMap,
			Map<String, AttributeData> tmpAttributeIdMap, String attrID, String attrName, BaseAVPBuilder baseAVPBuilder,
			Node dictNameNode) throws DictionaryParseException {
		NodeList groupeNodeList = dictNameNode.getChildNodes();
		ArrayList<AvpRule> fixedAttrList = new ArrayList<>();
		ArrayList<AvpRule> requiredAttrList = new ArrayList<>();
		ArrayList<AvpRule> optionalAttrList = new ArrayList<>();
		for (int n = 0; n < groupeNodeList.getLength(); n++) {
			Node groupChildNode = groupeNodeList.item(n);
			String groupChildNodeName = groupChildNode.getNodeName();
			if (groupChildNodeName.equalsIgnoreCase("fixed") || groupChildNodeName.equalsIgnoreCase("optional")
					|| groupChildNodeName.equalsIgnoreCase("required")) {
				NodeList groupChildNodeList = groupChildNode.getChildNodes();
				int groupChildNodeListSize = groupChildNodeList.getLength();
				NamedNodeMap ruleNamedNodeMap = null;
				AvpRule avpRule = null;
				for (int m = 0; m < groupChildNodeListSize; m++) {
					Node attributeRuleNode = groupChildNodeList.item(m);
					if ("attributerule".equalsIgnoreCase(attributeRuleNode.getNodeName())) {
						ruleNamedNodeMap = attributeRuleNode.getAttributes();
						avpRule = new AvpRule();
						if (ruleNamedNodeMap.getNamedItem("vendor-id").getTextContent() != null) {
							validateVendorId(ruleNamedNodeMap, avpRule);
						} else {
							throw new DictionaryParseException("Vendor id not found in grouped avp.");
						}
						if (ruleNamedNodeMap.getNamedItem("id").getTextContent() != null) {
							validateAttributeId(ruleNamedNodeMap, avpRule);
						} else {
							throw new DictionaryParseException("Id not found in grouped avp.");
						}
						if (ruleNamedNodeMap.getNamedItem("name").getTextContent() != null) {
							avpRule.setName(ruleNamedNodeMap.getNamedItem("name").getTextContent());
						} else {
							throw new DictionaryParseException("Attribute name not found in grouped avp.");
						}
						if (ruleNamedNodeMap.getNamedItem("minimum").getTextContent() != null) {
							avpRule.setMinimum(ruleNamedNodeMap.getNamedItem("minimum").getTextContent());
						} else {
							throw new DictionaryParseException("Attribute minimum not found in grouped avp.");
						}
						if (ruleNamedNodeMap.getNamedItem("maximum").getTextContent() != null) {
							avpRule.setMaximum(ruleNamedNodeMap.getNamedItem("maximum").getTextContent());
						} else {
							throw new DictionaryParseException("Attribute maximum not found in grouped avp.");
						}
						if (groupChildNodeName.equalsIgnoreCase("fixed")) {
							fixedAttrList.add(avpRule);
						} else if (groupChildNodeName.equalsIgnoreCase("optional")) {
							optionalAttrList.add(avpRule);
						} else {
							requiredAttrList.add(avpRule);
						}
						if (!contains(strAttrType))
							strAttrType = "OCTETS";
						AttributeData attributeData = new AttributeData(strVendorID, attrID, attrName, strMandatory,
								strProtected, strEncryption, AVPType.valueOf(strAttrType.toUpperCase()), "Active",
								"Diameter", avpRule.getMinimum(), avpRule.getMaximum(),
								ruleNamedNodeMap.getNamedItem("vendor-id").getTextContent(), null);
						tmpAttributeMap.put(attributeData, baseAVPBuilder);
						tmpAttributeIdMap.put(strVendorID + ":" + attrID, attributeData);
						tmpAttributeIdMap.put(attrName, attributeData);
					}
				}
			}
		}
		((BaseGroupAvpBuilder) baseAVPBuilder).setOptionalAttrList(optionalAttrList);
		((BaseGroupAvpBuilder) baseAVPBuilder).setFixedAttrList(fixedAttrList);
		((BaseGroupAvpBuilder) baseAVPBuilder).setRequiredAttrList(requiredAttrList);
		return strAttrType;
	}

	public Map<Object, Object> readDictionaryForServerManager(Reader inStream) throws DictionaryParseException {
		Map<Object, Object> resultMap = new HashMap<>();
		Map<String, Object> attributeMap = new LinkedHashMap<>();
		Map<String, Object> attribute = null;
		String VENDOR_ID = "vendor_id";
		String VENDOR_NAME = "vendor_name";
		String APPLICATION_ID = "application_id";
		String APPLICATION_NAME = "application_name";
		String ATTRIBUTE_ID = "attribute_id";
		String ATTRIBUTE_NAME = "attribute_name";
		String ATTRIBUTE_MANDATORY = "attribute_mandatory";
		String ATTRIBUTE_PROTECTED = "attribute_protected";
		String ATTRIBUTE_ENCRYPTION = "attribute_encryption";
		String ATTRIBUTE_TYPE = "attribute_type";
		String SUPPORTED_VALUE = "supported_value";
		String FIXED_GROUP_ATTRIBUTE_LIST = "fixed_group_attribute_list";
		String REQUIRED_GROUP_ATTRIBUTE_LIST = "required_group_attribute_list";
		String OPTIONAL_GROUP_ATTRIBUTE_LIST = "optional_group_attribute_list";
		String ATTRIBUTE_LIST = "attribute_list";
		String strVendorID = null;
		String strVendorName = null;
		String strApplicationId = null;
		String strApplicationName = null;
		String strEncryption = null;
		String strMandatory = null;
		String strProtected = null;
		String strAttrType = null;
		int iVendorId = 0;
		int iApplicationId = 0;
		DocumentBuilderFactory factory = null;
		DocumentBuilder documentBuilder = null;
		Document docDictionaryXMLParse = null;
		NodeList nodeList = null;
		try {
			factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);
			documentBuilder = factory.newDocumentBuilder();
			InputSource inputSource = new InputSource(inStream);
			docDictionaryXMLParse = documentBuilder.parse(inputSource);
			nodeList = docDictionaryXMLParse.getElementsByTagName("attribute-list").item(0).getChildNodes();
			Node vendorNode = docDictionaryXMLParse.getElementsByTagName("attribute-list").item(0);
			if ("attribute-list".equals(vendorNode.getNodeName())) {
				if (vendorNode.getAttributes().getNamedItem("vendorid").getTextContent() != null) {
					strVendorID = vendorNode.getAttributes().getNamedItem("vendorid").getTextContent().trim();
					try {
						iVendorId = Integer.parseInt(strVendorID);
						resultMap.put("vendor_id", Integer.valueOf(iVendorId));
					} catch (NumberFormatException e) {
						throw new DictionaryParseException(strVendorID + " is not a valid Vendor-Id");
					}
				} else {
					throw new DictionaryParseException("Vendor-Id not defined for the dictionary.");
				}
				if (vendorNode.getAttributes().getNamedItem("vendor-name").getTextContent() != null) {
					strVendorName = vendorNode.getAttributes().getNamedItem("vendor-name").getTextContent().trim();
					resultMap.put("vendor_name", strVendorName);
				} else {
					throw new DictionaryParseException("Vendor-Name not defined for the dictionary.");
				}
				if (vendorNode.getAttributes().getNamedItem("applicationid").getTextContent() != null) {
					strApplicationId = vendorNode.getAttributes().getNamedItem("applicationid").getTextContent().trim();
					try {
						iApplicationId = Integer.parseInt(strApplicationId);
						resultMap.put("application_id", Integer.valueOf(iApplicationId));
					} catch (NumberFormatException e) {
						throw new DictionaryParseException(strVendorID + " is not a valid Application-Id");
					}
				} else {
					throw new DictionaryParseException("Application-Id not defined for the dictionary.");
				}
				if (vendorNode.getAttributes().getNamedItem("application-name").getTextContent() != null) {
					strApplicationName = vendorNode.getAttributes().getNamedItem("application-name").getTextContent()
							.trim();
					resultMap.put("application_name", strApplicationName);
				} else {
					throw new DictionaryParseException("Application-Name not defined for the dictionary.");
				}
				for (int i = 0; i < nodeList.getLength(); i++) {
					attribute = new HashMap<>();
					Node node = nodeList.item(i);
					String attrID = null;
					int iAttrID = -1;
					String attrName = null;
					if ("attribute".equals(node.getNodeName())) {
						if (node.getAttributes().getNamedItem("id").getTextContent() != null) {
							attrID = node.getAttributes().getNamedItem("id").getTextContent().trim();
							try {
								iAttrID = Integer.parseInt(attrID);
								attribute.put("attribute_id", Integer.valueOf(iAttrID));
							} catch (NumberFormatException e) {
								if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
									LogManager.getLogger().trace("DIAMETER-DICTIONARY", "Attribute id " + attrID
											+ " is not in proper format for " + strVendorID + ":" + strVendorName);
								throw new DictionaryParseException("Attribute id " + attrID
										+ " is not in proper format for " + strVendorID + ":" + strVendorName);
							}
						}
						if (node.getAttributes().getNamedItem("name").getTextContent() != null) {
							attrName = node.getAttributes().getNamedItem("name").getTextContent().trim();
							attribute.put("attribute_name", attrName);
						} else {
							throw new DictionaryParseException("Attribute name not specified for " + strVendorID + ":"
									+ strVendorName + ":" + attrID);
						}
						if (node.getAttributes().getNamedItem("type").getTextContent() != null) {
							strAttrType = node.getAttributes().getNamedItem("type").getTextContent().trim();
							attribute.put("attribute_type", strAttrType);
						} else {
							throw new DictionaryParseException("Attribute type not specified for " + strVendorID + ":"
									+ strVendorName + ":" + attrID);
						}
						if (node.getAttributes().getNamedItem("protected").getTextContent() != null) {
							strProtected = node.getAttributes().getNamedItem("protected").getTextContent();
							attribute.put("attribute_protected", strProtected);
						}
						if (node.getAttributes().getNamedItem("mandatory").getTextContent() != null) {
							strMandatory = node.getAttributes().getNamedItem("mandatory").getTextContent();
							attribute.put("attribute_mandatory", strMandatory);
						}
						if (node.getAttributes().getNamedItem("encryption").getTextContent() != null) {
							strEncryption = node.getAttributes().getNamedItem("encryption").getTextContent();
							attribute.put("attribute_encryption", strEncryption);
						}
						StringBuilder strSupportedValue = new StringBuilder("");
						attribute.put("supported_value", strSupportedValue.toString());
						NodeList subNodeList = node.getChildNodes();
						for (int k = 0; k < subNodeList.getLength(); k++) {
							String valueID = null;
							String valueName = null;
							Node dictNameNode = subNodeList.item(k);
							processNodes(attribute, "supported_value", "fixed_group_attribute_list",
									"required_group_attribute_list", "optional_group_attribute_list", strVendorID,
									strVendorName, attrID, strSupportedValue, dictNameNode);
						}
						attributeMap.put("" + iAttrID + "", attribute);
					}
				}
				resultMap.put("attribute_list", attributeMap);
			}
		} catch (SAXParseException sax) {
			LogManager.getLogger().error("DIAMETER-DICTIONARY", sax.getMessage());
			LogManager.getLogger().trace("DIAMETER-DICTIONARY", "Unexpected error while parsing dictionary " + sax);
			throw new DictionaryParseException("Unexpected error while parsing dictionary at Line Number: "
					+ sax.getLineNumber() + " Column Number: " + sax.getColumnNumber());
		} catch (IOException io) {
			LogManager.getLogger().error("DIAMETER-DICTIONARY", io.getMessage());
			LogManager.getLogger().trace("DIAMETER-DICTIONARY", "Unexpected error while parsing dictionary " + io);
			throw new DictionaryParseException("Unexpected error while parsing dictionary, Reason: " + io.getMessage());
		} catch (Exception ex) {
			LogManager.getLogger().error("DIAMETER-DICTIONARY", ex.getMessage());
			LogManager.getLogger().trace("DIAMETER-DICTIONARY", "Unexpected error while parsing dictionary " + ex);
			throw new DictionaryParseException("Unexpected error while parsing dictionary ");
		}
		return resultMap;
	}

	private void processNodes(Map<String, Object> attribute, String SUPPORTED_VALUE, String FIXED_GROUP_ATTRIBUTE_LIST,
			String REQUIRED_GROUP_ATTRIBUTE_LIST, String OPTIONAL_GROUP_ATTRIBUTE_LIST, String strVendorID,
			String strVendorName, String attrID, StringBuilder strSupportedValue, Node dictNameNode)
			throws DictionaryParseException {
		if ("supported-values".equals(dictNameNode.getNodeName())) {
			NodeList valueNodeList = dictNameNode.getChildNodes();
			for (int m = 0; m < valueNodeList.getLength(); m++) {
				Node valueNameNode = valueNodeList.item(m);
				if ("value".equals(valueNameNode.getNodeName())) {
					String valueID;
					String valueName;
					if (valueNameNode.getAttributes().getNamedItem("id").getTextContent() != null) {
						valueID = valueNameNode.getAttributes().getNamedItem("id").getTextContent().trim();
						try {
							Integer.parseInt(valueID);
						} catch (NumberFormatException e) {
							if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace("DIAMETER-DICTIONARY", "Not a valid attribute value id "
										+ valueID + " for " + strVendorID + ":" + strVendorName + ":" + attrID);
							throw new DictionaryParseException("Not a valid attribute value id " + valueID + " for "
									+ strVendorID + ":" + strVendorName + ":" + attrID);
						}
					} else {
						throw new DictionaryParseException(
								"Attribute value id not found for " + strVendorID + ":" + strVendorName + ":" + attrID);
					}
					if (valueNameNode.getAttributes().getNamedItem("name").getTextContent() != null) {
						valueName = valueNameNode.getAttributes().getNamedItem("name").getTextContent().trim();
					} else {
						throw new DictionaryParseException("Attribute value name not found for " + strVendorID + ":"
								+ strVendorName + ":" + attrID);
					}
					if (valueID != null && valueName != null)
						if ("".equals(strSupportedValue.toString())) {
							strSupportedValue.append(valueName);
							strSupportedValue.append(':');
							strSupportedValue.append(valueID);
						} else {
							strSupportedValue.append("," + valueName + ":" + valueID);
						}
				}
			}
			attribute.put(SUPPORTED_VALUE, strSupportedValue.toString());
		} else if ("Grouped".equalsIgnoreCase(dictNameNode.getNodeName())) {
			NodeList groupeNodeList = dictNameNode.getChildNodes();
			HashMap<String, Object> fixedAttrMap = new HashMap<>();
			HashMap<String, Object> requiredAttrMap = new HashMap<>();
			HashMap<String, Object> optionalAttrMap = new HashMap<>();
			for (int n = 0; n < groupeNodeList.getLength(); n++) {
				Node valueNameNode = groupeNodeList.item(n);
				if ("fixed".equals(valueNameNode.getNodeName())) {
					NodeList fixedNodeList = valueNameNode.getChildNodes();
					for (int j = 0; j < fixedNodeList.getLength(); j++) {
						Node valNameNode = fixedNodeList.item(j);
						if ("attributerule".equals(valNameNode.getNodeName())) {
							AvpRule avpRule = new AvpRule();
							processGroupedAttribute(fixedAttrMap, valNameNode, avpRule);
						}
					}
					attribute.put(FIXED_GROUP_ATTRIBUTE_LIST, fixedAttrMap);
				}
				if ("required".equals(valueNameNode.getNodeName())) {
					NodeList requiredNodeList = valueNameNode.getChildNodes();
					for (int j = 0; j < requiredNodeList.getLength(); j++) {
						Node valNameNode = requiredNodeList.item(j);
						if ("attributerule".equals(valNameNode.getNodeName())) {
							AvpRule avpRule = new AvpRule();
							processGroupedAttribute(requiredAttrMap, valNameNode, avpRule);
						}
					}
					attribute.put(REQUIRED_GROUP_ATTRIBUTE_LIST, requiredAttrMap);
				}
				if ("optional".equals(valueNameNode.getNodeName())) {
					NodeList optionalNodeList = valueNameNode.getChildNodes();
					for (int j = 0; j < optionalNodeList.getLength(); j++) {
						Node valNameNode = optionalNodeList.item(j);
						if ("attributerule".equals(valNameNode.getNodeName())) {
							AvpRule avpRule = new AvpRule();
							processGroupedAttribute(optionalAttrMap, valNameNode, avpRule);
						}
					}
					attribute.put(OPTIONAL_GROUP_ATTRIBUTE_LIST, optionalAttrMap);
				}
			}
		}
	}

	private void processGroupedAttribute(HashMap<String, Object> fixedAttrMap, Node valNameNode, AvpRule avpRule)
			throws DictionaryParseException {
		if (valNameNode.getAttributes().getNamedItem("vendor-id").getTextContent() != null) {
			try {
				avpRule.setVendorId(
						Integer.parseInt(valNameNode.getAttributes().getNamedItem("vendor-id").getTextContent()));
			} catch (NumberFormatException e) {
				if ("*".equalsIgnoreCase(valNameNode.getAttributes().getNamedItem("vendor-id").getTextContent())) {
					avpRule.setVendorId(-1);
				} else {
					throw new DictionaryParseException("Invalid vendor id found");
				}
			}
		} else {
			throw new DictionaryParseException("Attribute name not found in grouped avp.");
		}
		if (valNameNode.getAttributes().getNamedItem("id").getTextContent() != null) {
			try {
				avpRule.setAttrId(Integer.parseInt(valNameNode.getAttributes().getNamedItem("id").getTextContent()));
			} catch (NumberFormatException e) {
				if ("*".equalsIgnoreCase(valNameNode.getAttributes().getNamedItem("id").getTextContent())) {
					avpRule.setAttrId(-1);
				} else {
					throw new DictionaryParseException("Invalid Attribute id found");
				}
			}
		} else {
			throw new DictionaryParseException("Attribute name not found in grouped avp.");
		}
		if (valNameNode.getAttributes().getNamedItem("name").getTextContent() != null) {
			avpRule.setName(valNameNode.getAttributes().getNamedItem("name").getTextContent());
		} else {
			throw new DictionaryParseException("Attribute name not found in grouped avp.");
		}
		if (valNameNode.getAttributes().getNamedItem("minimum").getTextContent() != null) {
			avpRule.setMinimum(valNameNode.getAttributes().getNamedItem("minimum").getTextContent());
		} else {
			throw new DictionaryParseException("Attribute minimum not found in grouped avp.");
		}
		if (valNameNode.getAttributes().getNamedItem("maximum").getTextContent() != null) {
			avpRule.setMaximum(valNameNode.getAttributes().getNamedItem("maximum").getTextContent());
		} else {
			throw new DictionaryParseException("Attribute maximum not found in grouped avp.");
		}
		fixedAttrMap.put(avpRule.getName(), avpRule);
	}

	private static Map<Integer, String> getSupportedValueMap(String strVendorID, String strVendorName,
			String strAttrType, String attrID, BaseAVPBuilder baseAVPBuilder, String valueName, Node dictNameNode)
			throws DictionaryParseException {
		Map<Integer, String> supportedValueMap = new HashMap<>();
		NodeList valueNodeList = dictNameNode.getChildNodes();
		for (int m = 0; m < valueNodeList.getLength(); m++) {
			int ivalueID = -1;
			Node valueNameNode = valueNodeList.item(m);
			if ("value".equals(valueNameNode.getNodeName())) {
				if (valueNameNode.getAttributes().getNamedItem("id").getTextContent() != null) {
					String valueID = valueNameNode.getAttributes().getNamedItem("id").getTextContent().trim();
					ivalueID = validateAttributeValueIdFormat(valueID,
							"Not a valid attribute value id " + valueID + " for Not a valid attribute value id "
									+ valueID + " for " + strVendorID + ":" + strVendorName + ":" + attrID
									+ ":Not a valid attribute value id " + valueID + " for " + strVendorID + ":"
									+ strVendorName + ":" + attrID + ":Not a valid attribute value id " + valueID
									+ " for " + strVendorID + ":" + strVendorName + ":" + attrID);
				} else {
					throw new DictionaryParseException(
							"Attribute value id not found for " + strVendorID + ":" + strVendorName + ":" + attrID);
				}
				if (valueNameNode.getAttributes().getNamedItem("name").getTextContent() != null) {
					valueName = valueNameNode.getAttributes().getNamedItem("name").getTextContent().trim();
				} else {
					throw new DictionaryParseException(
							"Attribute value name not found for " + strVendorID + ":" + strVendorName + ":" + attrID);
				}
			}
			if (ivalueID != -1)
				supportedValueMap.put(Integer.valueOf(ivalueID), valueName);
		}
		if (AVPType.ENUMERATED.name().equalsIgnoreCase(strAttrType))
			((AvpEnumeratedBuilder) baseAVPBuilder).setSupportedValuesMap(supportedValueMap);
		if (AVPType.UNSIGNED32.name().equalsIgnoreCase(strAttrType))
			((AvpUnsigned32Builder) baseAVPBuilder).setSupportedValuesMap(supportedValueMap);
		return supportedValueMap;
	}

	private static int validateAttributeValueIdFormat(String valueID, String strMessage)
			throws DictionaryParseException {
		int ivalueID;
		try {
			ivalueID = Integer.parseInt(valueID);
		} catch (NumberFormatException e) {
			LogManager.getLogger().trace("DIAMETER-DICTIONARY", strMessage);
			throw new DictionaryParseException(strMessage);
		}
		return ivalueID;
	}

	private static int validateAttributeIdFormat(String strVendorID, String strVendorName, String attrID)
			throws DictionaryParseException {
		return validateAttributeValueIdFormat(attrID,
				"Attribute id " + attrID + " is not in proper format for " + strVendorID + ":" + strVendorName);
	}

	private static int validateApplicationIdFormat(String strApplicationId, String message)
			throws DictionaryParseException {
		int iApplicationId;
		try {
			iApplicationId = Integer.parseInt(strApplicationId);
		} catch (NumberFormatException e) {
			throw new DictionaryParseException(message);
		}
		return iApplicationId;
	}

	private static int validateVendorIdFormat(String strVendorID) throws DictionaryParseException {
		int iVendorId;
		try {
			iVendorId = Integer.parseInt(strVendorID);
		} catch (NumberFormatException e) {
			throw new DictionaryParseException(strVendorID + " is not a valid Vendor-Id");
		}
		return iVendorId;
	}

	private static void validateAttributeId(NamedNodeMap ruleNamedNodeMap, AvpRule avpRule)
			throws DictionaryParseException {
		try {
			avpRule.setAttrId(Integer.parseInt(ruleNamedNodeMap.getNamedItem("id").getTextContent()));
		} catch (NumberFormatException e) {
			if ("*".equalsIgnoreCase(ruleNamedNodeMap.getNamedItem("id").getTextContent())) {
				avpRule.setAttrId(-1);
			} else {
				throw new DictionaryParseException("Invalid id found");
			}
		}
	}

	private static void validateVendorId(NamedNodeMap ruleNamedNodeMap, AvpRule avpRule)
			throws DictionaryParseException {
		String vendorId = ruleNamedNodeMap.getNamedItem("vendor-id").getTextContent();
		try {
			avpRule.setVendorId(Integer.parseInt(vendorId));
		} catch (NumberFormatException e) {
			if ("*".equalsIgnoreCase(vendorId)) {
				avpRule.setVendorId(-1);
			} else {
				throw new DictionaryParseException("Invalid vendor id " + vendorId + "found");
			}
		}
	}

	public String getVendorNameById(String vendorId) {
		return ((VendorInformation) this.vendorMap.get(vendorId)).getName();
	}

	public String getAttributeNameById(String avpId) {
		return ((AttributeData) this.attributeIdMap.get(avpId)).getName();
	}

	public void loadFromDB(List<Vendor> attributeWithVendors) throws DictionaryParseException {
	    if (attributeWithVendors == null || attributeWithVendors.isEmpty()) {
	        throw new DictionaryParseException("Attribute with Vendor list is null or empty.");
	    }
		
	    Map<String, AttributeData> tempAttributeIdMap = new HashMap<>();
	    Map<String, VendorInformation> tmpVendorMap = new HashMap<>();
	    Map<AttributeData, BaseAVPBuilder> tmpAttributeMap = new HashMap<>();
	    Map<String, AttributeData> tmpAttributeIdMap = new HashMap<>();
	    List<String> tmpVendorList = new ArrayList<>();

	    try {

	        for (Vendor av : attributeWithVendors) {
	            List<Attribute> attributes = av.getAttributes();
	            VendorInformation vendor = new VendorInformation(String.valueOf(av.getVendor_id()), av.getName(), "Active");
	            
	            String vendorId = vendor.getVendorId();
	            String vendorName = vendor.getName();
	            int iVendorId = validateVendorIdFormat(vendorId);

	            if (!tmpVendorMap.containsKey(vendorId)) {
	                tmpVendorMap.put(vendorId, vendor);
	                tmpVendorMap.put(vendorName, vendor);
	                if (!tmpVendorList.contains(vendorId)) {
	                    tmpVendorList.add(vendorId);
	                }
	            }
	            for (Attribute attribute : attributes) {
		            BaseAVPBuilder baseAVPBuilder = getAVPBuilder(attribute.getEncryption(), attribute.getMandatory(), attribute.getProtectedFlag(), attribute.getType(), iVendorId,
		            		attribute.getAttributeId());
		            
		            Map<Integer, String> supportedValueMap = new HashMap<>();

		            String attrType = attribute.getType();
		            if (!contains(attrType)) {
		                attrType = "OCTETS";
		            }
		            setAttibuteData(vendorId, attribute.getEncryption(), attribute.getMandatory(), attribute.getProtectedFlag(), attrType,
		            		tmpAttributeMap, tmpAttributeIdMap, String.valueOf(attribute.getAttributeId()), attribute.getName(), baseAVPBuilder, supportedValueMap);
	            }
	        }
	    } catch (Exception e) {
	        throw new DictionaryParseException(e);
	    }

	    this.vendorMap.putAll(tmpVendorMap);
	    this.attributeMap.putAll(tmpAttributeMap);
	    this.attributeIdMap.putAll(tmpAttributeIdMap);
	    this.vendorIdList.addAll(tmpVendorList);

	    for (Map.Entry<String, AttributeData> entry : tempAttributeIdMap.entrySet()) {
	        AttributeData diameterAvpId = entry.getValue();
	        if (diameterAvpId.isGrouped()) {
	            parseGroupedAvp(entry.getKey());
	        }
	    }
		
	}

}
