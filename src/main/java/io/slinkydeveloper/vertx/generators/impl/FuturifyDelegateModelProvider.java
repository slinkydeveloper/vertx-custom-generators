package io.slinkydeveloper.vertx.generators.impl;

import io.slinkydeveloper.vertx.generators.Futurify;
import io.vertx.codegen.Model;
import io.vertx.codegen.ModelProvider;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * @author <a href="https://github.com/slinkydeveloper">Francesco Guardiani</a>
 */
public class FuturifyDelegateModelProvider implements ModelProvider {
  @Override
  public Model getModel(ProcessingEnvironment env, TypeElement elt) {
    if (elt.getAnnotation(Futurify.class) != null) {
      FuturifyDelegateModel model = new FuturifyDelegateModel(env, elt);
      return model;
    } else {
      return null;
    }
  }
}
