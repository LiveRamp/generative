package com.liveramp.generative.default_values;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.StructMetaData;
import org.apache.thrift.protocol.TType;

import com.liveramp.generative.ArbitraryBoolean;
import com.liveramp.generative.ArbitraryBoundedInt;
import com.liveramp.generative.ArbitraryByteArray;
import com.liveramp.generative.ArbitraryString;
import com.liveramp.generative.ArbitraryThrift;

/**
 * The class that determines which value creator we should use for a given field.
 * Since we have to recursively inspect container classes, their value creators
 * also require a {@link DefaultValueCreatorFactory}. Strictly, since we don't
 * check {@link org.apache.thrift.meta_data.FieldMetaData#requirementType} it's
 * entirely possible that this method doesn't terminate for recursively defined
 * Thrift structs.
 *
 * Therefore, this should only be used for non-recursively defined Thrift structs.
 *
 * For this and associated classes, the {@link FieldValueMetaData} does not provide
 * sufficient information to actually correctly obtain the type. We cast using the
 * {@link TType} and so we'll get unchecked cast warnings which we should ignore
 */
@SuppressWarnings("unchecked")
public class DefaultValueCreatorFactory implements Function<FieldValueMetaData, DefaultValueCreator<?>> {

  private static final DefaultValueCreator<Boolean> boolPrim = fv -> new ArbitraryBoolean();
  private static final DefaultValueCreator<String> stringPrim =
      f -> new ArbitraryBoundedInt(1, 10)
          .flatMap(ArbitraryString::new, String::length);
  private static final DefaultValueCreator<Double> doublePrim =
      fv -> Random::nextDouble;
  private static final DefaultValueCreator<Long> longPrim =
      fv -> Random::nextLong;
  private static final DefaultValueCreator<Integer> intPrim =
      fv -> new ArbitraryBoundedInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
  private static final DefaultValueCreator<Short> shortPrim =
      fv -> new ArbitraryBoundedInt(Short.MIN_VALUE, Short.MAX_VALUE)
          .map(Integer::shortValue);
  private static final DefaultValueCreator<Byte> bytePrim =
      fv -> new ArbitraryByteArray(1).map(b -> b[0]);
  private static final DefaultValueCreator structPrim =
      fv -> new ArbitraryThrift(((StructMetaData)fv).structClass, new HashMap<>());
  private static final DefaultValueCreator<ByteBuffer> bbPrim =
      f -> new ArbitraryBoundedInt(1, 10)
          .flatMap(ArbitraryByteArray::new, s -> s.length)
          .map(ByteBuffer::wrap, ByteBuffer::array);

  private static final Map<Byte, DefaultValueCreator<?>> PRIMITIVES_TO_VALUE_CREATORS = generatePrimitivesToValueCreatorMaps();

  private static Map<Byte, DefaultValueCreator<?>> generatePrimitivesToValueCreatorMaps() {
    Map<Byte, DefaultValueCreator<?>> primitivesToValueCreators = new HashMap<>();
    primitivesToValueCreators.put(TType.BOOL, boolPrim);
    primitivesToValueCreators.put(TType.BYTE, bytePrim);
    primitivesToValueCreators.put(TType.I16, shortPrim);
    primitivesToValueCreators.put(TType.I32, intPrim);
    primitivesToValueCreators.put(TType.DOUBLE, doublePrim);
    primitivesToValueCreators.put(TType.STRING, stringPrim);
    primitivesToValueCreators.put(TType.I64, longPrim);
    return primitivesToValueCreators;
  }

  @Override
  public DefaultValueCreator<?> apply(FieldValueMetaData fv) {
    if (PRIMITIVES_TO_VALUE_CREATORS.containsKey(fv.type)) {
      if (fv.isBinary() || fv.getTypedefName() != null) {
        return bbPrim;
      } else {
        return PRIMITIVES_TO_VALUE_CREATORS.get(fv.type);
      }
    } else if (fv.type == TType.ENUM) {
      return new TEnumValueCreator();
    } else if (fv.type == TType.STRUCT) {
      return structPrim;
    } else if (fv.type == TType.SET) {
      return new SetValueCreator(new DefaultValueCreatorFactory());
    } else if (fv.type == TType.LIST) {
      return new ListValueCreator(new DefaultValueCreatorFactory());
    } else if (fv.type == TType.MAP) {
      return new MapValueCreator<>(new DefaultValueCreatorFactory());
    } else {
      throw new IllegalArgumentException();
    }
  }
}
