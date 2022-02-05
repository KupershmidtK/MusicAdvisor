package advisor;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (args.length > 1) {
            for (int i = 0; i < args.length - 1; i = i + 2) {
                switch (args[i]) {
                    case "-access":
                        Context.setAuthorizationServerURL(args[i + 1]);
                        break;
                    case "-resource":
                        Context.setApiServer(args[i + 1]);
                        break;
                    case "-page":
                        Context.setLinesPerPage(Integer.valueOf(args[i + 1]));
                        break;
                    default:
                        break;
                }
            }
        }

        App app = new App();
        app.run();
    }
}

class Viewer {
    final Scanner scanner = new Scanner(System.in);

    public String requestCommand() {
        return scanner.nextLine();
    }

    public void showResults(String answer) {
        System.out.println(answer);
    }
}

class App {
    private Viewer viewer = new Viewer();
    Controller controller = new Controller();

    public void run() {
        while (true) {
            String command = viewer.requestCommand();
            if ("exit".equals(command)) {
                viewer.showResults("---GOODBYE!---");
                break;
            }

            String results = controller.execute(command);
            viewer.showResults(results);
        }
    }
}