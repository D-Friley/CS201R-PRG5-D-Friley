public class Song extends Media {

    private boolean Top40;

    public Song(char type, String title, String name,
                int rating, String genre, int length,
                int yearReleased, boolean Top40) {

        super(type, title, name, rating, genre, length, yearReleased);
        this.Top40 = Top40;
    }

    public boolean getTop40() {
        return Top40;
    }

    public boolean getTop40Text() {
        return Top40;
    }
    // Used copilot to assist with this method and formating.
    @Override
    public String printMedia() {
        String Top40Text = getTop40Text() ? "Top 40 hit" : "Not Top 40 hit";

        return String.format(
            "%-35s %-10d %-8d %-20s %-8c %s",
            getTitle(),
            getYearReleased(),
            getRating(),
            getGenre(),
            getType(),
            Top40Text
        );
    }
}
