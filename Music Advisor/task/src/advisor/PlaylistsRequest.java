package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

public class PlaylistsRequest extends SpotifyRequest{
    private final String object = "playlists";
    private String categoryName;
    private Map<String, String> listOfCategories = new HashMap<>();

    public PlaylistsRequest(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String execute() {
        fillCategoriesList();
        if (!listOfCategories.containsKey(categoryName))
            return "Unknown category name.";

        String id = listOfCategories.get(categoryName);
        return requestStartPage("/v1/browse/categories/" + id + "/playlists", object);
    }

    @Override
    public String next() {
        return requestNextPage(object);
    }

    @Override
    public String prev() {
        return requestPrevPage(object);
    }

    @Override
    protected String parseResults(JsonObject jsonObject ) {
        StringBuilder retString = new StringBuilder();
        for (JsonElement album : jsonObject.getAsJsonArray("items")) {
            String albumName = album.getAsJsonObject().get("name").getAsString();
            String albumURL = album.getAsJsonObject()
                    .getAsJsonObject("external_urls").get("spotify").getAsString();

            retString.append(albumName).append("\n")
                    .append(albumURL).append("\n")
                    .append("\n");

        }
        return retString.toString();
    }

    private void fillCategoriesList() {
        fillFromFirstPage();
    }

    private void fillFromFirstPage() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Context.getApiServer() + "/v1/browse/categories?limit=50"))
                .header("Authorization", "Bearer " + Context.getToken())
                .GET()
                .build();

        try {
            String response = LocalHttpClient.getInstance().request(request);
            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject().getAsJsonObject("categories");
            addToMap(jsonObject);

            String nextURL = jsonObject.get("next").getAsString();
            fillFromNextPage(nextURL);
        } catch (Exception e) {
            //return "Test unpredictable error message";
        }
    }

    private void fillFromNextPage(String nextURL) {
        if (nextURL.equals("null")) { return; }

        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + Context.getToken())
                .uri(URI.create(nextPageURL))
                .GET()
                .build();

        try {
            String response = LocalHttpClient.getInstance().request(request);
            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject().getAsJsonObject("categories");
            addToMap(jsonObject);

            nextURL = jsonObject.get("next").getAsString();
            fillFromNextPage(nextURL);
        } catch (Exception e) {
            //return "Test unpredictable error message";
        }
    }

    private void addToMap(JsonObject jsonObject) {
        for (JsonElement album : jsonObject.getAsJsonArray("items")) {
            String name = album.getAsJsonObject().get("name").getAsString();
            String id = album.getAsJsonObject().get("id").getAsString();
            listOfCategories.put(name, id);
        }
    }
}