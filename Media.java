public abstract class Media {

    protected char type;
    protected String title;
    protected String name;
    protected int rating;
    protected String genre;
    protected int length;
    protected int yearReleased;

    public static int[] mediaCounts = {0, 0, 0};

    // Default constructor
    public Media() {
        this.type = ' ';
        this.title = " ";
        this.name = " ";
        this.rating = -1;
        this.genre = " ";
        this.length = -1;
        this.yearReleased = -1;
    }

    // Overloaded constructor
    public Media(char type, String title, String name,
                 int rating, String genre, int length,
                 int yearReleased) {

        this.type = type;
        this.title = title;
        this.name = name;
        this.rating = rating;
        this.genre = genre;
        this.length = length;
        this.yearReleased = yearReleased;

        // Update media counts
        if (type == 'B') {
            mediaCounts[0]++;
        } else if (type == 'M') {
            mediaCounts[1]++;
        } else if (type == 'S') {
            mediaCounts[2]++;
        }
    }

    // Getters
    public char getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    public String getGenre() {
        return genre;
    }

    public int getLength() {
        return length;
    }

    public int getYearReleased() {
        return yearReleased;
    }

    // Setters
    public void setType(char type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setYearReleased(int yearReleased) {
        this.yearReleased = yearReleased;
    }

    // Abstract method implemented by subclasses
    public String printMedia() {
        return String.format(
            "%-35s %-10d %-8d %-20s %-8c",
            getTitle(),
            getYearReleased(),
            getRating(),
            getGenre(),
            getType()
    );
}

    @Override
        public String toString() {
            return printMedia();
}
}