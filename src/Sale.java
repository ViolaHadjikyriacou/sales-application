import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;



public class Sale implements Comparable<Sale> {
  private final LocalDate saleDate;
  private final float saleAmount;

  public Sale(LocalDate date, float amount) {
    saleDate = date;
    saleAmount = amount;
  }

  public static Sale extract(String input, DateTimeFormatter formatter) throws NumberFormatException, IllegalArgumentException, DateTimeParseException {
    String[] parts = input.split("##");
    if (parts.length != 2) {
        throw new IllegalArgumentException("Input must contain exactly one ## delimiter");
    }

    LocalDate date = LocalDate.parse(parts[0], formatter); // Possible DateTimeParseException
    float amount = Float.parseFloat(parts[1]); // Possible NumberFormatException

    return new Sale(date, amount);
  }

  public static ArrayList<Sale> getRange(ArrayList<Sale> allsales, LocalDate startDate, LocalDate endDate){
    ArrayList<Sale> rangeofsales = new ArrayList<>();
    for (Sale s : allsales) {
        if (!s.saleDate.isBefore(startDate) && !s.saleDate.isAfter(endDate)) {
            rangeofsales.add(s); // only add sales within range
        }
    }
    return rangeofsales;
  }

  public static double[] saleStatistics(ArrayList<Sale> rangeofsales){
    double saleavg = 0.0d;
    double salestddev = 0.0d;
    for (Sale s : rangeofsales) {
      saleavg += s.getAmount()/rangeofsales.size();
    }
    if (rangeofsales.size() > 1) {
      for (Sale s : rangeofsales) {
      salestddev += (s.getAmount() - saleavg) * (s.getAmount() - saleavg)/(rangeofsales.size() - 1);
      }
    }
    salestddev = Math.sqrt(salestddev);
    double[] salestats = {saleavg, salestddev};
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