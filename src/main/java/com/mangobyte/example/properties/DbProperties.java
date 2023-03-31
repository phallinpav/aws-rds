package com.mangobyte.example.properties;

public interface DbProperties {
    String getType();
    String getIdentifier();
    String getInstanceClass();
    String getEngine();
    boolean isMultiAZ();
    String getMasterUserName();
    String getMasterPassword();
    String getDbName();
    String getStorageType();
    int getAllocatedStorage();
}
