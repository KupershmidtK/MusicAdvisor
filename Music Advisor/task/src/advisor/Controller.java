package advisor;

public class Controller {
    SpotifyRequest spotifyRequest;

    public String execute(String command) {
        String retString = "Wrong command!";
        if ("prev".equals(command) && spotifyRequest != null) {
            retString = spotifyRequest.prev();
        } else if ("next".equals(command) && spotifyRequest != null) {
            retString = spotifyRequest.next();
        } else {
            spotifyRequest = RequestFactory.createRequest(command);
            if (spotifyRequest != null)
                retString = spotifyRequest.execute();
        }
        return retString;
    }
}

class RequestFactory {
    public static SpotifyRequest createRequest(String command) {
        SpotifyRequest request = null;

        String category = "";
        int idx = command.indexOf(" ");
        if (idx != -1) {
            category = command.substring(idx + 1).trim();
            command = command.substring(0, idx);
        }

        switch (command) {
            case "new":
                request = new NewRequest();
                break;
            case "featured":
                request = new FeaturedRequest();
                break;
            case "categories":
                request = new CategoriesRequest();
                break;
            case "playlists":
                request = new PlaylistsRequest(category);
                break;
            case "auth":
                request = new AuthRequest();
                break;
            default:
                break;
        }

        return request != null ? new AuthorizationDecorator(request) : null;
    }
}

abstract class SpotifyRequestDecorator extends SpotifyRequest {
    protected SpotifyRequest request;
    SpotifyRequestDecorator(SpotifyRequest request) {
        this.request = request;
    }
}

class AuthorizationDecorator extends SpotifyRequestDecorator {
    public AuthorizationDecorator(SpotifyRequest request) {
        super(request);
    }

    @Override
    public String execute() {
        String retString = "Please, provide access for application.";
        if (!request.needAuthorization || LocalHttpClient.getInstance().isAuthorized()) {
            retString = request.execute();
        }
        return retString;
    }

    @Override
    public String next() {
        String retString = "Please, provide access for application.";
        if (!request.needAuthorization || LocalHttpClient.getInstance().isAuthorized()) {
            retString = request.next();
        }
        return retString;
    }

    @Override
    public String prev() {
        String retString = "Please, provide access for application.";
        if (!request.needAuthorization || LocalHttpClient.getInstance().isAuthorized()) {
            retString = request.prev();
        }
        return retString;
    }
}