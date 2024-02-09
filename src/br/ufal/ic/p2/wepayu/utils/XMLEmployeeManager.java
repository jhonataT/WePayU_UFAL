package br.ufal.ic.p2.wepayu.utils;

import br.ufal.ic.p2.wepayu.models.Employee;
import br.ufal.ic.p2.wepayu.models.Timestamp;
import br.ufal.ic.p2.wepayu.models.Sale;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLEmployeeManager {
    private String fileName;
    private File file;
    public XMLEmployeeManager(String fileName) throws Exception {
        try {
            this.fileName = "src/br/ufal/ic/p2/wepayu/database/"+fileName+".xml";
            this.file = new File(this.fileName);

            if(!this.file.exists()) {
                this.file.createNewFile();
            }
        } catch(Exception error) {
            error.printStackTrace();
        }
    }

    public String getFileName() {
        return this.fileName;
    }

    public File getFile() {
        return this.file;
    }

    public Map<String, Employee> readAndGetEmployeeFile() {
        Map<String, Employee> newEmployeeListToReturn = new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(this.fileName);

            Element rootElement = document.getDocumentElement();

            NodeList employeeList = rootElement.getElementsByTagName("employee");

            for (int i = 0; i < employeeList.getLength(); i++) {
                Node employeeNode = employeeList.item(i);

                if (employeeNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element employeeElement = (Element) employeeNode;

                    NodeList timestamps = employeeElement.getElementsByTagName("timestamp");
                    NodeList sales = employeeElement.getElementsByTagName("sale");

                    // Obter informações do elemento "employee"
                    String id = employeeElement.getAttribute("id");
                    String name = employeeElement.getElementsByTagName("name").item(0).getTextContent();
                    String address = employeeElement.getElementsByTagName("address").item(0).getTextContent();
                    String type = employeeElement.getElementsByTagName("type").item(0).getTextContent();
                    String remuneration = employeeElement.getElementsByTagName("remuneration").item(0).getTextContent();
                    String commission = employeeElement.getElementsByTagName("commission").item(0).getTextContent();
                    String unionized = employeeElement.getElementsByTagName("unionized").item(0).getTextContent();
                    String syndicateId = employeeElement.getElementsByTagName("syndicateId").item(0).getTextContent();

                    Employee newEmployee = new Employee(
                        id,
                        name,
                        address,
                        type,
                        Double.parseDouble(remuneration),
                        Double.parseDouble(commission),
                        Boolean.parseBoolean(unionized)
                    );

                    newEmployee.setLinkedSyndicate(syndicateId);

                    for (int j = 0; j < timestamps.getLength(); j++) {
                        Node timestampNode = timestamps.item(j);

                        Element timestampElement = (Element) timestampNode;

                        if(timestampNode.getNodeType() == Node.ELEMENT_NODE) {
                            String timeStampId = timestampElement.getElementsByTagName("id").item(0).getTextContent();
                            String timeStampDate = timestampElement.getElementsByTagName("date").item(0).getTextContent();
                            String timeStampHours = timestampElement.getElementsByTagName("hours").item(0).getTextContent();

                            Timestamp newTimeStamp = new Timestamp(
                                timeStampId,
                                DateFormat.stringToDate(timeStampDate, false),
                                Double.parseDouble(timeStampHours)
                            );

                            newEmployee.setTimestamp(newTimeStamp);
                        }
                    }

                    for (int k = 0; k < sales.getLength(); k++) {
                        Node saleNode = sales.item(k);

                        Element safeElement = (Element) saleNode;

                        if(saleNode.getNodeType() == Node.ELEMENT_NODE) {
                            String safeId = safeElement.getAttribute("id");
                            String safeDate = safeElement.getElementsByTagName("date").item(0).getTextContent();
                            String safeValue = safeElement.getElementsByTagName("value").item(0).getTextContent();

                            Sale newSale = new Sale(
                                safeId,
                                DateFormat.stringToDate(safeDate, false),
                                Double.parseDouble(safeValue)
                            );

                            newEmployee.setSale(newSale);
                        }
                    }

                    newEmployeeListToReturn.put(newEmployee.getId(), newEmployee);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newEmployeeListToReturn;
    }

    public void createAndSaveEmployeeDocument(Map<String, Employee> newEmployeeList) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document document = db.newDocument();

        Element rootElement = document.createElement("Employees");
        document.appendChild(rootElement);

        for(Employee newEmployee : newEmployeeList.values()) {
            List<Timestamp> timestamps = newEmployee.getTimestamp();
            String linkedSyndicate = newEmployee.getLinkedSyndicateId();
            List<Sale> sales = newEmployee.getSales();

            Element childElement = document.createElement("employee");

            Element childNameElement = document.createElement("name");
            childNameElement.appendChild(document.createTextNode(newEmployee.getName()));

            Element childAddressElement = document.createElement("address");
            childAddressElement.appendChild(document.createTextNode(newEmployee.getAddress()));

            Element childTypeElement = document.createElement("type");
            childTypeElement.appendChild(document.createTextNode(newEmployee.getType()));

            Element childRemunerationElement = document.createElement("remuneration");
            childRemunerationElement.appendChild(document.createTextNode(Double.toString(newEmployee.getRemuneration())));

            Element childComissionElement = document.createElement("commission");
            childComissionElement.appendChild(document.createTextNode(Double.toString(newEmployee.getCommission())));

            Element childUnionizedElement = document.createElement("unionized");
            childUnionizedElement.appendChild(document.createTextNode(Boolean.toString(newEmployee.getUnionized())));

            Element childTimestampsElement = document.createElement("timestamps");
            Element childSalesElement = document.createElement("sales");

            Element childSyndicatesElement = document.createElement("syndicates");

            Element childSyndicateElement = document.createElement("syndicateId");
            childSyndicateElement.appendChild(document.createTextNode(linkedSyndicate));

            for(Timestamp timestamp : timestamps) {
                Element childTimestampElement = document.createElement("timestamp");

                Element childtmIdElement = document.createElement("id");
                childtmIdElement.appendChild(document.createTextNode(timestamp.getId()));

                Element childtmDateElement = document.createElement("date");
                childtmDateElement.appendChild(document.createTextNode(timestamp.getDate().toString()));

                Element childtmHoursElement = document.createElement("hours");
                childtmHoursElement.appendChild(document.createTextNode(Double.toString(timestamp.getHours())));

                childTimestampsElement.appendChild(childTimestampElement);
                childTimestampElement.appendChild(childtmIdElement);
                childTimestampElement.appendChild(childtmDateElement);
                childTimestampElement.appendChild(childtmHoursElement);
            }

            for(Sale sale : sales) {
                Attr saleAttr = document.createAttribute("id");
                saleAttr.setValue(sale.getId());

                Element childSaleElement = document.createElement("sale");

                Element childSafeDateElement = document.createElement("date");
                childSafeDateElement.appendChild(document.createTextNode(sale.getDate().toString()));

                Element childSafeHoursElement = document.createElement("value");
                childSafeHoursElement.appendChild(document.createTextNode(Double.toString(sale.getValue())));

                childSalesElement.appendChild(childSaleElement);
                childSaleElement.setAttributeNode(saleAttr);
                childSaleElement.appendChild(childSafeDateElement);
                childSaleElement.appendChild(childSafeHoursElement);
            }

            Attr attr = document.createAttribute("id");
            attr.setValue(newEmployee.getId());
            childElement.setAttributeNode(attr);

            rootElement.appendChild(childElement);
            childElement.appendChild(childNameElement);
            childElement.appendChild(childTimestampsElement);
            childElement.appendChild(childSalesElement);
            childElement.appendChild(childAddressElement);
            childElement.appendChild(childTypeElement);
            childElement.appendChild(childRemunerationElement);
            childElement.appendChild(childComissionElement);
            childElement.appendChild(childUnionizedElement);
            childElement.appendChild(childSyndicatesElement);
            childSyndicatesElement.appendChild(childSyndicateElement);
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(this.file);

        t.transform(domSource, streamResult);
    }

}
