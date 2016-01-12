package org.mule.policies.commons;

public class ConfigurationParameter {

	private final String propertyName;
	private final String type;
	private final String defaultValue;
	private final boolean optional;
	private final boolean sensitive;
	private final String description;
	private final String name;
	
	private ConfigurationParameter(Builder builder) {
	  this.propertyName = builder.propertyName;
	  this.type = builder.type;
	  this.defaultValue = builder.defaultValue;
	  this.optional = builder.optional;
	  this.sensitive = builder.sensitive;
	  this.description = builder.description;
	  this.name = builder.name;
	}
	public static class Builder{

		private String propertyName;
		private String type;
		private String defaultValue;
		private boolean optional;
		private boolean sensitive;
		private String description;
		private String name;
		public Builder withPropertyName(String propertyName) {
		  this.propertyName = propertyName;
		  return this;
		}
		public Builder withType(String type) {
		  this.type = type;
		  return this;
		}
		public Builder withDefaultValue(String defaultValue) {
		  this.defaultValue = defaultValue;
		  return this;
		}
		public Builder withOptional(boolean optional) {
		  this.optional = optional;
		  return this;
		}
		public Builder withSensitive(boolean sensitive) {
		  this.sensitive = sensitive;
		  return this;
		}
		public Builder withDescription(String description) {
		  this.description = description;
		  return this;
		}
		public Builder withName(String name) {
		  this.name = name;
		  return this;
		}
		public ConfigurationParameter build() {
		  return new ConfigurationParameter(this);
		}
	}
	public String getPropertyName() {
		return propertyName;
	}
	public String getType() {
		return type;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public boolean isOptional() {
		return optional;
	}
	public boolean isSensitive() {
		return sensitive;
	}
	public String getDescription() {
		return description;
	}
	public String getName() {
		return name;
	}	
	
}
