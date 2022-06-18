package io.github.zrdzn.minecraft.greatlifesteal.config.bean.beans;

public class ExampleBean {

    private int identifier = 0;
    private boolean enabled = true;
    private String name = "Default name";

    public int getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
