# CLIENT REQUIREMENTS

A client in the retail sector provides several files tracking all their sales. Each file contains up to a million lines of the following format:

`31/03/2021##245.39`

Each line consists of the date of sale, a delimiter (##), and the sale amount. Note that multiple sales can exist for the same date. The format of the date and the amount shall be configurable by the user.

Every now and then, the client needs to generate some statistics on their data. Supported statistics:

- Average of earnings for a range of years.
- Standard deviation of earnings within a specific year.
- Standard deviation of earnings for a range of years.

The client has requested a console application written in Java.

---

# USING THE APPLICATION

### Instructions

**1. Download OpenJDK**

**2. Compile the program:**

  - Execute the following command from the project root directory:
  `javac -d classes src/Main.java src/Sale.java`

**3. Run the program:**

  - Execute the following command from the project root directory
  `java -cp classes Main <path/to/file1> <path/to/file2> ...`,
  with at least one filepath as argument
  - `<path/to/file>` is the relative or absolute path of a text file to be analyzed

**4. Program flow:**

- The program validates all provided file paths before processing and will exit with an error message if any files are invalid or missing
- The user will be prompted to select the date format that is used in provided files. There are four supported date formats: "dd/MM/yyyy", "MM/dd/yyyy", "yyyy/MM/dd", "yyyy-MM-dd"
- Each file is processed line by line, extracting sales data in the format `date##amount`. Invalid lines are skipped and reported as warnings
- After processing all files, the program displays a summary showing:
  - Total number of valid sales found across all files
  - The complete date range of sales (from earliest to latest date)
- The user is then prompted to enter a date range for statistical analysis:
  - Start date and end date must be entered in the selected date format
  - Both dates must fall within the available sales data range
- For the specified date range, the program calculates and displays:
  - Number of sales in the range
  - Average sale amount (rounded to two decimal places)
  - Standard deviation of sale amounts (rounded to two decimal places)
- After displaying results, the user can choose to analyze another date range or exit the program
