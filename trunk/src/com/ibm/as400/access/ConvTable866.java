///////////////////////////////////////////////////////////////////////////////
//                                                                             
// AS/400 Toolbox for Java - OSS version                                       
//                                                                             
// Filename: ConvTable866.java
//                                                                             
// The source code contained herein is licensed under the IBM Public License   
// Version 1.0, which has been approved by the Open Source Initiative.         
// Copyright (C) 1997-2000 International Business Machines Corporation and     
// others. All rights reserved.                                                
//                                                                             
///////////////////////////////////////////////////////////////////////////////

package com.ibm.as400.access;

class ConvTable866 extends ConvTableAsciiMap
{
  private static final String copyright = "Copyright (C) 1997-2000 International Business Machines Corporation and others.";

  private static final String toUnicode_ = 
    "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000B\f\r\u000E\u000F" +
    "\u0010\u0011\u0012\u0013\u00B6\u00A7\u0016\u0017\u0018\u0019\u001C\u001B\u007F\u001D\u001E\u001F" +
    "\u0020\u0021\"\u0023\u0024\u0025\u0026\'\u0028\u0029\u002A\u002B\u002C\u002D\u002E\u002F" +
    "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037\u0038\u0039\u003A\u003B\u003C\u003D\u003E\u003F" +
    "\u0040\u0041\u0042\u0043\u0044\u0045\u0046\u0047\u0048\u0049\u004A\u004B\u004C\u004D\u004E\u004F" +
    "\u0050\u0051\u0052\u0053\u0054\u0055\u0056\u0057\u0058\u0059\u005A\u005B\\\u005D\u005E\u005F" +
    "\u0060\u0061\u0062\u0063\u0064\u0065\u0066\u0067\u0068\u0069\u006A\u006B\u006C\u006D\u006E\u006F" +
    "\u0070\u0071\u0072\u0073\u0074\u0075\u0076\u0077\u0078\u0079\u007A\u007B\u007C\u007D\u007E\u001A" +
    "\u0410\u0411\u0412\u0413\u0414\u0415\u0416\u0417\u0418\u0419\u041A\u041B\u041C\u041D\u041E\u041F" +
    "\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042A\u042B\u042C\u042D\u042E\u042F" +
    "\u0430\u0431\u0432\u0433\u0434\u0435\u0436\u0437\u0438\u0439\u043A\u043B\u043C\u043D\u043E\u043F" +
    "\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255D\u255C\u255B\u2510" +
    "\u2514\u2534\u252C\u251C\u2500\u253C\u255E\u255F\u255A\u2554\u2569\u2566\u2560\u2550\u256C\u2567" +
    "\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256B\u256A\u2518\u250C\u2588\u2584\u258C\u2590\u2580" +
    "\u0440\u0441\u0442\u0443\u0444\u0445\u0446\u0447\u0448\u0449\u044A\u044B\u044C\u044D\u044E\u044F" +
    "\u0401\u0451\u0404\u0454\u0407\u0457\u040E\u045E\u00B0\u2219\u00B7\u221A\u2116\u00A4\u25A0\u00A0";


  private static final String fromUnicode_ = 
    "\u0001\u0203\u0405\u0607\u0809\u0A0B\u0C0D\u0E0F\u1011\u1213\u7F7F\u1617\u1819\u7F1B\u1A1D\u1E1F" +
    "\u2021\u2223\u2425\u2627\u2829\u2A2B\u2C2D\u2E2F\u3031\u3233\u3435\u3637\u3839\u3A3B\u3C3D\u3E3F" +
    "\u4041\u4243\u4445\u4647\u4849\u4A4B\u4C4D\u4E4F\u5051\u5253\u5455\u5657\u5859\u5A5B\u5C5D\u5E5F" +
    "\u6061\u6263\u6465\u6667\u6869\u6A6B\u6C6D\u6E6F\u7071\u7273\u7475\u7677\u7879\u7A7B\u7C7D\u7E1C" +
    "\uFFFF\u0010\u7F7F\uFF7F\u7F7F\uFD7F\u7F15\uFFFF\u0004\u7F7F\uF87F\u7F7F\u7F7F\u14FA\uFFFF\u01A4" +
    "\u7F7F\u7FF0\u7F7F\uF27F\u7FF4\u7F7F\u7F7F\u7F7F\uF67F\u8081\u8283\u8485\u8687\u8889\u8A8B\u8C8D" +
    "\u8E8F\u9091\u9293\u9495\u9697\u9899\u9A9B\u9C9D\u9E9F\uA0A1\uA2A3\uA4A5\uA6A7\uA8A9\uAAAB\uACAD" +
    "\uAEAF\uE0E1\uE2E3\uE4E5\uE6E7\uE8E9\uEAEB\uECED\uEEEF\u7FF1\u7F7F\uF37F\u7FF5\u7F7F\u7F7F\u7F7F" +
    "\uF77F\uFFFF\u0E5B\u7F7F\uFC7F\uFFFF\u0080\u7F7F\u7FF9\uFB7F\uFFFF\u0172\u7F7F\uC47F\uB37F\uFFFF" +
    "\u0004\u7F7F\uDA7F\u7F7F\uBF7F\u7F7F\uC07F\u7F7F\uD97F\u7F7F\uC37F\u7F7F\u7F7F\u7F7F\uB47F\u7F7F" +
    "\u7F7F\u7F7F\uC27F\u7F7F\u7F7F\u7F7F\uC17F\u7F7F\u7F7F\u7F7F\uC57F\uFFFF\t\u7F7F\uCDBA\uD5D6" +
    "\uC9B8\uB7BB\uD4D3\uC8BE\uBDBC\uC6C7\uCCB5\uB6B9\uD1D2\uCBCF\uD0CA\uD8D7\uCE7F\uFFFF\t\u7F7F" +
    "\uDF7F\u7F7F\uDC7F\u7F7F\uDB7F\u7F7F\uDD7F\u7F7F\uDEB0\uB1B2\uFFFF\u0006\u7F7F\uFE7F\uFFFF\u6D2F" +
    "\u7F7F";


  ConvTable866()
  {
    super(866, toUnicode_.toCharArray(), fromUnicode_.toCharArray());
  }
}
