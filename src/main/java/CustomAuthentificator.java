import com.sun.net.httpserver.BasicAuthenticator;

import java.util.Map;

import static utils.HashGenerator.createHash;

/**
 * Created by marek on 22.5.16.
 */
public class CustomAuthentificator extends BasicAuthenticator {

    private final Map<String, String> adminHashMap;
    private final Map<String, String> userHashMap;

    private Permission permission = Permission.NONE;

    public CustomAuthentificator(Map<String, String> adminHashMap, Map<String, String> userHashMap) {
        super("test");
        this.adminHashMap = adminHashMap;
        this.userHashMap = userHashMap;
    }

    @Override
    public boolean checkCredentials(String s, String s1) {
        if (adminHashMap.containsKey(s) && adminHashMap.get(s).equals(createHash(s1))) {
            permission = Permission.ADMIN;
            return true;
        } else if (userHashMap.containsKey(s) && userHashMap.get(s).equals(createHash(s1))) {
            permission = Permission.USER;
            return true;
        }
        return false;
    }

    public Permission getPermission() {
        return permission;
    }
}
