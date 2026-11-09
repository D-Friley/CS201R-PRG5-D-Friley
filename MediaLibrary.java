
import java.io.*;
import java.util.*;

public class MediaLibrary {

    private static ArrayList<Media> mediaList = new ArrayList<>();
    private static PrintWriter report;
    private static PrintWriter error;

    public static void main(String[] args) {

        System.out.println("Welcome to Media Library");

        try (
            PrintWriter reportWriter =
                new PrintWriter(new FileWriter("report.txt"));

            PrintWriter errorWriter =
                new PrintWriter(new FileWriter("error.txt"))
        ) {

            report = reportWriter;
            error = errorWriter;

            readMediaFile("mediaList.txt");
            processCommands("mediaCommands.txt");

            System.out.println(
                "Processing complete. Check report.txt and error.txt."
            );

        } catch (IOException e) {

            System.out.println("File error: " + e.getMessage());
        }
    }

    // Reads initial media records.
    private static void readMediaFile(String fileName)
            throws IOException {

        try (BufferedReader input =
                new BufferedReader(new FileReader(fileName))) {

            String line;
            int lineNumber = 0;

            while ((line = input.readLine()) != null) {

                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                try {

                    String[] fields = parseCSV(line);

                    if (fields.length < 8) {
                        throw new IllegalArgumentException(
                            "Expected at least 8 fields."
                        );
                    }

                    Media media = createMedia(fields);
                    mediaList.add(media);

                } catch (IllegalArgumentException e) {

                    error.println(
                        "mediaList.txt, line " +
                        lineNumber + ": " + e.getMessage()
                    );
                }
            }
        }
    }

 // Creates the correct concrete subclass.
    private static Media createMedia(String[] fields) {

    if (fields.length < 8) {
        throw new IllegalArgumentException(
            "Expected at least 8 fields."
        );
    }

    String typeText = fields[0].trim().toUpperCase();

    if (typeText.length() != 1 ||
        "MBS".indexOf(typeText.charAt(0)) == -1) {

        throw new IllegalArgumentException(
            "Invalid media type: " + fields[0]
        );
    }

    char type = typeText.charAt(0);

    String title = fields[1].trim();
    String name = fields[2].trim();
    int rating = parseInteger(fields[3], "rating");
    String genre = fields[4].trim();
    int length = parseInteger(fields[5], "length");
    int year = parseInteger(fields[6], "year released");

    if (rating < 1 || rating > 10) {
        throw new IllegalArgumentException(
            "Rating must be between 1 and 10."
        );
    }

    if (length <= 0) {
        throw new IllegalArgumentException(
            "Length must be greater than 0."
        );
    }

    if (year <= 0) {
        throw new IllegalArgumentException(
            "Year must be greater than 0."
        );
    }

    switch (type) {

        case 'M':

            StringBuilder stars = new StringBuilder();

            for (int i = 7; i < fields.length; i++) {

                if (stars.length() > 0) {
                    stars.append(", ");
                }

                stars.append(fields[i].trim());
            }

            if (stars.length() == 0) {
                throw new IllegalArgumentException(
                    "Movie must have at least one star."
                );
            }

            return new Movie(
                type, title, name, rating,
                genre, length, year, stars.toString()
            );

        case 'B':

            if (fields.length != 8) {
                throw new IllegalArgumentException(
                    "Book requires exactly 8 fields."
                );
            }

            int nytList =
                parseInteger(fields[7], "NYTList");

            return new Book(
                type, title, name, rating,
                genre, length, year, nytList
            );

        case 'S':

            if (fields.length != 8) {
                throw new IllegalArgumentException(
                    "Song requires exactly 8 fields."
                );
            }

            int top40Value =
                parseInteger(fields[7], "Top40");

            if (top40Value != 0 && top40Value != 1) {
                throw new IllegalArgumentException(
                    "Top40 must be 0 or 1."
                );
            }

            boolean top40 = (top40Value == 1);

            return new Song(
                type, title, name, rating,
                genre, length, year, top40
            );

        default:

            throw new IllegalArgumentException(
                "Unknown media type."
            );
    }
}

