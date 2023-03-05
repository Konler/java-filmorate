package ru.yandex.practicum.filmorate.sevice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.messages.LogMessages;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Service
@Slf4j
public class FilmService {
    public static final Comparator<Film> COMPARATOR = Comparator.comparing(Film::getPopularFilmsList).reversed();
    private static final LocalDate BIRTH_DATE_OF_CINEMA = LocalDate.of(1895, 12, 28);
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    @Autowired
    public FilmService(FilmStorage  filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }
    public void addLike(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId);
        checkIfFilmNull(film);
        userStorage.findUserById(userId);
        film.addLike(userId);
        log.info(LogMessages.LIKED_FILM.toString(), film);
    }

    public void removeLike(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId);
        checkIfFilmNull(film);
        userStorage.findUserById(userId);
        film.removeLike(userId);
        log.info(LogMessages.UNLIKED_FILM.toString(), film);
    }
    public List<Film> getPopularFilmsList(int count) {
        log.info(LogMessages.POPULAR_TOTAL.toString(), count);
        return filmStorage.getAllFilms().stream()
                .sorted(COMPARATOR)
                .limit(count)
                .collect(Collectors.toList());
    }
    public void validate(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(BIRTH_DATE_OF_CINEMA)) {
            log.warn(LogMessages.INCORRECT_FILM_RELEASE_DATE.toString());
            throw new ValidationException(LogMessages.INCORRECT_FILM_RELEASE_DATE.toString());
        }
    }
    public void checkIfFilmNull(Film film) {
        if (film == null) {
            log.warn(LogMessages.NULL_OBJECT.toString());
        }
    }
    public Film findFilm(long id) {
        return filmStorage.findFilmById(id);
    }
    public void deleteFilmById(long id) {
        filmStorage.deleteFilm(id);
        log.info(LogMessages.FILM_DELETED.toString(), id);
    }
    public Film addFilm(Film film) {
        validate(film);
       filmStorage.add(film);
        log.info(LogMessages.FILM_ADDED.toString(), film);
        return film;
    }
    public Film updateFilm(Film film) {
        validate(film);
       filmStorage.update(film);
        log.info(LogMessages.FILM_UPDATED.toString(), film);
        return film;
    }

    public List<Film> getAll() {
        return filmStorage.getAllFilms();
    }
}