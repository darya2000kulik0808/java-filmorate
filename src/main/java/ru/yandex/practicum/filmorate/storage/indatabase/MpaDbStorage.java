package ru.yandex.practicum.filmorate.storage.indatabase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
@Qualifier("MpaDbStorage")
@Slf4j
public class MpaDbStorage implements MpaStorage  {

    JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Mpa> getAllMpa() {
        try {
            log.info("Запрос к базе данных: получение всех рейтингов MPA.");
            String sql = "SELECT * FROM MPA;";
            Collection<Mpa> mpa = jdbcTemplate.query(sql, this::makeMpa);
            log.info("Получили все MPA.");
            return mpa;
        } catch (EmptyResultDataAccessException exception) {
            throw new ObjectNotFoundException("Рейтинги не найдены.");
        }
    }

    public Mpa getMpaById(long mpaId) {
        try {
            log.info("Запрос к базе данных: получение рейтинга по айди.");
            String sql = "SELECT * FROM MPA WHERE MPA.MPA_ID = ?;";
            Mpa mpa = jdbcTemplate.queryForObject(sql, this::makeMpa, mpaId);
            log.info("нашли рейтинг по айди {}.", mpa);
            return mpa;
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundException("Рейтинг с айди " + mpaId + " не найден.");
        }
    }

    private Mpa makeMpa(ResultSet resultSet,  int rowNum) throws SQLException {
        long id = resultSet.getLong("MPA_ID");
        String name = resultSet.getString("MPA_NAME");
        return new Mpa(id, name);
    }
}
