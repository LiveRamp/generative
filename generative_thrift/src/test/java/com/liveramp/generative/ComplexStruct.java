/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.liveramp.generative;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.11.0)", date = "2018-10-04")
public class ComplexStruct implements org.apache.thrift.TBase<ComplexStruct, ComplexStruct._Fields>, java.io.Serializable, Cloneable, Comparable<ComplexStruct> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ComplexStruct");

  private static final org.apache.thrift.protocol.TField STRCT_FIELD_DESC = new org.apache.thrift.protocol.TField("strct", org.apache.thrift.protocol.TType.STRUCT, (short)1);
  private static final org.apache.thrift.protocol.TField U_FIELD_DESC = new org.apache.thrift.protocol.TField("u", org.apache.thrift.protocol.TType.STRUCT, (short)2);
  private static final org.apache.thrift.protocol.TField I1_FIELD_DESC = new org.apache.thrift.protocol.TField("i1", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField S_FIELD_DESC = new org.apache.thrift.protocol.TField("s", org.apache.thrift.protocol.TType.STRUCT, (short)4);
  private static final org.apache.thrift.protocol.TField S2_FIELD_DESC = new org.apache.thrift.protocol.TField("s2", org.apache.thrift.protocol.TType.STRUCT, (short)5);
  private static final org.apache.thrift.protocol.TField S3_FIELD_DESC = new org.apache.thrift.protocol.TField("s3", org.apache.thrift.protocol.TType.STRUCT, (short)6);
  private static final org.apache.thrift.protocol.TField S4_FIELD_DESC = new org.apache.thrift.protocol.TField("s4", org.apache.thrift.protocol.TType.STRUCT, (short)7);
  private static final org.apache.thrift.protocol.TField S5_FIELD_DESC = new org.apache.thrift.protocol.TField("s5", org.apache.thrift.protocol.TType.STRUCT, (short)8);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new ComplexStructStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new ComplexStructTupleSchemeFactory();

  private NestedStruct strct; // optional
  private ComplexUnion u; // optional
  private int i1; // required
  private PrimitiveStruct s; // required
  private PrimitiveStruct s2; // required
  private PrimitiveStruct s3; // required
  private NestedStruct s4; // optional
  private PrimitiveStruct s5; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    STRCT((short)1, "strct"),
    U((short)2, "u"),
    I1((short)3, "i1"),
    S((short)4, "s"),
    S2((short)5, "s2"),
    S3((short)6, "s3"),
    S4((short)7, "s4"),
    S5((short)8, "s5");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

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
        case 1: // STRCT
          return STRCT;
        case 2: // U
          return U;
        case 3: // I1
          return I1;
        case 4: // S
          return S;
        case 5: // S2
          return S2;
        case 6: // S3
          return S3;
        case 7: // S4
          return S4;
        case 8: // S5
          return S5;
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
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __I1_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.STRCT,_Fields.U,_Fields.S4,_Fields.S5};
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.STRCT, new org.apache.thrift.meta_data.FieldMetaData("strct", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, NestedStruct.class)));
    tmpMap.put(_Fields.U, new org.apache.thrift.meta_data.FieldMetaData("u", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ComplexUnion.class)));
    tmpMap.put(_Fields.I1, new org.apache.thrift.meta_data.FieldMetaData("i1", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.S, new org.apache.thrift.meta_data.FieldMetaData("s", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, PrimitiveStruct.class)));
    tmpMap.put(_Fields.S2, new org.apache.thrift.meta_data.FieldMetaData("s2", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, PrimitiveStruct.class)));
    tmpMap.put(_Fields.S3, new org.apache.thrift.meta_data.FieldMetaData("s3", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, PrimitiveStruct.class)));
    tmpMap.put(_Fields.S4, new org.apache.thrift.meta_data.FieldMetaData("s4", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, NestedStruct.class)));
    tmpMap.put(_Fields.S5, new org.apache.thrift.meta_data.FieldMetaData("s5", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, PrimitiveStruct.class)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ComplexStruct.class, metaDataMap);
  }

  public ComplexStruct() {
  }

  public ComplexStruct(
    int i1,
    PrimitiveStruct s,
    PrimitiveStruct s2,
    PrimitiveStruct s3)
  {
    this();
    this.i1 = i1;
    set_i1_isSet(true);
    this.s = s;
    this.s2 = s2;
    this.s3 = s3;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ComplexStruct(ComplexStruct other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.is_set_strct()) {
      this.strct = new NestedStruct(other.strct);
    }
    if (other.is_set_u()) {
      this.u = new ComplexUnion(other.u);
    }
    this.i1 = other.i1;
    if (other.is_set_s()) {
      this.s = new PrimitiveStruct(other.s);
    }
    if (other.is_set_s2()) {
      this.s2 = new PrimitiveStruct(other.s2);
    }
    if (other.is_set_s3()) {
      this.s3 = new PrimitiveStruct(other.s3);
    }
    if (other.is_set_s4()) {
      this.s4 = new NestedStruct(other.s4);
    }
    if (other.is_set_s5()) {
      this.s5 = new PrimitiveStruct(other.s5);
    }
  }

  public ComplexStruct deepCopy() {
    return new ComplexStruct(this);
  }

  @Override
  public void clear() {
    this.strct = null;
    this.u = null;
    set_i1_isSet(false);
    this.i1 = 0;
    this.s = null;
    this.s2 = null;
    this.s3 = null;
    this.s4 = null;
    this.s5 = null;
  }

  public NestedStruct get_strct() {
    return this.strct;
  }

  public ComplexStruct set_strct(NestedStruct strct) {
    this.strct = strct;
    return this;
  }

  public void unset_strct() {
    this.strct = null;
  }

  /** Returns true if field strct is set (has been assigned a value) and false otherwise */
  public boolean is_set_strct() {
    return this.strct != null;
  }

  public void set_strct_isSet(boolean value) {
    if (!value) {
      this.strct = null;
    }
  }

  public ComplexUnion get_u() {
    return this.u;
  }

  public ComplexStruct set_u(ComplexUnion u) {
    this.u = u;
    return this;
  }

  public void unset_u() {
    this.u = null;
  }

  /** Returns true if field u is set (has been assigned a value) and false otherwise */
  public boolean is_set_u() {
    return this.u != null;
  }

  public void set_u_isSet(boolean value) {
    if (!value) {
      this.u = null;
    }
  }

  public int get_i1() {
    return this.i1;
  }

  public ComplexStruct set_i1(int i1) {
    this.i1 = i1;
    set_i1_isSet(true);
    return this;
  }

  public void unset_i1() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __I1_ISSET_ID);
  }

  /** Returns true if field i1 is set (has been assigned a value) and false otherwise */
  public boolean is_set_i1() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __I1_ISSET_ID);
  }

  public void set_i1_isSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __I1_ISSET_ID, value);
  }

  public PrimitiveStruct get_s() {
    return this.s;
  }

  public ComplexStruct set_s(PrimitiveStruct s) {
    this.s = s;
    return this;
  }

  public void unset_s() {
    this.s = null;
  }

  /** Returns true if field s is set (has been assigned a value) and false otherwise */
  public boolean is_set_s() {
    return this.s != null;
  }

  public void set_s_isSet(boolean value) {
    if (!value) {
      this.s = null;
    }
  }

  public PrimitiveStruct get_s2() {
    return this.s2;
  }

  public ComplexStruct set_s2(PrimitiveStruct s2) {
    this.s2 = s2;
    return this;
  }

  public void unset_s2() {
    this.s2 = null;
  }

  /** Returns true if field s2 is set (has been assigned a value) and false otherwise */
  public boolean is_set_s2() {
    return this.s2 != null;
  }

  public void set_s2_isSet(boolean value) {
    if (!value) {
      this.s2 = null;
    }
  }

  public PrimitiveStruct get_s3() {
    return this.s3;
  }

  public ComplexStruct set_s3(PrimitiveStruct s3) {
    this.s3 = s3;
    return this;
  }

  public void unset_s3() {
    this.s3 = null;
  }

  /** Returns true if field s3 is set (has been assigned a value) and false otherwise */
  public boolean is_set_s3() {
    return this.s3 != null;
  }

  public void set_s3_isSet(boolean value) {
    if (!value) {
      this.s3 = null;
    }
  }

  public NestedStruct get_s4() {
    return this.s4;
  }

  public ComplexStruct set_s4(NestedStruct s4) {
    this.s4 = s4;
    return this;
  }

  public void unset_s4() {
    this.s4 = null;
  }

  /** Returns true if field s4 is set (has been assigned a value) and false otherwise */
  public boolean is_set_s4() {
    return this.s4 != null;
  }

  public void set_s4_isSet(boolean value) {
    if (!value) {
      this.s4 = null;
    }
  }

  public PrimitiveStruct get_s5() {
    return this.s5;
  }

  public ComplexStruct set_s5(PrimitiveStruct s5) {
    this.s5 = s5;
    return this;
  }

  public void unset_s5() {
    this.s5 = null;
  }

  /** Returns true if field s5 is set (has been assigned a value) and false otherwise */
  public boolean is_set_s5() {
    return this.s5 != null;
  }

  public void set_s5_isSet(boolean value) {
    if (!value) {
      this.s5 = null;
    }
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case STRCT:
      if (value == null) {
        unset_strct();
      } else {
        set_strct((NestedStruct)value);
      }
      break;

    case U:
      if (value == null) {
        unset_u();
      } else {
        set_u((ComplexUnion)value);
      }
      break;

    case I1:
      if (value == null) {
        unset_i1();
      } else {
        set_i1((java.lang.Integer)value);
      }
      break;

    case S:
      if (value == null) {
        unset_s();
      } else {
        set_s((PrimitiveStruct)value);
      }
      break;

    case S2:
      if (value == null) {
        unset_s2();
      } else {
        set_s2((PrimitiveStruct)value);
      }
      break;

    case S3:
      if (value == null) {
        unset_s3();
      } else {
        set_s3((PrimitiveStruct)value);
      }
      break;

    case S4:
      if (value == null) {
        unset_s4();
      } else {
        set_s4((NestedStruct)value);
      }
      break;

    case S5:
      if (value == null) {
        unset_s5();
      } else {
        set_s5((PrimitiveStruct)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case STRCT:
      return get_strct();

    case U:
      return get_u();

    case I1:
      return get_i1();

    case S:
      return get_s();

    case S2:
      return get_s2();

    case S3:
      return get_s3();

    case S4:
      return get_s4();

    case S5:
      return get_s5();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case STRCT:
      return is_set_strct();
    case U:
      return is_set_u();
    case I1:
      return is_set_i1();
    case S:
      return is_set_s();
    case S2:
      return is_set_s2();
    case S3:
      return is_set_s3();
    case S4:
      return is_set_s4();
    case S5:
      return is_set_s5();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof ComplexStruct)
      return this.equals((ComplexStruct)that);
    return false;
  }

  public boolean equals(ComplexStruct that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_strct = true && this.is_set_strct();
    boolean that_present_strct = true && that.is_set_strct();
    if (this_present_strct || that_present_strct) {
      if (!(this_present_strct && that_present_strct))
        return false;
      if (!this.strct.equals(that.strct))
        return false;
    }

    boolean this_present_u = true && this.is_set_u();
    boolean that_present_u = true && that.is_set_u();
    if (this_present_u || that_present_u) {
      if (!(this_present_u && that_present_u))
        return false;
      if (!this.u.equals(that.u))
        return false;
    }

    boolean this_present_i1 = true;
    boolean that_present_i1 = true;
    if (this_present_i1 || that_present_i1) {
      if (!(this_present_i1 && that_present_i1))
        return false;
      if (this.i1 != that.i1)
        return false;
    }

    boolean this_present_s = true && this.is_set_s();
    boolean that_present_s = true && that.is_set_s();
    if (this_present_s || that_present_s) {
      if (!(this_present_s && that_present_s))
        return false;
      if (!this.s.equals(that.s))
        return false;
    }

    boolean this_present_s2 = true && this.is_set_s2();
    boolean that_present_s2 = true && that.is_set_s2();
    if (this_present_s2 || that_present_s2) {
      if (!(this_present_s2 && that_present_s2))
        return false;
      if (!this.s2.equals(that.s2))
        return false;
    }

    boolean this_present_s3 = true && this.is_set_s3();
    boolean that_present_s3 = true && that.is_set_s3();
    if (this_present_s3 || that_present_s3) {
      if (!(this_present_s3 && that_present_s3))
        return false;
      if (!this.s3.equals(that.s3))
        return false;
    }

    boolean this_present_s4 = true && this.is_set_s4();
    boolean that_present_s4 = true && that.is_set_s4();
    if (this_present_s4 || that_present_s4) {
      if (!(this_present_s4 && that_present_s4))
        return false;
      if (!this.s4.equals(that.s4))
        return false;
    }

    boolean this_present_s5 = true && this.is_set_s5();
    boolean that_present_s5 = true && that.is_set_s5();
    if (this_present_s5 || that_present_s5) {
      if (!(this_present_s5 && that_present_s5))
        return false;
      if (!this.s5.equals(that.s5))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((is_set_strct()) ? 131071 : 524287);
    if (is_set_strct())
      hashCode = hashCode * 8191 + strct.hashCode();

    hashCode = hashCode * 8191 + ((is_set_u()) ? 131071 : 524287);
    if (is_set_u())
      hashCode = hashCode * 8191 + u.hashCode();

    hashCode = hashCode * 8191 + i1;

    hashCode = hashCode * 8191 + ((is_set_s()) ? 131071 : 524287);
    if (is_set_s())
      hashCode = hashCode * 8191 + s.hashCode();

    hashCode = hashCode * 8191 + ((is_set_s2()) ? 131071 : 524287);
    if (is_set_s2())
      hashCode = hashCode * 8191 + s2.hashCode();

    hashCode = hashCode * 8191 + ((is_set_s3()) ? 131071 : 524287);
    if (is_set_s3())
      hashCode = hashCode * 8191 + s3.hashCode();

    hashCode = hashCode * 8191 + ((is_set_s4()) ? 131071 : 524287);
    if (is_set_s4())
      hashCode = hashCode * 8191 + s4.hashCode();

    hashCode = hashCode * 8191 + ((is_set_s5()) ? 131071 : 524287);
    if (is_set_s5())
      hashCode = hashCode * 8191 + s5.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(ComplexStruct other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(is_set_strct()).compareTo(other.is_set_strct());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (is_set_strct()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.strct, other.strct);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(is_set_u()).compareTo(other.is_set_u());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (is_set_u()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.u, other.u);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(is_set_i1()).compareTo(other.is_set_i1());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (is_set_i1()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.i1, other.i1);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(is_set_s()).compareTo(other.is_set_s());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (is_set_s()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.s, other.s);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(is_set_s2()).compareTo(other.is_set_s2());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (is_set_s2()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.s2, other.s2);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(is_set_s3()).compareTo(other.is_set_s3());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (is_set_s3()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.s3, other.s3);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(is_set_s4()).compareTo(other.is_set_s4());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (is_set_s4()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.s4, other.s4);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(is_set_s5()).compareTo(other.is_set_s5());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (is_set_s5()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.s5, other.s5);
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
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("ComplexStruct(");
    boolean first = true;

    if (is_set_strct()) {
      sb.append("strct:");
      if (this.strct == null) {
        sb.append("null");
      } else {
        sb.append(this.strct);
      }
      first = false;
    }
    if (is_set_u()) {
      if (!first) sb.append(", ");
      sb.append("u:");
      if (this.u == null) {
        sb.append("null");
      } else {
        sb.append(this.u);
      }
      first = false;
    }
    if (!first) sb.append(", ");
    sb.append("i1:");
    sb.append(this.i1);
    first = false;
    if (!first) sb.append(", ");
    sb.append("s:");
    if (this.s == null) {
      sb.append("null");
    } else {
      sb.append(this.s);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("s2:");
    if (this.s2 == null) {
      sb.append("null");
    } else {
      sb.append(this.s2);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("s3:");
    if (this.s3 == null) {
      sb.append("null");
    } else {
      sb.append(this.s3);
    }
    first = false;
    if (is_set_s4()) {
      if (!first) sb.append(", ");
      sb.append("s4:");
      if (this.s4 == null) {
        sb.append("null");
      } else {
        sb.append(this.s4);
      }
      first = false;
    }
    if (is_set_s5()) {
      if (!first) sb.append(", ");
      sb.append("s5:");
      if (this.s5 == null) {
        sb.append("null");
      } else {
        sb.append(this.s5);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // alas, we cannot check 'i1' because it's a primitive and you chose the non-beans generator.
    if (s == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 's' was not present! Struct: " + toString());
    }
    if (s2 == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 's2' was not present! Struct: " + toString());
    }
    if (s3 == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 's3' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
    if (strct != null) {
      strct.validate();
    }
    if (s != null) {
      s.validate();
    }
    if (s2 != null) {
      s2.validate();
    }
    if (s3 != null) {
      s3.validate();
    }
    if (s4 != null) {
      s4.validate();
    }
    if (s5 != null) {
      s5.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class ComplexStructStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public ComplexStructStandardScheme getScheme() {
      return new ComplexStructStandardScheme();
    }
  }

  private static class ComplexStructStandardScheme extends org.apache.thrift.scheme.StandardScheme<ComplexStruct> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ComplexStruct struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // STRCT
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.strct = new NestedStruct();
              struct.strct.read(iprot);
              struct.set_strct_isSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // U
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.u = new ComplexUnion();
              struct.u.read(iprot);
              struct.set_u_isSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // I1
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.i1 = iprot.readI32();
              struct.set_i1_isSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // S
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.s = new PrimitiveStruct();
              struct.s.read(iprot);
              struct.set_s_isSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // S2
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.s2 = new PrimitiveStruct();
              struct.s2.read(iprot);
              struct.set_s2_isSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // S3
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.s3 = new PrimitiveStruct();
              struct.s3.read(iprot);
              struct.set_s3_isSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // S4
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.s4 = new NestedStruct();
              struct.s4.read(iprot);
              struct.set_s4_isSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 8: // S5
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.s5 = new PrimitiveStruct();
              struct.s5.read(iprot);
              struct.set_s5_isSet(true);
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
      if (!struct.is_set_i1()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'i1' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, ComplexStruct struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.strct != null) {
        if (struct.is_set_strct()) {
          oprot.writeFieldBegin(STRCT_FIELD_DESC);
          struct.strct.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      if (struct.u != null) {
        if (struct.is_set_u()) {
          oprot.writeFieldBegin(U_FIELD_DESC);
          struct.u.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldBegin(I1_FIELD_DESC);
      oprot.writeI32(struct.i1);
      oprot.writeFieldEnd();
      if (struct.s != null) {
        oprot.writeFieldBegin(S_FIELD_DESC);
        struct.s.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.s2 != null) {
        oprot.writeFieldBegin(S2_FIELD_DESC);
        struct.s2.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.s3 != null) {
        oprot.writeFieldBegin(S3_FIELD_DESC);
        struct.s3.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.s4 != null) {
        if (struct.is_set_s4()) {
          oprot.writeFieldBegin(S4_FIELD_DESC);
          struct.s4.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      if (struct.s5 != null) {
        if (struct.is_set_s5()) {
          oprot.writeFieldBegin(S5_FIELD_DESC);
          struct.s5.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ComplexStructTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public ComplexStructTupleScheme getScheme() {
      return new ComplexStructTupleScheme();
    }
  }

  private static class ComplexStructTupleScheme extends org.apache.thrift.scheme.TupleScheme<ComplexStruct> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ComplexStruct struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      oprot.writeI32(struct.i1);
      struct.s.write(oprot);
      struct.s2.write(oprot);
      struct.s3.write(oprot);
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.is_set_strct()) {
        optionals.set(0);
      }
      if (struct.is_set_u()) {
        optionals.set(1);
      }
      if (struct.is_set_s4()) {
        optionals.set(2);
      }
      if (struct.is_set_s5()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.is_set_strct()) {
        struct.strct.write(oprot);
      }
      if (struct.is_set_u()) {
        struct.u.write(oprot);
      }
      if (struct.is_set_s4()) {
        struct.s4.write(oprot);
      }
      if (struct.is_set_s5()) {
        struct.s5.write(oprot);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ComplexStruct struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      struct.i1 = iprot.readI32();
      struct.set_i1_isSet(true);
      struct.s = new PrimitiveStruct();
      struct.s.read(iprot);
      struct.set_s_isSet(true);
      struct.s2 = new PrimitiveStruct();
      struct.s2.read(iprot);
      struct.set_s2_isSet(true);
      struct.s3 = new PrimitiveStruct();
      struct.s3.read(iprot);
      struct.set_s3_isSet(true);
      java.util.BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.strct = new NestedStruct();
        struct.strct.read(iprot);
        struct.set_strct_isSet(true);
      }
      if (incoming.get(1)) {
        struct.u = new ComplexUnion();
        struct.u.read(iprot);
        struct.set_u_isSet(true);
      }
      if (incoming.get(2)) {
        struct.s4 = new NestedStruct();
        struct.s4.read(iprot);
        struct.set_s4_isSet(true);
      }
      if (incoming.get(3)) {
        struct.s5 = new PrimitiveStruct();
        struct.s5.read(iprot);
        struct.set_s5_isSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

