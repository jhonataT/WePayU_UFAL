package br.ufal.ic.p2.wepayu.controllers;

import br.ufal.ic.p2.wepayu.exceptions.SyndicateException;
import br.ufal.ic.p2.wepayu.models.Employee;
import br.ufal.ic.p2.wepayu.models.Syndicate;
import br.ufal.ic.p2.wepayu.models.UnionFee;
import br.ufal.ic.p2.wepayu.models.UnionizedEmployee;
import br.ufal.ic.p2.wepayu.utils.XMLSyndicateManager;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class SyndicateController {
    private static Map<String, Syndicate> syndicates;
    private static final SyndicateController instance = new SyndicateController();

    private SyndicateController() {}

    public static SyndicateController getInstance() {
        return instance;
    }

    public void initializeSyndicates(XMLSyndicateManager database, EmployeeController employeeController) {
        syndicates = database.readAndGetSyndicateFile(employeeController.getEmployees());
    }

    public void resetSyndicates() { syndicates = new HashMap<>(); }

    public Map<String, Syndicate> getSyndicates() { return syndicates; }

    public Syndicate getSyndicateById(String syndicateId) throws NoSuchFieldException, ClassNotFoundException {
        if(syndicateId.isEmpty()) SyndicateException.emptySyndicateId();

        return syndicates.get(syndicateId);
    }

    public void updateSyndicate(Syndicate syndicate) {
        syndicates.put(syndicate.getId(), syndicate);
    }

    public void launchServiceFee(Syndicate syndicate, LocalDate date, double value) throws ClassNotFoundException {
        if(syndicate == null) SyndicateException.syndicateNotFound();
        if(value <= 0) SyndicateException.negativeValue();

        String newId = "unionFee_id_" + (syndicate.getUnionFeeList() == null || syndicate.getUnionFeeList().isEmpty() ? 0 : syndicate.getUnionFeeList().size());

        UnionFee newUnionFee = new UnionFee(newId, date, value);
        syndicate.addNewUnionFee(newUnionFee);
    }

    public void saveSyndicateInDatabase(XMLSyndicateManager database) throws Exception {
        try {
            database.createAndSaveSyndicateDocument(syndicates);
        } catch(Exception e) {
            throw new Exception("Erro ao salvar SYNDICATES_XML");
        }
    }
}
