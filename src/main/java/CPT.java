import java.util.*;

/**
 * @author Serj
 * A class that embodies the concept of a factor in a Baysion Network.
 */
public class CPT {
    public static final String SLOEPNEA = "sloepnea";
    public static final String FORIENNDITIS = "foriennditis";
    public static final String DEGAR_SPOTS = "degar spots";
    public static final String TRIMONO_HTS = "TRIMONO HT/S";
    public static final String MILD_DUNETTES = "mild duettes";
    public static final String SEVERE_DUNETTES = "severe duettes";


    private final String probabilityOf;
    private final List<String> variableNames;
    private final List<List<Boolean>> booleans;
    private final List<Double> probabilities;

    CPT(String probabilityOf,
        List<String> variableNames,
           List<List<Boolean>> booleans,
           List<Double> probabilities) {

        this.probabilityOf = probabilityOf;

        if(booleans.size() != probabilities.size()) {
            if(probabilities.size() != 1) {
                throw new IllegalArgumentException("number of probabilities and number of boolean combinations must be the same");
            }
        }
        if(Math.round(Math.pow(2, variableNames.size())) != booleans.size()){
            if(variableNames.size() != 1 || booleans.size() != 2) {
                throw new IllegalArgumentException("There must be 2^variables number of boolean columns: " +
                        "variableNames: " + variableNames + ", booleans: " + booleans + ", factors: " + probabilities);
            }
        }

        for(List<Boolean> bools : booleans) {
            if(bools.size() != variableNames.size()) {
                throw new IllegalArgumentException("There must be as many booleans as there are variables. Problem: " +
                        bools);
            }
        }

        this.variableNames = variableNames;
        this.probabilities = probabilities;
        this.booleans = booleans;
    }

    public String getProbabilityOf() {
        return probabilityOf;
    }

    public List<String> getVariableNames() {
        return variableNames;
    }

    public List<List<Boolean>> getBooleans() {
        return booleans;
    }

    public List<Double> getProbabilities() {
        return probabilities;
    }

    private boolean isVariableInFactor(String checkVariable) {
        for(String variable : getVariableNames()) {
            if(variable.equals(checkVariable))
                return true;
        }
        return false;
    }

    // the M step. Set the values of each row of this CPT.

    /**
     * The M step.
     * Set the values of each row of this CPT. This is done by going through the evidence and looking for an evidence
     * that matches the row. If it matches the row, it will add to a sum:
     *      sumOfAllIncludingProbabilityOf: the numerator. This is the sum of the weights for the specific assignments
     *                                      of the row.
     *      sumOfAllExcludingProbabilityOf: the denominator. This is the sum of the weights of all evidences, where
     *                                      the probabilityOf, the main probability we are calculating in this CPT,
     *                                      can take on either true or false.
     * Then calculate the new probability of that row by doing sumOfAllIncludingProbabilityOf/sumOfAllExcludingProbabilityOf
     *
     * If no evidence matches the row trying to be updated, then it is ignored and the row's probability does not change.
     */
    public void calculateNewValuesBasedOnEvidence(HashMap<Evidence, Probability> evidenceProbability) {
        for(int j = 0; j < getBooleans().size(); j++) {
            List<Boolean> assignment = getBooleans().get(j);

            double sumOfAllIncludingProbabilityOf = 0;
            double sumOfAllExcludingProbabilityOf = 0;
            for(Map.Entry<Evidence, Probability> entrySet : evidenceProbability.entrySet()) {
                Evidence evidence = entrySet.getKey();
                Probability probability = entrySet.getValue();

                // only true if ALL assignments are equal
                boolean isEvidenceThisAssignment = true;
                // true if all assignments EXCEPT the main assignments are equal
                boolean isEvidenceIncludingProbabilityOf = true;
                for(int i = 0; i < getVariableNames().size(); i++) {
                    String attribute = getVariableNames().get(i);
                    boolean attributeValue = assignment.get(i);

                    // see if the current assignment we are looking at matches the given evidence
                    if(attribute.equals(SLOEPNEA)) {
                        if(attributeValue != evidence.isSloepnea()) {
                            isEvidenceThisAssignment = false;
                            isEvidenceIncludingProbabilityOf = false;
                            if(getProbabilityOf().equals(attribute)) {
                                isEvidenceIncludingProbabilityOf = true;
                            }
                        }
                    } else if(attribute.equals(FORIENNDITIS)) {
                        if(attributeValue != evidence.isForiennditis()) {
                            isEvidenceThisAssignment = false;
                            isEvidenceIncludingProbabilityOf = false;
                            if(getProbabilityOf().equals(attribute)) {
                                isEvidenceIncludingProbabilityOf = true;
                            }
                        }
                    } else if(attribute.equals(DEGAR_SPOTS)) {
                        if(attributeValue != evidence.isDegarSpots()) {
                            isEvidenceThisAssignment = false;
                            isEvidenceIncludingProbabilityOf = false;
                            if(getProbabilityOf().equals(attribute)) {
                                isEvidenceIncludingProbabilityOf = true;
                            }
                        }
                    } else if(attribute.equals(TRIMONO_HTS)) {
                        if(attributeValue != evidence.isTrimonoHTS()) {
                            isEvidenceThisAssignment = false;
                            isEvidenceIncludingProbabilityOf = false;
                            if(getProbabilityOf().equals(attribute)) {
                                isEvidenceIncludingProbabilityOf = true;
                            }
                        }
                    } else if(attribute.equals(MILD_DUNETTES)) {
                        int dunettes = evidence.getDunettes();
                        if(attributeValue && (dunettes == 0 || dunettes == 2)) {
                            // having mild dunettes is wrong if hes got severe dunettes or no dunettes
                            isEvidenceThisAssignment = false;
                            isEvidenceIncludingProbabilityOf = false;
                            if(getProbabilityOf().equals(attribute)) {
                                isEvidenceIncludingProbabilityOf = true;
                            }
                        } else if(!attributeValue && dunettes == 1) {
                            // not having mild dunettes is wrong if he's got mild durettes
                            isEvidenceThisAssignment = false;
                            isEvidenceIncludingProbabilityOf = false;
                            if(getProbabilityOf().equals(attribute)) {
                                isEvidenceIncludingProbabilityOf = true;
                            }
                        }
                    } else if(attribute.equals(SEVERE_DUNETTES)) {
                        int dunettes = evidence.getDunettes();
                        if(attributeValue && (dunettes == 0 || dunettes == 1)) {
                            // having severe dunettes is wrong if hes got mild dunettes or no dunettes
                            isEvidenceThisAssignment = false;
                            isEvidenceIncludingProbabilityOf = false;
                            if(getProbabilityOf().equals(attribute)) {
                                isEvidenceIncludingProbabilityOf = true;
                            }
                        } else if(!attributeValue && dunettes == 2) {
                            // not having severe dunettes is wrong if he's got severe durettes
                            isEvidenceThisAssignment = false;
                            isEvidenceIncludingProbabilityOf = false;
                            if(getProbabilityOf().equals(attribute)) {
                                isEvidenceIncludingProbabilityOf = true;
                            }
                        }
                    } else {
                        throw new RuntimeException("Unknown symptom type");
                    }
                } // for variableName
                if(isEvidenceThisAssignment && isEvidenceIncludingProbabilityOf) {
                    sumOfAllIncludingProbabilityOf += probability.getNormalizedProbability();
                    sumOfAllExcludingProbabilityOf += probability.getNormalizedProbability();
                } else if(isEvidenceIncludingProbabilityOf) {
                    sumOfAllExcludingProbabilityOf += probability.getNormalizedProbability();
                }
            }

            if(sumOfAllExcludingProbabilityOf != 0) {
                double newProbability = sumOfAllIncludingProbabilityOf/sumOfAllExcludingProbabilityOf;
                getProbabilities().set(j, newProbability);
            }
        } // for all rows
    }


