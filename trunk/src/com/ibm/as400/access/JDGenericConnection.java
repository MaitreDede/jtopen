///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (IBM Toolbox for Java - OSS version)                                 
//                                                                             
// Filename: JDGenericConnection.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2001 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Savepoint; //@A1A
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;


class JDGenericConnection
implements Connection
{
  private static final String copyright = "Copyright (C) 1997-2001 International Business Machines Corporation and others.";


  // Private data.
  private Connection actualConnection_;



  public void clearWarnings ()
    throws SQLException
  {
    actualConnection_.clearWarnings ();
  }


  public void close ()
    throws SQLException
  {
    actualConnection_.close ();
  }


  public void commit ()
    throws SQLException
  {
    actualConnection_.commit ();
  }



  public Statement createStatement ()
    throws SQLException
  {
    return actualConnection_.createStatement ();
  }



  // JDBC 2.0
  public Statement createStatement (int resultSetType,
                                    int resultSetConcurrency)
    throws SQLException
  {
    return actualConnection_.createStatement (resultSetType, resultSetConcurrency);
  }


    //@A1A
    public Statement createStatement (int resultSetType,
                                      int resultSetConcurrency,
                                      int resultSetHoldability)
    throws SQLException
    {
        return actualConnection_.createStatement (resultSetType, resultSetConcurrency, resultSetHoldability);
    }



  protected void finalize ()
    throws Throwable
  {
    if (actualConnection_ != null &&
        ! actualConnection_.isClosed ())
      actualConnection_.close ();

    super.finalize ();
  }



  public boolean getAutoCommit ()
    throws SQLException
  {
    return actualConnection_.getAutoCommit ();
  }



  public String getCatalog ()
    throws SQLException
  {
    return actualConnection_.getCatalog ();
  }



    //@A1A
    public int getHoldability ()
    throws SQLException
    {
        return actualConnection_.getHoldability ();
    }



  public DatabaseMetaData getMetaData ()
    throws SQLException
  {
    return actualConnection_.getMetaData ();
  }



  public int getTransactionIsolation ()
    throws SQLException
  {
    return actualConnection_.getTransactionIsolation ();
  }



  // JDBC 2.0
  public Map getTypeMap ()
    throws SQLException
  {
    return actualConnection_.getTypeMap ();
  }



  public SQLWarning getWarnings ()
    throws SQLException
  {
    return actualConnection_.getWarnings ();
  }



  public boolean isClosed ()
    throws SQLException
  {
    return actualConnection_.isClosed ();
  }



  public boolean isReadOnly ()
    throws SQLException
  {
    return actualConnection_.isReadOnly ();
  }



  public String nativeSQL (String sql)
    throws SQLException
  {
    return actualConnection_.nativeSQL (sql);
  }



  public CallableStatement prepareCall (String sql)
    throws SQLException
  {
    return actualConnection_.prepareCall (sql);
  }



  // JDBC 2.0
  public CallableStatement prepareCall (String sql,
                                        int resultSetType,
                                        int resultSetConcurrency)
    throws SQLException
  {
    return actualConnection_.prepareCall (sql, resultSetType, resultSetConcurrency);
  }



    //@A1A
    public CallableStatement prepareCall (String sql,
                                          int resultSetType,
                                          int resultSetConcurrency,
                                          int resultSetHoldability)
    throws SQLException
    {
        return actualConnection_.prepareCall (sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }



  public PreparedStatement prepareStatement (String sql)
    throws SQLException
  {
    return actualConnection_.prepareStatement (sql);
  }


    //@A1A
    public PreparedStatement prepareStatement (String sql, int autoGeneratedKeys)
    throws SQLException
    {
        return actualConnection_.prepareStatement (sql, autoGeneratedKeys);
    }


    //@A1A
    public PreparedStatement prepareStatement (String sql, int[] columnIndexes)
    throws SQLException
    {
        return actualConnection_.prepareStatement(sql, columnIndexes);
    }


  // JDBC 2.0
  public PreparedStatement prepareStatement (String sql,
                                             int resultSetType,
                                             int resultSetConcurrency)
    throws SQLException
  {
    return actualConnection_.prepareStatement (sql, resultSetType, resultSetConcurrency);
  }


    //@A1A
    public PreparedStatement prepareStatement (String sql,
                                               int resultSetType,
                                               int resultSetConcurrency, 
                                               int resultSetHoldability)
    throws SQLException
    {
        return actualConnection_.prepareStatement (sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }



    //@A1A
    public PreparedStatement prepareStatement (String sql, String[] columnNames)
    throws SQLException
    {
        return actualConnection_.prepareStatement (sql, columnNames);
    }



    //@A1A
    public void releaseSavepoint(Savepoint savepoint)
    throws SQLException
    {   
        actualConnection_.releaseSavepoint(savepoint);
    }



  public void rollback ()
    throws SQLException
  {
    actualConnection_.rollback ();
  }



    //@A1A
    public void rollback(Savepoint savepoint)
    throws SQLException
    {
        actualConnection_.rollback(savepoint);
    }



  public void setAutoCommit (boolean autoCommit)
    throws SQLException
  {
    actualConnection_.setAutoCommit (autoCommit);
  }



  public void setCatalog (String catalog)
    throws SQLException
  {
    actualConnection_.setCatalog (catalog);
  }


    //@A1A
    public void setHoldability (int holdability)
    throws SQLException
    {
        actualConnection_.setHoldability (holdability);
    }



  void setProperties (String url, Properties properties)
    throws SQLException
  {
    // Set actualConnection_ to point to an appropriate Connection object
    // for the specified database.
    actualConnection_ = java.sql.DriverManager.getConnection (url,
                                                              properties);
  }



  public void setReadOnly (boolean readOnly)
    throws SQLException
  {
    actualConnection_.setReadOnly (readOnly);
  }



    //@A1A
    public Savepoint setSavepoint()
    throws SQLException
    {      
        return actualConnection_.setSavepoint();
    }



    //@A1A
    public Savepoint setSavepoint(String name)
    throws SQLException
    { 
        return actualConnection_.setSavepoint(name);
    }



  public void setTransactionIsolation (int level)
    throws SQLException
  {
    actualConnection_.setTransactionIsolation (level);
  }



  // JDBC 2.0
  public void setTypeMap (Map typeMap)
    throws SQLException
  {
    actualConnection_.setTypeMap (typeMap);
  }



  // Note - This method is not required by java.sql.Connection,
  //        but it is used by the JDBC testcases.
  public String toString ()
  {
    return actualConnection_.toString ();
  }

}
