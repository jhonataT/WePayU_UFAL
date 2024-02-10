package br.ufal.ic.p2.wepayu.controllers;

import br.ufal.ic.p2.wepayu.exceptions.SyndicateException;
import br.ufal.ic.p2.wepayu.models.Syndicate;
import br.ufal.ic.p2.wepayu.models.UnionFee;
import br.ufal.ic.p2.wepayu.models.UnionizedEmployee;
import br.ufal.ic.p2.wepayu.utils.XMLSyndicateManager;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class SyndicateController {
    private static Map<String, Syndicate> syndicates;

    public static void initializeSyndicates(XMLSyndicateManager database) {
        syndicates = database.readAndGetSyndicateFile(EmployeeController.getEmployees());
    }

    public static void resetSyndicates() { syndicates = new HashMap<>(); }

    public static Map<String, Syndicate> getSyndicates() { return syndicates; }

    public static Syndicate getSyndicateById(String syndicateId) throws NoSuchFieldException, ClassNotFoundException {
        if(syndicateId.isEmpty()) SyndicateException.emptySyndicateId();

        return syndicates.get(syndicateId);
    }

    public static void updateSyndicate(Syndicate syndicate) {
        syndicates.put(syndicate.getId(), syndicate);
    }

    public static void launchServiceFee(Syndicate syndicate, LocalDate date, double value) throws ClassNotFoundException {
        if(syndicate == null) SyndicateException.syndicateNotFound();
        if(value <= 0) SyndicateException.negativeValue();

        String newId = "unionFee_id_" + (syndicate.getUnionFeeList() == null || syndicate.getUnionFeeList().isEmpty() ? 0 : syndicate.getUnionFeeList().size());

        UnionFee newUnionFee = new UnionFee(newId, date, value);
        syndicate.addNewUnionFee(newUnionFee);
    }

    public static void saveSyndicateInDatabase(XMLSyndicateManager database) throws Exception {
        try {
            database.createAndSaveSyndicateDocument(syndicates);
        } catch(Exception e) {
            throw new Exception("Erro ao salvar SYNDICATES_XML");
        }
    }
}
