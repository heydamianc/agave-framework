/*
 * Copyright (c) 2011, Damian Carrillo
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of 
 *     conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of 
 *     conditions and the following disclaimer in the documentation and/or other materials 
 *     provided with the distribution.
 *   * Neither the name of the copyright holder's organization nor the names of its contributors 
 *     may be used to endorse or promote products derived from this software without specific 
 *     prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package co.cdev.agave.sample;

import java.util.Collection;
import java.util.Collections;

import co.cdev.agave.Destination;
import co.cdev.agave.Destinations;
import co.cdev.agave.RoutingContext;
import co.cdev.agave.Route;
import co.cdev.agave.HttpMethod;
import co.cdev.agave.exception.AgaveException;

/**
 * Assume there is a domain object named {@code Ticket} whose role is to grant
 * access into another domain object named {@code Movie} that is playing in some
 * {@code Theater}.
 * 
 * @author <a href="mailto:damiancarrillo@gmail.com">Damian Carrillo</a>
 */
public class MovieHandler {

    private MovieRepository movieRepository;

    public MovieHandler() {
        movieRepository = new MovieRepository();
    }

    @Route(uri = "/movies", method = HttpMethod.GET)
    public Destination listMovies(RoutingContext context) throws AgaveException {
        context.getRequest().setAttribute("movies", movieRepository.list());
        return Destinations.forward("/WEB-INF/movies/list.jsp");
    }

    @Route(uri = "/movies", method = HttpMethod.PUT)
    public Destination replaceMovies(RoutingContext context, MoviesForm moviesForm) throws AgaveException {
        context.getRequest().setAttribute("success", movieRepository.add(moviesForm.list()));
        return Destinations.forward("/WEB-INF/movies/replace.jsp");
    }

    @Route(uri = "/movies", method = HttpMethod.POST)
    public Destination createMovie(RoutingContext context, MovieForm movieForm) throws AgaveException {
        context.getRequest().setAttribute("success", movieRepository.add(movieForm.getMovie()));
        return Destinations.forward("/WEB-INF/movies/create.jsp");
    }

    @Route(uri = "/movies", method = HttpMethod.DELETE)
    public Destination deleteMovies(RoutingContext context) throws AgaveException {
        Collection<Movie> movies = movieRepository.list();
        context.getRequest().setAttribute("success", movieRepository.remove(movies));
        return Destinations.forward("/WEB-INF/movies/delete.jsp");
    }

    @Route(uri = "/movies/${title}", method = HttpMethod.GET)
    public Destination retrieveMovie(RoutingContext context, MovieForm movieForm) throws AgaveException {
        context.getRequest().setAttribute("movie", movieRepository.get(movieForm.getTitle()));
        return Destinations.forward("/WEB-INF/movie/display.jsp");
    }

    @Route(uri = "/movies/${title}", method = HttpMethod.PUT)
    public Destination replaceMovie(RoutingContext context, MovieForm movieForm) throws AgaveException {
        if (movieRepository.remove(movieForm.getTitle())) {
            context.getRequest().setAttribute("success", movieRepository.add(movieForm.getMovie()));
        }
        return Destinations.forward("/WEB-INF/movie/replace.jsp");
    }

    @Route(uri = "/movies/${title}", method = HttpMethod.DELETE)
    public Destination deleteMovie(RoutingContext context, MovieForm movieForm) throws AgaveException {
        movieRepository.remove(movieForm.getTitle());
        return Destinations.forward("/WEB-INF/movie/delete.jsp");
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // The following classes illustrate the example, however they would
    // typically be placed inside of their own file and have actual properties,
    // methods, etc.
    public static class MovieRepository {

        Collection<Movie> list() {
            return Collections.emptyList();
        }

        Movie get(Object id) {
            return new Movie();
        }

        boolean add(Collection<Movie> movies) {
            return true;
        }

        boolean add(Movie movie) {
            return true;
        }

        boolean remove(Collection<Movie> movies) {
            return true;
        }

        boolean remove(Object movieId) {
            return true;
        }
    }

    public static class Movie {
    }

    public static class Ticket {
    }

    public static class Theater {
    }

    public static class MoviesForm {

        Collection<Movie> list() {
            return null;
        }
    }

    public static class MovieForm {

        private String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        Movie getMovie() {
            return new Movie();
        }
    }
}