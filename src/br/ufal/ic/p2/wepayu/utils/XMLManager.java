package br.ufal.ic.p2.wepayu.utils;

import br.ufal.ic.p2.wepayu.models.Employee;
import br.ufal.ic.p2.wepayu.models.EmployeeHistory;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;

public class XMLManager {
    private String fileType;
    private String fileName;
    private File file;
    public XMLManager(String fileType, String fileName) throws Exception {
        try {
            this.fileType = fileType;
            this.fileName = "src/br/ufal/ic/p2/wepayu/database/"+fileName+".xml";
            this.file = new File(this.fileName);

            if(!this.file.exists()) {
                this.file.createNewFile();
            }
        } catch(Exception error) {
            error.printStackTrace();
        }
    }

    public List<Employee> readAndGetEmployeeFile() throws Exception, ParserConfigurationException, IOException, SAXException {
        if(!this.fileType.equals("employee")) {
            throw new Exception("invalid method");
        }

        List<Employee> newEmployeeListToReturn = new ArrayList<Employee>();

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

                    // Obter informações do elemento "employee"
                    String id = employeeElement.getAttribute("id");
                    String name = employeeElement.getElementsByTagName("name").item(0).getTextContent();
                    String address = employeeElement.getElementsByTagName("address").item(0).getTextContent();
                    String type = employeeElement.getElementsByTagName("type").item(0).getTextContent();
                    String remuneration = employeeElement.getElementsByTagName("remuneration").item(0).getTextContent();
                    String commission = employeeElement.getElementsByTagName("commission").item(0).getTextContent();
                    String unionized = employeeElement.getElementsByTagName("unionized").item(0).getTextContent();

                    Employee newEmployee = new Employee(
                        id,
                        name,
                        address,
                        type,
                        Double.parseDouble(remuneration),
                        Double.parseDouble(commission),
                        Boolean.parseBoolean(unionized)
                    );

                    newEmployeeListToReturn.add(newEmployee);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newEmployeeListToReturn;
    }


    public void createAndSaveEmployeeDocument(List<Employee> newEmployeeList) throws Exception {
        if(!this.fileType.equals("employee")) {
            throw new Exception("invalid method");
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document document = db.newDocument();

        Element rootElement = document.createElement("Employees");
        document.appendChild(rootElement);

        for (Employee newEmployee : newEmployeeList) {
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
            childUnionizedElement.appendChild(document.createTextNode(Double.toString(newEmployee.getCommission())));

            Attr attr = document.createAttribute("id");
            attr.setValue(newEmployee.getId());
            childElement.setAttributeNode(attr);

            rootElement.appendChild(childElement);
            childElement.appendChild(childNameElement);
            childElement.appendChild(childAddressElement);
            childElement.appendChild(childTypeElement);
            childElement.appendChild(childRemunerationElement);
            childElement.appendChild(childComissionElement);
            childElement.appendChild(childUnionizedElement);
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(this.file);

        t.transform(domSource, streamResult);
    }

    public void createAndSaveHistoryDocument(List<EmployeeHistory> newHistoryList) throws Exception {
        if(!this.fileType.equals("history")) {
            throw new Exception("invalid method");
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document document = db.newDocument();

        Element rootElement = document.createElement("EmployeesHistory");
        document.appendChild(rootElement);

        for (EmployeeHistory newHistory : newHistoryList) {
            Element childElement = document.createElement("history");

            Element childEmployeeIdElement = document.createElement("employeeId");
            childEmployeeIdElement.appendChild(document.createTextNode(newHistory.getEmployeeId()));

            Element childDateElement = document.createElement("date");
            childDateElement.appendChild(document.createTextNode(newHistory.getDate().toString()));

            Element childHoursElement = document.createElement("hours");
            childHoursElement.appendChild(document.createTextNode(Double.toString(newHistory.getHours())));

            Attr attr = document.createAttribute("id");
            attr.setValue(newHistory.getId());
            childElement.setAttributeNode(attr);

            rootElement.appendChild(childElement);
            childElement.appendChild(childEmployeeIdElement);
            childElement.appendChild(childDateElement);
            childElement.appendChild(childHoursElement);
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(this.file);

        t.transform(domSource, streamResult);
    }

    public List<EmployeeHistory> readAndGetHistoryFile() throws Exception {
        if(!this.fileType.equals("history")) {
            throw new Exception("invalid method");
        }

        List<EmployeeHistory> newHistoryListToReturn = new ArrayList<EmployeeHistory>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(this.fileName);

            Element rootElement = document.getDocumentElement();

            NodeList historyList = rootElement.getElementsByTagName("history");

            for (int i = 0; i < historyList.getLength(); i++) {
                Node historyNode = historyList.item(i);

                if (historyNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element employeeElement = (Element) historyNode;

                    // Obter informações do elemento "employee"
                    String id = employeeElement.getAttribute("id");
                    String employeeId = employeeElement.getElementsByTagName("employeeId").item(0).getTextContent();
                    String date = employeeElement.getElementsByTagName("date").item(0).getTextContent();
                    String hours = employeeElement.getElementsByTagName("hours").item(0).getTextContent();

                    EmployeeHistory newHistory = new EmployeeHistory(id, employeeId, DateFormat.stringToDate(date, false), Double.parseDouble(hours));

                    newHistoryListToReturn.add(newHistory);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newHistoryListToReturn;
    }
}
