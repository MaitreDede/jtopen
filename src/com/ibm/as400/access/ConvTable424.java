///////////////////////////////////////////////////////////////////////////////
//                                                                             
// JTOpen (AS/400 Toolbox for Java - OSS version)                              
//                                                                             
// Filename: ConvTable424.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

class ConvTable424 extends ConvTableBidiMap
{
  private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";

  private static final String toUnicode_ = 
    "\u0000\u0001\u0002\u0003\u009C\t\u0086\u007F\u0097\u008D\u008E\u000B\f\r\u000E\u000F" +
    "\u0010\u0011\u0012\u0013\u009D\u0085\b\u0087\u0018\u0019\u0092\u008F\u001C\u001D\u001E\u001F" +
    "\u0080\u0081\u0082\u0083\u0084\n\u0017\u001B\u0088\u0089\u008A\u008B\u008C\u0005\u0006\u0007" +
    "\u0090\u0091\u0016\u0093\u0094\u0095\u0096\u0004\u0098\u0099\u009A\u009B\u0014\u0015\u009E\u001A" +
    "\u0020\u05D0\u05D1\u05D2\u05D3\u05D4\u05D5\u05D6\u05D7\u05D8\u00A2\u002E\u003C\u0028\u002B\u007C" +
    "\u0026\u05D9\u05DA\u05DB\u05DC\u05DD\u05DE\u05DF\u05E0\u05E1\u0021\u0024\u002A\u0029\u003B\u00AC" +
    "\u002D\u002F\u05E2\u05E3\u05E4\u05E5\u05E6\u05E7\u05E8\u05E9\u00A6\u002C\u0025\u005F\u003E\u003F" +
    "\u001A\u05EA\u001A\u001A\u00A0\u001A\u001A\u001A\u2017\u0060\u003A\u0023\u0040\'\u003D\"" +
    "\u001A\u0061\u0062\u0063\u0064\u0065\u0066\u0067\u0068\u0069\u00AB\u00BB\u001A\u001A\u001A\u00B1" +
    "\u00B0\u006A\u006B\u006C\u006D\u006E\u006F\u0070\u0071\u0072\u001A\u001A\u20AC\u00B8\u20AA\u00A4" +
    "\u00B5\u007E\u0073\u0074\u0075\u0076\u0077\u0078\u0079\u007A\u001A\u001A\u001A\u001A\u001A\u00AE" +
    "\u005E\u00A3\u00A5\u2022\u00A9\u00A7\u00B6\u00BC\u00BD\u00BE\u005B\u005D\u203E\u00A8\u00B4\u00D7" +
    "\u007B\u0041\u0042\u0043\u0044\u0045\u0046\u0047\u0048\u0049\u00AD\u001A\u001A\u001A\u001A\u001A" +
    "\u007D\u004A\u004B\u004C\u004D\u004E\u004F\u0050\u0051\u0052\u00B9\u202D\u202E\u202C\u001A\u001A" +
    "\\\u00F7\u0053\u0054\u0055\u0056\u0057\u0058\u0059\u005A\u00B2\u001A\u001A\u001A\u001A\u001A" +
    "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037\u0038\u0039\u00B3\u202A\u202B\u200E\u200F\u009F";


  private static final String fromUnicode_ = 
    "\u0001\u0203\u372D\u2E2F\u1605\u250B\u0C0D\u0E0F\u1011\u1213\u3C3D\u3226\u1819\u3F27\u1C1D\u1E1F" +
    "\u405A\u7F7B\u5B6C\u507D\u4D5D\u5C4E\u6B60\u4B61\uF0F1\uF2F3\uF4F5\uF6F7\uF8F9\u7A5E\u4C7E\u6E6F" +
    "\u7CC1\uC2C3\uC4C5\uC6C7\uC8C9\uD1D2\uD3D4\uD5D6\uD7D8\uD9E2\uE3E4\uE5E6\uE7E8\uE9BA\uE0BB\uB06D" +
    "\u7981\u8283\u8485\u8687\u8889\u9192\u9394\u9596\u9798\u99A2\uA3A4\uA5A6\uA7A8\uA9C0\u4FD0\uA107" +
    "\u2021\u2223\u2415\u0617\u2829\u2A2B\u2C09\u0A1B\u3031\u1A33\u3435\u3608\u3839\u3A3B\u0414\u3EFF" +
    "\u743F\u4AB1\u9FB2\u6AB5\uBDB4\u3F8A\u5FCA\uAF3F\u908F\uEAFA\uBEA0\uB63F\u9DDA\u3F8B\uB7B8\uB93F" +
    "\uFFFF\u000B\u3F3F\u3FBF\uFFFF\u000F\u3F3F\u3FE1\uFFFF\u0397\u3F3F\u6C3F\uFFFF\u003F\u3F3F\u7169" +
    "\u6867\u6665\u6463\u6259\u5857\u5655\u5453\u5251\u4948\u4746\u4544\u4342\u413F\uFFFF\u0BA6\u3F3F" +
    "\uFD3F\u3F3F\u3F3F\uB33F\uFFFF\u0004\u3F3F\u3F78\u3F3F\u3F3F\u3F3F\u3FFE\u3F3F\uFBFC\uDDDB\uDC3F" +
    "\uFFFF\u0007\u3F3F\uBC3F\uFFFF\u0035\u3F3F\u9E3F\u9C3F\uFFFF\u6D38\u3F3F\uF0F1\uF2F3\uF4F5\uF6F7" +
    "\uF8F9\u614B\u606B\u4E5C\u5D4D\u7D50\u6C5B\u7B7F\u5A3F\uFFFF\u01F1\u3F3F\u7A5E\u4C7E\u6E6F\u7CC1" +
    "\uC2C3\uC4C5\uC6C7\uC8C9\uD1D2\uD3D4\uD5D6\uD7D8\uD9E2\uE3E4\uE5E6\uE7E8\uE9BA\uE0BB\uB06D\u7981" +
    "\u8283\u8485\u8687\u8889\u9192\u9394\u9596\u9798\u99A2\uA3A4\uA5A6\uA7A8\uA9C0\u4FD0\uA13F\uFFFF" +
    "\u0050\u3F3F";


  ConvTable424()
  {
    super(424, toUnicode_.toCharArray(), fromUnicode_.toCharArray());
  }
}
