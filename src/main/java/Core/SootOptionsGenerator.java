package Core;

import java.util.Map;
import javax.lang.model.element.Modifier;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

public class SootOptionsGenerator {
    private Map<String, String> options;

    public SootOptionsGenerator(Map<String, String> options) {
        this.options = options;
    }

    public TypeSpec generateClass(MethodSpec main) {
        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(main)
                .build();
        return helloWorld;
    }
}
