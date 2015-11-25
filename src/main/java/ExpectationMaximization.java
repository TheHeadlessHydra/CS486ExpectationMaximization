import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Serj on 23/11/2015.
 */
public class ExpectationMaximization {
    public static final int NOT_PRESENT = 0;
    public static final int MILD_DUNETTES = 1;
    public static final int SEVERE_DUNETTES = 2;

    private final PD pD = new PD();
    private final PT pT = new PT();
    private final PDSgD pDSgD = new PDSgD();
    private final PFgD pFgD = new PFgD();
    private final PSgDT pSgDT = new PSgDT();

    public void runExpectationMaximization(ArrayList<Evidence> evidences, double stoppingPoint) {
        HashMap<Evidence, Integer> evidenceHashMap = new HashMap<Evidence, Integer>();

        // count the number of times specific assignments of evidence occur
        for(Evidence evidence : evidences) {
            if(evidenceHashMap.containsKey(evidence)) {
                Integer count = evidenceHashMap.get(evidence);
                evidenceHashMap.put(evidence, count+1);
            } else {
                evidenceHashMap.put(evidence, 0);
            }
        }

        // augmentation. Add a 0, 1 and 2 for each evidence that does not have an entry for dunettes
        double sumOfAllNormalizedWeights0 = 0d;
        double sumOfAllNormalizedWeights1 = 0d;
        double sumOfAllNormalizedWeights2 = 0d;
        HashMap<Evidence, Probability> assignmentProbabilities = new HashMap<Evidence, Probability>();
        for (Map.Entry<Evidence, Integer> entry : evidenceHashMap.entrySet()) {
            Evidence evidence = entry.getKey();
            Integer count = entry.getValue();


            Evidence evidenceWith0Dunettes = new Evidence(evidence.isSloepnea(),evidence.isForiennditis(),evidence.isDegarSpots(),evidence.isTrimonoHTS(),0);
            Evidence evidenceWith1Dunettes = new Evidence(evidence.isSloepnea(),evidence.isForiennditis(),evidence.isDegarSpots(),evidence.isTrimonoHTS(),1);
            Evidence evidenceWith2Dunettes = new Evidence(evidence.isSloepnea(),evidence.isForiennditis(),evidence.isDegarSpots(),evidence.isTrimonoHTS(),2);


            double probability0Dunettes = getProbabilityDistributionGivenAssignment(evidenceWith0Dunettes);
            double probability1Dunettes = getProbabilityDistributionGivenAssignment(evidenceWith1Dunettes);
            double probability2Dunettes = getProbabilityDistributionGivenAssignment(evidenceWith2Dunettes);

            double sumOfAll = probability0Dunettes + probability1Dunettes + probability2Dunettes;

            if(evidence.getDunettes() == -1) {
                double individualCount = (double)count/3d;
                double probability0NonNormalized = probability0Dunettes*individualCount;
                double probability1NonNormalized = probability1Dunettes*individualCount;
                double probability2NonNormalized = probability2Dunettes*individualCount;

                double probability0Normalized = probability0NonNormalized/sumOfAll;
                double probability1Normalized = probability1NonNormalized/sumOfAll;
                double probability2Normalized = probability2NonNormalized/sumOfAll;

                sumOfAllNormalizedWeights0 += probability0Normalized;
                sumOfAllNormalizedWeights1 += probability1Normalized;
                sumOfAllNormalizedWeights2 += probability2Normalized;

                assignmentProbabilities.put(evidence, new Probability(probability0NonNormalized, probability0Normalized));
                assignmentProbabilities.put(evidence, new Probability(probability1NonNormalized, probability1Normalized));
                assignmentProbabilities.put(evidence, new Probability(probability2NonNormalized, probability2Normalized));
            } else {
                double probabilityNonNormalized = getProbabilityDistributionGivenAssignment(evidence) * (double)count;
                double probabilityNormalized = probabilityNonNormalized/sumOfAll;

                if(evidence.getDunettes() == 0) {
                    sumOfAllNormalizedWeights0 += probabilityNonNormalized;
                } else if(evidence.getDunettes() == 1) {
                    sumOfAllNormalizedWeights1 += probabilityNonNormalized;
                } else {
                    sumOfAllNormalizedWeights2 += probabilityNonNormalized;
                }

                assignmentProbabilities.put(evidence, new Probability(probabilityNonNormalized, probabilityNormalized));
            }
        }

        // loop through evidence, and for each one, update its associated probability by dividing it with the
        // sumOfAllNormalizedWeights0/1/2 given above.
        // when i say associated probability i mean the one in the CPT table that matches. -1 doesnt exist anymore so...
        for (Map.Entry<Evidence, Probability> entry : assignmentProbabilities.entrySet()) {
            Evidence evidence = entry.getKey();
            Probability probability = entry.getValue();

            updateProbabilityOfCPTs(evidence, probability, sumOfAllNormalizedWeights0, sumOfAllNormalizedWeights1, sumOfAllNormalizedWeights2);
        }
    }

