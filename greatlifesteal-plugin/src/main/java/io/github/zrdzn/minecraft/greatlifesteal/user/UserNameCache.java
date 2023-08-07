package io.github.zrdzn.minecraft.greatlifesteal.user;

import java.util.Collections;
import java.util.Set;

public class UserNameCache {

    private final Set<String> usersNames;

    public UserNameCache(Set<String> usersNames) {
        this.usersNames = usersNames;
    }

    public void addUserName(String userName) {
        this.usersNames.add(userName);
    }

    public void removeUserName(String userName) {
        this.usersNames.remove(userName);
    }

    public Set<String> getUsersNames() {
        return Collections.unmodifiableSet(this.usersNames);
    }

    public void clear() {
        this.usersNames.clear();
    }

}
