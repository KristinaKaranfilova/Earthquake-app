import React, { useState, useCallback } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import FetchButton from './components/FetchButton';
import FilterPanel from './components/FilterPanel';
import EarthquakeTable from './components/EarthquakeTable';
import {
    fetchFromUSGS,
    getAllEarthquakes,
    filterByMagnitude,
    filterByTime,
    deleteEarthquake,
} from './services/earthquakeService';

function App() {
    const [earthquakes, setEarthquakes] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [statusMsg, setStatusMsg] = useState('');

    const handleError = (err) => {
        setError(err?.response?.data?.error || err.message || 'Unknown error');
    };

    const handleFetch = useCallback(async () => {
        setLoading(true);
        setError(null);
        setStatusMsg('');
        try {
            const res = await fetchFromUSGS();
            setStatusMsg(`Fetched and stored ${res.data.count} earthquakes.`);
            const all = await getAllEarthquakes();
            setEarthquakes(all.data);
        } catch (err) {
            handleError(err);
        } finally {
            setLoading(false);
        }
    }, []);

    const handleFilterMagnitude = useCallback(async (min) => {
        setError(null);
        try {
            const res = await filterByMagnitude(min);
            setEarthquakes(res.data);
            setStatusMsg(`Showing ${res.data.length} earthquakes with magnitude > ${min}`);
        } catch (err) {
            handleError(err);
        }
    }, []);

    const handleFilterTime = useCallback(async (afterMs) => {
        setError(null);
        try {
            const res = await filterByTime(afterMs);
            setEarthquakes(res.data);
            setStatusMsg(`Showing ${res.data.length} earthquakes after selected time`);
        } catch (err) {
            handleError(err);
        }
    }, []);

    const handleClearFilters = useCallback(async () => {
        setError(null);
        setStatusMsg('');
        try {
            const res = await getAllEarthquakes();
            setEarthquakes(res.data);
            setStatusMsg('All filters cleared');
        } catch (err) {
            handleError(err);
        }
    }, []);

    const handleDelete = useCallback(async (id) => {
        setError(null);
        try {
            await deleteEarthquake(id);
            setEarthquakes(prev => prev.filter(eq => eq.id !== id));
        } catch (err) {
            handleError(err);
        }
    }, []);

    return (
        <div className="container mt-4">
            <h1 className="mb-1">Earthquake Monitor</h1>
            <p className="text-muted mb-4">Live data from USGS — last hour, magnitude &gt; 2.0</p>

            <FetchButton onFetch={handleFetch} loading={loading} />

            {statusMsg && (
                <div className="alert alert-info py-2 mb-3">{statusMsg}</div>
            )}
            {error && (
                <div className="alert alert-danger py-2 mb-3">Error: {error}</div>
            )}

            <FilterPanel
                onFilterMagnitude={handleFilterMagnitude}
                onFilterTime={handleFilterTime}
                onClear={handleClearFilters}
            />

            <EarthquakeTable
                earthquakes={earthquakes}
                onDelete={handleDelete}
            />
        </div>
    );
}

export default App;
