package ir.sharif.ce.parameteradderextractor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import ir.sharif.ce.parameteradderextractor.util.JavaClassComparator;

public class ParameterAdderExtractorMain {
	
	/* args[0] = oldClassFilePath, args[1] = newClassFilePath, args[2] = outputFilePath */
	public static void main(String[] args) throws IOException {
//		File outputFile = new File(args[2]);
		File outputFile = new File("output");
		outputFile.createNewFile();
		PrintWriter outputPw = new PrintWriter(outputFile);
		
//		JavaClassComparator comparator = new JavaClassComparator(args[0], args[1]);
		JavaClassComparator comparator = new JavaClassComparator("JavaClassComparatorOld.java", "JavaClassComparator.java");
		Set<String> methodsWithOneAddedParameter = comparator.getMethodWithOneParameterAdded();
		for(String methodWithOneAddedParameter : methodsWithOneAddedParameter){
			outputPw.println(methodWithOneAddedParameter);
		}
		outputPw.flush();
		
		outputPw.close();
	}
}
