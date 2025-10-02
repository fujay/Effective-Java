package youtube;

import java.util.Arrays;
import java.util.List;

public class NetflixMatchExample {

    public static class Movie {

        private String title;
        private double rating;
        private boolean hdAvailable;

        public Movie(String title, double rating, boolean hdAvailable) {
            this.title = title;
            this.rating = rating;
            this.hdAvailable = hdAvailable;
        }

        @Override
        public String toString() {
            return "Movie [title=" + title + ", rating=" + rating + ", hdAvailable=" + hdAvailable + "]";
        }
    }

    public static void main(String[] args) {
        List<Movie> movies = Arrays.asList(
                new Movie("Inception", 8.8, true),
                new Movie("The Dark Knight", 9.0, true),
                new Movie("Interstellar", 8.6, true),
                new Movie("Random Short Film", 4.2, false));

        // movies.stream()
        // .forEach(movie -> System.out.println(movie));

        // var matchingMovie = movies.stream().filter(m -> m.rating > 8.0);
        // matchingMovie.forEach(movie -> System.out.println(movie.title));

        boolean anyBlockBuster = movies.stream()
                .anyMatch(m -> m.rating > 9.0);
        System.out.println("Any blockbuster (>9.0)? " + anyBlockBuster);

        boolean isAllHd = movies.stream().allMatch(m -> m.hdAvailable);
        System.out.println("All movies available in HD? " + isAllHd);

        boolean noTrashMovies = movies.stream().noneMatch(m -> m.rating < 3.0);
        System.out.println("No movie with rating < 3.0? " + noTrashMovies);
    }

}
