package com.sqisland.friendspell.storage;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import nl.qbusict.cupboard.convert.EntityConverter;
import nl.qbusict.cupboard.convert.FieldConverter;

public class GsonFieldConverter<T> implements FieldConverter<T> {

  private final Gson gson;
  private final Type type;

  public GsonFieldConverter(Gson gson, Type type) {
    this.gson = gson;
    this.type = type;
  }

  @Override
  public T fromCursorValue(Cursor cursor, int columnIndex) {
    return gson.fromJson(cursor.getString(columnIndex), type);
  }

  @Override
  public EntityConverter.ColumnType getColumnType() {
    return EntityConverter.ColumnType.TEXT;
  }

  @Override
  public void toContentValue(T value, String key, ContentValues values) {
    values.put(key, gson.toJson(value));
  }
}