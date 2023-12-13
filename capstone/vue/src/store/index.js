import { createStore as _createStore } from 'vuex';
import axios from 'axios';
import BeerService from '../services/BeerService';
import BeerReviewService from '../services/BeerReviewService';

export function createStore(currentToken, currentUser) {
  let store = _createStore({
    state: {
      token: currentToken || '',
      user: currentUser || {},

      breweries: [],
      beers: [],
      reviews: [],
      savedBeers: [],
      brewery: [],
      beer: [],


    },
    getters: {
      getSavedBeers : (state) => {
        return state.savedBeers;
      }
    },
    mutations: {
      SET_AUTH_TOKEN(state, token) {
        state.token = token;
        localStorage.setItem('token', token);
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      },

      SET_USER(state, user) {
        state.user = user;
        localStorage.setItem('user', JSON.stringify(user));
      },

      LOGOUT(state) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        state.token = '';
        state.user = {};
        axios.defaults.headers.common = {};
      },
      SET_SAVED_BEERS(state, mySavedBeers) {
        state.savedBeers = mySavedBeers;
      },
      SET_BEER_LIST(state, beerList) {
        state.beers = beerList
      },
      SET_BREWERY(state, brewery) {
        state.brewery = brewery;
      },
      SET_BEER(state, beer) {
        state.beer = beer;
      },
      SET_REVIEWS(state, reviews) {
        state.reviews = reviews;
      }
    },
    actions: {
      getUpdatedBeers({ commit }) {
        BeerService.getSavedBeers()
        .then(response => {
          const updatedBeers = response.data;
          commit('SET_SAVED_BEERS', updatedBeers);
        })
      },
      getUpdatedReviews({commit}, beerId) {
        BeerReviewService.getReviewsByBeerId(beerId)
        .then(response => {
          const updatedReviews = response.data;
          commit('SET_REVIEWS', updatedReviews);
        })
      }
    }
    // set saved beers
    // set brewery beers
    // set all beers
    //

    /* 
    save to service - updates DB
    repopulate list in store
      - SET_BEER_LIST
      -dynamically updates beer list since it's pulling from store
    */
  });
  return store;
}
