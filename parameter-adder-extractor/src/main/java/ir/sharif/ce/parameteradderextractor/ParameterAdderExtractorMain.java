package ir.sharif.ce.parameteradderextractor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import ir.sharif.ce.parameteradderextractor.util.JavaClassComparator;

public class ParameterAdderExtractorMain {
	
	/* args[0] = oldClassFilePath, args[1] = newClassFilePath, args[2] = outputFilePath */
	public static void main(String[] args) throws IOException {
		File outputFile = new File(args[2]);
//		File outputFile = new File("output");
		outputFile.createNewFile();
		PrintWriter outputPw = new PrintWriter(outputFile);
		
		JavaClassComparator comparator = new JavaClassComparator(args[0], args[1]);
//		JavaClassComparator comparator = new JavaClassComparator("FieldVector3Dold.java", "FieldVector3D.java");
		Set<String> methodsWithOneAddedParameter = comparator.getMethodsWithOneParameterAdded();
		for(String methodWithOneAddedParameter : methodsWithOneAddedParameter){
			String[] methodSignatures = methodWithOneAddedParameter.split(JavaClassComparator.METHOD_LIST_SEPARATOR);
			String oldSignature = methodSignatures[0].substring(
					methodSignatures[0].indexOf(JavaClassComparator.METHOD_SIGNATURE_SEPARATOR)
							+ JavaClassComparator.METHOD_SIGNATURE_SEPARATOR.length()),
					newSignature = methodSignatures[1].substring(
							methodSignatures[1].indexOf(JavaClassComparator.METHOD_SIGNATURE_SEPARATOR)
									+ JavaClassComparator.METHOD_SIGNATURE_SEPARATOR.length());
			outputPw.println(oldSignature + ", " + newSignature);
		}
		outputPw.flush();
		
		outputPw.close();
	}
}
