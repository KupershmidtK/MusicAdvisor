package advisor;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class Context {
    private static String authorizationServer = "https://accounts.spotify.com";
    private static String apiServer = "https://api.spotify.com";
    private static final String localServerURL = "http://localhost:8080";
    private static final String clientID = "YOUR_CLIENT_ID";
    private static final String secret = "YOUR_SECRET_CODE";
    private static int linesPerPage = 5;
    private static String token = "";

    public static String getAuthorizationServerURI() {
        return authorizationServer;
    }

    public static void setAuthorizationServerURL(String serverURI) {
        Context.authorizationServer = serverURI;
    }

    public static String getLocalServerURL() {
        return localServerURL;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        Context.token = token;
    }

    public static String getClientID() {
        return clientID;
    }

    public static String getSecret() {
        return secret;
    }

    public static String getApiServer() {
        return apiServer;
    }

    public static void setApiServer(String apiServer) {
        Context.apiServer = apiServer;
    }

    public static int getLinesPerPage() {
        return linesPerPage;
    }

    public static void setLinesPerPage(int linesPerPage) {
        Context.linesPerPage = linesPerPage;
    }
}

class LocalHttpClient {
    private static LocalHttpClient instance = null;
    private final HttpClient client;

    public boolean isAuthorized() { return !Context.getToken().isEmpty(); }

    private LocalHttpClient() {
        client  = HttpClient.newBuilder().build();
    }

    public static LocalHttpClient getInstance() {
        if (instance == null)
            instance = new LocalHttpClient();
        return instance;
    }

    public String request(HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(
                    request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (Exception e) {
            System.out.println("We cannot access the site. Please, try later.");
            return null;
        }
    }
}


