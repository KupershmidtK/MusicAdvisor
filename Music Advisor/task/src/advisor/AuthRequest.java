package advisor;


import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Base64;

class AuthRequest extends SpotifyRequest {
    class LocalHttpServer {
        private HttpServer server;
        private String response;
        private String code;
        private boolean stopFlag = false;

        public boolean isStopFlag() {
            return stopFlag;
        }

        public String getCode() {
            return code;
        }

        public void start() {
            try {
                server = HttpServer.create();
                server.bind(new InetSocketAddress(8080), 0);
                server.createContext("/",
                        new HttpHandler() {
                            public void handle(HttpExchange exchange) throws IOException {
                                response = exchange.getRequestURI().getQuery();

                                String answer = parseResponse(response);
                                exchange.sendResponseHeaders(200, answer.length());
                                exchange.getResponseBody().write(answer.getBytes());
                                exchange.getResponseBody().close();
                            }
                        });

                server.start();
            } catch (Exception e) {}
        }

        public void stop() {
            server.stop(1);
        }

        private String parseResponse(String response) {
            String answer = "Authorization code not found. Try again.";
            if (response == null) { return  answer; }

            if (response.indexOf("code=") != -1) {
                answer = "Got the code. Return back to your program.";
                setCode(response);
            }
            return answer;
        }

        private void setCode(String response) {
            int idx = response.indexOf('=') + 1;
            code = response.substring(idx);
            stopFlag = true;
        }
    }

    public AuthRequest() {
        super.needAuthorization = false;
    }

    @Override
    public String execute() {
        System.out.println("use this link to spotifyRequest the access code:");
        String code = getCode();
        System.out.println("code received");

        System.out.println("making http spotifyRequest for access_token...");
        String response = getToken(code);

        Context.setToken(JsonParser.parseString(response).getAsJsonObject().get("access_token").getAsString());
        return "---SUCCESS---";
    }

    @Override
    public String next() {
        return "Wrong command";
    }

    @Override
    public String prev() {
        return "Wrong command";
    }

    private String getCode() {
        String str =  Context.getAuthorizationServerURI() +
                "/authorize?client_id=" + Context.getClientID() +
                "&redirect_uri=" + Context.getLocalServerURL() +
                "&response_type=code";
        System.out.println(str);
        System.out.println("waiting for code...");

        LocalHttpServer server = new LocalHttpServer();
        server.start();
        while (!server.isStopFlag()) {
            try {
                Thread.sleep(1);
            } catch (Exception e) { }
        }
        server.stop();
        return server.getCode();
    }

    private String getToken(String code) {
        String credentials = Context.getClientID() + ":" + Context.getSecret();
        String body = "grant_type=authorization_code" +
                "&code=" + code +
                "&redirect_uri=" + Context.getLocalServerURL();
        HttpRequest request = HttpRequest.newBuilder()
                .headers("Content-Type", "application/x-www-form-urlencoded",
                        "Authorization", "Basic " +
                        Base64.getEncoder().encodeToString(credentials.getBytes()))
                .uri(URI.create(Context.getAuthorizationServerURI() + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return LocalHttpClient.getInstance().request(request);
    }
}
