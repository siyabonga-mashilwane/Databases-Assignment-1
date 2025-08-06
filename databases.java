//BYTE BANDITS
// Siyabonga Mashilwane - 4255147
// Ofentse Masia - 4323404
// Tokelo Primrose Monnakgotla- 4269371
// Ngoako Malowa - 4202406
// Refenje Mkhabela - 4340009
//Filename: Tests.java
//DB Practical 1

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;


class City {
    String name;
    int pop;

    //constructor
    public City(String name, int pop) {
        this.name = name;
        this.pop = pop;
    }
}

class Databases{
    //Siyabonga
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
    Databases(ArrayList<ArrayList<String>> data) {
        this.data = data;
        createColumns();
        this.data = removeDuplicates();
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
    // This class-level variable stores a table-like structure,
    // where each inner ArrayList<String> represents a row of data.
    ArrayList<ArrayList<String>> data = new ArrayList<>();

    // This HashMap will map column names (String) to their index positions (Integer)
    // based on the header row of the data.
    HashMap<String, Integer> columns = new HashMap<>();

    // This method populates the 'columns' map by taking the first row of 'data'
    // (assumed to be the header) and mapping each column name to its index.
    void createColumns() {
        for (int i = 0; i < data.get(0).size(); i++) {
            columns.put(data.get(0).get(i), i);
        }
    }

    // A static utility method to check if a given string is a valid number.
    // Returns false for null or empty strings.
    // If the string can be parsed into a Double without an error, it is numeric.
    static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str); // Try converting the string to a double
            return true;
        } catch (NumberFormatException e) {
            return false; // If conversion fails, it's not numeric
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
    
    // Method to sort a 2D ArrayList of Strings by a specific column name, in ascending or descending order
    ArrayList<ArrayList<String>> sortArrayList(String by, boolean ascending, ArrayList<ArrayList<String>> data) {
        int index; // Will hold the index of the column to sort by

        // If the input data is null or empty, use the instance's default data
        if (data == null || data.isEmpty()) {
            data = this.data;
        }

        // Check if the first row (headers) contains the column name we're sorting by
        if(data.get(0).contains(by)) {
            index = data.get(0).indexOf(by); // Get the index of the column
        } else {
            // If the column doesn't exist, print a message and return null
            System.out.println("Column not found: " + by);
            return null;
        }

        // Remove and store the header row (first row of the dataset) to avoid sorting it
        ArrayList<String> headers = data.remove(0);

        // Create a new list that will be sorted (we don't want to sort the original list directly)
        ArrayList<ArrayList<String>> sortedList = new ArrayList<>(data);
        
        // Debug: print out the column name being sorted by
        System.out.println(by);

        // Sort the list using a custom comparator
        sortedList.sort((row1, row2) -> {
            String value1 = row1.get(index);
            String value2 = row2.get(index);

            // Handle "Null" values by sorting them to the top
            if ("Null".equalsIgnoreCase(value1) && "Null".equalsIgnoreCase(value2)) return 0;
            if ("Null".equalsIgnoreCase(value1)) return -1;
            if ("Null".equalsIgnoreCase(value2)) return 1;

            // Convert strings to numeric values if possible
            Double num1 = isNumeric(value1) ? Double.parseDouble(value1) : Double.NaN;
            Double num2 = isNumeric(value2) ? Double.parseDouble(value2) : Double.NaN;

            // Compare the numeric values based on the `ascending` flag
            if (ascending) return Double.compare(num1, num2);
            else return Double.compare(num2, num1);
        });

        // Re-insert the header row at the top of the sorted list
        sortedList.add(0, headers);

        // Return the sorted list
        return sortedList;
    }


    ArrayList<String> getTopFiveLandMass(){
        HashMap<String, Double> countriesLandMass = new HashMap<>();
        HashMap<String, Integer> countriesCount = new HashMap<>();

        // Iterate over each row in the data, starting from index 1 (skipping the header row)
        for (int i = 1; i < this.data.size(); i++) {
            ArrayList<String> row = this.data.get(i); // Get the current row of data

            // Check that the row is not empty
            if (row.size() > 0) {
                // Retrieve the country name from the "CountryName" column
                String countryName = row.get(this.columns.get("CountryName"));

                // Retrieve the land mass value from the "LandMass" column
                String lifeExpectancy = row.get(this.columns.get("LandMass"));

                // Check if the land mass value is numeric
                if (isNumeric(lifeExpectancy)) {
                    double landMass = Double.parseDouble(lifeExpectancy); // Convert land mass string to double

                    // If the country has already been recorded in the map
                    if (countriesLandMass.containsKey(countryName)) {
                        // Update the total land mass by adding the current value
                        Double val = countriesLandMass.get(countryName);
                        val += landMass;

                        // Increment the count of records for this country
                        Integer count = countriesCount.get(countryName);
                        count += 1;

                        // Save the updated values back into the maps
                        countriesLandMass.put(countryName, val);
                        countriesCount.put(countryName, count);
                    } else {
                        // If the country is not yet in the maps, initialize values
                        countriesCount.put(countryName, 1);
                        countriesLandMass.put(countryName, landMass);

                        // Print a warning if the land mass is negative
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
        
        ArrayList<String> topFive = new ArrayList<>();
        //Get average land mass of each country
        int countryIndex = sortedData.get(0).indexOf("CountryName");
        for (int i = 1; i <= 5 && i < sortedData.size(); i++) {
            ArrayList<String> row = sortedData.get(i);
            if (row.size() > 0) {
                topFive.add(row.get(countryIndex)); // Assuming the first column is the name of the country
            }
        }
        return topFive;
    }

    ArrayList<String> getTopFiveAfricanLifeExpectancy() {
        HashMap<String, Double> countriesLifeExpectancy = new HashMap<>();
        HashMap<String, Integer> countriesCount = new HashMap<>();
        HashMap<String, String> countriesContinents = new HashMap<>();

        // Loop through each row in the data, starting from index 1 (assuming index 0 is a header)
        for (int i = 1; i < this.data.size(); i++) {
            ArrayList<String> row = this.data.get(i);  // Get the current row of data

            if (row.size() > 0) {  // Make sure the row is not empty
                // Extract relevant fields from the row using the column name mappings
                String countryName = row.get(this.columns.get("CountryName")); // Get country name
                String lifeExpectancy = row.get(this.columns.get("LifeExpectancy")); // Get life expectancy
                String continent = row.get(this.columns.get("Continent")); // Get continent

                // Check if life expectancy value is numeric before processing
                if (isNumeric(lifeExpectancy)) {
                    double lifeExpected = Double.parseDouble(lifeExpectancy); // Convert string to double

                    // If the country has already been seen before
                    if (countriesLifeExpectancy.containsKey(countryName)) {
                        Double val = countriesLifeExpectancy.get(countryName); // Get current total life expectancy
                        val += lifeExpected;  // Add current value to the total

                        Integer count = countriesCount.get(countryName); // Get current count of records
                        count += 1; // Increment count

                        // Update values in the maps
                        countriesLifeExpectancy.put(countryName, val);
                        countriesCount.put(countryName, count);
                        countriesContinents.put(countryName, continent); // Update continent info
                    } else {
                        // First time seeing this country
                        countriesCount.put(countryName, 1);  // Set count to 1
                        countriesLifeExpectancy.put(countryName, lifeExpected); // Initialize life expectancy

                        // If life expectancy is negative, print a warning
                        if (lifeExpected < 0) {
                            System.out.println("Negative Life Expectancy for country: " + countryName + " with value: " + lifeExpected);
                        }
                    }

                    // Ensure that continent info is stored at least once
                    if (!countriesContinents.containsKey(countryName)) {
                        countriesContinents.put(countryName, continent);
                    }
                }
            }
        }

        ArrayList<ArrayList<String>> sortedData = new ArrayList<>();
        sortedData.add(new ArrayList<>(Arrays.asList("CountryName", "LifeExpectancy", "Continent")));
        for (Map.Entry<String,Double> entry : countriesLifeExpectancy.entrySet()) {
            String country = entry.getKey();
            Double value = entry.getValue();
            ArrayList<String> row = new ArrayList<>();
            row.add(country);
            row.add(String.valueOf(value / countriesCount.get(country)));
            row.add(countriesContinents.get(country));
            sortedData.add(row);
        }
        // Sort all data by LifeExpectancy in descending order
        sortedData = sortArrayList("LifeExpectancy", false, sortedData);
        for (ArrayList<String> row : sortedData) {
            System.out.println(row);
        }

        // Filter only African countries
        int countryIndex = sortedData.get(0).indexOf("CountryName");
        int continentIndex = sortedData.get(0).indexOf("Continent");
        sortedData.removeIf(row -> {
            return row.size() <= continentIndex || 
                !row.get(continentIndex).toLowerCase().contains("africa");
        });

        // Extract top 5 African countries by life expectancy
        ArrayList<String> topFive = new ArrayList<>();
        for (int i = 1; i <= 5 && i < sortedData.size(); i++) {
            ArrayList<String> row = sortedData.get(i);
            if (row.size() > countryIndex) {
                topFive.add(row.get(countryIndex));
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

    //TOKELO
    List <String> indepYears() {
        List <String> indepArray = new ArrayList <>();
        for (int i = 1; i < this.data.size(); i++) {
            ArrayList<String> row = this.data.get(i);
            String indepYear = row.get(this.columns.get("IndepYear")).trim();
            String countryName = row.get(this.columns.get("CountryName"));
            if (isValidYear(indepYear)) {
                indepArray.add(countryName + " - " + indepYear);
            }
        }
        return indepArray;
    }

    public boolean isValidYear (String yearStr) {
        if (yearStr.isEmpty() ||yearStr.equalsIgnoreCase("NULL")) {
            return false;
        }
        try { //try catch for in case a records year cannot be parsed to an int 
            
            int year = Integer.parseInt(yearStr); //making changing the year to integer so I may perform the check without having the string match up 20 years independtly
            return year>= 1830 && year <=1850; //returning the range check
                
        } catch (NumberFormatException e) {
            return false;
        }
    }//end of isValidYear

    ArrayList<Map.Entry<String, Integer>> mostCommonLanguage() {
        HashMap<String, Integer> langCount = new HashMap<>();
        for (int i = 1; i < this.data.size(); i++) {
            ArrayList<String> row = this.data.get(i);
            String lang = row.get(this.columns.get("Language")).trim();
            if(langCount.containsKey(lang)) {
                langCount.put(lang, langCount.get(lang) + 1);
            }
            else {
                langCount.put(lang, 1);
            }
        }
        // Sort languages by count
        ArrayList<Map.Entry<String, Integer>> langCountList = new ArrayList<>(langCount.entrySet());
        langCountList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        return langCountList;
    }

    //Ofentse
    //Unique Countries ending with a
    int uniqueCountriesEndingWithA() {
        int countryIndex = this.columns.get("CountryName");
        HashSet<String> uniqueCountries = new HashSet<>();
        for (int i = 1; i < this.data.size(); i++) {
            ArrayList<String> row = this.data.get(i);
            if (countryIndex < row.size()) {
                String country = row.get(countryIndex).trim().toLowerCase();
                if (!country.isEmpty() && !country.equals("countryname")) {
                    uniqueCountries.add(country);
                }
            }
        }
        int countEndingWithA = 0;
        for (String country : uniqueCountries) {
            if (country.toLowerCase().endsWith("a")) {
                countEndingWithA++;
            }
        }
        return countEndingWithA;
    }

    //Refentje
    //Question H
    public List<String> getCountriesEndingWithA(){
        Set<String> countries = new HashSet<>();
        //CountryName is 3rd column 3(Index 2)
        int countryCol = this.columns.get("CountryName"); 

        for (int i = 1; i < data.size();i++){
            ArrayList<String> row = data.get(i);
            if(row.size() > countryCol){
                String name = row.get(countryCol).trim();
                if (!name.isEmpty() && name.toLowerCase().endsWith("a")){
                    countries.add(name);
                }
            }
        }
        return countries.stream().sorted().collect(Collectors.toList());
    }
    
    //Question D
    public int countCountries(){
        int count = 0;
        Set<String> uniqueCountries = new HashSet<>();
        //CountryName is 3rd column 3(Index 2)
        final int countryCol = 2;
        //IndepYear is column 7(index 6)
        final int yearCol = 6;

        for(int i = 1; i < data.size(); i++){
            ArrayList<String> row = data.get(i);
            if(row.size() > yearCol){
                String yearStr = row.get(yearCol).trim();
                if(!yearStr.equalsIgnoreCase("NULL") && !yearStr.isEmpty()){
                    try{
                        int year = Integer.parseInt(yearStr);
                        if (year >= 1960 && year <=1980){
                            String country = row.get(countryCol);
                            if(uniqueCountries.add(country)){
                                count++;
                            }
                        }
                    }catch(NumberFormatException e){
                        //Skip invalid years
                    }
                }
            }
        }
        return count;
    }

    //Ngoako
    ArrayList<City> highestPopulation() {
        //list to store unique cities
        ArrayList<City> cities = new ArrayList<>();
        //set to keep track of city names already added
        HashSet<String> seenCities = new HashSet<>();
        for (int i = 1; i < this.data.size(); i++) {
            
            ArrayList<String> row = this.data.get(i);
            String cityName = row.get(this.columns.get("CityName")).trim(); // Assuming the first column is the name of the city
            String populationStr = row.get(this.columns.get("CityPopulation")).trim();
            Double population = 0.0;
            if(isNumeric(populationStr)) {
                population = Double.parseDouble(populationStr);
            } else {
                System.out.println("Non-numeric population value encountered: " + populationStr);
                continue; // Skip this row if population is not numeric
            }
            if (!seenCities.contains(cityName)) {
                cities.add(new City(cityName, population.intValue()));
                seenCities.add(cityName);
            }
        }
        cities.sort((a, b) -> b.pop - a.pop);
        return cities;
    }
    public static void main(String[] args) {
        Databases db = new Databases("C:\\Users\\bbdnet3407\\Documents\\Databases\\Databases-Assignment-1\\data.txt");   
               
        ArrayList<String> topFiveLandMass = db.getTopFiveLandMass(); 
        List<String> topFiveAfricanLifeExpectancy = db.getTopFiveAfricanLifeExpectancy();
        List <String> indepArray = db.indepYears();
        List<String> countriesEndingWithA = db.getCountriesEndingWithA();
        int countryCount = db.countCountries();
        ArrayList<Map.Entry<String, Integer>> langCountList = db.mostCommonLanguage();
        int uniques = db.uniqueCountriesEndingWithA();
        ArrayList<City> cities = db.highestPopulation();
        

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("answers.txt"))) {
            writer.write("Byte Bandits Assignment 1 Answers\n");

            writer.write("Question A: \n");
            writer.write(uniques + "\n");

            writer.write("\n");

            writer.write("Question B: \n");
            for (int i = 0; i < Math.min(5, cities.size()); i++) {
                City c = cities.get(i);
                writer.write((i + 1) + ". " + c.name + " - " + c.pop + "\n");
            }

            writer.write("\n");

            writer.write("Question C: \n");
            for (int i = 0; i < topFiveLandMass.size(); i++){
                writer.write((i + 1) + ". " + topFiveLandMass.get(i) + "\n");
            }

            writer.write("\n");

            writer.write("Question D:\n");
            writer.write(countryCount+ "\n");
            writer.write("\n");
            writer.write("Question E:\n");
            for (String country : indepArray) {
                writer.write(country + "\n");
            }
            writer.write("\n");
            writer.write("Question F: \n");
            for (String country : topFiveAfricanLifeExpectancy) {
                writer.write(country + "\n");
            }
            writer.write("\n");
            writer.write("Question G:\n");
            for (int i = 0; i < Math.min(5, langCountList.size()); i++) {
                Map.Entry<String, Integer> entry = langCountList.get(i);
                writer.write((i + 1) + ". " + entry.getKey() + " - " + entry.getValue() + "\n");
            }
            writer.write("\n");
            writer.write("Question H:\n");
            for (String country : countriesEndingWithA) {
                writer.write(country + "\n");
            }
            writer.write("\n");
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }  
    }
}