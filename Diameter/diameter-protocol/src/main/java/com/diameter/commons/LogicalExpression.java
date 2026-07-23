package com.diameter.commons;

import java.io.Serializable;

public interface LogicalExpression extends Expression, Serializable {
  boolean evaluate(ValueProvider paramValueProvider);
}
