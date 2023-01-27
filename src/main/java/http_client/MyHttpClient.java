package http_client;

import com.google.gson.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.Duration;

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
        String lastPostId = arr.get(arr.size()-1).getAsJsonObject().get("id").getAsString();

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

        HttpRequest postsRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/users/" + id + "/todos"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        String json = CLIENT.send(postsRequest, HttpResponse.BodyHandlers.ofString()).body();
        JsonArray arr = new JsonParser().parse(json).getAsJsonArray();

        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).getAsJsonObject().get("completed").getAsBoolean() == false) {
                System.out.println(gson.toJson(arr.get(i)));
            }
        }
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
