package com.diameter.commons;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@XmlRootElement(name = "plugin-entry")
@XmlType(propOrder = {"pluginName", "pluginArgument"})
public class PluginEntryDetail implements Differentiable {
  private String pluginName;
  
  private String pluginArgument;
  
  @XmlTransient
  private PluginCallerIdentity callerId;
  
  @XmlElement(name = "plugin-name")
  public String getPluginName() {
    return this.pluginName;
  }
  
  public void setPluginName(String pluginName) {
    this.pluginName = pluginName;
  }
  
  @XmlElement(name = "argument")
  public String getPluginArgument() {
    return this.pluginArgument;
  }
  
  public void setPluginArgument(String pluginArgument) {
    this.pluginArgument = pluginArgument;
  }
  
  @XmlTransient
  public PluginCallerIdentity getCallerId() {
    return this.callerId;
  }
  
  public void setCallerId(PluginCallerIdentity callerId) {
    this.callerId = callerId;
  }
  
  public String toString() {
    StringWriter writer = new StringWriter();
    PrintWriter out = new PrintWriter(writer);
    out.println(String.format("%-30s: %s", new Object[] { "Plugin Name", getPluginName() }));
    out.println(String.format("%-30s: %s", new Object[] { "Plugin Arguments", getPluginArgument() }));
    out.close();
    return writer.toString();
  }
  
  public JSONObject toJson() {
    JSONObject object = new JSONObject();
    try {
		object.put("Plug-in", this.pluginName);
		object.put("Argument", this.pluginArgument);
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    return object;
  }
}
