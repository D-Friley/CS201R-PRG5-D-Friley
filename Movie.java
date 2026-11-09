public class Movie extends Media {

    private String Stars;

    public Movie(char type, String title, String name,
                 int rating, String genre, int length,
                 int yearReleased, String Stars) {

        super(type, title, name, rating, genre, length, yearReleased);
        this.Stars = Stars;
    }

    public String getStars() {
        return Stars;
    }

    @Override
    public String printMedia() {

        return String.format(
            "%-35s %-10d %-8d %-20s %-8c %s",
            getTitle(),
            getYearReleased(),
            getRating(),
            getGenre(),
            getType(),
            getStars()
        );
    }
}
