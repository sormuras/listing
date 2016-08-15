package readme;

import com.github.sormuras.listing.Name;
import com.github.sormuras.listing.unit.CompilationUnit;
import com.github.sormuras.listing.unit.MethodDeclaration;
import com.github.sormuras.listing.unit.MethodParameter;
import com.github.sormuras.listing.unit.NormalClassDeclaration;
import javax.lang.model.element.Modifier;

public class HelloWorld {

  public static void main(String[] args) throws Exception {
    Name out = Name.of(System.class, "out");

    CompilationUnit unit = CompilationUnit.of("listing");
    unit.getImportDeclarations().addSingleStaticImport(out);

    NormalClassDeclaration world = unit.declareClass("World");
    world.addModifier(Modifier.PUBLIC);

    MethodParameter strings = MethodParameter.of(String[].class, "strings");
    MethodDeclaration main = world.declareMethod(void.class, "main");
    main.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
    main.addParameter(strings);
    main.addStatement("{N}.println({S} + {getName}[0]);", out, "Hello ", strings);

    System.out.println(unit.list(b -> b.setOmitJavaLangPackage(true)));

    Class<?> hello = unit.compile();
    Object[] arguments = {new String[] {"world!"}};
    hello.getMethod("main", String[].class).invoke(null, arguments);
  }
}
