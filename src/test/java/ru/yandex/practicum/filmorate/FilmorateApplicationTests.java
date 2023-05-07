package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.indatabase.*;

import javax.swing.*;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmorateApplicationTests {

	private final UserDbStorage userStorage;
	private final FilmDbStorage filmStorage;
	private final GenreDbStorage genreStorage;
	private final MpaDbStorage mpaStorage;
	private final FriendshipDbStorage friendshipDbStorage;
	private final LikesDbStorage likesDbStorage;

	@Test
	@Order(1)
	public void testFindUserById() {
		Executable executable = () -> {
			userStorage.findById(1);
		};

		final ObjectNotFoundException e = assertThrows(
				ObjectNotFoundException.class,
				executable
		);

		assertEquals("Пользователя с таким айди не существует.", e.getMessage());
	}

	@Test
	@Order(2)
	public void testFindFilmById() {
		Executable executable = () -> {
			filmStorage.findById(1);
		};

		final ObjectNotFoundException e = assertThrows(
				ObjectNotFoundException.class,
				executable
		);

		assertEquals("Фильма с айди 1 не существует.", e.getMessage());
	}

	@Test
	@Order(3)
	public void testFindGenreById() {

		Optional<Genre> userOptional = Optional.ofNullable(genreStorage.getGenreById(1));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(genre ->
						assertThat(genre).hasFieldOrPropertyWithValue("id", 1L)
				);
	}

	@Test
	@Order(4)
	public void testFindMpaById() {

		Optional<Mpa> userOptional = Optional.ofNullable(mpaStorage.getMpaById(1));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(mpa ->
						assertThat(mpa).hasFieldOrPropertyWithValue("id", 1L)
				);
	}

	@Test
	@Order(5)
	public void testFindAllGenres() {
		Collection<Genre> genres = new ArrayList<>();
		genres.add(new Genre(1, "Комедия"));
		genres.add(new Genre(2, "Драма"));
		genres.add(new Genre(3, "Мультфильм"));
		genres.add(new Genre(4, "Триллер"));
		genres.add(new Genre(5, "Документальный"));
		genres.add(new Genre(6, "Боевик"));

		Optional<Collection<Genre>> userOptional = Optional.ofNullable(genreStorage.getAllGenres());

		assertThat(userOptional)
				.isPresent()
				.isEqualTo(Optional.of(genres));
	}

	@Test
	@Order(6)
	public void testFindAllMpa() {
		Collection<Mpa> mpas = new ArrayList<>();
		mpas.add(new Mpa(1, "G"));
		mpas.add(new Mpa(2, "PG"));
		mpas.add(new Mpa(3, "PG-13"));
		mpas.add(new Mpa(4, "R"));
		mpas.add(new Mpa(5, "NC-17"));

		Optional<Collection<Mpa>> userOptional = Optional.ofNullable(mpaStorage.getAllMpa());

		assertThat(userOptional)
				.isPresent()
				.isEqualTo(Optional.of(mpas));
	}

	@Test
	@Order(7)
	public void testAddUser() {
		User userToAdd = new User(1, "mail@k.com",
				"login", "", LocalDate.of(2003, 12,20), new HashSet<>());
		User userToCompare = new User(1, "mail@k.com",
				"login", "login", LocalDate.of(2003, 12,20), new HashSet<>());

		Optional<User> userOptional = Optional.ofNullable(userStorage.create(userToAdd));

		assertThat(userOptional)
				.isPresent()
				.isEqualTo(Optional.of(userToCompare));
	}

	@Test
	@Order(8)
	public void testFindUserByIdAfterCreate() {
		Optional<User> userOptional1 = Optional.ofNullable(userStorage.findById(1));

		assertThat(userOptional1)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
				);
	}

	@Test
	@Order(9)
	public void testAddFilm() {
		Film filmToAdd = new Film(0, "film1",
				"description1",  LocalDate.of(2003, 12,20), 40,
				new Mpa(2, "PG"), new ArrayList<>(), new HashSet<>());
		Film filmToCompare = new Film(1, "film1",
				"description1",  LocalDate.of(2003, 12,20), 40,
				new Mpa(2, "PG"), new ArrayList<>(), new HashSet<>());

		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.create(filmToAdd));

		assertThat(filmOptional)
				.isPresent()
				.isEqualTo(Optional.of(filmToCompare));
	}

	@Test
	@Order(10)
	public void testFindFilmByIdAfterCreate() {
		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.findById(1));

		assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(film ->
						assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
				);
	}

	@Test
	@Order(11)
	public void testAddFriend() {
		User friendToAdd = new User(1, "mail1@k.com",
				"login1", "friend", LocalDate.of(2003, 12,20), new HashSet<>());
		User friendToCompare = new User(2, "mail1@k.com",
				"login1", "friend", LocalDate.of(2003, 12,20), new HashSet<>());

		Optional<User> userOptional = Optional.ofNullable(userStorage.create(friendToAdd));

		assertThat(userOptional)
				.isPresent()
				.isEqualTo(Optional.of(friendToCompare));
	}

	@Test
	@Order(12)
	public void testFindFriendByIdAfterCreate() {
		Optional<User> userOptional1 = Optional.ofNullable(userStorage.findById(2));

		assertThat(userOptional1)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 2L)
				);
	}

	@Test
	@Order(13)
	public void testUserAddFriend() {
		Optional<User> userOptional = Optional.ofNullable(userStorage.findById(1));
		Optional<User> friendOptional = Optional.ofNullable(userStorage.findById(2));

		friendshipDbStorage.addFriendship(userOptional.get().getId(),
						friendOptional.get().getId());

		Optional<User> friendToCompare = Optional.ofNullable(userStorage.findById(1));
		Set<Long> friends = new HashSet<>();
		friends.add(2L);

		assertThat(friendToCompare)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
				)
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("friends", friends));
	}

	@Test
	@Order(14)
	public void testUserLeaveLike() {
		Optional<User> userOptional = Optional.ofNullable(userStorage.findById(1));
		Optional<Film> filmOptional = Optional.ofNullable(filmStorage.findById(1));

		likesDbStorage.increaseLikes(userOptional.get().getId(),
				filmOptional.get().getId());

		Optional<Film> filmToCompare = Optional.ofNullable(filmStorage.findById(1));

		Set<Long> likes = new HashSet<>();
		likes.add(1L);

		assertThat(filmToCompare)
				.isPresent()
				.hasValueSatisfying(film ->
						assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
				)
				.hasValueSatisfying(film ->
						assertThat(film).hasFieldOrPropertyWithValue("likes", likes));
	}
}
