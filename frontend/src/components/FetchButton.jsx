import React from 'react';

function FetchButton({ onFetch, loading }) {
    return (
        <button
            className="btn btn-primary mb-3"
            onClick={onFetch}
            disabled={loading}
        >
            {loading ? 'Fetching...' : 'Fetch Latest Earthquakes'}
        </button>
    );
}

export default FetchButton;
