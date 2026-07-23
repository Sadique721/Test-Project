package com.diameter.commons;

import java.io.PrintWriter;
import java.io.StringWriter;

public class TableFormatter {
  public static final char ROW_SEPARATOR_HYPHEN = '-';
  
  public static final String COLUMN_SEPARATOR_PIPE = "|";
  
  public static final String COLUMN_SEPARATOR_COMMA = ",";
  
  private char rowSeparator = '-';
  
  private int[] width;
  
  private String[] header;
  
  private boolean isWidthExceed;
  
  private int patternLength = 0;
  
  private int format;
  
  private int[] column_alignment;
  
  public static final int LEFT = 0;
  
  public static final int RIGHT = 2;
  
  public static final int CENTER = 3;
  
  private String columnSeparator = "|";
  
  private static final int RIGHT_MARGIN = 1;
  
  private static final int LEFT_MARGIN = 1;
  
  public static final int ALL_BORDER = 0;
  
  public static final int OUTER_BORDER = 1;
  
  public static final int ONLY_HEADER_LINE = 2;
  
  public static final int NO_BORDERS = 3;
  
  public static final int BORDER_ABOVE_ONLY = 4;
  
  public static final int ONLY_HEADER_LINE_WITH_COL_SEPARATOR = 5;
  
  public static final int CSV = 6;
  
  private StringWriter stringWriter = new StringWriter();
  
  private PrintWriter out = new PrintWriter(this.stringWriter);
  
  public TableFormatter(String[] header, int[] width) {
    this(header, width, (int[])null, 0);
  }
  
  public TableFormatter(String[] header, int[] width, int[] column_alignment) {
    this(header, width, column_alignment, 0);
  }
  
  public TableFormatter(String[] header, int[] width, int format) {
    this(header, width, (int[])null, format);
  }
  
  public TableFormatter(String[] header, int[] width, int[] column_alignment, int format) {
    this(header, width, column_alignment, format, '-', "|");
  }
  
  public TableFormatter(String[] header, int[] width, int format, String columnSeparator) {
    this(header, width, null, format, '-', columnSeparator);
  }
  
  public TableFormatter(String[] header, int[] width, int[] column_alignment, int format, String columnSeparator) {
    this(header, width, column_alignment, format, '-', columnSeparator);
  }
  
  public TableFormatter(String[] header, int[] width, int[] column_alignment, int format, char rowSeparator, String columnSeparator) {
    this.columnSeparator = columnSeparator;
    this.rowSeparator = rowSeparator;
    this.width = width;
    this.header = header;
    if (column_alignment != null) {
      this.column_alignment = column_alignment;
    } else {
      this.column_alignment = new int[width.length];
    } 
    this.format = format;
    int[] headerAlignment = new int[header.length];
    if (column_alignment != null) {
      headerAlignment = column_alignment;
    } else {
      for (int j = 0; j < headerAlignment.length; j++)
        headerAlignment[j] = 3; 
    } 
    if (format == 2 || format == 3)
      this.columnSeparator = ""; 
    for (int i = 0; i < width.length - 1; i++)
      this.patternLength += width[i] + 1 + this.columnSeparator.length() + 1; 
    this.patternLength += width[width.length - 1] + 1 + 2 * this.columnSeparator.length() + 1;
    if (format == 0 || format == 1)
      this.out.println(fillChar("", this.patternLength, '-')); 
    addRecord(this.header, headerAlignment);
    if (format == 2 || format == 1 || format == 5)
      this.out.println(fillChar("", this.patternLength, this.rowSeparator)); 
  }
  
  public void addRecord(String[] dataValues) {
    addRecord(dataValues, this.column_alignment);
  }
  
