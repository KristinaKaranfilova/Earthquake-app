import React, { useState } from 'react';

function FilterPanel({ onFilterMagnitude, onFilterTime, onClear }) {
    const [minMag, setMinMag] = useState('');
    const [afterDate, setAfterDate] = useState('');

    const handleMagFilter = () => {
        if (minMag !== '') onFilterMagnitude(parseFloat(minMag));
    };

    const handleTimeFilter = () => {
        if (afterDate) {
            const epochMs = new Date(afterDate).getTime();
            onFilterTime(epochMs);
        }
    };

    return (
        <div className="card mb-3">
            <div className="card-body">
                <h6 className="card-title fw-semibold">Filter Earthquakes</h6>
                <div className="row g-2 align-items-end">
                    <div className="col-auto">
                        <label className="form-label">Min Magnitude</label>
                        <input
                            type="number"
                            className="form-control"
                            placeholder="e.g. 3.0"
                            value={minMag}
                            onChange={e => setMinMag(e.target.value)}
                            step="0.1"
                            min="0"
                        />
                    </div>
                    <div className="col-auto">
                        <button className="btn btn-outline-secondary" onClick={handleMagFilter}>
                            Apply
                        </button>
                    </div>
                    <div className="col-auto">
                        <label className="form-label">After Date &amp; Time</label>
                        <input
                            type="datetime-local"
                            className="form-control"
                            value={afterDate}
                            onChange={e => setAfterDate(e.target.value)}
                        />
                    </div>
                    <div className="col-auto">
                        <button className="btn btn-outline-secondary" onClick={handleTimeFilter}>
                            Apply
                        </button>
                    </div>
                    <div className="col-auto">
                        <button className="btn btn-outline-danger" onClick={onClear}>
                            Clear Filters
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default FilterPanel;
