package Core;

import java.util.Map;
import javax.lang.model.element.Modifier;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

public class SootOptionsGenerator {
    private Map<String, String> options;

    public SootOptionsGenerator(Map<String, String> options) {
        this.options = options;
    }

    public TypeSpec generateClass(MethodSpec main) {
        TypeSpec sootAnalysis = TypeSpec.classBuilder("SootAnalysis")
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(main)
                        .build();
        return sootAnalysis;
    }

    public JavaFile generateFile(TypeSpec sootAnalysis) {
        JavaFile javaFile = JavaFile.builder("com.example.SootAnalysis", sootAnalysis)
                .build();
        return javaFile;
    }

    public MethodSpec buildMain() {
        MethodSpec main = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .build();
        return main;
    }
}
