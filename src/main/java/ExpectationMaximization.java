import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Serj on 23/11/2015.
 */
public class ExpectationMaximization {
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
                evidenceHashMap.put(evidence, 1);
            }
        }

        System.out.println("INITIALIZE START ************************************************************");
        System.out.println("pSgDT: " + pSgDT);
        System.out.println("pDSgD: " + pDSgD);
        System.out.println("pFgD: " + pFgD);
        System.out.println("pT: " + pT);
        System.out.println("pD: " + pD);
        System.out.println("INITIALIZE END ************************************************************");

        double lastSumOfAllNonNormalized = 0;
        while(true) {
            // augmentation. Add a 0, 1 and 2 for each evidence that does not have an entry for dunettes
            double sumOfAllNonNormalized = 0d;
            double sumOfAllNormalizedWeights0 = 0d;
            double sumOfAllNormalizedWeights1 = 0d;
            double sumOfAllNormalizedWeights2 = 0d;

            // calculations for P(T)
            double sumOfAllTtrue = 0d;

            // calculations for P(F|D)
            double sumOfAllFtrue = 0d;
            double sumOfAllFtrueD0 = 0d;
            double sumOfAllFtrueD1 = 0d;
            double sumOfAllFtrueD2 = 0d;

            // calculations for P(DS|D)
            double sumOfAllDStrue = 0d;
            double sumOfAllDStrueD0 = 0d;
            double sumOfAllDStrueD1 = 0d;
            double sumOfAllDStrueD2 = 0d;

            // calculations for P(S|D,T)
            double sumOfAllSTrue = 0d;
            double sumOfAllD0Ttrue = 0d;
            double sumOfAllD1Ttrue = 0d;
            double sumOfAllD2Ttrue = 0d;
            double sumOfAllD0Tfalse = 0d;
            double sumOfAllD1Tfalse = 0d;
            double sumOfAllD2Tfalse = 0d;
            for (Map.Entry<Evidence, Integer> entry : evidenceHashMap.entrySet()) {
                Evidence evidence = entry.getKey();
                Integer count = entry.getValue();

                Evidence evidenceWith0Dunettes = new Evidence(evidence.isSloepnea(), evidence.isForiennditis(), evidence.isDegarSpots(), evidence.isTrimonoHTS(), 0);
                Evidence evidenceWith1Dunettes = new Evidence(evidence.isSloepnea(), evidence.isForiennditis(), evidence.isDegarSpots(), evidence.isTrimonoHTS(), 1);
                Evidence evidenceWith2Dunettes = new Evidence(evidence.isSloepnea(), evidence.isForiennditis(), evidence.isDegarSpots(), evidence.isTrimonoHTS(), 2);

                double probability0Dunettes = getProbabilityDistributionGivenAssignment(evidenceWith0Dunettes);
                double probability1Dunettes = getProbabilityDistributionGivenAssignment(evidenceWith1Dunettes);
                double probability2Dunettes = getProbabilityDistributionGivenAssignment(evidenceWith2Dunettes);

                if (evidence.getDunettes() == -1) {
                    double probability0NonNormalized = probability0Dunettes;
                    double probability1NonNormalized = probability1Dunettes;
                    double probability2NonNormalized = probability2Dunettes;
                    double sumOfAllProbabiitiesNonNormalized = (probability0NonNormalized + probability1NonNormalized + probability2NonNormalized);

                    double weightNonNormalizedAugmented = (sumOfAllProbabiitiesNonNormalized * count);
                    sumOfAllNonNormalized += weightNonNormalizedAugmented;

                    double probability0NormalizedAugmented = (probability0NonNormalized / sumOfAllProbabiitiesNonNormalized) * count;
                    double probability1NormalizedAugmented = (probability1NonNormalized / sumOfAllProbabiitiesNonNormalized) * count;
                    double probability2NormalizedAugmented = (probability2NonNormalized / sumOfAllProbabiitiesNonNormalized) * count;

                    sumOfAllNormalizedWeights0 += probability0NormalizedAugmented;
                    sumOfAllNormalizedWeights1 += probability1NormalizedAugmented;
                    sumOfAllNormalizedWeights2 += probability2NormalizedAugmented;

                    double sumOfAllNormalized = (probability0NormalizedAugmented + probability1NormalizedAugmented + probability2NormalizedAugmented);

                    if (evidence.isTrimonoHTS()) {
                        sumOfAllTtrue += sumOfAllNormalized;
                    }
                    if (evidence.isDegarSpots()) {
                        sumOfAllDStrue += sumOfAllNormalized;
                        sumOfAllDStrueD0 += probability0NormalizedAugmented;
                        sumOfAllDStrueD1 += probability1NormalizedAugmented;
                        sumOfAllDStrueD2 += probability2NormalizedAugmented;
                    }
                    if (evidence.isForiennditis()) {
                        sumOfAllFtrue += sumOfAllNormalized;
                        sumOfAllFtrueD0 += probability0NormalizedAugmented;
                        sumOfAllFtrueD1 += probability1NormalizedAugmented;
                        sumOfAllFtrueD2 += probability2NormalizedAugmented;
                    }
                    if (evidence.isSloepnea()) {
                        sumOfAllSTrue += sumOfAllNormalized;
                        if (evidence.isTrimonoHTS()) {
                            sumOfAllD0Ttrue += probability0NormalizedAugmented;
                            sumOfAllD1Ttrue += probability1NormalizedAugmented;
                            sumOfAllD2Ttrue += probability2NormalizedAugmented;
                        } else {
                            sumOfAllD0Tfalse += probability0NormalizedAugmented;
                            sumOfAllD1Tfalse += probability1NormalizedAugmented;
                            sumOfAllD2Tfalse += probability2NormalizedAugmented;
                        }
                    }
                } else {
                    /**
                     * In this case evidence already exists. The non-normalized probability of the other two options
                     * for D are considered 0 and do not add to the non-normalized sum. Since the other two are 0, when
                     * normalizing, what happens is: x/0+0+x = 1. So the probability after normalization is 1, or count
                     * in this case since there can be more than one of a single piece of evidence.
                     */
                    double probabilityNonNormalized = getProbabilityDistributionGivenAssignment(evidence) * (double) count;
                    sumOfAllNonNormalized += probabilityNonNormalized;
                    double probabilityNormalized = count;

                    if (evidence.getDunettes() == 0) {
                        sumOfAllNormalizedWeights0 += probabilityNormalized;
                    } else if (evidence.getDunettes() == 1) {
                        sumOfAllNormalizedWeights1 += probabilityNormalized;
                    } else {
                        sumOfAllNormalizedWeights2 += probabilityNormalized;
                    }

                    if (evidence.isTrimonoHTS()) {
                        sumOfAllTtrue += probabilityNormalized;
                    }
                    if (evidence.isDegarSpots()) {
                        sumOfAllDStrue += probabilityNormalized;
                        if (evidence.getDunettes() == 0) {
                            sumOfAllDStrueD0 += probabilityNormalized;
                        } else if (evidence.getDunettes() == 1) {
                            sumOfAllDStrueD1 += probabilityNormalized;
                        } else {
                            sumOfAllDStrueD2 += probabilityNormalized;
                        }
                    }
                    if (evidence.isForiennditis()) {
                        sumOfAllFtrue += probabilityNormalized;
                        if (evidence.getDunettes() == 0) {
                            sumOfAllFtrueD0 += probabilityNormalized;
                        } else if (evidence.getDunettes() == 1) {
                            sumOfAllFtrueD1 += probabilityNormalized;
                        } else {
                            sumOfAllFtrueD2 += probabilityNormalized;
                        }
                    }
                    if (evidence.isSloepnea()) {
                        sumOfAllSTrue += probabilityNormalized;
                        if (evidence.isTrimonoHTS()) {
                            if (evidence.getDunettes() == 0) {
                                sumOfAllD0Ttrue += probabilityNormalized;
                            } else if (evidence.getDunettes() == 1) {
                                sumOfAllD1Ttrue += probabilityNormalized;
                            } else if (evidence.getDunettes() == 2) {
                                sumOfAllD2Ttrue += probabilityNormalized;
                            }
                        } else {
                            if (evidence.getDunettes() == 0) {
                                sumOfAllD0Tfalse += probabilityNormalized;
                            } else if (evidence.getDunettes() == 1) {
                                sumOfAllD1Tfalse += probabilityNormalized;
                            } else if (evidence.getDunettes() == 2) {
                                sumOfAllD2Tfalse += probabilityNormalized;
                            }
                        }
                    }
                }
            }

            // update all the probabilities with their new assignment
            double sumOfAllD = sumOfAllNormalizedWeights0 + sumOfAllNormalizedWeights1 + sumOfAllNormalizedWeights2;

            // ending condition
            double absDifferenceBetweenThisAndLast = Math.abs(sumOfAllNonNormalized-lastSumOfAllNonNormalized);
            if (absDifferenceBetweenThisAndLast < stoppingPoint) {
                return;
            }
            lastSumOfAllNonNormalized = sumOfAllNonNormalized;

            // update P(D)
            if (sumOfAllD != 0) {
                // update P(D)
                if(sumOfAllNormalizedWeights0 != 0) pD.setD0(sumOfAllNormalizedWeights0 / sumOfAllD);
                if(sumOfAllNormalizedWeights1 != 0) pD.setD1(sumOfAllNormalizedWeights1 / sumOfAllD);
                if(sumOfAllNormalizedWeights2 != 0) pD.setD2(sumOfAllNormalizedWeights2 / sumOfAllD);

                // update P(T)
                if(sumOfAllTtrue != 0)  pT.setTt(sumOfAllTtrue / sumOfAllD);
            }

            // update P(F|D)
            if (sumOfAllFtrue != 0) {
                if(sumOfAllFtrueD0 != 0) pFgD.setD0Ft(sumOfAllFtrueD0 / sumOfAllFtrue);
                if(sumOfAllFtrueD1 != 0) pFgD.setD1Ft(sumOfAllFtrueD1 / sumOfAllFtrue);
                if(sumOfAllFtrueD2 != 0) pFgD.setD2Ft(sumOfAllFtrueD2 / sumOfAllFtrue);
            }

            // update P(DS|D)
            if (sumOfAllDStrue != 0) {
                if(sumOfAllDStrueD0 != 0) pDSgD.setD0DSt(sumOfAllDStrueD0 / sumOfAllDStrue);
                if(sumOfAllDStrueD1 != 0) pDSgD.setD1DSt(sumOfAllDStrueD1 / sumOfAllDStrue);
                if(sumOfAllDStrueD2 != 0) pDSgD.setD2DSt(sumOfAllDStrueD2 / sumOfAllDStrue);
            }

            // update P(S|D,T)
            if (sumOfAllSTrue != 0) {
                if(sumOfAllD0Ttrue != 0) pSgDT.setD0TtSt(sumOfAllD0Ttrue / sumOfAllSTrue);
                if(sumOfAllD1Ttrue != 0) pSgDT.setD1TtSt(sumOfAllD1Ttrue / sumOfAllSTrue);
                if(sumOfAllD2Ttrue != 0) pSgDT.setD2TtSt(sumOfAllD2Ttrue / sumOfAllSTrue);
                if(sumOfAllD0Tfalse != 0) pSgDT.setD0TfSt(sumOfAllD0Tfalse / sumOfAllSTrue);
                if(sumOfAllD1Tfalse != 0) pSgDT.setD1TfSt(sumOfAllD1Tfalse / sumOfAllSTrue);
                if(sumOfAllD2Tfalse != 0) pSgDT.setD2TfSt(sumOfAllD2Tfalse / sumOfAllSTrue);
            }

            System.out.println("LOOP ************************************************************");
            System.out.println("sumOfAllNonNormalized: " + sumOfAllNonNormalized);
            System.out.println("pSgDT: " + pSgDT);
            System.out.println("pDSgD: " + pDSgD);
            System.out.println("pFgD: " + pFgD);
            System.out.println("pT: " + pT);
            System.out.println("pD: " + pD);
            System.out.println("LOOP ************************************************************");

        } // while
    }

    public boolean testEvidence(Evidence evidence) {
        Evidence evidenceWith0Dunettes = new Evidence(evidence.isSloepnea(),evidence.isForiennditis(),evidence.isDegarSpots(),evidence.isTrimonoHTS(),0);
        Evidence evidenceWith1Dunettes = new Evidence(evidence.isSloepnea(),evidence.isForiennditis(),evidence.isDegarSpots(),evidence.isTrimonoHTS(),1);
        Evidence evidenceWith2Dunettes = new Evidence(evidence.isSloepnea(),evidence.isForiennditis(),evidence.isDegarSpots(),evidence.isTrimonoHTS(),2);

        double probWithDunettes0 = getProbabilityDistributionGivenAssignment(evidenceWith0Dunettes);
        double probWithDunettes1 = getProbabilityDistributionGivenAssignment(evidenceWith1Dunettes);
        double probWithDunettes2 = getProbabilityDistributionGivenAssignment(evidenceWith2Dunettes);

        if(probWithDunettes0 > probWithDunettes1 && probWithDunettes0 > probWithDunettes2) {
            return (evidence.getDunettes() == 0);
        } else if(probWithDunettes1 > probWithDunettes0 && probWithDunettes1 > probWithDunettes2) {
            return (evidence.getDunettes() == 1);
        } else {
            return (evidence.getDunettes() == 2);
        }
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
