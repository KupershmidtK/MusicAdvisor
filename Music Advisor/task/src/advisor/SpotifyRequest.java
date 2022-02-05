package advisor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpRequest;

abstract class SpotifyRequest {
    protected boolean needAuthorization = true;

    protected String listOfItems;
    protected int numberOfPages;
    protected int currentPage;
    protected String prevPageURL;
    protected String nextPageURL;

    abstract String execute();
    abstract String next();
    abstract String prev();
    protected String parseResults(JsonObject jsonObject) { return ""; }

    protected String requestStartPage(String uri, String object) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Context.getApiServer() + uri + "?limit=" + Context.getLinesPerPage()))
                .header("Authorization", "Bearer " + Context.getToken())
                .GET()
                .build();

        try {
            String response = LocalHttpClient.getInstance().request(request);
            setPageParameters(response, object);
            currentPage = 1;
            return String.format("%s---PAGE %d OF %d---", listOfItems, currentPage, numberOfPages);
        } catch (Exception e) {
            clearPageParameters();
            return "Test unpredictable error message";
        }
    }

    protected String requestPrevPage(String object) {
        if (prevPageURL == null) { return "No more pages."; }

        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + Context.getToken())
                .uri(URI.create(prevPageURL))
                .GET()
                .build();

        try {
            String response = LocalHttpClient.getInstance().request(request);
            setPageParameters(response, object);
            currentPage--;
            return String.format("%s---PAGE %d OF %d---", listOfItems, currentPage, numberOfPages);
        } catch (Exception e) {
            clearPageParameters();
            return "Test unpredictable error message";
        }
    }

    protected String requestNextPage(String object) {
        if (nextPageURL == null) { return "No more pages."; }

        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + Context.getToken())
                .uri(URI.create(nextPageURL))
                .GET()
                .build();

        try {
            String response = LocalHttpClient.getInstance().request(request);
            setPageParameters(response, object);
            currentPage++;
            return String.format("%s---PAGE %d OF %d---", listOfItems, currentPage, numberOfPages);
        } catch (Exception e) {
            clearPageParameters();
            return "Test unpredictable error message";
        }
    }

    protected String getPrevPageURL(JsonObject jsonObject) {
        try {
            String prev = jsonObject.get("previous").getAsString();
            return prev.equals("null") ? null : prev;
        } catch (Exception e) {
            return null;
        }
    }

    protected String getNextPageURL(JsonObject jsonObject) {
        try {
            String next = jsonObject.get("next").getAsString();
            return next.equals("null") ? null : next;
        } catch (Exception e) {
            return null;
        }
    }

    protected int getNumberOfPages(JsonObject jsonObject) {
        try {
            int total = jsonObject.get("total").getAsInt();
            int limit = jsonObject.get("limit").getAsInt();
            return (total + (limit - 1))/ limit;
        } catch (Exception e) {
            return 0;
        }
    }

    protected void setPageParameters(String response, String object) {
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject().getAsJsonObject(object);
        listOfItems = parseResults(jsonObject);
        numberOfPages = getNumberOfPages(jsonObject);
        nextPageURL = getNextPageURL(jsonObject);
        prevPageURL = getPrevPageURL(jsonObject);
    }

    protected void clearPageParameters() {
        numberOfPages = 0;
        currentPage = 0;
        nextPageURL = null;
        prevPageURL = null;
    }
}