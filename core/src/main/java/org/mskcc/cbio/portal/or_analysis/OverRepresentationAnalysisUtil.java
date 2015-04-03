package org.mskcc.cbio.portal.or_analysis;

import java.util.*;

import org.mskcc.cbio.portal.model.*;
import org.mskcc.cbio.portal.dao.*;


public class OverRepresentationAnalysisUtil {
    
    public static Map<Long, HashMap<Integer, String>> getValueMap(int cancerStudyId, int profileId, String profileType, List<Integer> alteredSampleIds, List<Integer> unalteredSampleIds) throws DaoException {

        DaoGeneticAlteration daoGeneticAlteration = DaoGeneticAlteration.getInstance();
        DaoGeneOptimized daoGeneOptimized = DaoGeneOptimized.getInstance();
        
        //get cancer genes
        Set<CanonicalGene> cancerGeneSet = daoGeneOptimized.getCbioCancerGenes();
        Set<Long> entrezGeneIds = new HashSet<Long>();
        for (CanonicalGene cancerGene : cancerGeneSet) {
            entrezGeneIds.add(cancerGene.getEntrezGeneId());
        }

        //join two lists
        List<Integer> sampleIds = new ArrayList<Integer>(alteredSampleIds);
        sampleIds.addAll(unalteredSampleIds);
        
        //Map<GeneId, HashMap<CaseId, Value>>
        Map<Long, HashMap<Integer, String>> result = new HashMap<Long, HashMap<Integer,String>>();
        
        if(profileType.equals(GeneticAlterationType.COPY_NUMBER_ALTERATION.toString())) {
            result = daoGeneticAlteration.getGeneticAlterationMap(profileId, entrezGeneIds);
        } else if (profileType.equals(GeneticAlterationType.MUTATION_EXTENDED.toString())) {
            ArrayList<ExtendedMutation> mutObjArr = DaoMutation.getMutations(profileId, sampleIds, entrezGeneIds);
            for (Long entrezGeneId : entrezGeneIds) {
                //Assign every sample (included non mutated ones) values -- mutated -> Mutation Type, non-mutated -> "Non"
                HashMap<Integer, String> singleGeneMutMap = new HashMap<Integer, String>();
                for (Integer sampleId : sampleIds) {
                    String mutationType = "Non";
                    for (ExtendedMutation mut : mutObjArr) {
                        if (mut.getSampleId() == sampleId && mut.getGene().getEntrezGeneId() == entrezGeneId) {
                            mutationType = "placeholder";
                        }
                    }
                    singleGeneMutMap.put(sampleId, mutationType);
                }
                //add a new entry into the overall result map
                result.put(entrezGeneId, singleGeneMutMap);
            }
        } else if (profileType.equals(GeneticAlterationType.MRNA_EXPRESSION.toString())) {
            result = daoGeneticAlteration.getGeneticAlterationMap(profileId, entrezGeneIds);
        }
        return result;
    }
	
}
