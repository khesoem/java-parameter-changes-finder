package ir.sharif.ce.parameteradderextractor.models;

import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;

import java.util.List;

/**
 * Created by khesoem on 9/8/2018.
 */
public class ListOfClassesAndInterfaces {
    private List<JavaInterfaceSource> interfaces;
    private List<JavaClassSource> classes;

    public ListOfClassesAndInterfaces(List<JavaClassSource> classes, List<JavaInterfaceSource> interfaces){
        this.classes = classes;
        this.interfaces = interfaces;
    }

    public List<JavaInterfaceSource> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<JavaInterfaceSource> interfaces) {
        this.interfaces = interfaces;
    }

    public List<JavaClassSource> getClasses() {
        return classes;
    }

    public void setClasses(List<JavaClassSource> classes) {
        this.classes = classes;
    }
}
