import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    
    // Get the file paths from command line arguments
    String[] filePaths = args;
    int numberOfFiles = filePaths.length;

    // Exit the program if no file paths are provided
    if (numberOfFiles == 0){
      System.out.println("No file paths provided.");
      System.out.println("To run the application execute the following command from the project root directory with at least one filepath as argument:");
      System.out.println("java -cp classes Main <path/to/file1> <path/to/file2> ...");
      System.exit(1);
    }

    // Normalize the file paths into a consistent, OS-specific format
    for (int i = 0; i < numberOfFiles; i++) {
      filePaths[i] = filePaths[i].replace("/", File.separator).replace("\\", File.separator);
    }

    // Check if all the file paths are valid
    List<String> invalidPaths = new ArrayList<>();
    for (String filePath : filePaths) {
      File file = new File(filePath);
      if (!file.exists() || !file.isFile()) {
        invalidPaths.add(filePath);
      }
    }
    if (!invalidPaths.isEmpty()) {
      System.out.println("The following file paths are invalid:");
      for (String path : invalidPaths) {
          System.out.println(path);
      }
      System.exit(1); // Exit with error after reporting all invalid file paths
    }

    // Create a single scanner object to process all user inputs
    Scanner cmdScanner = new Scanner(System.in);

    // User selects the correct date format
    String[] dateFormats = {"dd/MM/yyyy", "MM/dd/yyyy", "yyyy/MM/dd", "yyyy-MM-dd"};
    System.out.println("Select the date format used in your files by entering the corresponding number:");
    for (int i = 0; i < dateFormats.length; i++) {
      System.out.println((i + 1) + ": " + dateFormats[i]);
    }        
    int choice;
    do {
      System.out.print("Enter your choice: ");
      String formatInput = cmdScanner.nextLine();
      try {
          choice = Integer.parseInt(formatInput);
          if (choice >= 1 && choice <= dateFormats.length) {
              break; // valid input
          } else {
              System.out.println("Please enter a number between 1 and " + dateFormats.length + ".");
              choice = -1; // reset choice to stay in the loop
          }
      } catch (NumberFormatException e) {
          System.out.println("Invalid input. Please enter a number.");
          choice = -1; // reset choice to stay in the loop
      }
    } while (choice == -1);
    String dateFormat = dateFormats[choice - 1];
    System.out.println("You selected: " + dateFormat);
    System.out.println();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);

    //Read the files and create the sales ArrayList
    ArrayList<Sale> allsales = new ArrayList<Sale>();
    for (String filePath : filePaths) {
      int validLinesCount = 0;
      int invalidLinesCount = 0;
      try {
        File dataFile = new File(filePath); 
        Scanner dataReader = new Scanner(dataFile); // Possible FileNotFoundException
        while (dataReader.hasNextLine()) {
          String dataLine = dataReader.nextLine();
          try {
            Sale sale = Sale.extract(dataLine, formatter); // Possible IllegalArgumentException, DateTimeParseException
            allsales.add(sale);
            validLinesCount++;
          } catch (IllegalArgumentException | DateTimeParseException e) {
            invalidLinesCount++;
          } 
        }
        dataReader.close();
        if (validLinesCount == 0 && invalidLinesCount == 0) {
          System.out.println("File " + filePath + " is empty.");
          System.out.println();
        } else if (invalidLinesCount == 0) {
          System.out.println("File " + filePath + " was processed without errors.");
          System.out.println("Number of sales in file " + filePath + ": " + validLinesCount);
          System.out.println();
        } else {
          System.out.println("Warning: Some lines in file " + filePath + " could not be processed correctly.");
          System.out.println("File " + filePath + " was processed with " + invalidLinesCount + " invalid lines and " + validLinesCount + " valid sales.");
          System.out.println();
        }
      } catch (FileNotFoundException e) {
        System.out.println("File not found: " + filePath);
        System.exit(1);
      }
    }


    //Sort the sales ArrayList and print some information
    Collections.sort(allsales);
    int salesCount = allsales.size();
    if (salesCount == 0) {
      System.out.println("No sales found.");
      System.exit(1);
    }
    System.out.println("Total number of sales: " + salesCount);
    LocalDate firstDate = allsales.get(0).getDate();
    LocalDate lastDate = allsales.get(salesCount - 1).getDate();
    System.out.println("Sales date range: " + firstDate.format(formatter) + " to " + lastDate.format(formatter));
    System.out.println();
    

    // Analyze sales within a date range provided by the user; allows multiple iterations
    String continueAnalysis = "yes";
    LocalDate startDate;
    String startDateString;
    LocalDate endDate;
    String endDateString;
    ArrayList<Sale> rangeofsales;
    int rangeCount;
    double[] rangestats;
    do {
      System.out.println("Choose a range for sales analysis.");

      // Get the start date for analysis from the user
      while (true) {
        System.out.println("Enter a valid start date in the format: " + dateFormat);
        startDateString = cmdScanner.nextLine();

        try {
          startDate = LocalDate.parse(startDateString, formatter);
          if (startDate.isBefore(firstDate) || startDate.isAfter(lastDate)) {
            System.out.println("Start date must be within the range: " + firstDate.format(formatter) + " to " + lastDate.format(formatter));
            System.out.println();
            continue;
          }
          break;
        } catch (DateTimeParseException e) {
          System.out.println("Invalid date format: " + startDateString + " does not match the format " + dateFormat);
          System.out.println();
        }
      }

      // Get the end date for analysis from the user
      while (true) {
        System.out.println("Enter a valid end date in the format: " + dateFormat);
        endDateString = cmdScanner.nextLine();

        try {
          endDate = LocalDate.parse(endDateString, formatter);
          if (endDate.isBefore(startDate)) {
            System.out.println("End date cannot be before the start date: " + startDate.format(formatter));
            System.out.println();
            continue;
          }
          if (endDate.isBefore(firstDate) || endDate.isAfter(lastDate)) {
            System.out.println("End date must be within the sales range: " + firstDate.format(formatter) + " to " + lastDate.format(formatter));
            System.out.println();
            continue;
          }
          break;
        } catch (DateTimeParseException e) {
          System.out.println("Invalid date format: " + endDateString + " does not match the format " + dateFormat);
          System.out.println();
        }
      }

      // Perform analysis on the sales data within the specified date range
      rangeofsales = Sale.getRange(allsales, startDate, endDate);
      rangeCount = rangeofsales.size();
      if (rangeCount == 0) {
        System.out.println("There are no sales in this range.");
        System.out.println();
        continue;
      }
      rangestats = Sale.saleStatistics(rangeofsales);
      System.out.println("For sales within the range " + startDate.format(formatter) + " - " + endDate.format(formatter) + " the statistics (rounded to two decimal places) are :");
      System.out.printf("Number of sales: %d, average: %.2f, standard deviation: %.2f%n", rangeCount, rangestats[0], rangestats[1]);
      System.out.println();

      // Ask if user wants to analyse a new range
      while (true) {
        System.out.print("Do you want to enter another date range? (yes/no): ");
        continueAnalysis = cmdScanner.nextLine().trim().toLowerCase();
        if (continueAnalysis.equals("yes") || continueAnalysis.equals("no")) {
            break; // valid input, exit the loop
        } else {
            System.out.println("Invalid input! Please enter 'yes' or 'no'.");
        }
      }

    } while (continueAnalysis.equals("yes"));

    cmdScanner.close();
    
  }
}
