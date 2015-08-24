package com.sqisland.friendspell.storage;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.convert.FieldConverter;
import nl.qbusict.cupboard.convert.FieldConverterFactory;

public class GsonFieldConverterFactory implements FieldConverterFactory {
  private Gson gson;
  private Type type;

  public GsonFieldConverterFactory(Gson gson, Type type) {
    this.gson = gson;
    this.type = type;
  }

  public GsonFieldConverterFactory(Type type) {
    this.gson = new Gson();
    this.type = type;
  }

  @Override
  public FieldConverter<?> create(Cupboard cupboard, Type type) {
    if (type.equals(this.type)) {
      return new GsonFieldConverter(gson, type);
    }
    return null;
  }
}
