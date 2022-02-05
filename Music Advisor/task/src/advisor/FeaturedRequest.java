package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class FeaturedRequest extends SpotifyRequest {
    private final String object = "playlists";

    @Override
    public String execute() {
        return requestStartPage("/v1/browse/featured-playlists", object);
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
            String albumURL = album.getAsJsonObject()
                    .getAsJsonObject("external_urls").get("spotify").getAsString();

            retString.append(albumName).append("\n")
                    .append(albumURL).append("\n").append("\n");
        }
        return retString.toString();
    }
}
