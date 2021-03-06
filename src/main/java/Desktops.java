import java.sql.*;
import java.util.Scanner;
import java.util.UUID;


/**
 * Created by clara on 11/10/16.
 * Simple example with UUID as keys - universal unique ID numbers,
 * A UUID is a 128-bit string represented as hex, and the DB guarantees that they will be unique
 * MySQL can generate UUID values by calling the UUID() function
 * UUIDs are 32 characters long and look something like this: 90adda0d-6bbc-4c5e-8fd8-c3e037c3a910
 *
 *
 * If you wanted to generate UUIDs in code, then Java can generate UUIDs too, with a call to UUID.randomUUID().toString();
 */


public class Desktops {

    final static String DESKTOP_TABLE_NAME = "Desktops";
    final static String ID_COL = "id";
    final static String MANUFACTURER_COL = "manufacturer";
    final static String MODEL_COL = "model";

    static Scanner stringScanner = new Scanner(System.in);
    
    public static void main(String[] args) {

        DBUtils.registerDriver();

        createTable();
        insertTestData();
        
        insertNewDesktop();
        findDesktopByModel();

        stringScanner.close();
        
    }


    private static void createTable() {
        
        try (Connection connection = DBUtils.getConnection();
             Statement createTableStatement = connection.createStatement()) {
            
            //The SQL to create the Desktop table is
            // CREATE TABLE IF NOT EXISTS Desktops (id VARCHAR(36) NOT NULL, manufacturer VARCHAR(100), model VARCHAR(100), PRIMARY KEY (id) )
            String createTableSQLtemplate = "CREATE TABLE IF NOT EXISTS %s (%s VARCHAR(36) NOT NULL, %s VARCHAR(100), %s VARCHAR(100), PRIMARY KEY(%s) )";
            String createTableSQL = String.format(createTableSQLtemplate, DESKTOP_TABLE_NAME, ID_COL, MANUFACTURER_COL, MODEL_COL, ID_COL);
            System.out.println("The SQL to be executed is: " + createTableSQL);

            createTableStatement.execute(createTableSQL);
            
            System.out.println("Created Desktop table");
            
            connection.close();
            createTableStatement.close();
            
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        
    }
    
    /* Insert some example data */
    private static void insertTestData() {
        
        try (Connection connection = DBUtils.getConnection()) {
            
            // Example of the SQL to execute. The first value, the ID, will be generated by MySQL by calling MySQL's UUID() function
            //
            //   INSERT INTO Desktops VALUES ( 90adda0d-6bbc-4c5e-8fd8-c3e037c3a910, 'HP', 'Pavilion 510' )
            //   INSERT INTO Desktops VALUES ( 3552da0d-6b5c-235e-14d8-d93137c3a910, 'Apple', 'iMac 2016' )
            
            String insertSQL = String.format("INSERT INTO %s VALUES ( UUID() , ? , ?) " , DESKTOP_TABLE_NAME);
            PreparedStatement insertTestDataStatement = connection.prepareStatement(insertSQL);
            
            //Add one row of test data
            insertTestDataStatement.setString(1, "HP");
            insertTestDataStatement.setString(2, "Pavilion 510");
            insertTestDataStatement.execute();

            //Add another row of test data
            insertTestDataStatement.setString(1, "Apple");
            insertTestDataStatement.setString(2, "iMac 2016");
            insertTestDataStatement.execute();

            insertTestDataStatement.close();
            connection.close();
            
            System.out.println("Added two rows of test data");
            
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }


    private static void insertNewDesktop() {
        
        System.out.println("Enter manufacturer of new Desktop");
        String manuf = stringScanner.nextLine();
        System.out.println("Enter model of new Desktop");
        String model = stringScanner.nextLine();
        
        try (Connection connection = DBUtils.getConnection()) {
            
            String insertSQL = String.format("INSERT INTO %s VALUES ( UUID() , ? , ?) " , DESKTOP_TABLE_NAME);
            PreparedStatement insertTestDataStatement = connection.prepareStatement(insertSQL);
            
            //Add one row of test data
            insertTestDataStatement.setString(1, manuf);
            insertTestDataStatement.setString(2, model);
            insertTestDataStatement.execute();

            System.out.println("Added new Desktop.");
            
            insertTestDataStatement.close();
            connection.close();
            
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
    
    
    //An example query. Could write other queries if desired.
    
    private static void findDesktopByModel() {
        
        System.out.println("Enter model name to search for in Desktop table");
        String modelToFind = stringScanner.nextLine();
        
        try (Connection connection = DBUtils.getConnection()) {
            
            String searchSQL = String.format("SELECT * FROM %s WHERE %s = ? " , DESKTOP_TABLE_NAME, MODEL_COL);
            PreparedStatement searchStatement = connection.prepareStatement(searchSQL);
            
            //Add one row of test data
            searchStatement.setString(1, modelToFind);
            
            ResultSet rs = searchStatement.executeQuery();
            
            System.out.println("Results of your query: ");
            while (rs.next()) {

                String id = rs.getString(ID_COL);
                String manf = rs.getString(MANUFACTURER_COL);
                String model = rs.getString(MODEL_COL);
                
                System.out.println(String.format("id = %s Manufacturer = %s Model = %s", id, manf, model));
                
            }
            System.out.println("End of results");
            
            rs.close();
            searchStatement.close();
            connection.close();
            
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}
