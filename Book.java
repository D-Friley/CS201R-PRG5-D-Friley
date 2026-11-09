public class Book extends Media {
    private int NYTList;

    public Book(char type, String title, String name,
                int rating, String genre, int length,
                int yearReleased, int NYTList) {

    super(type, title, name, rating, genre, length, yearReleased);
    this.NYTList = NYTList;
}

    public int getNYTList() {
        return NYTList;
}

    @Override
        public String printMedia() {
            return String.format(
                "%-35s %-10d %-8d %-20s %-8c %d",
                getTitle(),
                getYearReleased(),
                getRating(),
                getGenre(),
                getType(),
                getNYTList()
    );
}

}
