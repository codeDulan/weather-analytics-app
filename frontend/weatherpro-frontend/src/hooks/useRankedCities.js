import { useCallback, useEffect, useState } from 'react';

import { fetchRankedCities } from '../api/weather';

export function useRankedCities() {
    const [cities, setCities] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const load = useCallback(async () => {
        setLoading(true);
        setError(null);

        try {
            const data = await fetchRankedCities();
            setCities(Array.isArray(data) ? data : []);
        } catch (err) {
            setError(err);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        load();
    }, [load]);

    return { cities, loading, error, reload: load };
}