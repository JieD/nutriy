import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

public class CleanData {
	
	private final static String READ_PATH = "nutrition_data/ABBREV.txt";
	private final static String WRITE_PATH = "nutrition_data/nutrition_db.txt";
	private Path inputFilePath;
	private String outputFileName;
	private final static Charset ENCODING = StandardCharsets.UTF_8;
	
	public CleanData(String inputFileName, String outputFileName){
	  this.inputFilePath = Paths.get(inputFileName);
	  this.outputFileName = outputFileName; 
	}
	
	/**
	 * create the output writer
	 * not use FileWriter due to it doesn't take an explicit character set
	 * use BufferedWriter for efficiency
	 * @return PrintWriter
	 * @throws IOException
	 */
	private PrintWriter createWriter() throws IOException {
		FileOutputStream fOut = new FileOutputStream(outputFileName);
		Writer writer = new BufferedWriter(new OutputStreamWriter(fOut, ENCODING.name()));
		return new PrintWriter(writer);
	}
	  
	/** Template method that calls {@link #cleanLine(String)}.  
	 *  USDA abbreviated file format:
	 *  1. Fields are separated by a caret (^) and text fields are surrounded by tildes (~).
	 *  2. Data refer to 100 g of the edible portion of the food item. 
	 *     Decimal points are included in the fields.
	 *  3. Missing values are denoted by the null value of two consecutive carets (^^) or 
	 *     two carets and two tildes (~~).
	 *  Steps to clean raw nutrition_data file:
	 *  1. keep '^' and '~' as field separators
	 *  2. remove USDA db ID
	 *  3. extract necessary nutrition data
	 */
	public final void clean() throws IOException {
		Scanner fileScanner =  new Scanner(inputFilePath, ENCODING.name());
		PrintWriter output = createWriter();		
		CleanLine lineCleaner = new CleanLine();
		//int lineN = 1;
		String line = "";
	    while (fileScanner.hasNextLine()){
	      //log(lineN);
	      line = lineCleaner.clean((fileScanner.nextLine()));
	      if (line != "") output.println(line);
	      //lineN++;
	    }      
	    fileScanner.close();
	    output.flush();
	    output.close(); // close the stream
	}

	private static void log(Object aMsg) {
	    System.out.println(String.valueOf(aMsg));
    }
	
	private void test() {
		String sample = "~16200~^~CAMPBELL'S BRN SUGAR&BACON FLAV BKD BNS~^69.40^123^3.85^1.92^1.75^23.08^6.2^10.00^31^1.11^^^^362^^^^^0.0^^^^^^^^^^^^0^^^^^^^^^^^^0.385^^^4^130^~.5 cup~^130^~1 serving~^0";
		String[] result = sample.split("[/^]");
		System.out.println(Arrays.toString(result));
	}
	
	public static void main(String args[]) {
		String sample = "~16200~^~CAMPBELL'S BRN SUGAR&BACON FLAV BKD BNS~^69.40^123^3.85^1.92^1.75^23.08^6.2^10.00^31^1.11^^^^362^^^^^0.0^^^^^^^^^^^^0^^^^^^^^^^^^0.385^^^4^130^~.5 cup~^130^~1 serving~^0";
		CleanData clean = new CleanData(READ_PATH, WRITE_PATH);
		try {
			clean.clean();
		} catch (IOException e) {			
		}	
	}
}