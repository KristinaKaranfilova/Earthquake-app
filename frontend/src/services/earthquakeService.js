import axios from 'axios';

const BASE_URL = '/api/earthquakes';

export const fetchFromUSGS = () =>
    axios.get(`${BASE_URL}/fetch`);

export const getAllEarthquakes = () =>
    axios.get(BASE_URL);

export const filterByMagnitude = (min) =>
    axios.get(`${BASE_URL}/filter/magnitude`, { params: { min } });

export const filterByTime = (after) =>
    axios.get(`${BASE_URL}/filter/time`, { params: { after } });

export const deleteEarthquake = (id) =>
    axios.delete(`${BASE_URL}/${id}`);
