package io.slinkydeveloper.vertx.generators.impl;

import io.slinkydeveloper.vertx.generators.Futurify;
import io.vertx.codegen.ClassModel;
import io.vertx.codegen.Generator;
import io.vertx.codegen.MethodInfo;
import io.vertx.codegen.ParamInfo;
import io.vertx.codegen.annotations.ModuleGen;
import io.vertx.codegen.type.*;
import io.vertx.codegen.writer.CodeWriter;

import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="http://slinkydeveloper.github.io">Francesco Guardiani @slinkydeveloper</a>
 */
public class FuturifyDelegateGen extends Generator<ClassModel> {

  private static final String FUTURE_IMPORT = "io.vertx.core.Future";

  public FuturifyDelegateGen() {
    super();
    kinds = Collections.singleton("futurify");
    name = "futurify_delegate_gen";
  }

  @Override
  public Collection<Class<? extends Annotation>> annotations() {
    return Arrays.asList(Futurify.class, ModuleGen.class);
  }

  public String className(ClassModel model) {
    return model.getIfaceSimpleName() + "Futurized";
  }

  @Override
  public String filename(ClassModel model) {
    return model.getIfacePackageName() + "." + this.className(model) + ".java";
  }

  public String render(ClassModel model, int index, int size, Map<String, Object> session) {
    StringWriter buffer = new StringWriter();
    CodeWriter writer = new CodeWriter(buffer);
    String className = this.className(model);
    String delegateClassName = model.getIfaceSimpleName();

    writer
        .stmt("package " + model.getIfacePackageName())
        .newLine()
        .javaImport(model.getIfaceFQCN());

    // Handle imports
    Set<String> imports = model
        .getImportedTypes()
        .stream()
        .filter(c -> !c.getPackageName().equals("java.lang"))
        .map(TypeInfo::toString).collect(Collectors.toSet());
    imports.add(FUTURE_IMPORT);
    imports.forEach(writer::javaImport);

    writer
        .code("public class " + className + " {\n")
        .newLine()
        .indent()
          .stmt("private final " + delegateClassName + " delegate")
          .newLine()
          .code("public " + className + "(" + delegateClassName + " delegate){\n")
          .indent()
            .stmt("this.delegate = delegate")
          .unindent()
          .code("}\n")
          .newLine()
          .code("public " + delegateClassName + " getDelegate(){\n")
          .indent()
            .stmt("return this.delegate")
          .unindent()
          .code("}\n");

    var partitionedMethods = model.getInstanceMethods().stream().collect(Collectors.groupingBy(this::isLastParamHandlerAsyncResult));

    partitionedMethods.get(true).stream().forEach(m -> renderFuturizedMethod(m, writer));
    partitionedMethods.get(false).stream().forEach(m -> renderUnfuturizedMethod(m, writer));

    writer.unindent().code("}");

    return buffer.toString();
  }

  private boolean isLastParamHandlerAsyncResult(MethodInfo methodInfo) {
    ParamInfo pHandler = !methodInfo.getParams().isEmpty() ? methodInfo.getParam(methodInfo.getParams().size() - 1) : null;
    if (pHandler == null || pHandler.getType().getKind() != ClassKind.HANDLER) return false;
    TypeInfo arType = ((ParameterizedTypeInfo)pHandler.getType()).getArg(0);
    return arType != null && arType.getKind() == ClassKind.ASYNC_RESULT;
  }

  private TypeInfo extractAsyncReturnType(MethodInfo methodInfo){
    return (
        (ParameterizedTypeInfo) (
            (ParameterizedTypeInfo) methodInfo.getParam(methodInfo.getParams().size() - 1).getType()
        ).getArg(0)
    ).getArg(0);
  }

  private void renderFuturizedMethod(MethodInfo method, CodeWriter writer) {
    List<ParamInfo> paramsToRender = method.getParams().subList(0, method.getParams().size() - 1);
    String renderedSignatureParams = paramsToRender.stream().map(ParamInfo::toString).collect(Collectors.joining(", "));
    String renderedParams = paramsToRender.stream().map(ParamInfo::getName).collect(Collectors.joining(", "));
    TypeInfo returnType = extractAsyncReturnType(method);

    writer
        .codeln("public Future<" + returnType + "> " + method.getName() + "(" + renderedSignatureParams + "){")
        .indent()
          .stmt("Future<" + returnType + "> fut = Future.future()")
          .stmt("this.delegate." + method.getName() + "(" + (!paramsToRender.isEmpty() ? renderedParams + ", " : "") + "fut.completer())")
          .stmt("return fut")
        .unindent()
        .codeln("}");
  }

  private void renderUnfuturizedMethod(MethodInfo method, CodeWriter writer) {
    String renderedSignatureParams = method.getParams().stream().map(ParamInfo::toString).collect(Collectors.joining(", "));
    String renderedParams = method.getParams().stream().map(ParamInfo::getName).collect(Collectors.joining(", "));

    if (method.getReturnType().isVoid()) {
      writer
          .codeln("public void " + method.getName() + "(" + renderedSignatureParams + "){")
          .indent()
            .stmt("this.delegate." + method.getName() + "(" + renderedParams + ")")
          .unindent()
          .codeln("}");
    } else {
      writer
          .codeln("public " + method.getReturnType() + " " + method.getName() + "(" + renderedSignatureParams + "){")
          .indent()
            .stmt("return this.delegate." + method.getName() + "(" + renderedParams + ")")
          .unindent()
          .codeln("}");
    }
  }

}