    // Converts text to an integer.
    private static int parseInteger(
            String value, String fieldName) {

        try {

            return Integer.parseInt(value.trim());

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                "Invalid integer in " +
                fieldName + ": " + value
            );
        }
    }

    // Checks whether a value is an integer.
    private static boolean isInteger(String value) {

        try {

            Integer.parseInt(value.trim());
            return true;

        } catch (NumberFormatException e) {

            return false;
        }
    }

  // Parses CSV records with quoted commas.
    private static String[] parseCSV(String line) {

        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();

        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {

            char current = line.charAt(i);

            if (current == '"') {

                if (inQuotes &&
                    i + 1 < line.length() &&
                    line.charAt(i + 1) == '"') {

                    field.append('"');
                    i++;

                } else {

                    inQuotes = !inQuotes;
                }

            } else if (current == ',' && !inQuotes) {

                fields.add(field.toString());
                field.setLength(0);

            } else {

                field.append(current);
            }
        }

        if (inQuotes) {
            throw new IllegalArgumentException(
                "Unclosed quoted field."
            );
        }

        fields.add(field.toString());

        return fields.toArray(new String[0]);
    }

    // Processes commands.
    private static void processCommands(String fileName)
            throws IOException {

        try (BufferedReader input =
                new BufferedReader(new FileReader(fileName))) {

            String line;
            int lineNumber = 0;

            while ((line = input.readLine()) != null) {

                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields;

                try {

                    fields = parseCSV(line);

                } catch (IllegalArgumentException e) {

                    error.println(
                        "mediaCommands.txt, line " +
                        lineNumber + ": " + e.getMessage()
                    );

                    continue;
                }

                String command =
                    fields[0].trim().toUpperCase();

                if (command.equals("Q")) {
                    break;
                }

                try {

                    executeCommand(command, fields);

                } catch (IllegalArgumentException e) {

                    error.println(
                        "mediaCommands.txt, line " +
                        lineNumber + ": " + e.getMessage()
                    );
                }
            }
        }
    }

    // Executes commands.
    private static void executeCommand(
            String command, String[] fields) {

        switch (command) {

            case "A":

                if (fields.length == 1) {

                    printMedia(mediaList, "All Media");

                } else if (fields.length == 2) {

                    String value = fields[1].trim();

                    if (isInteger(value)) {
                        printByRating('A', value);
                    } else {
                        printByGenre('A', value);
                    }

                } else if (fields.length == 3 &&
                    (fields[1].trim().equalsIgnoreCase("G") ||
                     fields[1].trim().equalsIgnoreCase("GENRE"))) {

                    printByGenre('A', fields[2]);

                } else {

                    invalidCommand(command);
                }

                break;

            case "M":
            case "B":
            case "S":

                if (fields.length == 1) {

                    printByType(command.charAt(0));

                } else if (fields.length == 2) {

                    String value = fields[1].trim();

                    if (isInteger(value)) {

                        printByRating(
                            command.charAt(0), value
                        );

                    } else {

                        printByGenre(
                            command.charAt(0), value
                        );
                    }

                } else {

                    invalidCommand(command);
                }

                break;

            case "T":

                if (fields.length != 1) {
                    invalidCommand(command);
                }

                printCounts();
                break;

            case "N":

                addMedia(fields);
                break;

            case "L":

                if (fields.length != 2) {
                    invalidCommand(command);
                }

                printStars(fields[1]);
                break;

            case "F":

                if (fields.length != 2) {
                    invalidCommand(command);
                }

                printMoviesByActor(fields[1]);
                break;

            case "K":

                if (fields.length != 2) {
                    invalidCommand(command);
                }

                printByKeyName(fields[1]);
                break;

            default:

                throw new IllegalArgumentException(
                    "Unknown command: " + command
                );
        }
    }

    // Reports invalid command fields.
    private static void invalidCommand(String command) {

        throw new IllegalArgumentException(
            "Invalid fields for command " + command
        );
    }

    // Adds new media.
    private static void addMedia(String[] fields) {

    if (fields.length < 9) {
        throw new IllegalArgumentException(
            "N command requires at least 8 media fields."
        );
    }

    String[] mediaFields =
        Arrays.copyOfRange(fields, 1, fields.length);

    Media media = createMedia(mediaFields);

    mediaList.add(media);
}

    // Prints media of a specific type.
    private static void printByType(char type) {

        ArrayList<Media> results = new ArrayList<>();

        for (Media media : mediaList) {

            if (media.getType() == type) {
                results.add(media);
            }
        }

        String heading =
            type == 'M' ? "Movies" :
            type == 'B' ? "Books" : "Songs";

        printMedia(results, heading);
    }

    // Prints media with a minimum rating.
    private static void printByRating(char type, String ratingText) {

    int minRating = parseInteger(ratingText, "rating");

    if (minRating < 1 || minRating > 10) {
        throw new IllegalArgumentException(
            "Command rating must be between 1 and 10."
        );
    }

    ArrayList<Media> results = new ArrayList<>();

    for (Media media : mediaList) {

        if ((type == 'A' || media.getType() == type) &&
            media.getRating() >= minRating) {

            results.add(media);
        }
    }

    String heading =
        (type == 'A' ? "All Media" :
         type == 'M' ? "Movies" :
         type == 'B' ? "Books" : "Songs")
        + " with Rating >= " + minRating;

    printMedia(results, heading);
}

    // Prints media matching a genre.
    private static void printByGenre(
            char type, String genreName) {

        ArrayList<Media> results = new ArrayList<>();

        String targetGenre = genreName.trim();

        for (Media media : mediaList) {

            if ((type == 'A' || media.getType() == type) &&
                media.getGenre().equalsIgnoreCase(targetGenre)) {

                results.add(media);
            }
        }

        String heading =
            (type == 'A' ? "All Media" :
             type == 'M' ? "Movies" :
             type == 'B' ? "Books" : "Songs")
            + " in Genre: " + targetGenre;

        printMedia(results, heading);
    }

    // L command: Prints stars of a movie.
    private static void printStars(String title) {

        boolean found = false;

        for (Media media : mediaList) {

            if (media instanceof Movie &&
                media.getTitle().equalsIgnoreCase(title.trim())) {

                Movie movie = (Movie) media;

                report.println(
                    "========================================"
                );

                report.println("Stars in " + movie.getTitle());

                report.println(
                    "========================================"
                );

                report.println(movie.getStars());
                report.println();

                found = true;
            }
        }

        if (!found) {
            error.println("Movie not found: " + title);
        }
    }

    // F command: Lists movies featuring an actor.
    private static void printMoviesByActor(String actor) {

        boolean found = false;

        for (Media media : mediaList) {

            if (media instanceof Movie) {

                Movie movie = (Movie) media;

                if (movie.getStars().toLowerCase()
                        .contains(actor.trim().toLowerCase())) {

                    if (!found) {

                        report.println(
                            "========================================"
                        );

                        report.println(
                            "Movies featuring " + actor
                        );

                        report.println(
                            "========================================"
                        );

                        report.println(
                            String.format(
                                "%-35s %-10s %-8s %-20s %-8s %s",
                                "Title", "Year", "Rating",
                                "Genre", "Type", "Data"
                            )
                        );
                    }

                    report.println(movie.printMedia());

                    found = true;
                }
            }
        }

        if (found) {
            report.println();
        } else {
            error.println("Actor not found: " + actor);
        }
    }

    // K command: Lists media matching a key name.
    private static void printByKeyName(String keyName) {

        ArrayList<Media> results = new ArrayList<>();

        for (Media media : mediaList) {

            if (media.getName().equalsIgnoreCase(
                    keyName.trim())) {

                results.add(media);
            }
        }

        if (results.isEmpty()) {

            error.println(
                "Key name not found: " + keyName
            );

        } else {

            printMedia(
                results,
                "Media with Key Name: " + keyName
            );
        }
    }
    // Prints heading and matching media records.
    private static void printMedia(
        ArrayList<Media> items, String heading) {

    report.println("==============================================================");
    report.println(heading);
    report.println("==============================================================");

    String dataHeading = "Data";

    if (heading.equalsIgnoreCase("Movies")) {
        dataHeading = "Stars";
    } else if (heading.equalsIgnoreCase("Books")) {
        dataHeading = "NYTList";
    } else if (heading.equalsIgnoreCase("Songs")) {
        dataHeading = "Top40";
    }
    //Used copilot to assist with this method and formating.
    report.printf(
        "%-35s %-10s %-8s %-20s %-8s %s%n",
        "Title",
        "Year",
        "Rating",
        "Genre",
        "Type",
        dataHeading
    );

    for (Media media : items) {
        report.println(media.printMedia());
    }

    report.println();
}
    // Prints global media counts.
    private static void printCounts() {

        report.println(
            "========================================"
        );

        report.println("Total Media Counts Tracker");

        report.println(
            "========================================"
        );

        report.println(
            "Books Total:   " + Media.mediaCounts[0]
        );

        report.println(
            "Movies Total:  " + Media.mediaCounts[1]
        );

        report.println(
            "Songs Total:   " + Media.mediaCounts[2]
        );

        report.println(
            "========================================"
        );

        report.println();
    }
}


