import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Serj on 25/11/2015.
 */
public class LoadAndStartEM {
    public static void main(String[] args) {
        /**
         * Load the train data
         */
        InputStreamReader trainDataInput = new InputStreamReader(LoadAndStartEM.class.getResourceAsStream("traindata.txt"));
        ArrayList<Evidence> trainEvidences = new ArrayList<Evidence>();
        try {
            BufferedReader trainDataReader = new BufferedReader(trainDataInput);

            String line;
            while ((line = trainDataReader.readLine()) != null) {
                String[] split = line.split(" ");
                boolean sloepnea;
                boolean foriennditis;
                boolean degarSpots;
                boolean trimonoHTS;

                sloepnea = split[1].equals("1");
                foriennditis = split[2].equals("1");
                degarSpots = split[3].equals("1");
                trimonoHTS = split[4].equals("1");
                Integer dunettes = Integer.parseInt(split[5]);

                trainEvidences.add(new Evidence(sloepnea, foriennditis, degarSpots, trimonoHTS, dunettes));
            }
        } catch(Exception e) {
            System.out.println("Failure when loading in train set: " + e);
            return;
        }

        /**
         * Load the train data
         */
        InputStreamReader testDataStreamReader = new InputStreamReader(LoadAndStartEM.class.getResourceAsStream("testdata.txt"));
        ArrayList<Evidence> testEvidences = new ArrayList<Evidence>();
        try {
            BufferedReader testDataReader = new BufferedReader(testDataStreamReader);

            String line;
            while ((line = testDataReader.readLine()) != null) {
                String[] split = line.split(" ");
                boolean sloepnea;
                boolean foriennditis;
                boolean degarSpots;
                boolean trimonoHTS;

                sloepnea = split[1].equals("1");
                foriennditis = split[2].equals("1");
                degarSpots = split[3].equals("1");
                trimonoHTS = split[4].equals("1");
                Integer dunettes = Integer.parseInt(split[5]);

                testEvidences.add(new Evidence(sloepnea, foriennditis, degarSpots, trimonoHTS, dunettes));
            }
        } catch(Exception e) {
            System.out.println("Failure when loading in test set: " + e);
            return;
        }

        ExpectationMaximization expectationMaximization = new ExpectationMaximization();
        expectationMaximization.runExpectationMaximization(trainEvidences, .001d);

        int numberAccurate = 0;
        for(Evidence evidence : testEvidences) {
            if(expectationMaximization.testEvidence(evidence)) {
                numberAccurate++;
            }
        }
        double accuracyPerc = (double)numberAccurate/(double)testEvidences.size();
        System.out.println("LoadAndStartEM.main accuracyPerc: " + accuracyPerc);

    }
}
