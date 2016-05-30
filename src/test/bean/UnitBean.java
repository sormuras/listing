package bean;

public interface UnitBean {

  default void addImport(Class<?> type) {
    addImport(TypeBean.of(type));
  }

  void addImport(TypeBean type);

  void setPackageName(String name);

  AnnotationBean declareAnnotation();

  ClassBean declareClass();
}
