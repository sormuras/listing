package bean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.lang.model.element.Modifier;

import org.junit.Assert;
import org.junit.Test;

public class BeanTest implements InvocationHandler {

  UnitBean createUnitBean() {
    Class<?>[] interfaces = {UnitBean.class};
    return (UnitBean) Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, this);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // System.out.println(method.getName());
    Class<?>[] interfaces = {null};
    switch (method.getName()) {
      case "declareAnnotation":
        interfaces[0] = AnnotationBean.class;
        return Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, this);
      case "declareClass":
        interfaces[0] = ClassBean.class;
        return Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, this);
      case "declareMethod":
        interfaces[0] = MethodBean.class;
        return Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, this);
      case "toString":
        return "123";
      default:
        break;
    }
    return null;
  }

  @Test
  public void test() {
    UnitBean unit = createUnitBean();
    unit.setPackageName("bean");
    unit.addImport(Modifier.class);
    unit.addImport(Assert.class);
    unit.addImport(Test.class);

    AnnotationBean x = unit.declareAnnotation();
    x.setName("X");

    ClassBean cd = unit.declareClass();
    cd.addModifier(Modifier.PUBLIC);
    cd.setName("BeanTest");

    MethodBean test = cd.declareMethod();
    test.addAnnotation(Test.class);
    test.addAnnotation(x);
    test.addModifier(Modifier.PUBLIC);
    test.setName("test");
    test.setBody("//...");

    MethodBean chain = cd.declareMethod();
    chain.setReturnType(cd);
    chain.setName("chain");
    chain.setBody("return (" + cd + ") this;");

    Assert.assertEquals("123", unit.toString());
  }
}
