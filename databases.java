import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Map;

class Databases{

    Databases(String pathname) {
        File file = new File(pathname);
        try {
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                this.data.add(parseCSVLine(line));
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
        createColumns();
    }

    public static ArrayList<String> parseCSVLine(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes; 
            } else if (c == ',' && !inQuotes) {
                fields.add(field.toString().trim());
                field.setLength(0); 
            } else {
                field.append(c);
            }
        }

        // Add last field
        fields.add(field.toString().trim());
        return fields;
    }
    ArrayList<ArrayList<String>> data = new ArrayList<>();
    HashMap<String, Integer> columns = new HashMap<>();
    void createColumns(){
        for(int i = 0; i < data.get(0).size(); i++) {
            columns.put(data.get(0).get(i), i);
        }
    }
    boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str); // Use Integer.parseInt(str) if you only want to check for integers
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    ArrayList<ArrayList<String>> removeDuplicates(){
        int duplicateCount = 0;
        ArrayList<ArrayList<String>> newData = new ArrayList<>();
        Set<ArrayList<String>> seen = new HashSet<>();
        for (int i = 0; i < data.size(); i++) {
            ArrayList<String> row = data.get(i);
            boolean added = seen.add(row);
            duplicateCount += (added) ? 0 : 1;
            if (added) {
                newData.add(row);
            }
        }
        System.out.println("Number of duplicates removed: " + duplicateCount);
        return newData;
    }

    //Handle data duplication
     //Use Sets to avoid duplicates
     //Use those sets to contain the highest bvalue of each column
     //Use a map to store the column names and their indices
    
    ArrayList<ArrayList<String>> sortArrayList(String by, boolean ascending, ArrayList<ArrayList<String>> data) {
        int index;

        if (data == null || data.isEmpty()) {
            data = this.data;
        }
        if(data.get(0).contains(by)) {
            index = data.get(0).indexOf(by);
        }else {
            System.out.println("Column not found: " + by);
            return null;
        }

        ArrayList<String> headers = data.remove(0); // Remove header row for sorting

        ArrayList<ArrayList<String>> sortedList = new ArrayList<>(data);
        System.out.println(by);
        sortedList.sort((row1, row2) -> {
            String value1 = row1.get(index);
            String value2 = row2.get(index);
            //if(columns.containsValue(index)) System.out.println("Sorting by column: " + by + " at index: " + index);
            //Handle cases where values are null
            if ("Null".equalsIgnoreCase(value1) && "Null".equalsIgnoreCase(value2)) return 0;
            if ("Null".equalsIgnoreCase(value1)) return -1;
            if ("Null".equalsIgnoreCase(value2)) return 1;

            Double num1 = isNumeric(value1) ? Double.parseDouble(value1) : Double.NaN;
            Double num2 = isNumeric(value2) ? Double.parseDouble(value2) : Double.NaN;
            //Handle ordering of numbers to adhere to order
            if(ascending) return Double.compare(num1, num2);
            else return Double.compare(num2, num1);           
        });
        sortedList.add(0, headers);
        return sortedList;
    }
    Integer sortingTest(int by, boolean ascending, ArrayList<ArrayList<String>> data) {
        Integer unsorted = 0;    
        Double prev = ascending ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        System.out.println("Sorting test for column: " + by + " in " + (ascending ? "ascending" : "descending") + " order.");

        for (int i = 1; i < data.size(); i++) {
            String valueStr = data.get(i).get(by);
            if (isNumeric(valueStr)) {
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
    ArrayList<String> getTopFiveLandMass(){
        HashMap<String, Double> countriesLandMass = new HashMap<>();
        HashMap<String, Integer> countriesCount = new HashMap<>();

        //ArrayList<ArrayList<String>> sortedData = sortArrayList("LandMass", false, null);
        for (int i = 1; i < this.data.size(); i++) {
            ArrayList<String> row = this.data.get(i);
            if (row.size() > 0) {
                String countryName = row.get(this.columns.get("CountryName")); // Assuming the first column is the name of the country
                String landMassStr = row.get(this.columns.get("LandMass"));
                if (isNumeric(landMassStr)) {
                    
                    double landMass = Double.parseDouble(landMassStr);
                    if (countriesLandMass.containsKey(countryName)) {
                        Double val = countriesLandMass.get(countryName);
                        val += landMass;
                        Integer count = countriesCount.get(countryName);
                        count += 1;
                        countriesLandMass.put(countryName, val);
                        countriesCount.put(countryName, count);
                    }else{
                        countriesCount.put(countryName, 1);
                        countriesLandMass.put(countryName, landMass);
                        if (landMass < 0) {
                            System.out.println("Negative land mass for country: " + countryName + " with value: " + landMass);
                        }
                    }
                }
            }
        }
        //build a new data structure to hold the average land mass
        ArrayList<ArrayList<String>> sortedData = new ArrayList<>();
        sortedData.add(new ArrayList<>(Arrays.asList("CountryName", "AverageLandMass")));
        for (Map.Entry<String,Double> entry : countriesLandMass.entrySet()) {
            String country = entry.getKey();
            Double value = entry.getValue();
            ArrayList<String> row = new ArrayList<>();
            row.add(country);
            row.add(String.valueOf(value / countriesCount.get(country)));
            sortedData.add(row);
        }
        
        sortedData = sortArrayList("AverageLandMass", false, sortedData);
        for (int i = 0; i < sortedData.size(); i++) {
            ArrayList<String> row = sortedData.get(i);
            System.out.println("Row " + i + ": " + row);
        }
        ArrayList<String> topFive = new ArrayList<>();
        //Get average land mass of each country
        int countryIndex = sortedData.get(0).indexOf("CountryName");
        int averageLandMassIndex = sortedData.get(0).indexOf("AverageLandMass");
        for (int i = 1; i <= 5 && i < sortedData.size(); i++) {
            ArrayList<String> row = sortedData.get(i);
            if (row.size() > 0) {
                topFive.add(row.get(countryIndex)); // Assuming the first column is the name of the country
                System.out.println("Country: " + row.get(countryIndex) + ", Average Land Mass: " + row.get(averageLandMassIndex));
            }
        }
        return topFive;
    }

    ArrayList<String> getTopFiveAfricanLifeExpectancy(){
        ArrayList<ArrayList<String>> sortedData = sortArrayList("LifeExpectancy", false, null);
        //Remove non-African countries
        sortedData.removeIf(row -> !row.get(this.columns.get("Continent")).toLowerCase().contains("africa"));
        //Get top 5 African countries by life expectancy
        ArrayList<String> topFive = new ArrayList<>();
        for (int i = 1; i <= 5 && i < sortedData.size(); i++) {
            ArrayList<String> row = sortedData.get(i);
            if (row.size() > 0) {
                int countryIndex = this.columns.get("CountryName");
                topFive.add(row.get(countryIndex)); // Assuming the first column is the name of the country
            }
        }
        return topFive;
    }

    ArrayList<String> getColumn(String columnName) {
        ArrayList<String> columnData = new ArrayList<>();
        if (columns.containsKey(columnName)) {
            int index = columns.get(columnName);
            for (ArrayList<String> row : data) {
                if (index < row.size()) {
                    columnData.add(row.get(index));
                }
            }
        } else {
            System.out.println("Column not found: " + columnName);
        }
        return columnData;
    }

    

    
    public static void main(String[] args) {
        Databases db = new Databases("C:\\Users\\bbdnet3407\\Documents\\Databases\\Databases-Assignment-1\\data.txt");
        HashMap<String, Integer> columns = db.columns;

        //db.data = new ArrayList<>(db.removeDuplicates());
        //db.createColumns();
        //System.out.println(db.data.get(0));
        //Debugging purposes
        //ArrayList<ArrayList<String>> sortedData = db.sortArrayList("LandMass", false, null);
        //System.out.println(db.sortingTest(db.columns.get("LandMass"), false, sortedData));
        //System.out.println("Number of elements in sorted array :" + sortedData.size());
        //System.out.println(sortedData.get(0));
        // for (ArrayList<String> row : sortedData) {
        //     if( !db.isNumeric(row.get(db.columns.get("LandMass")))) System.out.println(row);
        // }

        ArrayList<String> topFiveLandMass = db.getTopFiveLandMass();
        System.out.println("Top 5 countries by land mass:");
        for (String country : topFiveLandMass) {
            System.out.println(country);
        }

        // ArrayList<String> topFiveAfricanLifeExpectancy = db.getTopFiveAfricanLifeExpectancy();
        // System.out.println("Top 5 African countries by life expectancy:");
        // for (String country : topFiveAfricanLifeExpectancy) {
        //     System.out.println(country);
        // }
        
        //System.out.println(db.data.get(0));
    }
}