//BYTE BANDITS
// Siyabonga Mashilwane - 4255147
// Ofentse Masia - 4323404
// Tokelo Primrose Monnakgotla- 4269371
// Ngoako Malowa - 4202406
// Refenje Mkhabela - 4340009
//Filename: Tests.java
//DB Practical 1

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Tests {
    Integer sortingTest(int by, boolean ascending, ArrayList<ArrayList<String>> data) {
        Integer unsorted = 0;    
        Double prev = ascending ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        System.out.println("Sorting test for column: " + by + " in " + (ascending ? "ascending" : "descending") + " order.");

        for (int i = 1; i < data.size(); i++) {
            String valueStr = data.get(i).get(by);
            if (Databases.isNumeric(valueStr)) {
                Double value = Double.parseDouble(valueStr);
                if ((ascending && prev > value) || (!ascending && prev < value)) {
                    unsorted += 1;
                    System.out.println(data.get(i));
                } else {
                    prev = value;
                }
            } else {
                System.out.println("Non-numeric value encountered: " + valueStr);
            }
        }
        return unsorted;
    }
        void CSVtestCases(){
        testCSVParser(",foo,", Arrays.asList("", "foo", ""));
        testCSVParser(",\"foo\",", Arrays.asList("", "foo", ""));
        testCSVParser(",\"foo\nbar\"", Arrays.asList("", "foo\nbar"));
        testCSVParser(",\"foo,bar\"", Arrays.asList("", "foo,bar"));
        testCSVParser(",\"foo\"\"bar\"", Arrays.asList("", "foo\"bar"));
        testCSVParser(",,", Arrays.asList("", "", ""));
        testCSVParser(",\"\"", Arrays.asList("", ""));
        testCSVParser("a,b,c", Arrays.asList("a", "b", "c"));
        testCSVParser("1,2", Arrays.asList("1", "2"));
    }
    void testCSVParser(String input, List<String> expected) {
        ArrayList<String> actual = Databases.parseCSVLine(input);
        if (actual.equals(expected)) {
            System.out.println("PASS: \"" + input + "\" => " + actual);
        } else {
            System.out.println("FAIL: \"" + input + "\"");
            System.out.println("Expected: " + expected);
            System.out.println("Actual:   " + actual);
        }
    }

    //Refentje Test Cases
    private static Databases prepareDatabase(List<List<String>> mockData){
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        for (List<String> row : mockData){
            data.add(new ArrayList<>(row));
        }
        return new Databases(data);
    }
    private static void assertEquals(Object expected, Object actual, String testCaseName){
        if (expected.equals(actual)){
            System.out.println("PASS: " + testCaseName);
        }else{
            System.out.println("FAIL: " + testCaseName);
            System.out.println("Expected: " + expected);
            System.out.println("Actual: " + actual);
        }
    }

    void testAll(){//test 1: countCountries (1960-1980)
        List<List<String>> sampleData1 = Arrays.asList(
                Arrays.asList("ID", "Code", "CountryName", "C4", "C5", "C6", "IndepYear"),
                Arrays.asList("1", "AAA", "South Africa", "x", "y", "z", "1961"),
                Arrays.asList("2", "BBB", "Canada", "x", "y", "z", "1967"),
                Arrays.asList("3", "CCC", "Nigeria", "x", "y", "z", "1960"),
                Arrays.asList("4", "DDD", "India", "x", "y", "z", "1947"), // Out of range
                Arrays.asList("5", "EEE", "Brazil", "x", "y", "z", "NULL"), //Invalid year
                Arrays.asList("6", "FFF", "Australia", "x", "y", "z", "1980"),
                Arrays.asList("7", "GGG", "South Africa", "x", "y", "z", "1975") //Duplicate country
        );

        Databases db1 = prepareDatabase(sampleData1);
        int expectedCount1 = 4; // South Africa, Canada, Nigeria, Australia
        int actualCount1 = db1.countCountries();
        assertEquals(expectedCount1, actualCount1, "Test 1: countCountries 1960-1980 Unique Countries");

        // Test 2: countCountries Invalid Years
        List<List<String>> sampleData2 = Arrays.asList(
                Arrays.asList("ID", "Code", "CountryName", "C4", "C5", "C6", "IndepYear"),
                Arrays.asList("1", "AAA", "Ghana", "x", "y", "z", "N/A"), //Invalid
                Arrays.asList("2", "BBB", "Kenya", "x", "y", "z", ""), //Empty
                Arrays.asList("3", "CCC", "Egypt", "x", "y", "z", "NULL") //NULL
        );
        Databases db2 = prepareDatabase(sampleData2);

        int expectedCount2 = 0; //All invalid years should be skipped
        int actualCount2 = db2.countCountries();
        assertEquals(expectedCount2, actualCount2, "Test 2: countCountries Invalid Years Handling");

        //Test 3: getCountriesEndingWithA Normal Case
        List<List<String>> sampleData3 = Arrays.asList(
                Arrays.asList("ID", "Code", "CountryName", "C4", "C5", "C6", "IndepYear"),
                Arrays.asList("1", "AAA", "South Africa", "x", "y", "z", "1961"),
                Arrays.asList("2", "BBB", "Canada", "x", "y", "z", "1967"),
                Arrays.asList("3", "CCC", "India", "x", "y", "z", "1947"),
                Arrays.asList("4", "DDD", "Nigeria", "x", "y", "z", "1960"),
                Arrays.asList("5", "EEE", "Brazil", "x", "y", "z", "NULL")
        );
        Databases db3 = prepareDatabase(sampleData3);

        List<String> expectedList3 = Arrays.asList("Canada", "India", "Nigeria", "South Africa");
        List<String> actualList3 = db3.getCountriesEndingWithA();

        assertEquals(expectedList3, actualList3, "Test 3: getCountriesEndingWithA Normal Case");

        // Test 4: getCountriesEndingWithA Empty/Invalid Data Handling
        List<List<String>> sampleData4 = Arrays.asList(
                Arrays.asList("ID", "Code", "CountryName", "C4", "C5", "C6", "IndepYear"),
                Arrays.asList("1", "AAA", "", "x", "y", "z", "1961"), //Empty country
                Arrays.asList() //Completely empty
        );
        Databases db4 = prepareDatabase(sampleData4);

        List<String> expectedList4 = Arrays.asList(); //No valid countries
        List<String> actualList4 = db4.getCountriesEndingWithA();
        assertEquals(expectedList4, actualList4, "Test 4: getCountriesEndingWithA Empty Data");
        System.out.println("\nTests Completed.");
    }
    //Ngoako Test Cases
    void cityPopulationTest(ArrayList<City> cities) {
        // Expected output
        List<String> expectedLines = Arrays.asList(
            "question b:",
            "five cities that have the highest city population:",
            "1. Mumbai (Bombay) - 10500000",
            "2. Seoul - 9981619",
            "3. São Paulo - 9968485",
            "4. Shanghai - 9696300",
            "5. Jakarta - 9604900"
        );
        // Read the output file using Scanner
        List<String> actualLines = new ArrayList<>();
        for (int i = 0; i < Math.min(5, cities.size()); i++) {
            City c = cities.get(i);
            actualLines.add((i + 1) + ". " + c.name + " - " + c.pop);
        }    

        //compares expected vs actual
        boolean match = true;
        for (int i = 0; i < expectedLines.size(); i++) {
            if (i >= actualLines.size() || !actualLines.get(i).equals(expectedLines.get(i))) {
                System.out.println("mismatch at line " + (i + 1));
                System.out.println("expected: " + expectedLines.get(i));
                System.out.println("actual  : " + (i < actualLines.size() ? actualLines.get(i) : "missing line"));
                match = false;
            }
        }
        if (match) {
            System.out.println("our test passed: output matches expected");
        } else {
            System.out.println("our test failed: output does not match");
        }
    }
}
