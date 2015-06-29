package advancedsystemsmanager.util;


import com.mojang.authlib.GameProfile;

import java.util.UUID;

public class UserPermission
{
    private GameProfile user;
    private boolean op;
    private boolean active;

    public UserPermission(String name, UUID uuid)
    {
        user = new GameProfile(uuid, name);
    }

    public UserPermission(GameProfile user)
    {
        this.user = user;
    }

    public UserPermission copy()
    {
        UserPermission temp = new UserPermission(getUser());
        temp.setOp(isOp());
        temp.setActive(isActive());
        return temp;
    }

    public GameProfile getUser()
    {
        return user;
    }

    public boolean isOp()
    {
        return op;
    }

    public void setOp(boolean op)
    {
        this.op = op;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }
}
