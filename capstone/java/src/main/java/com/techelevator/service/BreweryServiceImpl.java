package com.techelevator.service;

import com.techelevator.dao.BreweryDao;
import com.techelevator.model.BrewSearchDTO;
import com.techelevator.model.Brewery;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@Component
public class BreweryServiceImpl implements BreweryService{
    private BreweryDao breweryDao;
    public BreweryServiceImpl (BreweryDao breweryDao) {
        this.breweryDao = breweryDao;
    }
    @Override
    public Brewery getBreweryById(int breweryId) {
        Brewery brewery = breweryDao.getBreweryById(breweryId);
        return brewery;
    }

    @Override
    public List<Brewery> getAllBreweries() {
        return null;
    }

    @Override
    public List<Brewery> searchBreweries(BrewSearchDTO searchTerms) {
        return null;
    }

    @Override
    public List<Brewery> getSavedBreweries(Principal principal) {
        return null;
    }

    @Override
    public Brewery createBrewery(Brewery newBrewery, Principal principal) {
        return null;
    }

    @Override
    public Brewery updateBrewery(Brewery updatedBrewery, Principal principal) {
        return null;
    }

    @Override
    public void deleteBrewery(int breweryId, Principal principal) {

    }
}
