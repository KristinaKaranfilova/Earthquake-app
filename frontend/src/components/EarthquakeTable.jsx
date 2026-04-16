import React from 'react';

function formatTime(epochMs) {
    if (!epochMs) return '—';
    return new Date(epochMs).toLocaleString();
}

function EarthquakeTable({ earthquakes, onDelete }) {
    if (!earthquakes || earthquakes.length === 0) {
        return <p className="text-muted">No earthquakes to display. Click "Fetch Latest Earthquakes" to load data.</p>;
    }

    return (
        <div className="table-responsive">
            <table className="table table-striped table-hover align-middle">
                <thead className="table-dark">
                    <tr>
                        <th>Magnitude</th>
                        <th>Mag Type</th>
                        <th>Place</th>
                        <th>Title</th>
                        <th>Time</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    {earthquakes.map(eq => (
                        <tr key={eq.id}>
                            <td>
                                <span className={`badge ${eq.magnitude >= 5 ? 'bg-danger' : eq.magnitude >= 3 ? 'bg-warning text-dark' : 'bg-secondary'}`}>
                                    {eq.magnitude?.toFixed(1)}
                                </span>
                            </td>
                            <td>{eq.magType || '—'}</td>
                            <td>{eq.place || '—'}</td>
                            <td>{eq.title || '—'}</td>
                            <td>{formatTime(eq.time)}</td>
                            <td>
                                <button
                                    className="btn btn-sm btn-outline-danger"
                                    onClick={() => onDelete(eq.id)}
                                >
                                    Delete
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default EarthquakeTable;
