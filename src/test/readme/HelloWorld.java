package readme;

import com.github.sormuras.listing.unit.CompilationUnit;
import com.github.sormuras.listing.unit.MethodDeclaration;
import com.github.sormuras.listing.unit.NormalClassDeclaration;
import javax.lang.model.element.Modifier;

public class HelloWorld {

  public static void main(String[] args) throws Exception {
    CompilationUnit unit = CompilationUnit.of("listing");

    NormalClassDeclaration world = unit.declareClass("HelloWorld");
    world.addModifier(Modifier.PUBLIC);

    MethodDeclaration main = world.declareMethod(void.class, "main");
    main.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
    main.addParameter(String[].class, "args");
    main.addStatement("System.out.println(\"Hello \" + args[0])");

    System.out.println(unit.list());

    Class<?> hello = unit.compile();
    Object[] arguments = {new String[] {"world!"}};
    hello.getMethod("main", String[].class).invoke(null, arguments);
  }
}
