package io.slinkydeveloper.vertx.generators.impl;

import io.vertx.codegen.*;
import io.vertx.codegen.type.TypeInfo;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * @author <a href="https://github.com/slinkydeveloper">Francesco Guardiani</a>
 */
public class FuturifyDelegateModel extends ClassModel {

  public FuturifyDelegateModel(ProcessingEnvironment env, TypeElement modelElt) {
    super(env, modelElt);
  }

  @Override
  public String getKind() {
    return "futurify";
  }

  @Override
  public boolean process() {
    boolean processed = super.process();
    if (processed) {
      if (this.getElement().getKind() != ElementKind.INTERFACE)
        throw new GenException(this.getElement(), "@Futurify can only be used with interfaces in " + this.getElement().asType().toString());
    }
    return processed;
  }
}
