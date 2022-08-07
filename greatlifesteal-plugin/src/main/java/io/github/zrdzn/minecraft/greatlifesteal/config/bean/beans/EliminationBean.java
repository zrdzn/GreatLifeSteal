package io.github.zrdzn.minecraft.greatlifesteal.config.bean.beans;

import io.github.zrdzn.minecraft.greatlifesteal.config.bean.BeanBuilder;

public class EliminationBean {

    private boolean enabled = true;
    private String action = "eliminate";
    private ReviveBean revive = BeanBuilder.from(ReviveBean.class).build();

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ReviveBean getRevive() {
        return this.revive;
    }

    public void setRevive(ReviveBean revive) {
        this.revive = revive;
    }

}
