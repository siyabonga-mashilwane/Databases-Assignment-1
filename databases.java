import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.HashMap;
class Databases {
    ArrayList<ArrayList<String>> data = new ArrayList<>();
    HashMap<String, Integer> columns = new HashMap<>();
    void createColumns(){
        for(int i = 0; i < data.get(0).size(); i++) {
            columns.put(data.get(0).get(i), i);
        }
    }
    Databases(String pathname) {
        File file = new File(pathname);
        try {
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                this.data.add(new ArrayList<>(Arrays.asList(line.split(","))));
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
        createColumns();
    }

    
    public static void main(String[] args) {
        Databases db = new Databases("C:\\Users\\bbdnet3407\\Documents\\Databases\\Databases-Assignment-1\\data.txt");
        HashMap<String, Integer> columns = db.columns;
        System.out.println(db.data.get(0));
    }
}