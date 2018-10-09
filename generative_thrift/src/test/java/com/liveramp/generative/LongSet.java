package com.liveramp.generative;

/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.11.0)", date = "2018-09-26")
public class LongSet implements org.apache.thrift.TBase<LongSet, LongSet._Fields>, java.io.Serializable, Cloneable, Comparable<LongSet> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("LongSet");

  private static final org.apache.thrift.protocol.TField LONGS_FIELD_DESC = new org.apache.thrift.protocol.TField("longs", org.apache.thrift.protocol.TType.SET, (short)1);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new LongSetStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new LongSetTupleSchemeFactory();

  private java.util.Set<Long> longs; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    LONGS((short)1, "longs");

    private static final java.util.Map<String, _Fields> byName = new java.util.HashMap<String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // LONGS
          return LONGS;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.LONGS, new org.apache.thrift.meta_data.FieldMetaData("longs", org.apache.thrift.TFieldRequirementType.REQUIRED,
        new org.apache.thrift.meta_data.SetMetaData(org.apache.thrift.protocol.TType.SET,
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(LongSet.class, metaDataMap);
  }

  public LongSet() {
  }

  public LongSet(
    java.util.Set<Long> longs)
  {
    this();
    this.longs = longs;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public LongSet(LongSet other) {
    if (other.is_set_longs()) {
      java.util.Set<Long> __this__longs = new java.util.HashSet<Long>(other.longs);
      this.longs = __this__longs;
    }
  }

  public LongSet deepCopy() {
    return new LongSet(this);
  }

  @Override
  public void clear() {
    this.longs = null;
  }

  public int get_longs_size() {
    return (this.longs == null) ? 0 : this.longs.size();
  }

  public java.util.Iterator<Long> get_longs_iterator() {
    return (this.longs == null) ? null : this.longs.iterator();
  }

  public void add_to_longs(long elem) {
    if (this.longs == null) {
      this.longs = new java.util.HashSet<Long>();
    }
    this.longs.add(elem);
  }

  public java.util.Set<Long> get_longs() {
    return this.longs;
  }

  public LongSet set_longs(java.util.Set<Long> longs) {
    this.longs = longs;
    return this;
  }

  public void unset_longs() {
    this.longs = null;
  }

  /** Returns true if field longs is set (has been assigned a value) and false otherwise */
  public boolean is_set_longs() {
    return this.longs != null;
  }

  public void set_longs_isSet(boolean value) {
    if (!value) {
      this.longs = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case LONGS:
      if (value == null) {
        unset_longs();
      } else {
        set_longs((java.util.Set<Long>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case LONGS:
      return get_longs();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case LONGS:
      return is_set_longs();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof LongSet)
      return this.equals((LongSet)that);
    return false;
  }

  public boolean equals(LongSet that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_longs = true && this.is_set_longs();
    boolean that_present_longs = true && that.is_set_longs();
    if (this_present_longs || that_present_longs) {
      if (!(this_present_longs && that_present_longs))
        return false;
      if (!this.longs.equals(that.longs))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((is_set_longs()) ? 131071 : 524287);
    if (is_set_longs())
      hashCode = hashCode * 8191 + longs.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(LongSet other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(is_set_longs()).compareTo(other.is_set_longs());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (is_set_longs()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.longs, other.longs);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("LongSet(");
    boolean first = true;

    sb.append("longs:");
    if (this.longs == null) {
      sb.append("null");
    } else {
      sb.append(this.longs);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (longs == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'longs' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class LongSetStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public LongSetStandardScheme getScheme() {
      return new LongSetStandardScheme();
    }
  }

  private static class LongSetStandardScheme extends org.apache.thrift.scheme.StandardScheme<LongSet> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, LongSet struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
          break;
        }
        switch (schemeField.id) {
          case 1: // LONGS
            if (schemeField.type == org.apache.thrift.protocol.TType.SET) {
              {
                org.apache.thrift.protocol.TSet _set32 = iprot.readSetBegin();
                struct.longs = new java.util.HashSet<Long>(2*_set32.size);
                long _elem33;
                for (int _i34 = 0; _i34 < _set32.size; ++_i34)
                {
                  _elem33 = iprot.readI64();
                  struct.longs.add(_elem33);
                }
                iprot.readSetEnd();
              }
              struct.set_longs_isSet(true);
            } else {
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, LongSet struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.longs != null) {
        oprot.writeFieldBegin(LONGS_FIELD_DESC);
        {
          oprot.writeSetBegin(new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.I64, struct.longs.size()));
          for (long _iter35 : struct.longs)
          {
            oprot.writeI64(_iter35);
          }
          oprot.writeSetEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class LongSetTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public LongSetTupleScheme getScheme() {
      return new LongSetTupleScheme();
    }
  }

  private static class LongSetTupleScheme extends org.apache.thrift.scheme.TupleScheme<LongSet> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, LongSet struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      {
        oprot.writeI32(struct.longs.size());
        for (long _iter36 : struct.longs)
        {
          oprot.writeI64(_iter36);
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, LongSet struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      {
        org.apache.thrift.protocol.TSet _set37 = new org.apache.thrift.protocol.TSet(org.apache.thrift.protocol.TType.I64, iprot.readI32());
        struct.longs = new java.util.HashSet<Long>(2*_set37.size);
        long _elem38;
        for (int _i39 = 0; _i39 < _set37.size; ++_i39)
        {
          _elem38 = iprot.readI64();
          struct.longs.add(_elem38);
        }
      }
      struct.set_longs_isSet(true);
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

