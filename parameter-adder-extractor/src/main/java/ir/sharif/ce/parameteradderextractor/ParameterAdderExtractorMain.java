package ir.sharif.ce.parameteradderextractor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Set;

import ir.sharif.ce.parameteradderextractor.util.JavaClassComparator;

public class ParameterAdderExtractorMain {
	
	/*  args[0] = inputFilePath, args[1] = outputFilePath
	*   input-format: listOf(commit_id, file_name, old_file_path, new_file_path)
	*/
	public static void main(String[] args) throws IOException {
		File outputFile = new File(args[1]);
		outputFile.createNewFile();
		PrintWriter outputPw = new PrintWriter(outputFile);

        int numberOfCheckedFileChanges = 0;
        Scanner sc = new Scanner(new File(args[0]));
        while(sc.hasNextLine()) {
            String inputStr = sc.nextLine();
            if(inputStr.startsWith("#"))
                continue;
            String[] inputArr = inputStr.split(", ");
            String commitId = inputArr[0], fileName = inputArr[1], oldFilePath = inputArr[2], newFilePath = inputArr[3];
            printParameterAddedMethods(outputPw, commitId, fileName, oldFilePath, newFilePath);
            numberOfCheckedFileChanges++;
            if(numberOfCheckedFileChanges % 100 == 0){
                System.out.println(numberOfCheckedFileChanges + " changed_files are checked");
            }
        }
		outputPw.close();
	}

    private static void printParameterAddedMethods(PrintWriter outputPw, String commitId, String fileName, String oldFilePath, String newFilePath) throws IOException {
        JavaClassComparator comparator = new JavaClassComparator(oldFilePath, newFilePath);
        Set<String> methodsWithOneAddedParameter = comparator.getMethodsWithOneParameterAdded();
        for (String methodWithOneAddedParameter : methodsWithOneAddedParameter) {
            String[] methodSignatures = methodWithOneAddedParameter.split(JavaClassComparator.METHOD_LIST_SEPARATOR);
            String oldSignature = methodSignatures[0].substring(
                    methodSignatures[0].indexOf(JavaClassComparator.METHOD_SIGNATURE_SEPARATOR)
                            + JavaClassComparator.METHOD_SIGNATURE_SEPARATOR.length()),
                    newSignature = methodSignatures[1].substring(
                            methodSignatures[1].indexOf(JavaClassComparator.METHOD_SIGNATURE_SEPARATOR)
                                    + JavaClassComparator.METHOD_SIGNATURE_SEPARATOR.length());
            outputPw.println(commitId + ", " + fileName + ", " + oldSignature + ", " + newSignature);
        }
        outputPw.flush();
    }
}
