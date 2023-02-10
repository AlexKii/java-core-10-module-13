package http_client;

import com.google.gson.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MyHttpClient {
    private MyHttpClient() {
        throw new IllegalStateException("Utility class");
    }

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final String BASE = "files";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void createNewUser() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/users"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofFile(Paths.get(BASE, "user.json")))
                .build();

        sendAsyncRequest(request);
    }

    public static void editUser(int id) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/users/" + id))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .method("PUT", HttpRequest.BodyPublishers.ofFile(Paths.get(BASE, "user.json")))
                .build();

        sendAsyncRequest(request);
    }

    public static void deleteUser(int id) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/users/" + id))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        sendAsyncRequest(request);
    }

    public static void getAllUsers() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/users"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        sendAsyncRequest(request);
    }

    public static void getUserById(int id) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/users/" + id))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        sendAsyncRequest(request);
    }

    public static void getUserByUsername(String userName) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s?username=%s", "https://jsonplaceholder.typicode.com/users", userName)))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        sendAsyncRequest(request);
    }

    public static void getCommentsToLastUsersPostByUserId(int id) throws Exception {

        HttpRequest postsRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/users/" + id + "/posts"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        String json = CLIENT.send(postsRequest, HttpResponse.BodyHandlers.ofString()).body();

        JsonArray arr = new JsonParser().parse(json).getAsJsonArray();
        String lastPostId = arr.get(arr.size() - 1).getAsJsonObject().get("id").getAsString();

        HttpRequest commentsRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts/" + lastPostId + "/comments"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        String fileName = "user-" + id + "-post-" + lastPostId + "-comments.json";

        CLIENT.send(commentsRequest, HttpResponse.BodyHandlers.ofFile(Paths.get(BASE, fileName)));
    }

    public static void getOpenTasksByUserId(int id) throws Exception {

        HttpRequest tasksRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/users/" + id + "/todos"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        String json = CLIENT.send(tasksRequest, HttpResponse.BodyHandlers.ofString()).body();

        new JsonParser().parse(json).getAsJsonArray().asList().stream()
                .filter(el -> !el.getAsJsonObject().get("completed").getAsBoolean())
                .forEach(el -> System.out.println(gson.toJson(el.getAsJsonObject())));
    }

    private static void sendAsyncRequest(HttpRequest request) {

        CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(stringHttpResponse -> {
                    System.out.println(stringHttpResponse);
                    return stringHttpResponse;
                })
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .join();
    }
}
