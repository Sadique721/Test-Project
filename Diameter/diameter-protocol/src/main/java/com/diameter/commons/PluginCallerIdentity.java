package com.diameter.commons;

public final class PluginCallerIdentity {
  private static final char SEPARATOR = '.';
  
  private ServiceTypeConstants serviceName;
  
  private String servicePolicyName;
  
  private ServicePolicyFlow servicePolicyFlow;
  
  private String pluginHandlerName;
  
  private PluginType pluginType;
  
  private PluginMode pluginMode;
  
  private String pluginName;
  
  private int pluginIndex;
  
  private String key;
  
  private PluginCallerIdentity() {}
  
  public static PluginKeyBuilder createAndGetIdentity(ServiceTypeConstants serviceName, PluginMode pluginMode, int pluginIndex, String pluginName) {
    return new PluginKeyBuilder(serviceName, pluginMode, pluginIndex, pluginName);
  }
  
  public ServiceTypeConstants getServiceName() {
    return this.serviceName;
  }
  
  public String getPluginHandlerName() {
    return this.pluginHandlerName;
  }
  
  public PluginMode getPluginMode() {
    return this.pluginMode;
  }
  
  public PluginType getPluginType() {
    return this.pluginType;
  }
  
  public String getServicePolicyName() {
    return this.servicePolicyName;
  }
  
  public ServicePolicyFlow getServicePolicyFlow() {
    return this.servicePolicyFlow;
  }
  
  public int getPluginIndex() {
    return this.pluginIndex;
  }
  
  public String getPluginName() {
    return this.pluginName;
  }
  
  public String getKey() {
    return this.key;
  }
  
  public static class PluginKeyBuilder {
    private PluginCallerIdentity pluginKey;
    
    private byte flag = 0;
    
    private PluginKeyBuilder(ServiceTypeConstants serviceName, PluginMode pluginMode, int pluginIndex, String pluginName) {
      this.pluginKey = new PluginCallerIdentity();
      this.pluginKey.serviceName = serviceName;
      this.pluginKey.pluginMode = pluginMode;
      this.pluginKey.pluginIndex = pluginIndex;
      this.pluginKey.pluginName = pluginName;
    }
    
    public PluginKeyBuilder setPluginHandlerName(String name) {
      this.pluginKey.pluginHandlerName = name;
      this.flag = (byte)(this.flag | 0x8);
      return this;
    }
    
    public PluginKeyBuilder setPluginType(PluginType pluginType) {
      this.pluginKey.pluginType = pluginType;
      this.flag = (byte)(this.flag | 0x4);
      return this;
    }
    
    public PluginKeyBuilder setServicePolicyName(String servicePolicyName) {
      this.pluginKey.servicePolicyName = servicePolicyName;
      this.flag = (byte)(this.flag | 0x2);
      return this;
    }
    
    public PluginKeyBuilder setServicePolicyFlow(ServicePolicyFlow servicePolicyFlow) {
      this.pluginKey.servicePolicyFlow = servicePolicyFlow;
      this.flag = (byte)(this.flag | 0x1);
      return this;
    }
    
    public PluginCallerIdentity getId() {
      StringBuilder builder = new StringBuilder();
      builder.append(this.pluginKey.serviceName).append('.');
      if ((this.flag & 0x2) == 2)
        builder.append(this.pluginKey.servicePolicyName).append('.'); 
      if ((this.flag & 0x1) == 1)
        builder.append(this.pluginKey.servicePolicyFlow).append('.'); 
      if ((this.flag & 0x8) == 8)
        builder.append(this.pluginKey.pluginHandlerName).append('.'); 
      if ((this.flag & 0x4) == 4)
        builder.append(this.pluginKey.pluginType).append('.'); 
      builder.append(this.pluginKey.pluginMode).append('.');
      builder.append(this.pluginKey.pluginName).append('.');
      builder.append(this.pluginKey.pluginIndex);
      this.pluginKey.key = builder.toString();
      return this.pluginKey;
    }
  }
}
