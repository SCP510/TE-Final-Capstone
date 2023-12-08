package com.techelevator.dao;

import com.techelevator.exception.DaoException;
import com.techelevator.model.Beer;
import com.techelevator.model.BrewSearchDTO;
import com.techelevator.model.Brewery;
import com.techelevator.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.security.Timestamp;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class JdbcBreweryDao implements BreweryDao {

    // Instance Variables/Properties
    private JdbcTemplate jdbcTemplate;
    private JdbcUserDao jdbcUserDao;



    // Constructors
    public JdbcBreweryDao(JdbcTemplate jdbcTemplate, JdbcUserDao jdbcUserDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcUserDao = jdbcUserDao;
    }



    // Methods
    @Override
    public Brewery getBreweryById (int breweryId) {
        Brewery brewery = null;
        brewery.setBreweryId(breweryId);
        String sql = "SELECT brewery_id, brewery_name, street_address, city, state, zip_code," +
                     " date_est, phone_number, about_us, website, logo_image, founder_id " +
                "FROM brewery WHERE brewery_id = ?;";
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, breweryId);
            if (result.next()) {
                brewery = mapRowToBrewery(result);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return brewery;
    }

    @Override
    public List<Brewery> getAllBreweries() { // TODO if the long searchBreweries is a go, we don't need this
        List<Brewery> breweries = new ArrayList<>();
        String sql = "SELECT brewery_id, brewery_name, street_address, city, state, zip_code," +
                " date_est, phone_number, about_us, website, logo_image, founder_id " +
                "FROM brewery;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                Brewery brewery = mapRowToBrewery(results);
                breweries.add(brewery);
            }
            return breweries;
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database.", e);
        }
    }

    @Override
    public List<Brewery> getSavedBreweries(Principal principal) {
        List<Brewery> myBreweries = new ArrayList<>();
        String sql = "SELECT brewery_id FROM favorite_brewery WHERE user_id = ?;";
        try {
            User user = jdbcUserDao.getUserByUsername(principal.getName());
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user.getId());
            while (results.next()) {
                myBreweries.add(getBreweryById(results.getInt("brewery_id")));
            }
            return myBreweries;
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database.", e);
        }
    }

    @Override
    public Brewery createBrewery(Brewery newBrewery, Principal principal) {
        String sql = "INSERT INTO brewery (brewery_name, street_address, city, state, zip_code," +
                " date_est, phone_number, about_us, website, logo_image, founder_id " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "RETURNING brewery_id;";
        try {
            int newBreweryId = jdbcTemplate.queryForObject(sql, int.class, newBrewery.getBreweryName(), newBrewery.getStreetAddress(),
                    newBrewery.getCity(), newBrewery.getState(), newBrewery.getZipcode(), newBrewery.getDateEst(), newBrewery.getPhoneNumber(),
                    newBrewery.getAboutUs(), newBrewery.getWebsite(), newBrewery.getLogoImage(), newBrewery.getFounderId());
            return getBreweryById(newBreweryId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data Integrity Violation", e);
        }
    }

    @Override
    public Brewery updateBreweryInfo(Brewery updatedBrewery, Principal principal) {
        User user = jdbcUserDao.getUserByUsername(principal.getName());
        String userValidSql = "SELECT founder_id FROM brewery WHERE brewery_id = ?;";
        String sql = "UPDATE brewery " +
                "SET brewery_name = ?, street_address = ?, city = ?, state = ?, zip_code = ?" +
                " date_est =?, phone_number = ?, about_us = ?, website = ?, logo_image = ?, founder_id = ?" +
                "WHERE brewery_id = ?";
        try {
            int founderId = jdbcTemplate.queryForObject(userValidSql, int.class, updatedBrewery.getBreweryId());
            if (user.getId() == founderId) {
                int rowsAffected = jdbcTemplate.update(sql, updatedBrewery.getBreweryName(), updatedBrewery.getStreetAddress(), updatedBrewery.getCity(),
                        updatedBrewery.getState(), updatedBrewery.getZipcode(), updatedBrewery.getDateEst(), updatedBrewery.getPhoneNumber(),
                        updatedBrewery.getAboutUs(), updatedBrewery.getWebsite(), updatedBrewery.getLogoImage(), updatedBrewery.getFounderId());
                return getBreweryById(updatedBrewery.getBreweryId());
            } else {
                throw new DaoException("You do not have required permissions to update this Brewery. Please contact the Brewery Founder.");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database.");
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data Integrity Violation");
        }
    }

    @Override
    public void deleteBrewery(int breweryId, Principal principal) {
        User user = jdbcUserDao.getUserByUsername(principal.getName());
        String favBrewerySql = "DELETE FROM favorite_brewery WHERE brewery_id = ?;"; //Deletes from favorite_brewery
        String brewerySql = "DELETE FROM brewery WHERE brewery_id = ?;"; //Deletes from brewery
        String userValidSql = "SELECT founder_id FROM brewery WHERE brewery_id = ?;";
        try {
            Brewery breweryToDelete = getBreweryById(breweryId);
            int founderId = jdbcTemplate.queryForObject(userValidSql, int.class, breweryToDelete.getBreweryId());
            if (user.getId() == founderId) {
                jdbcTemplate.update(favBrewerySql, breweryId); //Updates favorite_brewery
                jdbcTemplate.update(brewerySql, breweryId); //Updates brewery
            } else {
                throw new DaoException("You do not have required permissions to delete this Brewery. Please contact the Brewery Founder.");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database.");
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data Integrity Violation");
        }
    }


    // Helper Methods
    private Brewery mapRowToBrewery (SqlRowSet rs) {
        Brewery brewery = new Brewery();
        brewery.setBreweryId(rs.getInt("brewery_id"));
        brewery.setBreweryName(rs.getString("brewery_name"));
        brewery.setStreetAddress(rs.getString("street_address"));
        brewery.setCity(rs.getString("city"));
        brewery.setState(rs.getString("state"));
        brewery.setZipcode(rs.getString("zip_code"));

        if (rs.getTimestamp("date_est") != null) {
        brewery.setDateEst(rs.getTimestamp("date_est").toLocalDateTime());
        }
        brewery.setPhoneNumber(rs.getString("phone_number"));
        brewery.setAboutUs(rs.getString("about_us"));
        brewery.setWebsite(rs.getString("website"));
        brewery.setLogoImage(rs.getString("logo_image"));
        brewery.setFounderId(rs.getInt("founder_id"));
        return brewery;
    }

    // TODO there must be a better way, talk to Myron
//    @Override
//    public List<Brewery> searchBreweries(BrewSearchDTO searchTerms) {
//        final String SQL_WHERE_CITY_STATE_ZIPCODE = "WHERE city = ? AND state = ? AND zip_code = ?;";
//        final String SQL_WHERE_CITY_STATE = "WHERE city = ? AND state = ?;";
//        final String SQL_WHERE_CITY_ZIPCODE = "WHERE city = ? AND zip_code = ?;";
//        final String SQL_WHERE_STATE_ZIPCODE = "WHERE state = ? AND zipcode = ?;";
//        final String SQL_WHERE_CITY = "WHERE city = ?;";
//        final String SQL_WHERE_STATE = "WHERE state = ?;";
//        final String SQL_WHERE_ZIPCODE = "WHERE zipcode = ?;";
//
//        List<Brewery> breweries = new ArrayList<>();
//        SqlRowSet results = null;
//
//        String sql = "SELECT brewery_id, brewery_name, street_address, city, state, zip_code," +
//                " date_est, phone_number, about_us, website, logo_image, founder_id " +
//                "FROM brewery ";
//        try {
//            if (searchTerms.getCity() != null && !searchTerms.getCity().isEmpty() && searchTerms.getState() != null && !searchTerms.getState().isEmpty() && searchTerms.getZipcode() != null && !searchTerms.getZipcode().isEmpty()) {
//                sql += SQL_WHERE_CITY_STATE_ZIPCODE;
//                results = jdbcTemplate.queryForRowSet(sql, searchTerms.getCity(), searchTerms.getState(), searchTerms.getZipcode());
//
//            } else if (searchTerms.getCity() != null && !searchTerms.getCity().isEmpty() && searchTerms.getState() != null && !searchTerms.getState().isEmpty()) {
//                sql += SQL_WHERE_CITY_STATE;
//                results = jdbcTemplate.queryForRowSet(sql, searchTerms.getCity(), searchTerms.getState());
//
//            } else if (searchTerms.getCity() != null && !searchTerms.getCity().isEmpty() && searchTerms.getZipcode() != null && !searchTerms.getZipcode().isEmpty()) {
//                sql += SQL_WHERE_CITY_ZIPCODE;
//                results = jdbcTemplate.queryForRowSet(sql, searchTerms.getCity(), searchTerms.getZipcode());
//
//            } else if (searchTerms.getState() != null && !searchTerms.getState().isEmpty() && searchTerms.getZipcode() != null && !searchTerms.getZipcode().isEmpty()) {
//                sql += SQL_WHERE_STATE_ZIPCODE;
//                results = jdbcTemplate.queryForRowSet(sql, searchTerms.getState(), searchTerms.getZipcode());
//
//            } else if (searchTerms.getCity() != null && !searchTerms.getCity().isEmpty()) {
//                sql += SQL_WHERE_CITY;
//                results = jdbcTemplate.queryForRowSet(sql, searchTerms.getCity());
//
//            } else if (searchTerms.getState() != null && !searchTerms.getState().isEmpty()) {
//                sql += SQL_WHERE_STATE;
//                results = jdbcTemplate.queryForRowSet(sql, searchTerms.getState());
//
//            } else if (searchTerms.getZipcode() != null && !searchTerms.getZipcode().isEmpty()) {
//                sql += SQL_WHERE_ZIPCODE;
//                results = jdbcTemplate.queryForRowSet(sql, searchTerms.getZipcode());
//            } else {
//                results = jdbcTemplate.queryForRowSet(sql);
//            }
//
//            while (results.next()) {
//                Brewery brewery = mapRowToBrewery(results);
//                breweries.add(brewery);
//            }
//        }catch (CannotGetJdbcConnectionException e) {
//            throw new DaoException("Unable to connect to server or database", e);
//        }
//        return breweries;
//    }

    // TODO talk to Myron about this, in progress maybe
//    @Override
//    public List<Brewery> searchBreweries(BrewSearchDTO searchTerms) {
//        final String SQL_WHERE_CITY = "city = ? ";
//        final String SQL_WHERE_STATE = "state = ? ";
//        final String SQL_WHERE_ZIPCODE = "zipcode = ? ";
//
//        List<Brewery> breweries = new ArrayList<>();
//        SqlRowSet results = null;
//
//        String blankSql = "";
//        BrewSearchDTO searchBlank = null;
//
//        String sql = "SELECT brewery_id, brewery_name, street_address, city, state, zip_code," +
//                " date_est, phone_number, about_us, website, logo_image, founder_id " +
//                "FROM brewery " +
//                "WHERE city = ? state = ?";
//        try {
//            if (searchTerms.getCity() != null && !searchTerms.getCity().isEmpty()) {
//                sql += SQL_WHERE_CITY;
//            }
//            if (searchTerms.getState() != null && !searchTerms.getState().isEmpty()) {
//                sql += SQL_WHERE_STATE;
//            }
//            if (searchTerms.getZipcode() != null && !searchTerms.getZipcode().isEmpty()) {
//                sql += SQL_WHERE_ZIPCODE;
//            }
//
//            results = jdbcTemplate.queryForRowSet(sql, searchTerms.getCity(), searchTerms.getState(), searchTerms.getZipcode());
//
//            while (results.next()) {
//                Brewery brewery = mapRowToBrewery(results);
//                breweries.add(brewery);
//            }
//        }catch (CannotGetJdbcConnectionException e) {
//            throw new DaoException("Unable to connect to server or database", e);
//        }
//        return breweries;
//    }
}
