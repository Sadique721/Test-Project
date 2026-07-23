package com.savbill.integrationsystem.Mpesa.Util;

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.namespace.QName;
import java.io.StringReader;
import java.io.StringWriter;

public class JAXBHelper {

    // Method to marshal a Java object to XML string
    public static String marshal(Object response) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(response.getClass());
        Marshaller marshaller = context.createMarshaller();

        marshaller.setProperty(
                "com.sun.xml.bind.namespacePrefixMapper",
                new com.sun.xml.bind.marshaller.NamespacePrefixMapper() {
                    @Override
                    public String getPreferredPrefix(String s, String s1, boolean b) {
                        if ("http://inforwise.co.tz/broker/".equals(s))
                            return "";
                        return s1;
                    }
                }
        );

        // Pretty print
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        // IMPORTANT: remove standalone="yes"
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

        //marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, ValidateMpesaConstant.XML_NAMESPACE);

        StringWriter writer = new StringWriter();

        // Manually control XML declaration
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

        try {
            marshaller.marshal(response, writer);
        } catch (MarshalException e) {
            // Fallback: wrap dynamically
            QName qName = resolveQName(response.getClass());
            @SuppressWarnings("unchecked")
            JAXBElement<Object> jaxbElement =
                    new JAXBElement<>(qName, (Class<Object>) response.getClass(), response);
            marshaller.marshal(jaxbElement, writer);
        }

        return writer.toString();
    }

    // Method to unmarshal XML string into Java object
    public static <T> T unmarshal(String xml, Class<T> clazz) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        StringReader reader = new StringReader(xml);
        return (T) unmarshaller.unmarshal(reader);
    }

    private static QName resolveQName(Class<?> clazz) {
        XmlRootElement root = clazz.getAnnotation(XmlRootElement.class);

        String localName;
        String namespace;

        if (root != null && !"##default".equals(root.name())) {
            localName = root.name();
        } else {
            localName = clazz.getSimpleName();
        }

        if (root != null && !"##default".equals(root.namespace())) {
            namespace = root.namespace();
        } else {
            Package pkg = clazz.getPackage();
            XmlSchema schema = pkg != null ? pkg.getAnnotation(XmlSchema.class) : null;
            namespace = schema != null ? schema.namespace() : "";
        }

        return new QName(namespace, localName);
    }

}