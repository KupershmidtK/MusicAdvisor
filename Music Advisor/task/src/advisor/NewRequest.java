package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

class NewRequest extends SpotifyRequest {
    private String object = "albums";

    @Override
    public String execute() {
        return requestStartPage("/v1/browse/new-releases", object);
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

            List<String> artists = new ArrayList<>();
            for (JsonElement artist : album.getAsJsonObject().getAsJsonArray("artists")) {
                artists.add(artist.getAsJsonObject().get("name").getAsString());
            }

            retString.append(albumName).append("\n")
                    .append(artists).append("\n")
                    .append(albumURL).append("\n")
                    .append("\n");
        }
        return retString.toString();
    }

}
