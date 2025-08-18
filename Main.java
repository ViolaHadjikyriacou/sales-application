import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Arrays;

class Sale implements Comparable<Sale> {
  private final LocalDate saleDate;
  private final float saleAmount;

  public Sale(LocalDate date, float amount) {
    saleDate = date;
    saleAmount = amount;
  }

  public static Sale extract(String input, String dateFormat) {
    String[] parts = input.split("##");
    if (parts.length != 2) {
        throw new IllegalArgumentException("Input must contain exactly one ## delimiter");
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
    LocalDate date = LocalDate.parse(parts[0], formatter);
    float amount = Float.parseFloat(parts[1]);

    return new Sale(date, amount);
  }

  public static ArrayList<Sale> getRange(ArrayList<Sale> allsales, LocalDate startDate, LocalDate endDate){
    Iterator<Sale> it = allsales.iterator();
    while(it.hasNext()) {
      Sale s = it.next();
      if(s.saleDate.isBefore(startDate) || s.saleDate.isAfter(endDate)) {
        it.remove();
      }
    }
    return allsales;
  }

  public static float[] saleStatistics(ArrayList<Sale> rangeofsales){
    float saleavg = 0.0f;
    float salestddev = 0.0f;
    for (Sale s : rangeofsales) {
      saleavg += s.getAmount();
    }
    saleavg /= rangeofsales.size();

    for (Sale s : rangeofsales) {
      salestddev += (s.getAmount() - saleavg) * (s.getAmount() - saleavg);
    }
    salestddev /= (rangeofsales.size() - 1);
    salestddev = (float) Math.sqrt(salestddev);
    float[] salestats = {saleavg, salestddev};
    return salestats;
  }

  public LocalDate getDate(){
    return saleDate;
  }

  public float getAmount(){
    return saleAmount;
  }

  @Override
  public String toString() {
    return "Sale{date: " + saleDate + ", amount: " + saleAmount + "}";
  }

  @Override
  public int compareTo(Sale other) {
    return this.saleDate.compareTo(other.saleDate);
  }
}





public class Main {
  public static void main(String[] args) {
    String dateFormat = "dd/MM/yyyy";
    String stringStartDate = "01/01/2022";
    String stringEndDate = "31/12/2023";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
    LocalDate startDate = LocalDate.parse(stringStartDate, formatter);
    LocalDate endDate = LocalDate.parse(stringEndDate, formatter);

    ArrayList<Sale> allsales = new ArrayList<Sale>();
    try {
      File myFile = new File("data\\data1.txt");
      Scanner myReader = new Scanner(myFile);
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        System.out.println(data);
        Sale sale1 = Sale.extract(data,dateFormat);
        allsales.add(sale1);
        System.out.println("Date: " + sale1.getDate() + ", Amount: " + sale1.getAmount());
      }
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
    System.out.println(allsales);
    Collections.sort(allsales);
    System.out.println(allsales);
    ArrayList<Sale> rangeofsales = Sale.getRange(allsales, startDate, endDate);
    System.out.println(rangeofsales);
    System.out.println(Arrays.toString(Sale.saleStatistics(rangeofsales)));
}
}