package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class CategoriesRequest extends SpotifyRequest {
    private final String object = "categories";

    @Override
    public String execute() {
        return requestStartPage("/v1/browse/categories", object);
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
    protected String parseResults(JsonObject jsonObject) {
        StringBuilder retString = new StringBuilder();
        for (JsonElement album : jsonObject.getAsJsonArray("items")) {
            String albumName = album.getAsJsonObject().get("name").getAsString();
            retString.append(albumName).append("\n");
        }
        return retString.toString();
    }
}
