package readme;

import com.github.sormuras.listing.Name;
import com.github.sormuras.listing.Tool;
import com.github.sormuras.listing.unit.CompilationUnit;
import com.github.sormuras.listing.unit.MethodDeclaration;
import com.github.sormuras.listing.unit.NormalClassDeclaration;
import javax.lang.model.element.Modifier;

public class HelloWorld2 {

  public static void main(String[] args) throws Exception {
    CompilationUnit unit = CompilationUnit.of("listing");
    unit.getImportDeclarations().addSingleStaticImport(Name.of(System.class, "out"));

    NormalClassDeclaration world = unit.declareClass("World");
    world.addModifier(Modifier.PUBLIC);

    MethodDeclaration main = world.declareMethod(void.class, "main");
    main.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
    main.addParameter(String[].class, "args");
    main.setBody(
        listable ->
            listable
                .add(Name.of(System.class, "out"))
                .add(".println(")
                .add(Tool.escape("Hello "))
                .add(" + args[0]")
                .add(");")
                .newline());

    System.out.println(unit.list());

    Class<?> hello = unit.compile();
    Object[] arguments = {new String[] {"world!"}};
    hello.getMethod("main", String[].class).invoke(null, arguments);
  }
}
