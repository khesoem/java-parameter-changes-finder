package ir.sharif.ce.parameteradderextractor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ir.sharif.ce.parameteradderextractor.models.ListOfClassesAndInterfaces;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.JavaUnit;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MethodSource;

public class JavaClassComparator {
    public static final String METHOD_SIGNATURE_SEPARATOR = ":::", METHOD_LIST_SEPARATOR = "___";

    private ArrayList<JavaClassSource> oldClasses, newClasses;
    private ArrayList<JavaInterfaceSource> oldInterfaces, newInterfaces;
    private Set<String> oldMethodExtendedSignatures, newMethodsExtendedSignatures;

    public JavaClassComparator(String oldFilePath, String newFilePath) throws IOException {
        oldClasses = new ArrayList<JavaClassSource>();
        newClasses = new ArrayList<JavaClassSource>();
        oldInterfaces = new ArrayList<JavaInterfaceSource>();
        newInterfaces = new ArrayList<JavaInterfaceSource>();
        extractAllClassesAndInterfaces(oldFilePath, oldClasses, oldInterfaces);
        extractAllClassesAndInterfaces(newFilePath, newClasses, newInterfaces);

        setOldMethodExtendedSignatures(classListToMethodSignatures(oldClasses));
        oldMethodExtendedSignatures.addAll(interfaceListToMethodSignatures(oldInterfaces));
        setNewMethodsExtendedSignatures(classListToMethodSignatures(newClasses));
        newMethodsExtendedSignatures.addAll(interfaceListToMethodSignatures(newInterfaces));
    }

    public Set<String> getMethodsWithOneParameterAdded() {
        Set<String> ret = new HashSet<String>();
        for (String newMethod : newMethodsExtendedSignatures) {
            if (oldMethodExtendedSignatures.contains(newMethod))
                continue;
            String newMethodWithoutLastParam = eliminateLastParam(newMethod);
            if (newMethodsExtendedSignatures.contains(newMethodWithoutLastParam))
                continue;
            if (oldMethodExtendedSignatures.contains(newMethodWithoutLastParam))
                ret.add(newMethodWithoutLastParam + METHOD_LIST_SEPARATOR + newMethod);
        }
        return ret;
    }

    private String eliminateLastParam(String method) {
        if (method.contains(","))
            return method.substring(0, method.lastIndexOf(",")) + method.substring(method.lastIndexOf(")"));
        else
            return method.substring(0, method.lastIndexOf("(") + 1) + method.substring(method.lastIndexOf(")"));
    }

    public Set<String> classListToMethodSignatures(List<JavaClassSource> classes) {
        Set<String> ret = new HashSet<String>();
        for (JavaClassSource clazz : classes) {
            List<MethodSource<JavaClassSource>> methods = clazz.getMethods();
            for (MethodSource<JavaClassSource> method : methods) {
                ret.add(clazz.getName() + METHOD_SIGNATURE_SEPARATOR + method.toSignature());
            }
        }
        return ret;
    }

    public Set<String> interfaceListToMethodSignatures(List<JavaInterfaceSource> interfaces) {
        Set<String> ret = new HashSet<String>();
        for (JavaInterfaceSource interfaze : interfaces) {
            List<MethodSource<JavaInterfaceSource>> methods = interfaze.getMethods();
            for (MethodSource<JavaInterfaceSource> method : methods) {
                ret.add(interfaze.getName() + METHOD_SIGNATURE_SEPARATOR + method.toSignature());
            }
        }
        return ret;
    }

    public void extractAllClassesAndInterfaces
            (
                    String filePath,
                    List<JavaClassSource> outputClasses,
                    List<JavaInterfaceSource> outputInterfaces
            ) throws IOException {
        File file = new File(filePath);
        InputStream fileStream = new FileInputStream(file);

        JavaUnit unit = Roaster.parseUnit(fileStream);
        List<JavaType<?>> types = unit.getTopLevelTypes();

        // analyzing types
        for (int i = 0; i < types.size(); i++) {
            try {
                JavaType<?> type = types.get(i);
                ListOfClassesAndInterfaces extractedClassesAndInterfaces = extractAllClassesAndInterfaces(type);
                outputClasses.addAll(extractedClassesAndInterfaces.getClasses());
                outputInterfaces.addAll(extractedClassesAndInterfaces.getInterfaces());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        fileStream.close();
    }

    public ListOfClassesAndInterfaces extractAllClassesAndInterfaces(JavaType<?> type) {
        List<JavaClassSource> classes = new ArrayList<JavaClassSource>();
        List<JavaInterfaceSource> interfaces = new ArrayList<JavaInterfaceSource>();
        if (type instanceof JavaClassSource) {
            JavaClassSource clazz = (JavaClassSource) type;
            classes.add(clazz);
            List<JavaSource<?>> nestedTypes = clazz.getNestedTypes();
            for (JavaSource<?> nestedType : nestedTypes) {
                ListOfClassesAndInterfaces extractedClassesAndInterfaces = extractAllClassesAndInterfaces(nestedType);
                classes.addAll(extractedClassesAndInterfaces.getClasses());
                interfaces.addAll(extractedClassesAndInterfaces.getInterfaces());
            }
        } else if (type instanceof JavaInterfaceSource) {
            JavaInterfaceSource interfaze = (JavaInterfaceSource) type;
            interfaces.add(interfaze);
            List<JavaSource<?>> nestedTypes = interfaze.getNestedTypes();
            for (JavaSource<?> nestedType : nestedTypes) {
                ListOfClassesAndInterfaces extractedClassesAndInterfaces = extractAllClassesAndInterfaces(nestedType);
                classes.addAll(extractedClassesAndInterfaces.getClasses());
                interfaces.addAll(extractedClassesAndInterfaces.getInterfaces());
            }
        }
        return new ListOfClassesAndInterfaces(classes, interfaces);
    }

    public ArrayList<JavaClassSource> getOldClasses() {
        return oldClasses;
    }

    public void setOldClasses(ArrayList<JavaClassSource> oldClasses) {
        this.oldClasses = oldClasses;
    }

    public ArrayList<JavaClassSource> getNewClasses() {
        return newClasses;
    }

    public void setNewClasses(ArrayList<JavaClassSource> newClasses) {
        this.newClasses = newClasses;
    }

    public Set<String> getNewMethodsExtendedSignatures() {
        return newMethodsExtendedSignatures;
    }

    public void setNewMethodsExtendedSignatures(Set<String> newMethodsExtendedSignatures) {
        this.newMethodsExtendedSignatures = newMethodsExtendedSignatures;
    }

    public Set<String> getOldMethodExtendedSignatures() {
        return oldMethodExtendedSignatures;
    }

    public void setOldMethodExtendedSignatures(Set<String> oldMethodExtendedSignatures) {
        this.oldMethodExtendedSignatures = oldMethodExtendedSignatures;
    }

    public ArrayList<JavaInterfaceSource> getNewInterfaces() {
        return newInterfaces;
    }

    public void setNewInterfaces(ArrayList<JavaInterfaceSource> newInterfaces) {
        this.newInterfaces = newInterfaces;
    }

    public ArrayList<JavaInterfaceSource> getOldInterfaces() {
        return oldInterfaces;
    }

    public void setOldInterfaces(ArrayList<JavaInterfaceSource> oldInterfaces) {
        this.oldInterfaces = oldInterfaces;
    }
}