    /**
     * Given a piece of evidence, search through the CPT and look for the row that matches the evidence's assignment.
     * Give the probability of that row.:
     *
     *             D type
     * E: 0 1 0 0 -1
     * D type = -1, which means no durettes
     *
     * CPT:
     * SD MD  P(F)
     * T  T   0.99
     * T  F   0.8
     * F  T   0.2
     * F  F   0.05
     *
     * result: FF: 0.05... TODO THATS NOT RIGHT
     *
     * // TODO: wait wait what happens if the evidence says it doesnt know durettes
     */
    public Probability getProbabilityGivenEvidence(Evidence evidence) {
        List<Boolean> booleans = new ArrayList<Boolean>();
        for(int i = 0; i < getVariableNames().size(); i++) {
            String attribute = getVariableNames().get(i);
            if(attribute.equals(SLOEPNEA)) {
                booleans.add(evidence.isSloepnea());
            } else if(attribute.equals(FORIENNDITIS)) {
                booleans.add(evidence.isForiennditis());
            } else if(attribute.equals(DEGAR_SPOTS)) {
                booleans.add(evidence.isDegarSpots());
            } else if(attribute.equals(TRIMONO_HTS)) {
                booleans.add(evidence.isTrimonoHTS());
            } else if(attribute.equals(MILD_DUNETTES)) {
                int durettes = evidence.getDunettes();
                booleans.add(durettes == 1);
            } else if(attribute.equals(SEVERE_DUNETTES)) {
                int durettes = evidence.getDunettes();
                booleans.add(durettes == 2);
            } else {
                throw new RuntimeException("Unknown symptom type");
            }
        }

        int columnOfNormalizingAttribute = getVariableNames().indexOf(getProbabilityOf());
        double nonNormalizedProbability = getProbability(booleans);
        booleans.set(columnOfNormalizingAttribute, !booleans.get(columnOfNormalizingAttribute));
        double oppositeProbability = getProbability(booleans);

        double normalizedProbability = nonNormalizedProbability/(nonNormalizedProbability + oppositeProbability);
        return new Probability(nonNormalizedProbability, normalizedProbability);
    }

    private double getProbability(List<Boolean> valuation) {
        if(valuation.size() != this.variableNames.size()) {
            throw new IllegalArgumentException("Must give a boolean valuation of the same size as the CPT");
        }
        for(int j = 0; j < getBooleans().size(); j++) {
            List<Boolean> booleans = getBooleans().get(j);
            boolean isThisTheRightRow = true;
            for(int i = 0; i < valuation.size(); i++) {
                Boolean currentBool = booleans.get(i);
                Boolean lookupBool = valuation.get(i);
                if(currentBool != lookupBool) {
                    isThisTheRightRow = false;
                }
            }
            if(isThisTheRightRow) {
                return getProbabilities().get(j);
            }
        }
        throw new IllegalArgumentException("Given valuation does not match any row in CPT.");
    }

    @Override
    public String toString() {
        String toReturn = "Factor{\n";
        toReturn += "probabilityOf= " + probabilityOf + '\n';
        toReturn += "variableNames= " + variableNames + '\n';
        for(int i = 0; i < booleans.size(); i++) {
            List<Boolean> bools = booleans.get(i);
            Double probability = probabilities.get(i);
            toReturn += bools + " : " + probability + '\n';
        }
        toReturn += "}\n";
        return toReturn;
    }
}