    private void updateProbabilityOfCPTs(Evidence evidence,
                                        Probability probability,
                                        double sumOfAllNormalizedWeights0,
                                        double sumOfAllNormalizedWeights1,
                                        double sumOfAllNormalizedWeights2) {
        

    }

    /**
     * Given an assignment, this will caclulate the probability using the probabilities currently held
     * in the CPTs.
     *
     * Calculated as: P(D)*P(T)*P(S|D,T)*P(DS|D)*P(F|D)
     */
    private double getProbabilityDistributionGivenAssignment(Evidence evidence) {
        double finalProduct = 1;

        int evidenceDunettes = evidence.getDunettes();
        if(evidenceDunettes == -1) {
            throw new IllegalArgumentException("To get a probability, there must be an assignmnet of 0, 1 or 2 to " +
                    "the dunettes category of the evidence: " + evidence);
        }

        // P(D) * ...
        if(evidenceDunettes == 0) {
            finalProduct *= pD.getD0();
        } else if(evidenceDunettes == 1) {
            finalProduct *= pD.getD1();
        } else if(evidenceDunettes == 2) {
            finalProduct *= pD.getD2();
        }

        // P(T) * ...
        if(evidence.isTrimonoHTS()) {
            finalProduct *= pT.getTt();
        } else {
            finalProduct *= pT.getTf();
        }

        // P(DS|D) * ...
        if(evidence.isDegarSpots()) {
            if(evidenceDunettes == 0) {
                finalProduct *= pDSgD.getD0DSt();
            } else if(evidenceDunettes == 1) {
                finalProduct *= pDSgD.getD1DSt();
            } else if(evidenceDunettes == 2) {
                finalProduct *= pDSgD.getD2DSt();
            }
        } else {
            if(evidenceDunettes == 0) {
                finalProduct *= pDSgD.getD0DSf();
            } else if(evidenceDunettes == 1) {
                finalProduct *= pDSgD.getD1DSf();
            } else if(evidenceDunettes == 2) {
                finalProduct *= pDSgD.getD2DSf();
            }
        }

        // P(F|D) * ...
        if(evidence.isForiennditis()) {
            if(evidenceDunettes == 0) {
                finalProduct *= pFgD.getD0Ft();
            } else if(evidenceDunettes == 1) {
                finalProduct *= pFgD.getD1Ft();
            } else if(evidenceDunettes == 2) {
                finalProduct *= pFgD.getD2Ft();
            }
        } else {
            if(evidenceDunettes == 0) {
                finalProduct *= pFgD.getD0Ff();
            } else if(evidenceDunettes == 1) {
                finalProduct *= pFgD.getD1Ff();
            } else if(evidenceDunettes == 2) {
                finalProduct *= pFgD.getD2Ff();
            }
        }

        // P(S|D, T)
        if(evidence.isSloepnea()) {
            if(evidence.isTrimonoHTS()) {
                if(evidenceDunettes == 0) {
                    finalProduct *= pSgDT.getD0TtSt();
                } else if(evidenceDunettes == 1) {
                    finalProduct *= pSgDT.getD1TtSt();
                } else if(evidenceDunettes == 2) {
                    finalProduct *= pSgDT.getD2TtSt();
                }
            } else {
                if(evidenceDunettes == 0) {
                    finalProduct *= pSgDT.getD0TfSt();
                } else if(evidenceDunettes == 1) {
                    finalProduct *= pSgDT.getD1TfSt();
                } else if(evidenceDunettes == 2) {
                    finalProduct *= pSgDT.getD2TfSt();
                }
            }
        } else {
            if(evidence.isTrimonoHTS()) {
                if(evidenceDunettes == 0) {
                    finalProduct *= pSgDT.getD0TtSf();
                } else if(evidenceDunettes == 1) {
                    finalProduct *= pSgDT.getD1TtSf();
                } else if(evidenceDunettes == 2) {
                    finalProduct *= pSgDT.getD2TtSf();
                }
            } else {
                if(evidenceDunettes == 0) {
                    finalProduct *= pSgDT.getD0TfSf();
                } else if(evidenceDunettes == 1) {
                    finalProduct *= pSgDT.getD1TfSf();
                } else if(evidenceDunettes == 2) {
                    finalProduct *= pSgDT.getD2TfSf();
                }
            }
        }
        return finalProduct;
    }

}
