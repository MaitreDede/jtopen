///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                                 
//                                                                             
// Filename: SQLDecimal.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2003 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.io.InputStream;
import java.io.Reader;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

final class SQLDecimal
implements SQLData
{
    private static final String copyright = "Copyright (C) 1997-2003 International Business Machines Corporation and others.";

    // Private.
    private static final BigDecimal default_ = BigDecimal.valueOf(0); // @C2A
    private static final BigDecimal BYTE_MAX_VALUE = BigDecimal.valueOf(Byte.MAX_VALUE);
    private static final BigDecimal BYTE_MIN_VALUE = BigDecimal.valueOf(Byte.MIN_VALUE);
    private static final BigDecimal SHORT_MAX_VALUE = BigDecimal.valueOf(Short.MAX_VALUE);
    private static final BigDecimal SHORT_MIN_VALUE = BigDecimal.valueOf(Short.MIN_VALUE);
    private static final BigDecimal INTEGER_MAX_VALUE = BigDecimal.valueOf(Integer.MAX_VALUE);
    private static final BigDecimal INTEGER_MIN_VALUE = BigDecimal.valueOf(Integer.MIN_VALUE);
    private static final BigDecimal LONG_MAX_VALUE = BigDecimal.valueOf(Long.MAX_VALUE);
    private static final BigDecimal LONG_MIN_VALUE = BigDecimal.valueOf(Long.MIN_VALUE);
    private static final BigDecimal FLOAT_MAX_VALUE = new BigDecimal(Float.MAX_VALUE);
    private static final BigDecimal FLOAT_MIN_VALUE = new BigDecimal(Float.MIN_VALUE);
    private static final BigDecimal DOUBLE_MAX_VALUE = new BigDecimal(Double.MAX_VALUE);
    private static final BigDecimal DOUBLE_MIN_VALUE = new BigDecimal(Double.MIN_VALUE);

    private SQLConversionSettings   settings_;
    private int                     precision_;
    private int                     scale_;
    private int                     truncated_;
    private AS400PackedDecimal      typeConverter_;
    private BigDecimal              value_;
    private JDProperties            properties_;   // @M0A - added JDProperties so we can get the scale & precision
    private int                     vrm_;          // @M0A

    SQLDecimal(int precision,
               int scale,
               SQLConversionSettings settings,
               int vrm,                  // @M0C
               JDProperties properties)  // @M0C
    {
        settings_       = settings;
        precision_      = precision;
        scale_          = scale;
        truncated_      = 0;
        typeConverter_  = new AS400PackedDecimal(precision_, scale_);
        value_          = default_; // @C2C
        vrm_            = vrm;         // @M0A
        properties_     = properties;  // @M0A
    }

    public Object clone()
    {
        return new SQLDecimal(precision_, scale_, settings_, vrm_, properties_);  // @M0C
    }

    //---------------------------------------------------------//
    //                                                         //
    // CONVERSION TO AND FROM RAW BYTES                        //
    //                                                         //
    //---------------------------------------------------------//

    public void convertFromRawBytes(byte[] rawBytes, int offset, ConvTable ccisdConverter) //@P0C
    throws SQLException
    {
        value_ = ((BigDecimal)typeConverter_.toObject(rawBytes, offset));
    }

    public void convertToRawBytes(byte[] rawBytes, int offset, ConvTable ccsidConverter) //@P0C
    throws SQLException
    {
        try{
            typeConverter_.toBytes(value_, rawBytes, offset);
        }
        catch(ExtendedIllegalArgumentException e){
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH, e);
        }
    }

    //---------------------------------------------------------//
    //                                                         //
    // SET METHODS                                             //
    //                                                         //
    //---------------------------------------------------------//

    public void set(Object object, Calendar calendar, int scale)
    throws SQLException
    {
        BigDecimal bigDecimal = null;

        if(object instanceof String)
        {
            try
            {
                String value = SQLDataFactory.convertScientificNotation((String)object); // @F3C
                if(scale >= 0)
                    value = SQLDataFactory.truncateScale(value, scale);
                bigDecimal = new BigDecimal(value);
            }
            catch(NumberFormatException e)
            {
                JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH, e);
            }
            catch(StringIndexOutOfBoundsException e)        // jdk 1.3.x throws this instead of a NFE
            {
                JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
            }
        }

        else if(object instanceof Number)
        {
            String value = SQLDataFactory.convertScientificNotation(object.toString()); // @C1C
            if(scale >= 0)
                value = SQLDataFactory.truncateScale(value, scale);
            bigDecimal = new BigDecimal(value);
        }

        else if(object instanceof Boolean)
            bigDecimal = (((Boolean)object).booleanValue() == true) ? BigDecimal.valueOf(1) : BigDecimal.valueOf(0);

        else
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);

        // Truncate if necessary.  If we ONLY truncate on the right side, we don't    @E2C
        // need to report it.  If we truncate on the left side, then we report the    @E2A
        // number of truncated digits on both ends...this will make the dataSize      @E2A
        // and transferSize make sense on the resulting DataTruncation.               @E2A
        // Changed to throw exception if left side is truncated                       @PDC
        truncated_ = 0;
        int otherScale = bigDecimal.scale();
        if(otherScale > scale_)
            truncated_ += otherScale - scale_;
        value_ = bigDecimal.setScale(scale_, BigDecimal.ROUND_DOWN);       // @E2C

        /* @PDC throw exception instead of warning if truncate left of decimal
        int otherPrecision = SQLDataFactory.getPrecision(value_);
        if(otherPrecision > precision_)
        {
            int digits = otherPrecision - precision_;
            truncated_ += digits;
            value_ = SQLDataFactory.truncatePrecision(value_, digits);
        }*/
        // @PDC - Due to issue 29258, we are changing this back to throw exception.
        //        Even though code has been issuing truncation warnings for several years,
        //        the final decision was that we need to match native and odbc drivers behavior.
        
        // Also fix: if col is defined as DECIMAL(3,2) bigDecimal=xx.x0x, 
        //    then precision should be 5, not 3 (due to round_down), 
        //    and need to throw exception now instead of later in sql execute.
        //    (bigDecimal.setScale(scale_, BigDecimal.ROUND_DOWN) above cuts off last x and so number ends in 0.
        //    Then SQLDataFactory.getPrecision() gets rid of trailing 0s before calculating precision.)
        int otherPrecision = SQLDataFactory.getPrecision(bigDecimal);     //@PDC use before rounding down
        // This will catch a number left of decimalpt that is greater than the legal value during set()
        if((otherPrecision - otherScale) > (precision_ - scale_))
        { 
            JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);   
        }                                                                   
        else                                                               // @E2A
            truncated_ = 0;  // No left side truncation, report nothing       @E2A
                             // (even if there was right side truncation).    @E2A
    }

    //---------------------------------------------------------//
    //                                                         //
    // DESCRIPTION OF SQL TYPE                                 //
    //                                                         //
    //---------------------------------------------------------//

    public int getSQLType()
    {
        return SQLData.DECIMAL;
    }

    public String getCreateParameters()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(AS400JDBCDriver.getResource("PRECISION"));
        buffer.append(",");
        buffer.append(AS400JDBCDriver.getResource("SCALE"));
        return buffer.toString();
    }

    public int getDisplaySize()
    {
        return precision_ + 2;
    }

    //@F1A JDBC 3.0
    public String getJavaClassName()
    {
        return "java.math.BigDecimal";
    }

    public String getLiteralPrefix()
    {
        return null;
    }

    public String getLiteralSuffix()
    {
        return null;
    }

    public String getLocalName()
    {
        return "DECIMAL";
    }

    public int getMaximumPrecision()
    {
        // @M0C - change to check vrm and JDProperties
        if(vrm_ >= JDUtilities.vrm530)
            return properties_.getInt(JDProperties.MAXIMUM_PRECISION);
        else
            return 31;
    }

    public int getMaximumScale()
    {
        // @M0C - change to check vrm and JDProperties
        if(vrm_ >= JDUtilities.vrm530)
            return properties_.getInt(JDProperties.MAXIMUM_SCALE);
        else
            return 31;
    }

    public int getMinimumScale()
    {
        return 0;
    }

    public int getNativeType()
    {
        return 484;
    }

    public int getPrecision()
    {
        return precision_;
    }

    public int getRadix()
    {
        return 10;
    }

    public int getScale()
    {
        return scale_;
    }

    public int getType()
    {
        return java.sql.Types.DECIMAL;
    }

    public String getTypeName()
    {
        return "DECIMAL";
    }

    public boolean isSigned()
    {
        return true;
    }

    public boolean isText()
    {
        return false;
    }

    public int getActualSize()
    {
        return precision_;
    }

    public int getTruncated()
    {
        return truncated_;
    }

    //---------------------------------------------------------//
    //                                                         //
    // CONVERSIONS TO JAVA TYPES                               //
    //                                                         //
    //---------------------------------------------------------//

    public InputStream getAsciiStream()
    throws SQLException
    {
        truncated_ = 0;
        try
        {
            return new ByteArrayInputStream(ConvTable.getTable(819, null).stringToByteArray(getString()));
        }
        catch(UnsupportedEncodingException e)
        {
            JDError.throwSQLException(this, JDError.EXC_INTERNAL, e);
            return null;
        }
    }

    public BigDecimal getBigDecimal(int scale)
    throws SQLException
    {
        truncated_ = 0;
        if(scale >= 0)
        {
            if(scale >= value_.scale())
            {
                return value_.setScale(scale);
            }
            else
            {
                truncated_ = value_.scale() - scale;
                return value_.setScale(scale, BigDecimal.ROUND_HALF_UP);
            }
        }
        else
            return value_;
    }

    public InputStream getBinaryStream()
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }

    public Blob getBlob()
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }

    public boolean getBoolean()
    throws SQLException
    {
        truncated_ = 0;
        return(value_.compareTo(BigDecimal.valueOf(0)) != 0);
    }

    public byte getByte()
    throws SQLException
    {
        truncated_ = 0;
        if(value_.compareTo(BYTE_MAX_VALUE) > 0 || value_.compareTo(BYTE_MIN_VALUE) < 0)
        {
            // we don't count the fractional part of the number as truncation
            int length = value_.toBigInteger().toByteArray().length;
            truncated_ = length - 1;
        }
        return(byte) value_.byteValue();
    }

    public byte[] getBytes()
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }

    public Reader getCharacterStream()
    throws SQLException
    {
        truncated_ = 0;
        return new StringReader(getString());
    }

    public Clob getClob()
    throws SQLException
    {
        truncated_ = 0;
        String string = getString();
        return new AS400JDBCClob(string, string.length());
    }

    public Date getDate(Calendar calendar)
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }

    public double getDouble()
    throws SQLException
    {
        truncated_ = 0;
        double d = value_.doubleValue();    //@KBA
        //@KBD will never occur with current precision of 63
        //@KBD if(value_.compareTo(DOUBLE_MAX_VALUE) > 0 || value_.compareTo(DOUBLE_MIN_VALUE) < 0)
        if(d == Double.POSITIVE_INFINITY || d == Double.NEGATIVE_INFINITY)  //@KBA
        {
            // we don't count the fractional part of the number as truncation
            int length = value_.toBigInteger().toByteArray().length;
            truncated_ = length - 8;
        }
        return d; //@KBC value_.doubleValue();
    }

    public float getFloat()
    throws SQLException
    {
        truncated_ = 0;
        float f = value_.floatValue();  //@KBA
        //@KBD changed in order to avoid optimization problem in JRE 1.3
        //@KBD if(value_.compareTo(FLOAT_MAX_VALUE) > 0 || value_.compareTo(FLOAT_MIN_VALUE) < 0)
        if( f == Float.POSITIVE_INFINITY || f == Float.NEGATIVE_INFINITY)   //@KBA
        {
            // we don't count the fractional part of the number as truncation
            int length = value_.toBigInteger().toByteArray().length;
            truncated_ = length - 4;
        }
        return f;   //@KBC value_.floatValue();
    }

    public int getInt()
    throws SQLException
    {
        truncated_ = 0;
        if(value_.compareTo(INTEGER_MAX_VALUE) > 0 || value_.compareTo(INTEGER_MIN_VALUE) < 0)
        {
            // we don't count the fractional part of the number as truncation
            int length = value_.toBigInteger().toByteArray().length;
            truncated_ = length - 4;
        }
        return value_.intValue();
    }

    public long getLong()
    throws SQLException
    {
        truncated_ = 0;
        if(value_.compareTo(LONG_MAX_VALUE) > 0 || value_.compareTo(LONG_MIN_VALUE) < 0)
        {
            // we don't count the fractional part of the number as truncation
            int length = value_.toBigInteger().toByteArray().length;
            truncated_ = length - 8;
        }
        return value_.longValue();
    }

    public Object getObject()
    throws SQLException
    {
        truncated_ = 0;
        return value_;
    }

    public short getShort()
    throws SQLException
    {
        truncated_ = 0;
        if(value_.compareTo(SHORT_MAX_VALUE) > 0 || value_.compareTo(SHORT_MIN_VALUE) < 0)
        {
            // we don't count the fractional part of the number as truncation
            int length = value_.toBigInteger().toByteArray().length;
            truncated_ = length - 2;
        }
        return(short) value_.shortValue();
    }

    public String getString()
    throws SQLException
    {
        truncated_ = 0;
        String stringRep = value_.toString();
        int decimal = stringRep.indexOf('.');
        if(decimal == -1)
            return stringRep;
        else
            return stringRep.substring(0, decimal)
            + settings_.getDecimalSeparator()
            + stringRep.substring(decimal+1);
    }

    public Time getTime(Calendar calendar)
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }

    public Timestamp getTimestamp(Calendar calendar)
    throws SQLException
    {
        JDError.throwSQLException(this, JDError.EXC_DATA_TYPE_MISMATCH);
        return null;
    }

    public InputStream  getUnicodeStream()
    throws SQLException
    {
        truncated_ = 0;
        try
        {
            return new ByteArrayInputStream(ConvTable.getTable(13488, null).stringToByteArray(getString()));
        }
        catch(UnsupportedEncodingException e)
        {
            JDError.throwSQLException(this, JDError.EXC_INTERNAL, e);
            return null;
        }
    }
}