  public void addRecord(String[] dataValues, int[] column_alignment) {
    if (dataValues == null || dataValues.length != this.width.length)
      return; 
    String[] data = new String[dataValues.length];
    System.arraycopy(dataValues, 0, data, 0, data.length);
    if (this.format == 6) {
      for (int i = 0; i < data.length; i++) {
        this.out.print("\"" + data[i] + "\"");
        if (i != data.length - 1)
          this.out.print(this.columnSeparator); 
      } 
      this.out.println();
      return;
    } 
    this.isWidthExceed = true;
    while (this.isWidthExceed) {
      for (int i = 0; i < data.length; i++) {
        if (data[i].length() > this.width[i]) {
          this.isWidthExceed = true;
          break;
        } 
        this.isWidthExceed = false;
      } 
      String[] tempData = new String[data.length];
      int j;
      for (j = 0; j < data.length; j++)
        tempData[j] = formPrefixString(data[j], this.width[j]); 
      this.out.print(this.columnSeparator);
      for (j = 0; j < tempData.length; j++) {
        this.out.print(" ");
        this.out.print(getAlignedColumnValue(tempData[j], column_alignment[j], this.width[j]));
        this.out.print(" ");
        this.out.print(this.columnSeparator);
      } 
      this.out.println();
      for (j = 0; j < data.length; j++)
        data[j] = data[j].substring(tempData[j].length(), data[j].length()); 
    } 
    if (this.format == 0)
      this.out.println(fillChar("", this.patternLength, this.rowSeparator)); 
  }
  
  public void addNewLine() {
    this.out.println();
  }
  
  public void add(String string) {
    this.out.print(string);
  }
  
  public void add(String string, int alignment) {
    if (this.format == 6)
      string = "\"" + string + "\""; 
    this.out.println(getAlignedValue(string, alignment));
    if (this.format == 0)
      this.out.println(fillChar("", this.patternLength, this.rowSeparator)); 
  }
  
  public void add(int columnIndex, String value, int alignment) {
    if (columnIndex >= 0 || columnIndex <= this.width.length) {
      String[] data = new String[this.width.length];
      for (int i = 0; i < this.width.length; i++)
        data[i] = ""; 
      data[columnIndex - 1] = getAlignedColumnValue(value, alignment, this.width[columnIndex - 1]);
      addRecord(data);
    } 
  }
  
  private String getAlignedColumnValue(String value, int alignment, int width) {
    StringWriter writer = new StringWriter();
    PrintWriter out = new PrintWriter(writer);
    switch (alignment) {
      case 2:
        out.print(fillChar(width - value.length(), "", ' ') + value);
        break;
      case 3:
        out.print(fillChar((width - value.length()) / 2, "", ' ') + value + fillChar(width - (width - value.length()) / 2 - value.length(), "", ' '));
        break;
      case 0:
        out.print(value + fillChar("", width - value.length(), ' '));
        break;
    } 
    return writer.toString();
  }
  
  public void add(int columnIndex, String value) {
    if (columnIndex > 0 && columnIndex <= this.width.length) {
      String[] data = new String[this.width.length];
      for (int i = 0; i < this.width.length; i++)
        data[i] = ""; 
      data[columnIndex - 1] = value;
      addRecord(data);
    } 
  }
  
  private String getAlignedValue(String value, int alignment) {
    StringWriter writer = new StringWriter();
    PrintWriter out = new PrintWriter(writer);
    if (this.format != 6)
      out.print(this.columnSeparator); 
    out.print(" ");
    int width = this.patternLength - (this.columnSeparator.length() + 1) * 2;
    switch (alignment) {
      case 2:
        out.print(fillChar("", width - value.length(), ' ') + value);
        break;
      case 3:
        out.print(fillChar("", (width - value.length()) / 2, ' ') + value + fillChar(width - (width - value.length()) / 2 - value.length(), "", ' '));
        break;
      case 0:
        out.print(value + fillChar("", width - value.length(), ' '));
        break;
    } 
    out.print(" ");
    if (this.format != 6)
      out.print(this.columnSeparator); 
    return writer.toString();
  }
  
  public String getFormattedValues() {
    StringWriter writer = new StringWriter();
    PrintWriter out = new PrintWriter(writer);
    out.print(this.stringWriter.toString());
    if (this.format == 1)
      out.println(fillChar("", this.patternLength, this.rowSeparator)); 
    return writer.toString();
  }
  
  private String formPrefixString(String str, int length) {
    if (str.length() > length)
      return str.substring(0, length); 
    return str;
  }
  
  private String fillChar(String input, int length, char chr) {
    if (input == null)
      input = ""; 
    StringBuilder stringBuffer = new StringBuilder();
    stringBuffer.append(input);
    for (int i = input.length(); i < length; i++)
      stringBuffer.append(chr); 
    return stringBuffer.toString();
  }
  
  private String fillChar(int length, String input, char chr) {
    if (input == null)
      input = ""; 
    StringBuilder stringBuffer = new StringBuilder();
    for (int i = input.length(); i < length; i++)
      stringBuffer.append(chr); 
    stringBuffer.append(input);
    return stringBuffer.toString();
  }
}
