///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (AS/400 Toolbox for Java - OSS version)                              
//                                                                             
// Filename: DBSQLRPBDS.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;



/**
   Create SQL Request Parameter Block (RPB)
   data stream.
**/
class DBSQLRPBDS
extends DBBaseRequestDS
{
  private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";

  public static final int	FUNCTIONID_CHANGE_RPB             = 0x1D03;
  public static final int	FUNCTIONID_CREATE_RPB             = 0x1D00;
  public static final int	FUNCTIONID_CREATE_RPB_BASED_ON    = 0x1D01;
  public static final int	FUNCTIONID_DELETE_RPB             = 0x1D02;
  public static final int	FUNCTIONID_RESET_RPB              = 0x1D04;

/**
   Constructs a datastream for the SQL Server RPB functions.
   @param  requestId the 4 digit code that represents the function being called.
   @param  rpbId   the request parameter block id.
   @param  operationResultsBitmap the bitmap which describes how the results are to be returned.
   @param  parameterMarkerDescriptorHandle the Parameter marker descriptor handle identifier.
**/

  public DBSQLRPBDS(int requestId,
		     int rpbId,
		     int operationResultsBitmap,
		     int parameterMarkerDescriptorHandle)

  {
    // Create the datastream header and template
    super(requestId, rpbId, operationResultsBitmap,
		  parameterMarkerDescriptorHandle);
	setServerID(SERVER_SQL);
  }



// Returns the copyright.
  private static String getCopyright()
  {
    return Copyright.copyright;
  }



       //--------------------------------------------------//
       // Create the data stream optional /         	   //
       // variable length data section via addParameters   //
       //--------------------------------------------------//

/**
   Sets the Based on RPB Handle parameter in the data stream.
   @param value	the name of the handle of the RPB to copy from.
   @exception DBDataStreamException If there is not enough space left in the data byte array.
**/
   	void setBasedOnRPBHandle (int value)
		throws DBDataStreamException
	{
		addParameter (0x3800, (short)value);
	}



/**
   Sets the Blocking Factor parameter in the data stream.
   @param value	the blocking factor to be used on the fetch.
   @exception DBDataStreamException If there is not enough space left in the data byte array.
**/
   	void setBlockingFactor(int value)
		throws DBDataStreamException
	{
		addParameter (0x380C, value);
	}



/**
   Sets the Cursor Name parameter to the data stream.
   @param value	the name for the open cursor.
   @param converter the converter.
   @exception DBDataStreamException If there is not enough space left in the data byte array.
**/
	void setCursorName(String value, ConverterImplRemote converter)
		throws DBDataStreamException
	{
		addParameter (0x380B, converter, value);
	}



/**
   Sets the Describe Option parameter in the data stream.
   @param value	the describe option to be used.
   @exception DBDataStreamException If there is not enough space left in the data byte array.
**/
	void setDescribeOption(int value)
		throws DBDataStreamException
	{
		addParameter (0x380A, (byte)value);
	}



/**
   Sets the Fetch Scroll Option parameter in the data stream.
   @param value	the scroll options to use with the cursor.
   @exception DBDataStreamException If there is not enough space left in the data byte array.
**/
	void setFetchScrollOption(int value)
		throws DBDataStreamException
	{
		addParameter (0x380E, (short) value);
	}



/**
   Sets the Hold Indicator parameter to the data stream.
   @param value	the commit operation that is to be performed.
   @exception DBDataStreamException If there is not enough space left in the data byte array.
**/
    void setHoldIndicator(int value)
		throws DBDataStreamException
	{
		addParameter (0x380F, (byte)value);
	}



/**
   Sets the Library Name parameter in the data stream.
   @param value	the name of the library.
   @param converter the converter.
   @exception DBDataStreamException If there is not enough space left in the data byte array.
**/
  	void setLibraryName(String value, ConverterImplRemote converter)
		throws DBDataStreamException
	{
		addParameter (0x3801, converter, value);
	}



/**
   Sets the Open Attributes parameter in the data stream.
   @param value	the open attributes of the referenced file.
   @exception DBDataStreamException If there is not enough space left in the data byte array.
**/
    void setOpenAttributes(int value)
		throws DBDataStreamException
	{
		addParameter (0x3809, (byte)value);
	}



/**
   Sets the SQL Package Name parameter in the data stream.
   @param value	the SQL Package to use.
   @param converter the converter.
   @exception DBDataStreamException If there is not enough space left in the data byte array.
**/
	void setPackageName(String value, ConverterImplRemote converter)
		throws DBDataStreamException
	{
		addParameter (0x3804, converter, value);
	}



/**
   Sets the Package Threshold Value parameter in the data stream.
   @param value	the value used to determine if the package
   should be cleared.
   @exception DBDataStreamException If there is not enough space left in the data byte array.
**/
	void setPackageThresholdValue(int value)
		throws DBDataStreamException
	{
		addParameter (0x3813, (short) value);
	}



/**
   Sets the Parameter Marker Block Indicator parameter in the data stream.
   @param value	the value that indicates whether the parameter
   marker data is intended for a block operation or a non-block
   operation.
   @exception DBDataStreamException If there is not enough space left in the data byte array.
**/
	void setParameterMarkerBlockIndicator(int value)
		throws DBDataStreamException
	{
		addParameter (0x3814, (short) value);
	}



/**
   Sets the Prepare Option parameter in the data stream.
   @param value	the prepare option.
   @exception DBDataStreamException If there is not enough space left in the data byte array.
**/
    void setPrepareOption(int value)
		throws DBDataStreamException
	{
		addParameter (0x3808, (byte)value);
	}



/**
   Sets the Prepared Statement Name parameter in the data stream.
   @param value	the name of the prepared statement.
   @param converter the converter.
   @exception DBDataStreamException If there is not enough space left in the data byte array.
**/
    void setPrepareStatementName(String value, ConverterImplRemote converter)
		throws DBDataStreamException
	{
		addParameter (0x3806, converter, value);
	}



/**
   Sets the Query Timeout parameter in the data stream.
   @param value	the query timeout limit in seconds (-1 means
   no limit).
   @exception DBDataStreamException If there is not enough space left in the data byte array.
**/
//
// Implementation note: This only works for servers V4R1 and later.
//
	void setQueryTimeout(int value)
		throws DBDataStreamException
	{
		addParameter (0x3817, value);
	}


/**
   Sets the Reuse Indicator parameter in the data stream.
   @param value	the value that indicates if the client
   intends to open the cursor again for the same statement or not.
   @exception DBDataStreamException If there is not enough space left in the data byte array.
**/
	void setReuseIndicator(int value)
		throws DBDataStreamException
	{
		addParameter (0x3810, (byte)value);
	}



/**
   Sets the Scrollable Cursor Flag parameter in the data stream.
   @param value	the value to indicate whether or not cursor
   scrolling is supported.
   @exception DBDataStreamException If there is not enough space left in the data byte array.
**/
	void setScrollableCursorFlag(int value)
		throws DBDataStreamException
	{
		addParameter (0x380D, (short) value);
	}



/**
   Sets the Statement Text parameter in the data stream.
   @param value	the text for the SQL statement.
   @param converter the converter.
   @exception DBDataStreamException If there is not enough space left in the data byte array.
**/
   	void setStatementText(String value, ConverterImplRemote converter)
		throws DBDataStreamException
	{
		addParameter (0x3807, converter, value);
	}



/**
   Sets the Statement Type parameter in the data stream.
   @param value	the statement type.
   @exception DBDataStreamException If there is not enough space left in the data byte array.
**/
	void setStatementType(int value)
		throws DBDataStreamException
	{
		addParameter (0x3812, (short) value);
	}



/**
   Sets the Translate Indicator parameter in the data stream.
   @param value	the value that indicates if the data in the
   operational results set generated by this function should be
   translated to the client's CCSID before the data is returned.
   @exception DBDataStreamException If there is not enough space left in the data byte array.
**/
	void setTranslateIndicator(int value)
		throws DBDataStreamException
	{
		addParameter (0x3805, (byte)value);
	}


}  // End of DBSQLRPBDS class





