package com.company.data;

import java.lang.reflect.Field;

public abstract class FieldHelpers {
    public abstract Class getLowerClass();
    public abstract Object getLowerInstance();
    public abstract String getByReference(String reference);
}
