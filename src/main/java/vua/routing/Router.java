package vua.routing;

import vua.http.Response;
import vua.utils.StringUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class Router {

    protected HashMap<String, Tree> methods;
    private ArrayList<Group> groups;

    public Router() {
        methods = new HashMap<>();
        groups = new ArrayList<>();
        init();
    }

    private void init() {
        String[] m = new String[] {"GET", "POST", "PUT", "PATCH", "DELETE"};

        for (String method : m) {
            methods.put(method, new Tree());
        }
    }

    public Node get(String path, Class controllerClass, String action) {
        Action a = new Action(controllerClass, action);
        return insert("GET", path, a);
    }

    public Node post(String path, Class controllerClass, String action) {
        Action a = new Action(controllerClass, action);
        return insert("POST", path, a);
    }

    public Node put(String path, Class controllerClass, String action) {
        Action a = new Action(controllerClass, action);
        return insert("PUT", path, a);
    }

    public Node patch(String path, Class controllerClass, String action) {
        Action a = new Action(controllerClass, action);
        return insert("PATCH", path, a);
    }

    public Node delete(String path, Class controllerClass, String action) {
        Action a = new Action(controllerClass, action);
        return insert("DELETE", path, a);
    }

    /**
     * General method for route creation
     *
     * @param method
     * @param path
     * @param action
     * @return
     */
    private Node insert(String method, String path, Action action) {
        Tree tree = methods.get(method);

        StringBuilder builder = new StringBuilder();

        path = StringUtil.trim(path, '/');

        if (groups.size() > 0) {
            for (Group group : groups) {
                group.buildPrefix(builder);
            }
        }

        if (!path.isEmpty()) {
            // /home -> home
            if (builder.length() > 0) {
                builder.append('/');
            }
            builder.append(path);
        }

        return tree.insert(builder.toString(), action);
    }

    /**
     * Group routes by their prefix
     *
     * @param prefix
     * @param group
     */
    public void group(String prefix, RouteGroup group) {
        groups.add(new Group(prefix));
        group.init(this);
        groups.remove(groups.size() - 1);
    }

    /**
     * The route dispatcher
     *
     * @param context
     * @param request
     * @param response
     */
    public void handle(ServletContext context, HttpServletRequest request, HttpServletResponse response) {
        if ( ! methods.containsKey(request.getMethod())) {
            try {
                response.getWriter().printf("Method not found!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Tree tree = methods.get(request.getMethod());

        NodeMatchResult result = tree.match(request.getPathInfo());

        if ( ! result.isMatched()) {
            // not found
            return;
        }

        Action a = (Action) result.getNode().getHandler();

        Controller controller = a.getControllerInstance();
        Method method = a.getControllerMethod();

        Response res;

        try {
            res = (Response) method.invoke(controller);
            res.render(response);
        } catch (Exception e) {
            // something
            e.printStackTrace();
        }

    }

}