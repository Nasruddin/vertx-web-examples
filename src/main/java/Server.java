import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nasir on 4/1/16.
 */
public class Server extends AbstractVerticle {

    public static void main(String[] args) {

        RunnerUtil.runExample(Server.class);
    }

    private Map<String, JsonObject> employees = new HashMap<>();

    @Override
    public void start() throws Exception {
        initData();
        Router router = Router.router(vertx);
//        router.route().handler(routingContext -> {
//            routingContext.response().putHeader("content-type", "text/html")
//                    .end("First Example of Vertx Web");
//        });
//
//        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
            router.route().handler(BodyHandler.create());
            router.get("/employee/:employeeId").handler(this::handleGetEmployees);
            router.put("/employee/:employeeId/:name/:age").handler(this::handleAddEmployee);
            router.get("/employees").handler(this::handleListEmployees);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    private void handleListEmployees(RoutingContext routingContext) {

        JsonArray arr = new JsonArray();
        employees.forEach((k, v) -> arr.add(v));
        routingContext.response().putHeader("content-type", "application/json").end(arr.encodePrettily());
    }

    private void handleAddEmployee(RoutingContext routingContext) {

        String employeeId = routingContext.request().getParam("employeeId");
        HttpServerResponse response = routingContext.response();
        System.out.println(routingContext.toString());
        System.out.println("Response : " + response);
        if (employeeId == null) {
            sendError(404, response);
        } else {

            JsonObject employee = routingContext.getBodyAsJson();
            System.out.println(employee);
            if (employee == null) {
                sendError(404, response);
            } else {
                employees.put(employeeId, employee);
                response.end();
            }
        }
    }

    private void handleGetEmployees(RoutingContext routingContext) {

        String employeeId = routingContext.request().getParam("employeeId");
        HttpServerResponse response = routingContext.response();

        if (employeeId == null) {
            sendError(404, response);
        } else {
            JsonObject employee = employees.get(employeeId);
            if (employee == null) {
                sendError(404, response);
            } else {
                response.putHeader("content-type", "application/json").end(employee.encodePrettily());
            }
        }
    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }

    public void addEmployee(JsonObject employee) {
        employees.put(employee.getString("id"), employee);
    }

    public void initData() {
        addEmployee(new JsonObject().put("id", "emp1").put("name", "empname1").put("age", "25"));
        addEmployee(new JsonObject().put("id", "emp2").put("name", "empname2").put("age", "35"));
        addEmployee(new JsonObject().put("id", "emp3").put("name", "empname3").put("age", "24"));
        addEmployee(new JsonObject().put("id", "emp4").put("name", "empname4").put("age", "29"));
    }
}
