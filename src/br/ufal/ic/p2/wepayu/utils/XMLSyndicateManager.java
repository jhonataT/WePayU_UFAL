package br.ufal.ic.p2.wepayu.utils;

import br.ufal.ic.p2.wepayu.interfaces.InterfaceXMLManager;
import br.ufal.ic.p2.wepayu.models.*;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLSyndicateManager extends XMLEmployeeManager implements InterfaceXMLManager {
    public XMLSyndicateManager(String fileName) throws Exception {
        super(fileName);
    }

    @Override
    public String getFileName() {
        return super.getFileName();
    }

    @Override
    public File getFile() {
        return super.getFile();
    }

    public Map<String, Syndicate> readAndGetSyndicateFile(Map<String, Employee> employees){
        Map<String, Syndicate> newSyndicateListToReturn = new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(this.getFileName());

            Element rootElement = document.getDocumentElement();

            NodeList syndicateList = rootElement.getElementsByTagName("syndicate");

            for (int i = 0; i < syndicateList.getLength(); i++) {
                Node syndicateNode = syndicateList.item(i);

                if (syndicateNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element syndicateElement = (Element) syndicateNode;

                    NodeList unionizedEmployees = syndicateElement.getElementsByTagName("unionizedEmployee");
                    NodeList unionFees = syndicateElement.getElementsByTagName("unionFee");

                    String syndicateId = syndicateElement.getAttribute("id");

                    Syndicate newSyndicate = new Syndicate(syndicateId);

                    for (int j = 0; j < unionizedEmployees.getLength(); j++) {
                        Node unionizedEmployeeNode = unionizedEmployees.item(j);

                        Element unionizedEmployeeElement = (Element) unionizedEmployeeNode;

                        if(unionizedEmployeeElement.getNodeType() == Node.ELEMENT_NODE) {
                            String unionizedId = unionizedEmployeeElement.getElementsByTagName("unionizedId").item(0).getTextContent();
                            String unionFeeValue = unionizedEmployeeElement.getElementsByTagName("unionFeeValue").item(0).getTextContent();
                            String unionFeeDate = unionizedEmployeeElement.getElementsByTagName("unionFeeDate").item(0).getTextContent();
                            String employeeId = unionizedEmployeeElement.getElementsByTagName("employeeId").item(0).getTextContent();

                            Employee employee = employees.get(employeeId);

                            if(!employee.getId().isEmpty()) {
                                UnionizedEmployee newUnionizedEmployee = new UnionizedEmployee(
                                    employee.getId(),
                                    employee.getName(),
                                    employee.getType(),
                                    employee.getRemuneration(),
                                    employee.getSales(),
                                    unionizedId,
                                    Double.parseDouble(unionFeeValue),
                                    employee.getUnionized(),
                                    DateFormat.stringToDate(unionFeeDate, true)
                                );

                                newSyndicate.addNewEmployee(newUnionizedEmployee);
                            }
                        }
                    }

                    for (int k = 0; k < unionFees.getLength(); k++) {
                        Node unionFeeNode = unionFees.item(k);

                        Element unionFeeElement = (Element) unionFeeNode;

                        if(unionFeeNode.getNodeType() == Node.ELEMENT_NODE) {
                            String unionFeeId = unionFeeElement.getAttribute("id");
                            String unionFeeDate = unionFeeElement.getElementsByTagName("date").item(0).getTextContent();
                            String unionFeeValue = unionFeeElement.getElementsByTagName("value").item(0).getTextContent();

                            UnionFee newUnionFee = new UnionFee(
                                unionFeeId,
                                DateFormat.stringToDate(unionFeeDate, false),
                                Double.parseDouble(unionFeeValue)
                            );

                            newSyndicate.addNewUnionFee(newUnionFee);
                        }
                    }

                    newSyndicateListToReturn.put(syndicateId, newSyndicate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newSyndicateListToReturn;
    }

    public void createAndSaveSyndicateDocument(Map<String, Syndicate> newSyndicates) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document document = db.newDocument();

        Element rootElement = document.createElement("syndicates");
        document.appendChild(rootElement);

        for (Syndicate newSyndicate : newSyndicates.values()) {
            List<UnionizedEmployee> unionizedEmployees = newSyndicate.getEmployees();
            List<UnionFee> unionFeeList = newSyndicate.getUnionFeeList();

            Element childElement = document.createElement("syndicate");
            Element childUnEmployeesElement = document.createElement("unionizedEmployees");
            Element childunionFeesElement = document.createElement("unionFees");

            childElement.appendChild(childUnEmployeesElement);
            childElement.appendChild(childunionFeesElement);

            for(UnionizedEmployee unionizedEmployee : unionizedEmployees) {
                Element childUnEmployeeElement = document.createElement("unionizedEmployee");

                Element childUnIdElement = document.createElement("unionizedId");
                childUnIdElement.appendChild(document.createTextNode(unionizedEmployee.getUnionizedId()));

                Element childUnValueElement = document.createElement("unionFeeValue");
                childUnValueElement.appendChild(document.createTextNode(Double.toString(unionizedEmployee.getValue())));

                Element childUnDateElement = document.createElement("unionFeeDate");
                childUnDateElement.appendChild(document.createTextNode(unionizedEmployee.getDate().toString()));

                Element childEmployeeIdElement = document.createElement("employeeId");
                childEmployeeIdElement.appendChild(document.createTextNode(unionizedEmployee.getId()));

                childUnEmployeesElement.appendChild(childUnEmployeeElement);
                childUnEmployeeElement.appendChild(childUnIdElement);
                childUnEmployeeElement.appendChild(childUnValueElement);
                childUnEmployeeElement.appendChild(childEmployeeIdElement);
            }

            for(UnionFee unionFee : unionFeeList) {
                Attr idAttr = document.createAttribute("id");
                idAttr.setValue(unionFee.getId());

                Element childUnionFeeElement = document.createElement("unionFee");

                Element childUnionFeeDateElement = document.createElement("date");
                childUnionFeeDateElement.appendChild(document.createTextNode(unionFee.getDate().toString()));

                Element childUnionFeeValueElement = document.createElement("value");
                childUnionFeeValueElement.appendChild(document.createTextNode(Double.toString(unionFee.getValue())));

                childunionFeesElement.appendChild(childUnionFeeElement);
                childUnionFeeElement.appendChild(childUnionFeeDateElement);
                childUnionFeeElement.setAttributeNode(idAttr);
                childUnionFeeElement.appendChild(childUnionFeeValueElement);
            }

            Attr attr = document.createAttribute("id");
            attr.setValue(newSyndicate.getId());
            childElement.setAttributeNode(attr);

            rootElement.appendChild(childElement);
            childElement.appendChild(childUnEmployeesElement);
            childElement.appendChild(childunionFeesElement);
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(this.getFile());

        t.transform(domSource, streamResult);
    }
}
